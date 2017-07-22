package net.torocraft.nemesissystem.traits.logic;

import java.util.List;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.MathHelper;
import net.torocraft.nemesissystem.handlers.Death;
import net.torocraft.nemesissystem.traits.Trait;
import net.torocraft.nemesissystem.traits.TraitHandler;

public class Archer {

	private static Potion[] TIPPED_ARROWS = { MobEffects.HUNGER, MobEffects.POISON, MobEffects.WEAKNESS, MobEffects.SLOWNESS };
	private static String[] DROP_TYPES = { "harming", "poison", "weakness", "slowness"};

	public static void onDrop(List<EntityItem> drops, EntityCreature nemesisEntity, int level) {
		drops.add(Death.drop(nemesisEntity, new ItemStack(Items.ARROW, TraitHandler.rand.nextInt(64))));
		if (level > 3) {
			ItemStack arrows = new ItemStack(Items.TIPPED_ARROW, TraitHandler.rand.nextInt(64));
			PotionUtils.addPotionToItemStack(arrows, PotionType.getPotionTypeForName(DROP_TYPES[TraitHandler.rand.nextInt(DROP_TYPES.length)]));
			drops.add(Death.drop(nemesisEntity, arrows));
		}
	}

	public static void onUpdate(EntityLiving entity, int level) {
		EntityLivingBase target = entity.getAttackTarget();

		if (target == null) {
			return;
		}

		if (!entity.getEntitySenses().canSee(target)) {
			return;
		}

		for (int i = 0; i < MathHelper.clamp(level, 1, 3); i++) {
			attackWithArrow(entity, target, level);
		}
	}

	public static void attackWithArrow(EntityLiving archer, EntityLivingBase target, int level) {
		EntityArrow arrow = getArrow(archer, level);

		double dX = target.posX - archer.posX;
		double dY = target.getEntityBoundingBox().minY + (double) (target.height / 3.0F) - arrow.posY;
		double dZ = target.posZ - archer.posZ;

		double levelDistance = (double) MathHelper.sqrt(dX * dX + dZ * dZ);

		arrow.setThrowableHeading(dX, dY + levelDistance * 0.20000000298023224D, dZ, 1.6F,
				(float) (14 - archer.world.getDifficulty().getDifficultyId() * 4));

		archer.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (Trait.rand.nextFloat() * 0.4F + 0.8F));
		archer.world.spawnEntity(arrow);
	}

	private static EntityArrow getArrow(EntityLivingBase entity, int level) {
		EntityTippedArrow arrow = new EntityTippedArrow(entity.getEntityWorld(), entity);
		arrow.setEnchantmentEffectsFromEntity(entity, (float) 1 / (float) level);
		if (level > 3) {
			setPoisonArrow(arrow, level);
		}
		return arrow;
	}

	private static void setPoisonArrow(EntityArrow entityarrow, int level) {
		if (!(entityarrow instanceof EntityTippedArrow)) {
			return;
		}

		int chance = MathHelper.clamp(6 - (level - 3), 1, 5);

		if (TraitHandler.rand.nextInt(chance) != 0) {
			return;
		}

		((EntityTippedArrow) entityarrow).addEffect(new PotionEffect(TIPPED_ARROWS[TraitHandler.rand.nextInt(TIPPED_ARROWS.length)], 400 * level));
	}
}
