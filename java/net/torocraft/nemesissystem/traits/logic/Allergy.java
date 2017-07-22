package net.torocraft.nemesissystem.traits.logic;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.traits.Trait;

public class Allergy {
	private static final int ONE_SECOND = 20;

	public static void handleAllergy(LivingHurtEvent event, Nemesis nemesis) {
		EntityLiving entity = (EntityLiving) event.getEntity();
		Entity trueSource = event.getSource().getTrueSource();
		if (!(trueSource instanceof EntityPlayer)) {
			return;
		}

		EntityPlayer player = (EntityPlayer) trueSource;

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
			entity.addPotionEffect(new PotionEffect(MobEffects.POISON, ONE_SECOND * 3, 1));
			event.setAmount(event.getAmount() * 2.0f);
		}
	}

	private static boolean stoneAllergyApplies(Nemesis nemesis, String material) {
		return hasWeakness(nemesis, Trait.Type.STONE_ALLERGY) && material.equals("STONE");
	}

	private static boolean goldAllergyApplies(Nemesis nemesis, String material) {
		return hasWeakness(nemesis, Trait.Type.GOLD_ALLERGY) && material.equals("GOLD");
	}

	private static boolean woodAllergyApplies(Nemesis nemesis, String material) {
		return hasWeakness(nemesis, Trait.Type.WOOD_ALLERGY) && material.equals("WOOD");
	}

	private static boolean hasWeakness(Nemesis nemesis, Trait.Type weakness) {
		for (Trait t : nemesis.getWeaknesses()) {
			if (t.type.equals(weakness)) {
				return true;
			}
		}
		return false;
	}
}
