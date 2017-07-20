package net.torocraft.nemesissystem.handlers;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.network.MessageSyncNemesis;
import net.torocraft.nemesissystem.network.MessageSyncNemesisRequest;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.registry.NemesisRegistryProvider;
import net.torocraft.nemesissystem.util.BehaviorUtil;
import net.torocraft.nemesissystem.util.EntityDecorator;
import net.torocraft.nemesissystem.util.NemesisActions;
import net.torocraft.nemesissystem.util.NemesisUtil;
import net.torocraft.nemesissystem.util.SpawnUtil;

public class Spawn {

	private static final int SPAWN_CHANCE = 10;

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new Spawn());
	}

	@SubscribeEvent
	public void handleSpawn(EntityJoinWorldEvent event) {

		if (event.getEntity().world.isRemote) {
			requestNemesisDataFromServer(event);
			return;
		}

		if (!(event.getEntity() instanceof EntityCreature) || !NemesisUtil.isNemesisClassEntity(event.getEntity())) {
			return;
		}

		NemesisActions.handleRandomPromotions(event.getWorld(), (EntityCreature) event.getEntity());

		if (event.getEntity().getTags().contains(NemesisSystem.TAG_NEMESIS)) {
			handleRespawnOfNemesis(event);
			return;
		}

		if (event.getEntity().getTags().contains(NemesisSystem.TAG_BODY_GUARD)) {
			return;
		}

		Nemesis nemesis = getNemesisForSpawn(event);

		if (nemesis == null) {
			return;
		}

		replaceEntityWithNemesis((EntityCreature) event.getEntity(), nemesis);
	}

	private void requestNemesisDataFromServer(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof EntityCreature) {
			if (!event.getEntity().getTags().contains(NemesisSystem.TAG_NEMESIS)) {
				NemesisSystem.NETWORK.sendToServer(new MessageSyncNemesisRequest(event.getEntity().getPersistentID()));
			}
		}
	}

	private void replaceEntityWithNemesis(EntityCreature entity, Nemesis nemesis) {
		entity.setDead();
		spawnNemesis(entity.world, entity.getPosition(), nemesis);
	}

	private void handleRespawnOfNemesis(EntityJoinWorldEvent event) {
		Entity entity = event.getEntity();
		Nemesis nemesis = NemesisUtil.loadNemesisFromEntity(event.getEntity());
		if (nemesis == null) {
			/*
			 * missing nemesis data
			 */
			entity.setDead();
			event.setCanceled(true);
		} else if (entity.getTags().contains(NemesisSystem.TAG_SPAWNING)) {
			/*
			 * new nemesis spawn in progress
			 */
			entity.removeTag(NemesisSystem.TAG_SPAWNING);
			nemesis.setSpawned(entity.getEntityId());
			nemesis.setUnloaded(null);
			NemesisRegistryProvider.get(entity.world).update(nemesis);
			sendNemesisDataToClient(nemesis);
		} else if (!nemesis.isSpawned()) {
			/*
			 * nemesis has been marked as despawned
			 */
			event.setCanceled(true);
		} else {
			/*
			 * nemesis is marked unloaded, mark as loaded now he is respawning
			 */

			if (!entity.getPersistentID().equals(nemesis.getEntityUuid())) {
				event.setCanceled(true);
				return;
			}

			nemesis.setSpawned(entity.getEntityId());
			nemesis.setUnloaded(null);
			NemesisRegistryProvider.get(event.getWorld()).update(nemesis);
			sendNemesisDataToClient(nemesis);
		}
	}

	public static void spawnNemesis(World world, BlockPos pos, Nemesis nemesis) {
		if (nemesis.isDead()) {
			return;
		}
		if (nemesis.isSpawned()) {
			return;
		}
		EntityCreature nemesisEntity = SpawnUtil.getEntityFromString(world, nemesis.getMob());

		if (nemesisEntity == null) {
			return;
		}

		nemesisEntity.addTag(NemesisSystem.TAG_SPAWNING);

		EntityDecorator.decorate(nemesisEntity, nemesis);
		SpawnUtil.spawnEntityLiving(world, nemesisEntity, pos, 1);

		spawnBodyGuard(nemesisEntity, nemesis);
		nemesisAnnounceEffects(nemesisEntity);

		nemesis.setSpawned(nemesisEntity.getEntityId());
		nemesis.setEntityUuid(nemesisEntity.getPersistentID());
		nemesis.setUnloaded(null);
		NemesisRegistryProvider.get(world).update(nemesis);

		sendNemesisDataToClient(nemesis);
	}

	private static void sendNemesisDataToClient(Nemesis nemesis) {
		NemesisSystem.NETWORK.sendToAll(new MessageSyncNemesis(nemesis));
	}

	private static void nemesisAnnounceEffects(EntityCreature nemesisEntity) {
		World world = nemesisEntity.world;

		if (canSeeSky(nemesisEntity)) {
			world.addWeatherEffect(new EntityLightningBolt(nemesisEntity.world, nemesisEntity.posX, nemesisEntity.posY, nemesisEntity.posZ, true));
		}

		// TODO sound horn
	}

	private static boolean canSeeSky(Entity e) {
		return e.world.canSeeSky(new BlockPos(e.posX, e.posY + (double) e.getEyeHeight(), e.posZ));
	}

	private static void spawnBodyGuard(EntityLiving entity, Nemesis nemesis) {

		// TODO high level nemeses spawn other nemesis in their body guard

		// TODO add body guard ranks? (different armor, ai, weapons)

		// TODO use nemesis colors

		int count = 3 + nemesis.getLevel() * 3;

		for (int i = 0; i < count; i++) {
			EntityCreature bodyGuard = new EntityZombie(entity.getEntityWorld());
			bodyGuard.addTag(NemesisSystem.TAG_BODY_GUARD);
			bodyGuard.getEntityData().setUniqueId(NemesisSystem.NBT_NEMESIS_ID, nemesis.getId());
			equipBodyGuard(bodyGuard);
			SpawnUtil.spawnEntityLiving(entity.getEntityWorld(), bodyGuard, entity.getPosition(), 10);
			BehaviorUtil.setFollowSpeed(bodyGuard, 1.5);
		}
	}

	private static void equipBodyGuard(EntityCreature bodyGuard) {
		int color = 0xffffff;
		// TODO change weapon base on rank, or nemesis boss title, or trait?
		bodyGuard.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
		bodyGuard.setItemStackToSlot(EntityEquipmentSlot.HEAD, colorArmor(new ItemStack(Items.LEATHER_HELMET, 1), color));
		bodyGuard.setItemStackToSlot(EntityEquipmentSlot.CHEST, colorArmor(new ItemStack(Items.LEATHER_CHESTPLATE, 1), color));
		bodyGuard.setItemStackToSlot(EntityEquipmentSlot.LEGS, colorArmor(new ItemStack(Items.LEATHER_LEGGINGS, 1), color));
		bodyGuard.setItemStackToSlot(EntityEquipmentSlot.FEET, colorArmor(new ItemStack(Items.LEATHER_BOOTS, 1), color));
	}

	protected static ItemStack colorArmor(ItemStack stack, int color) {
		ItemArmor armor = (ItemArmor) stack.getItem();
		armor.setColor(stack, color);
		return stack;
	}

	private static Nemesis getNemesisForSpawn(EntityEvent event) {

		if (!(event.getEntity() instanceof EntityZombie)) {
			return null;
		}

		EntityLiving entity = (EntityLiving) event.getEntity();
		World world = entity.world;
		Random rand = entity.getRNG();

		if (!playerInRange(entity, world)) {
			return null;
		}

		if (otherNemesisNearby(entity, world)) {
			return null;
		}

		if (world.rand.nextInt(SPAWN_CHANCE) != 0) {
			return null;
		}

		List<Nemesis> nemeses = NemesisRegistryProvider.get(event.getEntity().world).list();

		nemeses.removeIf(Nemesis::isSpawned);
		nemeses.removeIf(Nemesis::isDead);
		nemeses.removeIf((Nemesis n) -> notReadyToSpawn(world, n));
		nemeses.removeIf((Nemesis n) -> outOfRange(entity, n));

		// TODO increase Nemesis level every time they spawn but are not killed

		if (nemeses.size() < 1) {
			return null;
		}

		return nemeses.get(event.getEntity().world.rand.nextInt(nemeses.size()));
	}

	private static boolean outOfRange(Entity entity, Nemesis nemesis) {
		// TODO change to a square radius, instead of a round one
		return entity.getDistanceSq(nemesis.getX(), entity.posY, nemesis.getZ()) > nemesis.getRangeSq();
	}

	private static boolean notReadyToSpawn(World world, Nemesis n) {
		return n.getLastSpawned() == null || world.getTotalWorldTime() - n.getLastSpawned() > 16000;
	}

	private static boolean otherNemesisNearby(EntityLiving entity, World world) {
		int distance = 100;
		List<EntityLiving> entities = world
				.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(entity.getPosition()).grow(distance, distance, distance));
		for (EntityLiving e : entities) {
			if (e.getTags().contains(NemesisSystem.TAG_NEMESIS)) {
				return true;
			}
		}
		return false;
	}

	private static boolean playerInRange(EntityLiving entity, World world) {
		int distance = 60;
		List<EntityPlayer> players = world
				.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(entity.getPosition()).grow(distance, distance, distance));
		for (EntityPlayer player : players) {
			if (entity.getEntitySenses().canSee(player)) {
				return true;
			}
		}
		return false;
	}

}
