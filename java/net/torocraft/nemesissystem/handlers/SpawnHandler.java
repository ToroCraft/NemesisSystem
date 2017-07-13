package net.torocraft.nemesissystem.handlers;

import java.util.List;
import java.util.Random;
import java.util.UUID;
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
import net.torocraft.nemesissystem.util.EntityDecorator;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.registry.NemesisRegistryProvider;
import net.torocraft.nemesissystem.util.NemesisUtil;
import net.torocraft.nemesissystem.util.SpawnUtil;

public class SpawnHandler {

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new SpawnHandler());
	}

	@SubscribeEvent
	public void handleSpawn(EntityJoinWorldEvent event) {
		if (event.getEntity().world.isRemote || !(event.getEntity() instanceof EntityCreature) || !NemesisUtil.isNemesisClassEntity(event.getEntity())) {
			return;
		}

		NemesisUtil.handleRandomPromotions(event.getWorld(), (EntityCreature) event.getEntity());

		if (event.getEntity().getTags().contains(NemesisSystem.TAG_NEMESIS)) {
			return;
		}

		if (event.getEntity().getTags().contains(NemesisSystem.TAG_BODY_GUARD)) {
			return;
		}

		Nemesis nemesis = getNemesisForSpawn(event);

		if (nemesis == null) {
			return;
		}

		World world = event.getWorld();
		EntityCreature nemesisEntity = (EntityCreature) event.getEntity();
		// TODO check age
		EntityDecorator.decorate(nemesisEntity, nemesis);
		NemesisRegistryProvider.get(world).load(nemesisEntity, nemesis.getId());
		spawnBodyGuard(nemesisEntity, nemesis);
		nemesisAnnounceEffects(nemesisEntity);
	}

	private void nemesisAnnounceEffects(EntityCreature nemesisEntity) {
		World world = nemesisEntity.world;

		if (canSeeSky(nemesisEntity)) {
			world.addWeatherEffect(new EntityLightningBolt(nemesisEntity.world, nemesisEntity.posX, nemesisEntity.posY, nemesisEntity.posZ, true));
		}

		// TODO sound horn (this would force a clients to have the mod)

	}

	private static boolean canSeeSky(Entity e) {
		return e.world.canSeeSky(new BlockPos(e.posX, e.posY + (double) e.getEyeHeight(), e.posZ));
	}

	private void spawnBodyGuard(EntityLiving entity, Nemesis nemesis) {

		// TODO high level nemeses spawn other nemesis in their body guard

		// TODO add body guard ranks? (different armor, ai, weapons)

		int count = 5 + nemesis.getLevel() * 5;

		for (int i = 0; i < count; i++) {
			EntityCreature bodyGuard = new EntityZombie(entity.getEntityWorld());
			bodyGuard.addTag(NemesisSystem.TAG_BODY_GUARD);
			bodyGuard.getEntityData().setUniqueId(NemesisSystem.NBT_NEMESIS_ID, nemesis.getId());
			equipBodyGuard(bodyGuard);
			SpawnUtil.spawnEntityLiving(entity.getEntityWorld(), bodyGuard, entity.getPosition(), 10);
			NemesisUtil.setFollowSpeed(bodyGuard, 1.5);
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

	private Nemesis getNemesisForSpawn(EntityEvent event) {

		if (!(event.getEntity() instanceof EntityLiving)) {
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

		List<Nemesis> nemeses = NemesisRegistryProvider.get(event.getEntity().world).list();

		nemeses.removeIf(Nemesis::isLoaded);
		nemeses.removeIf(Nemesis::isDead);

		// TODO only spawn once a day?

		// TODO add a spawn chance, unless nemesis has not been spawned in a long time

		// TODO figure out how to handle nemeses that cannot spawn in their location (Husk not in the desert)

		// TODO increase Nemesis level every time they spawn but are not killed
		
		if (nemeses == null || nemeses.size() < 1) {
			return null;
		}

		String entityType = NemesisUtil.getEntityType(event.getEntity());

		nemeses.removeIf(nemesis -> {

			if (!nemesis.getMob().equals(entityType)) {
				return true;
			}

			if (entity.getDistanceSq(nemesis.getX(), entity.posY, nemesis.getZ()) > nemesis.getRangeSq()) {
				return true;
			}

			return false;
		});

		if (nemeses.size() < 1) {
			return null;
		}

		return nemeses.get(event.getEntity().world.rand.nextInt(nemeses.size()));
	}

	private boolean otherNemesisNearby(EntityLiving entity, World world) {
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

	private boolean playerInRange(EntityLiving entity, World world) {
		int distance = 100;
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
