package net.torocraft.nemesissystem.util;

import java.util.List;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.network.MessageHealAnimation;
import net.torocraft.nemesissystem.network.MessageReflectDamageAnimation;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.registry.Nemesis.Trait;

public class TraitsUtil {

	private static final Random rand = new Random();

	public static void handleTraits(Nemesis nemesis, EntityLiving nemesisEntity) {
		// caching to an array to avoid: java.util.ArrayList$Itr.checkForComodification
		Trait[] traits = nemesis.getTraits().toArray(new Trait[0]);
		for (Trait trait : traits) {
			//TODO secondary traits should be used less frequently

			// TODO randomize attack timing
			handleTraitUpdate(nemesisEntity, nemesis, trait);
		}
	}

	private static void handleTraitUpdate(EntityLiving entity, Nemesis nemesis, Trait trait) {
		switch (trait) {
		case DOUBLE_MELEE:
			// TODO make the nemesis hit twice when attacking
			return;
		case FIREBALL:
			handleFireballTraitUpdate(entity, nemesis, trait);
			return;
		case ARROW:
			handleArrowTraitUpdate(entity, nemesis, trait);
			return;
		case SUMMON:
			handleSummonTraitUpdate(entity, nemesis, trait);
			return;
		case REFLECT:
			handleReflectTraitUpdate(entity, nemesis, trait);
			return;
		case HEAT:
			handleHeatTraitUpdate(entity, nemesis, trait);
			return;
		case POTION:
			handlePotionTraitUpdate(entity, nemesis, trait);
			return;
		case TELEPORT:
			handleTeleportTraitUpdate(entity, nemesis, trait);
			return;
		case HEAL:
			handleHealTraitUpdate(entity, nemesis, trait);
		}
	}

	private static void handleReflectTraitUpdate(EntityLiving entity, Nemesis nemesis, Trait trait) {

	}

	private static void handleHealTraitUpdate(EntityLiving entity, Nemesis nemesis, Trait trait) {
		World world = entity.world;
		Random rand = entity.getRNG();

		if (world.getTotalWorldTime() % 40 != 0) {
			return;
		}

		List<EntityCreature> guards = NemesisUtil.findNemesisBodyGuards(entity.world, nemesis.getId(), entity.getPosition());
		guards.forEach(TraitsUtil::possiblyHealCreature);
	}

	private static void possiblyHealCreature(EntityCreature entity) {
		if (entity.getRNG().nextInt(1) == 0) {
			healCreature(entity);
		}
	}

