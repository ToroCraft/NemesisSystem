package net.torocraft.nemesissystem.traits.logic;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.util.BehaviorUtil;

public class Hydrophobia {

	public static void onUpdate(EntityLiving entity, Nemesis nemesis) {
		if (entity.isWet()) {
			BehaviorUtil.moveToBlock(entity, BehaviorUtil.findRandomBlock((EntityCreature)entity), 2.0D);
		}
	}
}
