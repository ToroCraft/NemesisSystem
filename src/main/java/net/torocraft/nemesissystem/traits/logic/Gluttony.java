package net.torocraft.nemesissystem.traits.logic;

import java.util.Arrays;
import java.util.List;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.util.BehaviorUtil;
import net.torocraft.nemesissystem.util.NemesisUtil;

public class Gluttony {

	private static final Item[] TASTY_THINGS = { Items.BEEF, Items.CHICKEN, Items.MUTTON, Items.PORKCHOP, Items.RABBIT,
			Items.COOKED_BEEF, Items.COOKED_CHICKEN, Items.COOKED_MUTTON, Items.COOKED_PORKCHOP, Items.COOKED_RABBIT };

	public static void onUpdate(EntityCreature entity, Nemesis nemesis, int level) {
		if (BehaviorUtil.isWorshiping(entity)) {
			if (entity.getEntityData().getInteger(NemesisSystem.NBT_WORSHIP_COOLDOWN) >= 0) {
				return;
			}
			BehaviorUtil.stopWorshiping(entity, nemesis);
		}

		if (BehaviorUtil.stealAndWorshipItem(entity, getFoodWithinAABB(entity, 1), level)) {
			return;
		}
		searchForTastyTreats(entity);
	}

	private static void searchForTastyTreats(EntityCreature entity) {
		EntityItem food = lookForANearByTreat(entity);
		if (food != null) {
			entity.setAttackTarget(null);
			BehaviorUtil.moveToItem(entity, food);
		}
	}

	private static EntityItem lookForANearByTreat(EntityCreature entity) {
		return getFoodWithinAABB(entity, 20)
				.stream()
				.filter(e -> BehaviorUtil.canSee(entity, e))
				.findFirst().orElse(null);
	}

	private static List<EntityItem> getFoodWithinAABB(EntityLiving entity, int distance) {
		return entity.world.getEntitiesWithinAABB(EntityItem.class, NemesisUtil.nearByBox(entity.getPosition(), distance),
				Gluttony::isTasty);
	}

	private static boolean isTasty(EntityItem item) {
		return Arrays.stream(TASTY_THINGS).anyMatch(x -> x == item.getItem().getItem());
	}

}
