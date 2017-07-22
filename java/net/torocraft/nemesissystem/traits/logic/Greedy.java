package net.torocraft.nemesissystem.traits.logic;

import java.util.List;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.util.math.AxisAlignedBB;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.util.BehaviorUtil;

public class Greedy {
	public static final String NBT_COOLDOWN = "nemesissystem_cooldown";

	public static void handleGreedyBehavior(EntityLiving entity, Nemesis nemesis) {
		if (BehaviorUtil.isWorshiping(entity)) {
			if (entity.getEntityData().getInteger(NBT_COOLDOWN) >= 0) {
				return;
			}
			BehaviorUtil.stopWorshiping(entity, nemesis);
		}

		if (BehaviorUtil.pickupItem(entity, getShiniesWithinAABB(entity, 1.0D, 0.0D, 1.0D))) {
			return;
		}

		int distractDistance = 20;
		EntityItem shiny = Gluttony.getVisibleItem(entity, getShiniesWithinAABB(entity, distractDistance, distractDistance, distractDistance));
		if (shiny != null) {
			BehaviorUtil.moveToItem(entity, shiny);
		}

	}

	private static List<EntityItem> getShiniesWithinAABB(EntityLiving entity, double x, double y, double z) {
		return entity.world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(entity.getPosition()).grow(x, y, z),
				item -> item.getItem().getItem().equals(Items.GOLD_INGOT) || item.getItem().getItem().equals(Items.EMERALD) || item.getItem()
						.getItem().equals(Items.DIAMOND));
	}

	public static void decrementCooldown(Nemesis nemesis, EntityLiving entity) {
		if (!entity.getEntityData().hasKey(NBT_COOLDOWN)) {
			return;
		}
		entity.getEntityData().setInteger(NBT_COOLDOWN, entity.getEntityData().getInteger(NBT_COOLDOWN) - 1);
	}
}
