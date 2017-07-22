package net.torocraft.nemesissystem.traits;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.torocraft.nemesissystem.handlers.Death;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.traits.logic.Allergy;
import net.torocraft.nemesissystem.traits.logic.Chicken;
import net.torocraft.nemesissystem.traits.logic.Fire;
import net.torocraft.nemesissystem.traits.logic.Gluttony;
import net.torocraft.nemesissystem.traits.logic.Greedy;
import net.torocraft.nemesissystem.traits.logic.Heal;
import net.torocraft.nemesissystem.traits.logic.Hydrophobia;
import net.torocraft.nemesissystem.traits.logic.Potion;
import net.torocraft.nemesissystem.traits.logic.Pyrophobia;
import net.torocraft.nemesissystem.traits.logic.Reflection;
import net.torocraft.nemesissystem.traits.logic.Summon;
import net.torocraft.nemesissystem.traits.logic.Teleport;
import net.torocraft.nemesissystem.util.NemesisUtil;

public class TraitHandler {

	public static final String TAG_WORSHIPING = "nemesissystem_worshiping";

	public static void onUpdate(Nemesis nemesis, EntityLiving nemesisEntity) {
		// caching to an array to avoid: java.util.ArrayList$Itr.checkForComodification
		//Nemesis.Trait[] traits = nemesis.getTraits().toArray(new Nemesis.Trait[0]);
		Greedy.decrementCooldown(nemesis, nemesisEntity);
		for (Trait trait : nemesis.getTraits()) {
			//TODO secondary traits should be used less frequently

			// TODO randomize attack timing
			onUpdate(nemesisEntity, nemesis, trait);
		}
	}

	private static void onUpdate(EntityLiving entity, Nemesis nemesis, Trait trait) {
		switch (trait.type) {
		case DOUBLE_MELEE:
			// TODO make the nemesis hit twice when attacking
			return;
		case FIRE:
			Fire.handleFireballTraitUpdate(entity, nemesis);
			// TODO Fire.handleHeatTraitUpdate(entity, nemesis, trait);
			return;
		case ARROW:
			//Archer.handleArrowTraitUpdate(entity, nemesis, trait);
			return;
		case SUMMON:
			Summon.handleSummonTraitUpdate(entity, nemesis);
			return;
		case REFLECT:
			return;
		case POTION:
			Potion.handlePotionTraitUpdate(entity, nemesis);
			return;
		case TELEPORT:
			Teleport.handleTeleportTraitUpdate(entity, nemesis);
			return;
		case HEAL:
			Heal.handleHealTraitUpdate(entity, nemesis);
			return;
		case GREEDY:
			Greedy.handleGreedyBehavior(entity, nemesis);
			return;
		case CHICKEN:
			Chicken.handleChickenBehavior(entity, nemesis);
			return;
		case GLUTTONY:
			Gluttony.handleGluttonyBehavior(entity, nemesis);
			return;
		case PYROPHOBIA:
			Pyrophobia.handlePyrophobiaBehavior(entity, nemesis);
			return;
		case HYDROPHOBIA:
			Hydrophobia.handleHydrophobiaBehavior(entity, nemesis);
			return;
		case GOLD_ALLERGY:
			return;
		case WOOD_ALLERGY:
			return;
		case STONE_ALLERGY:
			return;
		}
	}

	public static void onDrops(List<EntityItem> drops, EntityCreature nemesisEntity, Nemesis nemesis) {
		Random rand = nemesisEntity.getRNG();
		for (Trait trait : nemesis.getTraits()) {
			switch (trait.type) {
			case DOUBLE_MELEE:

				break;
			case ARROW:
				drops.add(Death.drop(nemesisEntity, new ItemStack(Items.ARROW, rand.nextInt(64))));
				break;
			case SUMMON:
				break;
			case REFLECT:
				break;
			case FIRE:
				drops.add(Death.drop(nemesisEntity, new ItemStack(Blocks.TORCH, rand.nextInt(64))));
				if (rand.nextInt(5) == 0) {
					drops.add(Death.drop(nemesisEntity, new ItemStack(Items.LAVA_BUCKET)));
				}
				// TODO HEAT:
				//Fire.handleHeatTraitUpdate(entity, nemesis, trait);
				break;
			case POTION:
				drops.add(Death.drop(nemesisEntity,
						PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionType.REGISTRY.getRandomObject(rand))));
				break;
			case TELEPORT:
				drops.add(Death.drop(nemesisEntity, new ItemStack(Items.ENDER_PEARL, rand.nextInt(16))));
				break;
			}
		}
	}

	public static void onDefend(LivingHurtEvent event) {
		EntityCreature nemesisEntity = (EntityCreature) event.getEntity();
		Nemesis nemesis = NemesisUtil.loadNemesisFromEntity(nemesisEntity);
		if (nemesis == null) {
			return;
		}
		for (Trait trait : nemesis.getTraits()) {
			switch (trait.type) {
			case DOUBLE_MELEE:
				break;
			case ARROW:
				break;
			case SUMMON:
				break;
			case REFLECT:
				Reflection.reflectDamage(nemesisEntity, nemesis, event.getSource(), event.getAmount());
				break;
			case FIRE:
				break;
			case POTION:
				break;
			case TELEPORT:
				break;
			case HEAL:
				break;
			case GOLD_ALLERGY:
			case STONE_ALLERGY:
			case WOOD_ALLERGY:
				Allergy.handleAllergy(event, nemesis);
			}
		}

	}
}
