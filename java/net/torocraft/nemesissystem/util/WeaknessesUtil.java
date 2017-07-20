package net.torocraft.nemesissystem.util;

import net.minecraft.entity.EntityLiving;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.registry.Nemesis.Weakness;

public class WeaknessesUtil {

    public static final String TAG_WORSHIPPING = "worshipping";

    public static void handleTraits(Nemesis nemesis, EntityLiving nemesisEntity) {
        // caching to an array to avoid: java.util.ArrayList$Itr.checkForComodification
        Weakness[] weaknesses = nemesis.getWeaknesses().toArray(new Weakness[0]);
        for (Nemesis.Weakness weakness : nemesis.getWeaknesses()) {
            handleWeaknessUpdate(nemesisEntity, nemesis, weakness);
        }
    }

    private static void handleWeaknessUpdate(EntityLiving entity, Nemesis nemesis, Weakness weakness) {
        switch (weakness) {
            case GREEDY:
                handleGreedyBehavior(entity, nemesis);
                return;
            case CHICKEN:
                return;
            case GLUTTONY:
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

    private static void handleGreedyBehavior(EntityLiving entity, Nemesis nemesis) {
        /*
        check for gold ingots in line of sight
        move nemesis to gold ingot
        on gold ingot pickup, keep nemesis idle for 2s
         */

        if (isWorshipping(entity)) {
            cancelAllAITasks(entity);
        }

        if (canSeeGold(entity)) {
            moveToGold(entity);
        }

    }

    private static void moveToGold(EntityLiving entity) {
    }

    private static boolean canSeeGold(EntityLiving entity) {
        return false;
    }

    private static void cancelAllAITasks(EntityLiving entity) {
        entity.setNoAI(true);
    }

    private static boolean isWorshipping(EntityLiving entity) {
        return entity.getTags().contains(TAG_WORSHIPPING);
    }
}
