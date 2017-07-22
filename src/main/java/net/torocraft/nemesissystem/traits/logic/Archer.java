package net.torocraft.nemesissystem.traits.logic;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.traits.Trait;

public class Archer {

	public static void handleArrowTraitUpdate(EntityLiving entity, Nemesis nemesis) {
		EntityLivingBase target = entity.getAttackTarget();

		if (target == null) {
			return;
		}

		if (!entity.getEntitySenses().canSee(target)) {
			return;
		}

		attackWithArrow(entity, target);
	}

	public static void attackWithArrow(EntityLiving archer, EntityLivingBase target) {
		int charge = 2 + Trait.rand.nextInt(10);

		EntityArrow arrow = new EntityTippedArrow(archer.world, archer);

		double dX = target.posX - archer.posX;
		double dY = target.getEntityBoundingBox().minY + (double) (target.height / 3.0F) - arrow.posY;
		double dZ = target.posZ - archer.posZ;

		double levelDistance = (double) MathHelper.sqrt(dX * dX + dZ * dZ);

		arrow.setThrowableHeading(dX, dY + levelDistance * 0.20000000298023224D, dZ, 1.6F,
				(float) (14 - archer.world.getDifficulty().getDifficultyId() * 4));

		int power = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.POWER, archer);
		int punch = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.PUNCH, archer);

		arrow.setDamage((double) (charge * 2.0F) + Trait.rand.nextGaussian() * 0.25D
				+ (double) ((float) archer.world.getDifficulty().getDifficultyId() * 0.11F));

		if (power > 0) {
			arrow.setDamage(arrow.getDamage() + (double) power * 0.5D + 0.5D);
		}

		if (punch > 0) {
			arrow.setKnockbackStrength(punch);
		}

		// TODO bow enchants

		archer.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (Trait.rand.nextFloat() * 0.4F + 0.8F));

		archer.world.spawnEntity(arrow);
	}
}
