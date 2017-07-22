package net.torocraft.nemesissystem.traits.logic;

import java.util.List;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.util.math.AxisAlignedBB;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.util.BehaviorUtil;

public class Chicken {

	public static void onUpdate(EntityLiving entity, Nemesis nemesis) {
		List<EntityChicken> nearbyChickens = entity.world.getEntitiesWithinAABB(EntityChicken.class,
				new AxisAlignedBB(entity.getPosition()).grow(5, 5, 5));

		if (nearbyChickens.size() > 0) {
			BehaviorUtil.moveToBlock(entity, BehaviorUtil.findRandomBlock((EntityCreature)entity), 2.0D);
		}
	}

}