	private static void healCreature(EntityCreature entity) {
		if (canBeHealed(entity)) {
			float healTo = Math.min(entity.getHealth() + 1 + entity.getRNG().nextInt(5), entity.getMaxHealth());
			entity.setHealth(healTo);
			TargetPoint point = new TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, 100);
			NemesisSystem.NETWORK.sendToAllAround(new MessageHealAnimation(entity.getEntityId()), point);
			entity.playSound(SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 3.0F, 1.0F / (entity.getRNG().nextFloat() * 0.4F + 0.8F));
		}
	}

	private static boolean canBeHealed(EntityCreature entity) {
		return entity.getHealth() < entity.getMaxHealth() && entity.getHealth() > 0 && !entity.isDead;
	}

	private static void handleFireballTraitUpdate(EntityLiving entity, Nemesis nemesis, Trait trait) {
		World world = entity.world;
		Random rand = entity.getRNG();

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

		// TODO what is this?
		world.playEvent(null, 1015, new BlockPos(entity), 0);

		double d1 = 4.0D;
		Vec3d vec3d = entity.getLook(1.0F);
		double d2 = target.posX - (entity.posX + vec3d.x * 4.0D);
		double d3 = target.getEntityBoundingBox().minY + (double) (target.height / 2.0F) - (0.5D + entity.posY + (double) (entity.height / 2.0F));
		double d4 = target.posZ - (entity.posZ + vec3d.z * 4.0D);

		// TODO and this?
		world.playEvent(null, 1016, new BlockPos(entity), 0);

		EntityLargeFireball entitylargefireball = new EntityLargeFireball(world, entity, d2, d3, d4);
		entitylargefireball.explosionPower = 1;
		entitylargefireball.posX = entity.posX + vec3d.x * 4.0D;
		entitylargefireball.posY = entity.posY + (double) (entity.height / 2.0F) + 0.5D;
		entitylargefireball.posZ = entity.posZ + vec3d.z * 4.0D;
		world.spawnEntity(entitylargefireball);
	}

	private static void handleTeleportTraitUpdate(EntityLiving entity, Nemesis nemesis, Trait trait) {
		World world = entity.world;

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

		NemesisActions.throwPearl(entity, target);
	}

	private static void handleSummonTraitUpdate(EntityLiving entity, Nemesis nemesis, Trait trait) {
		World world = entity.world;
		Random rand = entity.getRNG();

		EntityLivingBase target = entity.getAttackTarget();

		if (target == null) {
			return;
		}

		if (!entity.getEntitySenses().canSee(target)) {
			return;
		}

		if (rand.nextInt(3) != 0) {
			return;
		}

		int summonedCount = entity.world.getEntitiesWithinAABB(EntityMob.class, around(entity.getPosition(), 40), TraitsUtil::isSummonedMob).size();

		if (summonedCount > (nemesis.getLevel() * 2)) {
			return;
		}

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
		mob.addTag(NemesisSystem.TAG_SUMMONED_MOB);
		world.spawnEntity(mob);
	}

	private static AxisAlignedBB around(BlockPos pos, int radius) {
		return new AxisAlignedBB(pos).grow(radius, radius, radius);
	}

	private static boolean isSummonedMob(Entity e) {
		return e.getTags().contains(NemesisSystem.TAG_SUMMONED_MOB);
	}

	private static void handleHeatTraitUpdate(EntityLiving entity, Nemesis nemesis, Trait trait) {
		World world = entity.world;
		int heatDistance = 8;

		List<EntityPlayer> playersToCook = world
				.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(entity.getPosition()).grow(heatDistance, heatDistance, heatDistance));
		for (EntityPlayer player : playersToCook) {
			if (entity.getEntitySenses().canSee(player)) {
				player.setFire(10);
			}
		}
	}

	private static void handlePotionTraitUpdate(EntityLiving entity, Nemesis nemesis, Trait trait) {
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

	private static void handleArrowTraitUpdate(EntityLiving entity, Nemesis nemesis, Trait trait) {
		EntityLivingBase target = entity.getAttackTarget();

		if (target == null) {
			return;
		}

		if (!entity.getEntitySenses().canSee(target)) {
			return;
		}

		attackWithArrow(entity, target);
	}

	private static void attackWithArrow(EntityLiving archer, EntityLivingBase target) {
		int charge = 2 + rand.nextInt(10);

		EntityArrow arrow = new EntityTippedArrow(archer.world, archer);

		double dX = target.posX - archer.posX;
		double dY = target.getEntityBoundingBox().minY + (double) (target.height / 3.0F) - arrow.posY;
		double dZ = target.posZ - archer.posZ;

		double levelDistance = (double) MathHelper.sqrt(dX * dX + dZ * dZ);

		arrow.setThrowableHeading(dX, dY + levelDistance * 0.20000000298023224D, dZ, 1.6F,
				(float) (14 - archer.world.getDifficulty().getDifficultyId() * 4));

		int power = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.POWER, archer);
		int punch = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.PUNCH, archer);

		arrow.setDamage((double) (charge * 2.0F) + rand.nextGaussian() * 0.25D
				+ (double) ((float) archer.world.getDifficulty().getDifficultyId() * 0.11F));

		if (power > 0) {
			arrow.setDamage(arrow.getDamage() + (double) power * 0.5D + 0.5D);
		}

		if (punch > 0) {
			arrow.setKnockbackStrength(punch);
		}

		// TODO bow enchants

		archer.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (rand.nextFloat() * 0.4F + 0.8F));

		archer.world.spawnEntity(arrow);
	}

	public static void reflectDamage(EntityCreature nemesisEntity, Nemesis nemesis, DamageSource source, float amount) {
		if (nemesisEntity.isEntityInvulnerable(source)) {
			return;
		}

		if (source instanceof EntityDamageSourceIndirect) {
			reflectArrowAtAttacker(nemesis, nemesisEntity, source);
		} else {
			reflectMeleeAttack(nemesis, nemesisEntity, source, amount);
		}
	}

	private static void reflectMeleeAttack(Nemesis nemesis, EntityCreature nemesisEntity, DamageSource source, float amount) {
		Entity attacker = source.getTrueSource();

		if (attacker == null) {
			return;
		}

		float reflectFactor = (float) nemesis.getLevel() * 0.2f;
		float reflectAmount = amount * reflectFactor;

		attacker.attackEntityFrom(DamageSource.causeMobDamage(nemesisEntity), reflectAmount);

		TargetPoint point = new TargetPoint(attacker.dimension, attacker.posX, attacker.posY, attacker.posZ, 100);
		NemesisSystem.NETWORK.sendToAllAround(new MessageReflectDamageAnimation(attacker.getEntityId()), point);
	}

	private static void reflectArrowAtAttacker(Nemesis nemesis, EntityCreature nemesisEntity, DamageSource source) {
		if (!"arrow".equals(source.getDamageType())) {
			return;
		}
		if (source.getTrueSource() != null && source.getTrueSource() instanceof EntityLivingBase) {
			int arrowCount = 1;
			if (nemesis.getLevel() > 8) {
				arrowCount = 4;
			} else if (nemesis.getLevel() > 6) {
				arrowCount = 3;
			} else if (nemesis.getLevel() > 4) {
				arrowCount = 2;
			}

			for (int i = 0; i < arrowCount; i++) {
				attackWithArrow(nemesisEntity, (EntityLivingBase) source.getTrueSource());
			}
		}
		if (source.getImmediateSource() != null) {
			source.getImmediateSource().setDead();
		}
	}
}
