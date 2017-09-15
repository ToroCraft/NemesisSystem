package net.torocraft.nemesissystem.handlers;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.entities.INemesisEntity;
import net.torocraft.nemesissystem.entities.husk.EntityHuskNemesis;
import net.torocraft.nemesissystem.entities.pigZombie.EntityPigZombieNemesis;
import net.torocraft.nemesissystem.entities.skeleton.EntitySkeletonNemesis;
import net.torocraft.nemesissystem.entities.stray.EntityStrayNemesis;
import net.torocraft.nemesissystem.entities.zombie.EntityZombieNemesis;
import net.torocraft.nemesissystem.entities.zombieVillager.EntityZombieVillagerNemesis;
import net.torocraft.nemesissystem.events.SpawnEvent;
import net.torocraft.nemesissystem.network.MessageSyncNemesis;
import net.torocraft.nemesissystem.network.MessageSyncNemesisRequest;
import net.torocraft.nemesissystem.registry.NemesisEntry;
import net.torocraft.nemesissystem.registry.NemesisRegistryProvider;
import net.torocraft.nemesissystem.util.EntityDecorator;
import net.torocraft.nemesissystem.util.NemesisActions;
import net.torocraft.nemesissystem.util.NemesisUtil;
import net.torocraft.torotraits.api.BehaviorApi;
import net.torocraft.torotraits.api.SpawnApi;

public class SpawnHandler {

