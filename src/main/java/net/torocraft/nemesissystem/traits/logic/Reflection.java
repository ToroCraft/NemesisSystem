package net.torocraft.nemesissystem.traits.logic;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.network.MessageReflectDamageAnimation;
import net.torocraft.nemesissystem.registry.Nemesis;

public class Reflection {

	public static void onHurt(EntityCreature nemesisEntity, Nemesis nemesis, DamageSource source, float amount) {
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
				Archer.attackWithArrow(nemesisEntity, (EntityLivingBase) source.getTrueSource(), 1);
			}
		}
		if (source.getImmediateSource() != null) {
			source.getImmediateSource().setDead();
		}
	}
}
