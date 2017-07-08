package net.torocraft.nemesissystem;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.monster.EntityMob;
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
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class SpawnHandler {
	public static final UUID EMPTY_UUID = new UUID(0, 0);
	public static final int NEMESIS_COUNT = 4;

	public static final String TAG_BODY_GUARD = "nemesis_body_guard";

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new SpawnHandler());
	}

	@SubscribeEvent
	public void handleSpawn(EntityJoinWorldEvent event) {
		if (event.getEntity().world.isRemote || !(event.getEntity() instanceof EntityCreature) || !isNemesisClassEntity(event.getEntity())) {
			return;
		}

		handleRandomPromotions(event.getWorld(), (EntityCreature) event.getEntity());

		if (event.getEntity().getTags().contains(EntityDecorator.TAG)) {
			return;
		}

		if (event.getEntity().getTags().contains(TAG_BODY_GUARD)) {
			return;
		}

		Nemesis nemesis = getNemesisForSpawn(event);

		if (nemesis == null) {
			return;
		}

		// TODO sound horn

		// TODO chat to near by players

		EntityDecorator.decorate((EntityLiving) event.getEntity(), nemesis);

		spawnBodyGuard((EntityLiving) event.getEntity(), nemesis);

		System.out.println("Spawning: " + event.getEntity().getName() + " at " + event.getEntity().getPosition());
	}



	private void handleRandomPromotions(World world, EntityCreature entity) {
		NemesisRegistry registry = NemesisRegistryProvider.get(world);

		List<Nemesis> nemeses = registry.list();
		nemeses.removeIf(Nemesis::isDead);

		if (nemeses.size() >= NEMESIS_COUNT / 2) {
			return;
		}

		promoteRandomNemesis(entity, registry, nemeses);
		createAndRegisterNemesis(entity, getRandomLocationAround(entity));
	}

	public static Nemesis createAndRegisterNemesis(EntityCreature entity, BlockPos nemesisLocation) {
		Nemesis nemesis = NemesisBuilder.build(getEntityType(entity), 1, nemesisLocation.getX(), nemesisLocation.getZ());
		NemesisRegistryProvider.get(entity.world).register(nemesis);
		return nemesis;
	}

	private static BlockPos getRandomLocationAround(EntityCreature entity) {
		int distance = 1000 + entity.getRNG().nextInt(4000);
		int degrees = entity.getRNG().nextInt(360);
		int x = distance * (int) Math.round(Math.cos(Math.toRadians(degrees)));
		int z = distance * (int) Math.round(Math.sin(Math.toRadians(degrees)));
		BlockPos here = entity.getPosition();
		return new BlockPos(here.getX() + x, here.getY(), here.getZ() + z);
	}

	private void promoteRandomNemesis(EntityCreature entity, NemesisRegistry registry, List<Nemesis> nemeses) {
		if (nemeses == null || nemeses.size() < 1) {
			return;
		}
		registry.promote(nemeses.get(entity.getRNG().nextInt(nemeses.size())).getId());
	}

	private void spawnBodyGuard(EntityLiving entity, Nemesis nemesis) {

		int count = 5 + nemesis.getLevel() * 5;

		for (int i = 0; i < count; i++) {
			// TODO spawn different body guards based on nemesis mob type
			EntityZombie bodyGuard = new EntityZombie(entity.getEntityWorld());
			bodyGuard.addTag(TAG_BODY_GUARD);
			bodyGuard.getEntityData().setUniqueId(EntityDecorator.NBT_ID, nemesis.getId());
			equipBodyGuard(bodyGuard);
			SpawnUtil.spawnEntityLiving(entity.getEntityWorld(), bodyGuard, entity.getPosition(), 10);
			setFollowSpeed(bodyGuard, 1.5);
		}

		// TODO nemesis can heal body guards?

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

	public static void setFollowSpeed(EntityCreature bodyGuard, double followSpeed) {
		EntityAIMoveTowardsRestriction ai = null;
		for (EntityAITaskEntry entry : bodyGuard.tasks.taskEntries) {
			if (entry.action instanceof EntityAIMoveTowardsRestriction) {
				ai = (EntityAIMoveTowardsRestriction) entry.action;
			}
		}
		if (ai == null) {
			System.out.println("guard ai not found");
			return;
		}
		//not sure field_75433_e is the correct name for EntityAIMoveTowardsRestriction.movementSpeed
		ObfuscationReflectionHelper.setPrivateValue(EntityAIMoveTowardsRestriction.class, ai, followSpeed, "field_75433_e", "movementSpeed");
	}

	public static boolean isNemesisClassEntity(Entity entity) {
		// TODO blacklist

		// TODO whitelist

		return entity instanceof EntityMob;
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

		if (nemeses == null || nemeses.size() < 1) {
			return null;
		}

		String entityType = getEntityType(event.getEntity());

		nemeses.removeIf(nemesis -> {

			if(!nemesis.getMob().equals(entityType)){
				return true;
			}

			if(entity.getDistanceSq(nemesis.getX(), entity.posY, nemesis.getZ()) > nemesis.getRangeSq()){
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
			if (e.getTags().contains(EntityDecorator.TAG)) {
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

	public static String getEntityType(Entity entityIn) {
		EntityEntry entry = EntityRegistry.getEntry(entityIn.getClass());
		if (entry == null) {
			return "";
		}
		return entry.getRegistryName().toString();
	}
}