	private static final int SPAWN_CHANCE = 2;
	private static final int MAX_SPAWN_DISTANCE = 100;
	public static final int SPAWN_COOLDOWN_PERIOD = 16000;

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new SpawnHandler());
	}

	@SubscribeEvent
	public void handleSpawn(EntityJoinWorldEvent event) {

		if (event.getWorld().isRemote) {
			requestNemesisDataFromServer(event);
			return;
		}

		if (!(event.getEntity() instanceof EntityCreature) || !NemesisUtil.isNemesisClassEntity(event.getEntity())) {
			return;
		}

		EntityCreature entity = (EntityCreature) event.getEntity();

		NemesisActions.handleRandomPromotions(event.getWorld(), entity);

		if (entity.getTags().contains(NemesisSystem.TAG_NEMESIS)) {
			handleRespawnOfNemesis(event);
			return;
		}

		if (entity.getTags().contains(NemesisSystem.TAG_BODY_GUARD)) {
			BehaviorApi.setFollowSpeed(entity, 2);
			return;
		}

		NemesisEntry nemesis = getNemesisForSpawn(event);

		if (nemesis != null) {
			replaceEntityWithNemesis(entity, nemesis);
			MinecraftForge.EVENT_BUS.post(new SpawnEvent(nemesis, entity));
			return;
		}

		buffMobInAroundNemeses(event.getWorld(), entity);
	}

	private void buffMobInAroundNemeses(World world, EntityCreature entity) {
		if (!(entity instanceof EntityMob)) {
			return;
		}

		List<NemesisEntry> nemeses = NemesisRegistryProvider.get(world).list();
		sortByHighestLevel(nemeses);

		for (NemesisEntry nemesis : nemeses) {
			if (buffEntity(entity, nemesis)) {
				return;
			}
		}
	}

	private boolean buffEntity(EntityCreature entity, NemesisEntry nemesis) {
		int buffAmount = determineBuffAmount(nemesis, entity.getPosition());

		if (buffAmount < 1) {
			return false;
		}

		updateSharedMonsterAttributes(entity, nemesis, buffAmount);

		// TODO add weapons/armor

		// TODO tag for tracking (increase book drop chance)

		return true;
	}

	private void updateSharedMonsterAttributes(EntityCreature entity, NemesisEntry nemesis, int buffAmount) {
		for (IAttributeInstance attribute : entity.getAttributeMap().getAllAttributes()) {
			if (attribute.getAttribute() == SharedMonsterAttributes.ATTACK_DAMAGE) {
				attribute.setBaseValue(determineAttackDamage(attribute.getAttributeValue(), buffAmount));
			}

			if (attribute.getAttribute() == SharedMonsterAttributes.MAX_HEALTH) {
				attribute.setBaseValue(determineMaxHealth(attribute.getAttributeValue(), buffAmount));
				entity.setHealth(entity.getMaxHealth());
			}
		}
	}

	protected static double determineAttackDamage(double initial, int buffAmount) {
		return initial * (1 + (double)buffAmount/6);
	}

	protected static double determineMaxHealth(double initial, int buffAmount) {
		return initial * (1 + (double)buffAmount/4);
	}

	protected static void sortByHighestLevel(List<NemesisEntry> nemeses) {
		nemeses.sort((a, b) -> b.getLevel() - a.getLevel());
	}

	protected static int determineBuffAmount(NemesisEntry nemesis, BlockPos pos) {
		if (nemesis.isDead()) {
			return 0;
		}

		double distance = getDistance(pos, nemesis.getX(), nemesis.getZ()) + 0.0001;
		double range = nemesis.getRange() * (1 + (nemesis.getLevel() / 2));

		if (distance > range) {
			return 0;
		}
		return (int) (range - distance + 1) / nemesis.getRange();
	}

	protected static double getDistance(BlockPos pos, int x, int z) {
		double d0 = pos.getX() - x;
		double d2 = pos.getZ() - z;
		return (double) MathHelper.sqrt(d0 * d0 + d2 * d2);
	}

	private void requestNemesisDataFromServer(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof EntityCreature) {
			if (!event.getEntity().getTags().contains(NemesisSystem.TAG_NEMESIS)) {
				NemesisSystem.NETWORK.sendToServer(new MessageSyncNemesisRequest(event.getEntity().getPersistentID()));
			}
		}
	}

	private void replaceEntityWithNemesis(EntityCreature entity, NemesisEntry nemesis) {
		entity.setDead();
		spawnNemesis(entity.world, entity.getPosition(), nemesis);
	}

	private void handleRespawnOfNemesis(EntityJoinWorldEvent event) {
		Entity entity = event.getEntity();
		NemesisEntry nemesis = NemesisUtil.loadNemesisFromEntity(event.getEntity());
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
			nemesis.setLastSpawned(entity.world.getTotalWorldTime());
			NemesisRegistryProvider.get(event.getWorld()).update(nemesis);
			sendNemesisDataToClient(nemesis);
		}
	}

	public static void spawnNemesis(World world, BlockPos pos, NemesisEntry nemesis) {
		if (nemesis.isDead()) {
			return;
		}
		if (nemesis.isSpawned()) {
			return;
		}

		String mobType = overrideMobType(nemesis.getMob());

		System.out.println("TYPE: " + mobType);

		EntityCreature nemesisEntity = SpawnApi.getEntityFromString(world, mobType);

		if (nemesisEntity == null) {
			return;
		}

		nemesisEntity.setAttackTarget(nemesis.getTargetPlayer());
		nemesisEntity.addTag(NemesisSystem.TAG_SPAWNING);
		if (nemesisEntity instanceof INemesisEntity) {
			((INemesisEntity) nemesisEntity).setNemesis(nemesis);
		}

		EntityDecorator.decorate(nemesisEntity, nemesis);
		SpawnApi.spawnEntityCreature(world, nemesisEntity, pos, 1);

		spawnBodyGuard(nemesisEntity, nemesis);
		nemesisAnnounceEffects(nemesisEntity);

		nemesis.setSpawned(nemesisEntity.getEntityId());
		nemesis.setLastSpawned(world.getTotalWorldTime());
		nemesis.setEntityUuid(nemesisEntity.getPersistentID());
		nemesis.setUnloaded(null);
		NemesisRegistryProvider.get(world).update(nemesis);

		sendNemesisDataToClient(nemesis);
	}

	private static String overrideMobType(String mob) {
		switch (mob) {
		case "minecraft:zombie":
			return NemesisSystem.MODID + ":" + EntityZombieNemesis.NAME;
		case "minecraft:zombie_pigman":
			return NemesisSystem.MODID + ":" + EntityPigZombieNemesis.NAME;
		case "minecraft:zombie_villager":
			return NemesisSystem.MODID + ":" + EntityZombieVillagerNemesis.NAME;
		case "minecraft:husk":
			return NemesisSystem.MODID + ":" + EntityHuskNemesis.NAME;
		case "minecraft:skeleton":
			return NemesisSystem.MODID + ":" + EntitySkeletonNemesis.NAME;
		case "minecraft:stray":
			return NemesisSystem.MODID + ":" + EntityStrayNemesis.NAME;
		}
		return mob;
	}

	private static void sendNemesisDataToClient(NemesisEntry nemesis) {
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

	private static void spawnBodyGuard(EntityLiving entity, NemesisEntry nemesis) {

		// TODO use nemesis colors

		int count = 3 + nemesis.getLevel() * 3;

		for (int i = 0; i < count; i++) {
			EntityCreature bodyGuard = new EntityZombie(entity.getEntityWorld());
			bodyGuard.addTag(NemesisSystem.TAG_BODY_GUARD);
			bodyGuard.getEntityData().setUniqueId(NemesisSystem.NBT_NEMESIS_ID, nemesis.getId());
			equipBodyGuard(bodyGuard);
			SpawnApi.spawnEntityCreature(entity.getEntityWorld(), bodyGuard, entity.getPosition(), 10);
			BehaviorApi.setFollowSpeed(bodyGuard, 1.5);
		}
	}

	private static void equipBodyGuard(EntityCreature bodyGuard) {
		int color = 0xffffff;
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

	private static NemesisEntry getNemesisForSpawn(EntityEvent event) {

		if (!NemesisUtil.isNemesisClassEntity(event.getEntity())) {
			return null;
		}

		EntityLiving entity = (EntityLiving) event.getEntity();
		World world = entity.world;
		EntityPlayer targetPlayer = findVisiblePlayer(entity);

		if (targetPlayer == null) {
			return null;
		}

		if (world.rand.nextInt(SPAWN_CHANCE) != 0) {
			return null;
		}

		List<NemesisEntry> nemeses = NemesisRegistryProvider.get(event.getEntity().world).list();
		nemeses.removeIf(NemesisEntry::isSpawned);
		nemeses.removeIf(NemesisEntry::isDead);
		nemeses.removeIf((NemesisEntry n) -> notReadyToSpawn(world, n));
		nemeses.removeIf((NemesisEntry n) -> !inRage(entity, n));

		if (nemeses.size() < 1) {
			return null;
		}

		NemesisEntry selectNemesis = nemeses.get(event.getEntity().world.rand.nextInt(nemeses.size()));
		selectNemesis.setTargetPlayer(targetPlayer);
		return selectNemesis;
	}

	private static boolean inRage(Entity entity, NemesisEntry nemesis) {
		int x = (int) entity.posX;
		int z = (int) entity.posZ;

		int nx = nemesis.getX();
		int nz = nemesis.getZ();

		int r = nemesis.getRange();

		return x < nx + r && x > nx - r && z < nz + r && z > nz - r;
	}

	private static boolean notReadyToSpawn(World world, NemesisEntry n) {
		if (n.getLastSpawned() == null) {
			return false;
		}
		long delay = (n.getLastSpawned() + SPAWN_COOLDOWN_PERIOD) - world.getTotalWorldTime();
		return delay > 0;
	}

	private static boolean otherNemesisNearby(EntityLiving entity, World world) {
		int distance = 100;
		List<EntityLiving> entities = world
				.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(entity.getPosition()).grow(distance, distance, distance));
		for (EntityLiving e : entities) {
			if (e.getTags().contains(NemesisSystem.TAG_NEMESIS) && !e.isDead) {
				return true;
			}
		}
		return false;
	}

	private static EntityPlayer findVisiblePlayer(EntityLiving entity) {
		int distance = MAX_SPAWN_DISTANCE;

		List<EntityPlayer> players = entity.world.getEntitiesWithinAABB(
				EntityPlayer.class,
				new AxisAlignedBB(entity.getPosition()).grow(distance, distance, distance));

		for (EntityPlayer player : players) {
			if (entity.getEntitySenses().canSee(player)) {
				return player;
			}
		}
		return null;
	}

}
