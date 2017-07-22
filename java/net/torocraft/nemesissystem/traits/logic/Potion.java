package net.torocraft.nemesissystem.traits.logic;

import java.util.Random;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.torocraft.nemesissystem.registry.Nemesis;

public class Potion {
	public static void handlePotionTraitUpdate(EntityLiving entity, Nemesis nemesis) {
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
}
