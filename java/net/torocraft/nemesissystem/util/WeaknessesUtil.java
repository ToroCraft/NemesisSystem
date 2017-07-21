package net.torocraft.nemesissystem.util;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.math.AxisAlignedBB;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.registry.Nemesis.Weakness;

import java.util.List;

public class WeaknessesUtil {

    public static final String TAG_WORSHIPING = "worshiping";
    public static final String TAG_COOLDOWN = "cooldown";

    public static void handleWeaknesses(Nemesis nemesis, EntityLiving nemesisEntity) {
        // caching to an array to avoid: java.util.ArrayList$Itr.checkForComodification
        Weakness[] weaknesses = nemesis.getWeaknesses().toArray(new Weakness[0]);
        for (Weakness weakness : nemesis.getWeaknesses()) {
            handleWeaknessUpdate(nemesisEntity, nemesis, weakness);
        }
    }

    private static void handleWeaknessUpdate(EntityLiving entity, Nemesis nemesis, Weakness weakness) {
        decrementCooldown(nemesis, entity);
        switch (weakness) {
            case GREEDY:
                handleGreedyBehavior(entity, nemesis);
                return;
            case CHICKEN:
                return;
            case GLUTTONY:
                handleGluttonyBehavior(entity, nemesis);
                return;
            case PYROPHOBIA:
                return;
            case HYDROPHOBIA:
                return;
            case GOLD_ALLERGY:
                // LivingHurtEvent needs to check damage source and if item is gold, apply more damage
                return;
            case WOOD_ALLERGY:
                // LivingHurtEvent needs to check damage source and if item is wooden, apply more damage
                return;
            case STONE_ALLERGY:
                // LivingHurtEvent needs to check damage source and if item is stone, apply more damage
                return;
        }
    }

    private static void handleGluttonyBehavior(EntityLiving entity, Nemesis nemesis) {
        if (isWorshiping(entity)) {
            if (entity.getEntityData().getInteger(TAG_COOLDOWN) >= 0) {
                return;
            }
            stopWorshiping(entity, nemesis);
        }

        if (pickupItem(entity, getFoodWithinAABB(entity, 1.0D, 0.0D, 1.0D))) {
            return;
        }

        int distractDistance = 20;
        EntityItem food = getVisibleItem(entity, getFoodWithinAABB(entity, distractDistance, distractDistance, distractDistance));
        if (food != null) {
            moveToItem(entity, food);
        }
    }

    private static void handleGreedyBehavior(EntityLiving entity, Nemesis nemesis) {
        if (isWorshiping(entity)) {
            if (entity.getEntityData().getInteger(TAG_COOLDOWN) >= 0) {
                return;
            }
            stopWorshiping(entity, nemesis);
        }

        if (pickupItem(entity, getShiniesWithinAABB(entity, 1.0D,0.0D, 1.0D))) {
            return;
        }

        int distractDistance = 20;
        EntityItem shiny = getVisibleItem(entity, getShiniesWithinAABB(entity, distractDistance, distractDistance, distractDistance));
        if (shiny != null) {
            moveToItem(entity, shiny);
        }

    }

    private static boolean pickupItem(EntityLiving entity, List<EntityItem> desiredItems) {
        if (desiredItems.size() > 0) {
            entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, desiredItems.get(0).getItem());
            startWorshiping(entity);
            for (EntityItem item : desiredItems) {
                entity.world.removeEntity(item);
            }
            return true;
        }
        return false;
    }

    private static List<EntityItem> getShiniesWithinAABB(EntityLiving entity, double x, double y, double z) {
        return entity.world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(entity.getPosition()).grow(x, y, z),
                item -> item.getItem().getItem().equals(Items.GOLD_INGOT) || item.getItem().getItem().equals(Items.EMERALD) || item.getItem().getItem().equals(Items.DIAMOND));
    }

    private static List<EntityItem> getFoodWithinAABB(EntityLiving entity, double x, double y, double z) {
        return entity.world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(entity.getPosition()).grow(x, y, z),
                item -> item.getItem().getItem().equals(Items.BEEF) || item.getItem().getItem().equals(Items.CHICKEN) ||
                        item.getItem().getItem().equals(Items.MUTTON) || item.getItem().getItem().equals(Items.PORKCHOP) ||
                        item.getItem().getItem().equals(Items.RABBIT));
    }

    private static void startWorshiping(EntityLiving entity) {
        entity.getTags().add(WeaknessesUtil.TAG_WORSHIPING);
        entity.getEntityData().setTag(WeaknessesUtil.TAG_COOLDOWN, new NBTTagInt(3));
        WeaknessesUtil.cancelAllAITasks(entity);
    }

    private static void stopWorshiping(EntityLiving entity, Nemesis nemesis) {
        entity.getEntityData().removeTag(TAG_COOLDOWN);
        entity.getTags().remove(TAG_WORSHIPING);
        entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, nemesis.getHandInventory().get(0));
        resumeAITasks(entity);
    }

    private static void moveToItem(EntityLiving entity, EntityItem item) {
        EntityCreature mob = (EntityCreature)entity;
        mob.getNavigator().tryMoveToXYZ(item.getPosition().getX() + 0.5D, item.getPosition().getY() + 1, item.getPosition().getZ() + 0.5D, 2.0);
    }

    private static EntityItem getVisibleItem(EntityLiving entity, List<EntityItem> desiredItems) {
        for (EntityItem item : desiredItems) {
            if (entity.getEntitySenses().canSee(item)) {
                return item;
            }
        }

        return null;
    }

    public static void cancelAllAITasks(EntityLiving entity) {
        entity.setNoAI(true);
    }

    public static void resumeAITasks(EntityLiving entity) {
        entity.setNoAI(false);
    }

    private static boolean isWorshiping(EntityLiving entity) {
        return entity.getTags().contains(TAG_WORSHIPING) && entity.getEntityData().hasKey(TAG_COOLDOWN);
    }

    private static void decrementCooldown(Nemesis nemesis, EntityLiving entity) {
        if (!entity.getEntityData().hasKey(TAG_COOLDOWN)) {
            return;
        }
        entity.getEntityData().setTag(TAG_COOLDOWN, new NBTTagInt(entity.getEntityData().getInteger(TAG_COOLDOWN) - 1));
    }
}
