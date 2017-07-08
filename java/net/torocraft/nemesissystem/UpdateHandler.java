package net.torocraft.nemesissystem;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.nemesissystem.Nemesis.Trait;

@EventBusSubscriber
public class UpdateHandler {

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new UpdateHandler());
	}

	@SubscribeEvent
	public void cleanUp(ChunkEvent.Load event) {
		findNemesesInChunk(event.getWorld(), event.getChunk()).forEach(UpdateHandler::unLoadNemesis);
	}

	@SubscribeEvent
	public void cleanUp(ChunkEvent.Unload event) {
		findNemesesInChunk(event.getWorld(), event.getChunk()).forEach(UpdateHandler::unLoadNemesis);
	}

	private static void unLoadNemesis(EntityCreature entity) {
		Nemesis nemesis = loadNemesisFromEntity(entity);
		if (nemesis == null) {
			return;
		}
		entity.setDead();
		NemesisRegistryProvider.get(entity.world).unload(nemesis.getId());
		findNemesisBodyGuards(entity.world, nemesis.getId(), entity.getPosition()).forEach((EntityCreature e) -> e.setDead());
	}

	private static List<EntityCreature> findNemesesInChunk(World world, Chunk chunk) {
		int x = chunk.x * 16;
		int z = chunk.z * 16;
		AxisAlignedBB chunkBox = new AxisAlignedBB(x, 0, z, x + 16, world.getActualHeight(), z + 16);
		return world.getEntitiesWithinAABB(EntityCreature.class, chunkBox, (EntityLiving searchEntity) -> isNemesis(searchEntity));
	}

	@SubscribeEvent
	public void livingUpdate(LivingUpdateEvent event) {
		World world = event.getEntity().getEntityWorld();

		if (world.isRemote) {
			return;
		}

		if (world.getTotalWorldTime() % 20 != 0) {
			return;
		}

		if (!(event.getEntity() instanceof EntityLiving)) {
			return;
		}

		if (event.getEntity().getTags().contains(SpawnHandler.TAG_BODY_GUARD)) {
			handleBodyGuardUpdate(event);
		} else if (event.getEntity().getTags().contains(EntityDecorator.TAG)) {
			handleNemesisUpdate(event);
		}
	}

	@SubscribeEvent
	public void stopBodyGuardsFromAttackingNemeses(LivingSetAttackTargetEvent event) {
		World world = event.getEntity().getEntityWorld();

		if (world.isRemote) {
			return;
		}

		if (event.getTarget() == null || !(event.getEntity() instanceof EntityCreature)) {
			return;
		}

		if (!event.getEntity().getTags().contains(SpawnHandler.TAG_BODY_GUARD)) {
			return;
		}

		if (event.getTarget().getTags().contains(EntityDecorator.TAG)) {
			((EntityCreature) event.getEntityLiving()).setAttackTarget(null);
		}
	}

	@SubscribeEvent
	public void onAttacked(LivingAttackEvent event) {

		World world = event.getEntity().getEntityWorld();

		if (world.isRemote || !(event.getEntity() instanceof EntityCreature)) {
			return;
		}

		if (event.getEntity().getTags().contains(EntityDecorator.TAG)) {
			orderGuardsToAttackAggressor((EntityCreature) event.getEntity(), event.getSource().getTrueSource());
		}
	}

	@SubscribeEvent
	public void onDeath(LivingDeathEvent event) {

		System.out.println("death event");

		World world = event.getEntity().getEntityWorld();

		if (world.isRemote || !(event.getEntity() instanceof EntityCreature)) {
			return;
		}

		if (event.getEntity().getTags().contains(EntityDecorator.TAG)) {
			handleNemesisDeath((EntityCreature) event.getEntity(), event.getSource().getTrueSource());
		}
	}

	@SubscribeEvent
	public void onDrops(LivingDropsEvent event) {

		System.out.println("drops event");

		World world = event.getEntity().getEntityWorld();

		if (world.isRemote || !(event.getEntity() instanceof EntityCreature)) {
			return;
		}

		if (event.getEntity().getTags().contains(EntityDecorator.TAG)) {
			handleNemesisDrops(event.getDrops(), (EntityCreature) event.getEntity());
		}
	}

	private void handleNemesisDrops(List<EntityItem> drops, EntityCreature nemesisEntity) {
		Nemesis nemesis = loadNemesisFromEntity(nemesisEntity);
		Random rand = nemesisEntity.getRNG();

		if(nemesis == null){
			return;
		}

		drops.add(drop(nemesisEntity, new ItemStack(Items.DIAMOND, rand.nextInt(nemesis.getLevel()))));

		ItemStack specialDrop = nemesisEntity.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
		specialDrop.setStackDisplayName("Property of " + nemesisEntity.getName());
		drops.add(drop(nemesisEntity, specialDrop));

		for(Trait trait : nemesis.getTraits()){
			switch (trait) {
			case DOUBLE_MELEE:

				break;
			case ARROW:
				drops.add(drop(nemesisEntity, new ItemStack(Items.ARROW, rand.nextInt(64))));
				break;
			case SUMMON:
				break;
			case REFLECT:
				break;
			case HEAT:
				drops.add(drop(nemesisEntity, new ItemStack(Blocks.TORCH, rand.nextInt(64))));
				if (rand.nextInt(5) == 0) {
					drops.add(drop(nemesisEntity, new ItemStack(Items.LAVA_BUCKET)));
				}
				break;
			case POTION:
				// TODO drop random potions
				break;
			case SHIELD:
				break;
			case TELEPORT:
				drops.add(drop(nemesisEntity, new ItemStack(Items.ENDER_PEARL, rand.nextInt(16))));
				break;
			}
		}

	}

	private EntityItem drop(EntityCreature entity, ItemStack stack) {
		return new EntityItem(entity.getEntityWorld(), entity.posX, entity.posY, entity.posZ, stack);
	}

	private void handleNemesisDeath(EntityCreature nemesisEntity, Entity attacker) {
		Nemesis nemesis = loadNemesisFromEntity(nemesisEntity);

		if (nemesis == null) {
			return;
		}

		NemesisRegistryProvider.get(nemesisEntity.world).setDead(nemesis.getId());

		findNemesisBodyGuards(nemesisEntity.world, nemesis.getId(), nemesisEntity.getPosition()).forEach((EntityCreature guard) -> {
			guard.setAttackTarget(null);
		});

		// TODO post message

		// TODO handle nemesis death (drop loot) clear guards attack target

		// TODO log death
	}

	private void orderGuardsToAttackAggressor(EntityCreature boss, Entity attacker) {
		if (attacker == null || !(attacker instanceof EntityLivingBase)) {
			return;
		}
		UUID id = boss.getEntityData().getUniqueId(EntityDecorator.NBT_ID);
		findNemesisBodyGuards(boss.world, id, boss.getPosition()).forEach((EntityCreature guard) -> {
			if (boss.getRNG().nextInt(7) == 0) {
				guard.setAttackTarget((EntityLivingBase) attacker);
			}
		});
	}

	// TODO handle player death

	private void handleBodyGuardUpdate(LivingUpdateEvent event) {
		if (!(event.getEntity() instanceof EntityCreature)) {
			return;
		}

		EntityCreature bodyGuard = (EntityCreature) event.getEntity();
		UUID id = bodyGuard.getEntityData().getUniqueId(EntityDecorator.NBT_ID);
		EntityLiving nemesisEntity = findNemesisAround(event.getEntity().world, id, event.getEntity().getPosition());

		if (nemesisEntity == null) {
			flee(bodyGuard);
			return;
		}

		followNemesisBoss(bodyGuard, nemesisEntity);
	}

	private void flee(EntityCreature bodyGuard) {
		bodyGuard.removeTag(SpawnHandler.TAG_BODY_GUARD);
		SpawnHandler.setFollowSpeed(bodyGuard, 2);
		int distance = 1000;
		int degrees = bodyGuard.getRNG().nextInt(360);
		int x = distance * (int) Math.round(Math.cos(Math.toRadians(degrees)));
		int z = distance * (int) Math.round(Math.sin(Math.toRadians(degrees)));
		BlockPos from = bodyGuard.getPosition();
		BlockPos to = new BlockPos(from.getX() + x, from.getY(), from.getZ() + z);
		bodyGuard.setHomePosAndDistance(to, 50);
	}

	private void followNemesisBoss(EntityCreature bodyGuard, EntityLiving nemesisEntity) {
		bodyGuard.setHomePosAndDistance(nemesisEntity.getPosition(), 2);
	}

	private EntityLiving findNemesisAround(World world, UUID id, BlockPos position) {
		int distance = 50;

		List<EntityLiving> entities = world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(position).grow(distance, distance, distance),
				(EntityLiving searchEntity) -> isNemesis(searchEntity, id)
		);

		if (entities.size() < 1) {
			return null;
		}

		return entities.get(0);
	}

	private static List<EntityCreature> findNemesisBodyGuards(World world, UUID id, BlockPos position) {
		int distance = 100;

		return world.getEntitiesWithinAABB(EntityCreature.class, new AxisAlignedBB(position).grow(distance, distance, distance),
				(EntityCreature searchEntity) -> isBodyGuard(searchEntity, id)
		);
	}

	public static boolean isNemesis(EntityLiving searchEntity, UUID id) {
		return isNemesis(searchEntity) && id.equals(searchEntity.getEntityData().getUniqueId(EntityDecorator.NBT_ID));
	}

	public static boolean isNemesis(EntityLiving searchEntity) {
		return searchEntity.getTags().contains(EntityDecorator.TAG);
	}

	public static boolean isBodyGuard(EntityLiving searchEntity, UUID id) {
		return searchEntity.getTags().contains(SpawnHandler.TAG_BODY_GUARD)
				&& id.equals(searchEntity.getEntityData().getUniqueId(EntityDecorator.NBT_ID));
	}

	private void handleNemesisUpdate(LivingUpdateEvent event) {
		Nemesis nemesis = loadNemesisFromEntity(event.getEntity());

		if (nemesis == null) {
			return;
		}

		EntityLiving entity = (EntityLiving) event.getEntity();

		// TODO look for closer target

		for (Trait trait : nemesis.getTraits()) {
			handleTraitUpdate(entity, nemesis, trait);
		}
	}

	private static Nemesis loadNemesisFromEntity(Entity nemesisEntity) {
		UUID id = nemesisEntity.getEntityData().getUniqueId(EntityDecorator.NBT_ID);
		return NemesisRegistryProvider.get(nemesisEntity.getEntityWorld()).getById(id);
	}

	private void handleTraitUpdate(EntityLiving entity, Nemesis nemesis, Trait trait) {
		switch (trait) {
		case DOUBLE_MELEE:
			return;
		case ARROW:
			handleArrowTraitUpdate(entity, nemesis, trait);
			return;
		case SUMMON:
			handleSummonTraitUpdate(entity, nemesis, trait);
			return;
		case REFLECT:
			return;
		case HEAT:
			handleHeatTraitUpdate(entity, nemesis, trait);
			return;
		case POTION:
			handlePotionTraitUpdate(entity, nemesis, trait);
			return;
		case TELEPORT:
			handleTeleportTraitUpdate(entity, nemesis, trait);
		}
	}

	private void handleTeleportTraitUpdate(EntityLiving entity, Nemesis nemesis, Trait trait) {
		World world = entity.world;
		Random rand = entity.getRNG();

		//TODO teleport away when hurt (back to body guard?)

		if (world.getTotalWorldTime() % 40 != 0) {
			return;
		}

		EntityLivingBase target = entity.getAttackTarget();

		if (target == null) {
			return;
		}

		if (!entity.getEntitySenses().canSee(target)) {
			return;
		}

		int charge = 2 + rand.nextInt(5);

		EntityEnderPearl pearl = new EntityEnderPearl(world, entity);

		double dX = target.posX - entity.posX;
		double dY = target.getEntityBoundingBox().minY + (double) (target.height / 3.0F) - pearl.posY;
		double dZ = target.posZ - entity.posZ;

		double distanceSq = dX * dX + dY * dY + dZ * dZ;

		if (distanceSq < 20) {
			return;
		}

		double levelDistance = MathHelper.sqrt(dX * dX + dZ * dZ);

		pearl.setThrowableHeading(dX, dY + levelDistance * 0.20000000298023224D, dZ, 1.6F,
				(float) (14 - world.getDifficulty().getDifficultyId() * 4));

		int power = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.POWER, entity);
		int punch = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.PUNCH, entity);

		entity.playSound(SoundEvents.ENTITY_ENDERPEARL_THROW, 1.0F, 1.0F / (rand.nextFloat() * 0.4F + 0.8F));

		world.spawnEntity(pearl);
	}

	private void handleSummonTraitUpdate(EntityLiving entity, Nemesis nemesis, Trait trait) {
		World world = entity.world;
		Random rand = entity.getRNG();

		EntityLivingBase target = entity.getAttackTarget();

		if (target == null) {
			return;
		}

		if (!entity.getEntitySenses().canSee(target)) {
			return;
		}

		if (rand.nextInt(5) != 0) {
			return;
		}

		// TODO check total number of near by mobs before spawning new ones

		int roll = rand.nextInt(100);

		EntityMob mob;

		if (roll < 45) {
			mob = new EntitySkeleton(world);
		} else if (roll < 90) {
			mob = new EntityZombie(world);
		} else {
			mob = new EntityWitch(world);
		}

		mob.setPosition(entity.posX, entity.posY, entity.posZ);
		world.spawnEntity(mob);
	}

	private void handleHeatTraitUpdate(EntityLiving entity, Nemesis nemesis, Trait trait) {
		World world = entity.world;
		Random rand = entity.getRNG();
		int heatDistance = 8;

		List<EntityPlayer> playersToCook = world
				.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(entity.getPosition()).grow(heatDistance, heatDistance, heatDistance));
		for (EntityPlayer player : playersToCook) {
			if (entity.getEntitySenses().canSee(player)) {
				player.setFire(10);
			}
		}
	}

	private void handlePotionTraitUpdate(EntityLiving entity, Nemesis nemesis, Trait trait) {
		World world = entity.world;
		Random rand = entity.getRNG();

		EntityLivingBase target = entity.getAttackTarget();

		if (target == null) {
			return;
		}

		if (!entity.getEntitySenses().canSee(target)) {
			return;
		}

		double targetY = target.posY + (double) target.getEyeHeight() - 1.100000023841858D;
		double targetX = target.posX + target.motionX - entity.posX;
		double d2 = targetY - entity.posY;
		double targetZ = target.posZ + target.motionZ - entity.posZ;

		float f = MathHelper.sqrt(targetX * targetX + targetZ * targetZ);
		PotionType potiontype = PotionTypes.HARMING;

		if (f >= 8.0F && !target.isPotionActive(MobEffects.SLOWNESS)) {
			potiontype = PotionTypes.SLOWNESS;
		} else if (target.getHealth() >= 8.0F && !target.isPotionActive(MobEffects.POISON)) {
			potiontype = PotionTypes.POISON;
		} else if (f <= 3.0F && !target.isPotionActive(MobEffects.WEAKNESS) && rand.nextFloat() < 0.25F) {
			potiontype = PotionTypes.WEAKNESS;
		}

		EntityPotion entitypotion = new EntityPotion(world, entity,
				PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), potiontype));
		entitypotion.rotationPitch -= -20.0F;
		entitypotion.setThrowableHeading(targetX, d2 + (double) (f * 0.2F), targetZ, 0.75F, 8.0F);

		world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_WITCH_THROW, entity.getSoundCategory(), 1.0F,
				0.8F + rand.nextFloat() * 0.4F);
		world.spawnEntity(entitypotion);
	}

	private void handleArrowTraitUpdate(EntityLiving entity, Nemesis nemesis, Trait trait) {

		World world = entity.world;
		Random rand = entity.getRNG();

		EntityLivingBase target = entity.getAttackTarget();

		if (target == null) {
			return;
		}

		if (!entity.getEntitySenses().canSee(target)) {
			return;
		}

		int charge = 2 + rand.nextInt(10);

		EntityArrow arrow = new EntityTippedArrow(world, entity);

		double dX = target.posX - entity.posX;
		double dY = target.getEntityBoundingBox().minY + (double) (target.height / 3.0F) - arrow.posY;
		double dZ = target.posZ - entity.posZ;

		double levelDistance = (double) MathHelper.sqrt(dX * dX + dZ * dZ);

		arrow.setThrowableHeading(dX, dY + levelDistance * 0.20000000298023224D, dZ, 1.6F,
				(float) (14 - world.getDifficulty().getDifficultyId() * 4));

		int power = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.POWER, entity);
		int punch = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.PUNCH, entity);

		arrow.setDamage((double) (charge * 2.0F) + rand.nextGaussian() * 0.25D
				+ (double) ((float) world.getDifficulty().getDifficultyId() * 0.11F));

		if (power > 0) {
			arrow.setDamage(arrow.getDamage() + (double) power * 0.5D + 0.5D);
		}

		if (punch > 0) {
			arrow.setKnockbackStrength(punch);
		}

		// TODO bow enchants

		entity.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (rand.nextFloat() * 0.4F + 0.8F));

		world.spawnEntity(arrow);
	}
}
