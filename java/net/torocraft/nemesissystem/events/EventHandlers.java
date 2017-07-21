package net.torocraft.nemesissystem.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.registry.Nemesis.Trait;
import net.torocraft.nemesissystem.registry.Nemesis.Weakness;
import net.torocraft.nemesissystem.util.NemesisActions;
import net.torocraft.nemesissystem.util.NemesisUtil;

import java.util.List;

public class EventHandlers {

    @SubscribeEvent
    public void onAllergyHit(LivingHurtEvent event) {
        if (!(event.getEntityLiving() instanceof EntityLiving) || !event.getEntityLiving().getTags().contains(NemesisSystem.TAG_NEMESIS)) {
            return;
        }

        EntityLiving entity = (EntityLiving)event.getEntityLiving();
        Nemesis nemesis = NemesisUtil.loadNemesisFromEntity(entity);
        if (nemesis == null) {
            return;
        }

        if (!nemesis.getWeaknesses().contains(Weakness.WOOD_ALLERGY) && !nemesis.getWeaknesses().contains(Weakness.STONE_ALLERGY) && !nemesis.getWeaknesses().contains(Weakness.GOLD_ALLERGY)) {
            return;
        }

        Entity trueSource = event.getSource().getTrueSource();
        if (!(trueSource instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer)trueSource;

        ItemStack heldItem = player.getHeldItem(EnumHand.MAIN_HAND);
        if (heldItem == null || heldItem.getItem() == null) {
            return;
        }

        Item item = heldItem.getItem();
        String material = null;
        if (item instanceof ItemSword) {
            material = ((ItemSword) item).getToolMaterialName();
        }
        if (item instanceof ItemTool) {
            material = ((ItemTool) item).getToolMaterialName();
        }

        if (material == null) {
            return;
        }

        if (woodAllergyApplies(nemesis, material) || goldAllergyApplies(nemesis, material) || stoneAllergyApplies(nemesis, material)) {
            event.setAmount(event.getAmount() * 1.5f);
        }
    }

    private boolean stoneAllergyApplies(Nemesis nemesis, String material) {
        return nemesis.getWeaknesses().contains(Weakness.STONE_ALLERGY) && material.equals("STONE");
    }

    private boolean goldAllergyApplies(Nemesis nemesis, String material) {
        return nemesis.getWeaknesses().contains(Weakness.GOLD_ALLERGY) && material.equals("GOLD");
    }

    private boolean woodAllergyApplies(Nemesis nemesis, String material) {
        return nemesis.getWeaknesses().contains(Weakness.WOOD_ALLERGY) && material.equals("WOOD");
    }

    @SubscribeEvent
    public void onTeleportEntityHarm(LivingHurtEvent event) {
        if (!(event.getEntityLiving() instanceof EntityLiving) || !event.getEntityLiving().getTags().contains(NemesisSystem.TAG_NEMESIS)) {
            return;
        }

        EntityLiving entity = (EntityLiving)event.getEntityLiving();
        Nemesis nemesis = NemesisUtil.loadNemesisFromEntity(entity);
        if (nemesis == null) {
            return;
        }

        if (!nemesis.getTraits().contains(Trait.TELEPORT)) {
            return;
        }

        World world = entity.getEntityWorld();
        if (world.rand.nextInt(2) != 0) {
            return;
        }

        List<EntityCreature> guards = NemesisUtil.findNemesisBodyGuards(world, nemesis.getId(), entity.getPosition());
        if (guards.size() < 1) {
            return;
        }
        EntityCreature teleportTarget = guards.get(world.rand.nextInt(guards.size()));

        NemesisActions.throwPearl(entity, teleportTarget);
    }

}
