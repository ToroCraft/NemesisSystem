package net.torocraft.nemesissystem.traits.logic;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityChicken;
import net.torocraft.nemesissystem.util.BehaviorUtil;
import net.torocraft.nemesissystem.util.NemesisUtil;

public class Chicken {

	public static void onUpdate(EntityLiving entity, int level) {
		if (chickensNearby(entity)) {
			entity.setAttackTarget(null);
			BehaviorUtil.moveToBlock(entity, BehaviorUtil.findPanicDestination((EntityCreature) entity, level), 2.0D);
		}
	}

	private static boolean chickensNearby(EntityLiving entity) {
		return entity.world.getEntitiesWithinAABB(EntityChicken.class, NemesisUtil.nearByBox(entity.getPosition(), 5)).size() > 0;
	}

}
