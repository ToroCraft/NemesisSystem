package net.torocraft.nemesissystem.traits.logic;

import java.util.List;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.util.math.AxisAlignedBB;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.util.BehaviorUtil;

public class Gluttony {
	public static void onUpdate(EntityLiving entity, Nemesis nemesis) {
		if (BehaviorUtil.isWorshiping(entity)) {
			if (entity.getEntityData().getInteger(Greedy.NBT_COOLDOWN) >= 0) {
				return;
			}
			BehaviorUtil.stopWorshiping(entity, nemesis);
		}

		if (BehaviorUtil.pickupItem(entity, getFoodWithinAABB(entity, 1.0D, 0.0D, 1.0D))) {
			return;
		}

		int distractDistance = 20;
		EntityItem food = getVisibleItem(entity, getFoodWithinAABB(entity, distractDistance, distractDistance, distractDistance));
		if (food != null) {
			BehaviorUtil.moveToItem(entity, food);
		}
	}

	private static List<EntityItem> getFoodWithinAABB(EntityLiving entity, double x, double y, double z) {
		return entity.world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(entity.getPosition()).grow(x, y, z),
				item -> item.getItem().getItem().equals(Items.BEEF) || item.getItem().getItem().equals(Items.CHICKEN) ||
						item.getItem().getItem().equals(Items.MUTTON) || item.getItem().getItem().equals(Items.PORKCHOP) ||
						item.getItem().getItem().equals(Items.RABBIT));
	}

	public static EntityItem getVisibleItem(EntityLiving entity, List<EntityItem> desiredItems) {
		for (EntityItem item : desiredItems) {
			if (entity.getEntitySenses().canSee(item)) {
				return item;
			}
		}

		return null;
	}

}
