package net.torocraft.nemesissystem.util;

import java.util.List;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.traits.TraitHandler;
import net.torocraft.nemesissystem.traits.logic.Greedy;

public class BehaviorUtil {

	public static void setFollowSpeed(EntityCreature bodyGuard, double followSpeed) {
		EntityAIMoveTowardsRestriction ai = null;
		for (EntityAITaskEntry entry : bodyGuard.tasks.taskEntries) {
			if (entry.action instanceof EntityAIMoveTowardsRestriction) {
				ai = (EntityAIMoveTowardsRestriction) entry.action;
			}
		}
		if (ai == null) {
			System.out.println("guard ai not found");
			return;
		}
		//not sure field_75433_e is the correct name for EntityAIMoveTowardsRestriction.movementSpeed
		ObfuscationReflectionHelper.setPrivateValue(EntityAIMoveTowardsRestriction.class, ai, followSpeed, "field_75433_e", "movementSpeed");
	}

	public static boolean moveToBlock(EntityLiving entity, BlockPos randBlock, double speed) {
		return entity.getNavigator().tryMoveToXYZ(randBlock.getX(), randBlock.getY(), randBlock.getZ(), speed);
	}

	public static BlockPos findRandomBlock(EntityCreature entity) {
		return new BlockPos(RandomPositionGenerator.findRandomTarget(entity, 5, 4));
	}

	public static boolean pickupItem(EntityLiving entity, List<EntityItem> desiredItems) {
		if (desiredItems.size() > 0) {
			entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, desiredItems.get(0).getItem());
			BehaviorUtil.startWorshiping(entity);
			for (EntityItem item : desiredItems) {
				entity.world.removeEntity(item);
			}
			return true;
		}
		return false;
	}

	public static void stopWorshiping(EntityLiving entity, Nemesis nemesis) {
		entity.getEntityData().removeTag(Greedy.NBT_COOLDOWN);
		entity.getTags().remove(TraitHandler.TAG_WORSHIPING);
		entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, nemesis.getHandInventory().get(0));
		resumeAITasks(entity);
	}

	public static boolean isWorshiping(EntityLiving entity) {
		return entity.getTags().contains(TraitHandler.TAG_WORSHIPING) && entity.getEntityData().hasKey(Greedy.NBT_COOLDOWN);
	}

	private static void startWorshiping(EntityLiving entity) {
		entity.getTags().add(TraitHandler.TAG_WORSHIPING);
		entity.getEntityData().setTag(Greedy.NBT_COOLDOWN, new NBTTagInt(3));
		cancelAllAITasks(entity);
	}

	public static void moveToItem(EntityLiving entity, EntityItem item) {
		EntityCreature mob = (EntityCreature)entity;
		mob.getNavigator().tryMoveToXYZ(item.getPosition().getX() + 0.5D, item.getPosition().getY() + 1, item.getPosition().getZ() + 0.5D, 2.0);
	}

	public static void cancelAllAITasks(EntityLiving entity) {
		entity.setNoAI(true);
	}

	public static void resumeAITasks(EntityLiving entity) {
		entity.setNoAI(false);
	}
}
