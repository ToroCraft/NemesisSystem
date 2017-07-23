package net.torocraft.nemesissystem.traits.logic;

import java.util.List;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.network.MessageWorshipAnimation;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.util.BehaviorUtil;

public class Greedy {

	public static void onUpdate(EntityLiving entity, Nemesis nemesis, int level) {
		if (BehaviorUtil.isWorshiping(entity)) {
			if (entity.getEntityData().getInteger(NemesisSystem.NBT_WORSHIP_COOLDOWN) >= 0) {
				TargetPoint point = new TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, 100);
				NemesisSystem.NETWORK.sendToAllAround(new MessageWorshipAnimation(entity.getEntityId()), point);
				return;
			}
			BehaviorUtil.stopWorshiping(entity, nemesis);
		}

		if (BehaviorUtil.stealAndWorshipItem(entity, getShiniesWithinAABB(entity, 1.0D, 0.0D, 1.0D), level)) {
			return;
		}

		int distractDistance = 20;
		EntityItem shiny = getVisibleItem(entity, getShiniesWithinAABB(entity, distractDistance, distractDistance, distractDistance));
		if (shiny != null) {
			entity.setAttackTarget(null);
			BehaviorUtil.moveToItem(entity, shiny);
		}

	}

	private static List<EntityItem> getShiniesWithinAABB(EntityLiving entity, double x, double y, double z) {
		return entity.world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(entity.getPosition()).grow(x, y, z),
				item -> item.getItem().getItem().equals(Items.GOLD_INGOT) || item.getItem().getItem().equals(Items.EMERALD) || item.getItem()
						.getItem().equals(Items.DIAMOND));
	}

	public static void decrementCooldown(Nemesis nemesis, EntityLiving entity) {
		if (!entity.getEntityData().hasKey(NemesisSystem.NBT_WORSHIP_COOLDOWN)) {
			return;
		}
		entity.getEntityData()
				.setInteger(NemesisSystem.NBT_WORSHIP_COOLDOWN, entity.getEntityData().getInteger(NemesisSystem.NBT_WORSHIP_COOLDOWN) - 1);
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
