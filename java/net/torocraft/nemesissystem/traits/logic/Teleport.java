package net.torocraft.nemesissystem.traits.logic;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.util.NemesisActions;

public class Teleport {

	public static void onUpdate(EntityLiving entity, Nemesis nemesis) {
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
}
