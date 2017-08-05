package net.torocraft.nemesissystem.traits;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.torocraft.nemesissystem.handlers.DeathHandler;
import net.torocraft.nemesissystem.registry.NemesisEntry;
import net.torocraft.nemesissystem.traits.logic.Allergy;
import net.torocraft.nemesissystem.traits.logic.Archer;
import net.torocraft.nemesissystem.traits.logic.Chicken;
import net.torocraft.nemesissystem.traits.logic.Fireball;
import net.torocraft.nemesissystem.traits.logic.Gluttony;
import net.torocraft.nemesissystem.traits.logic.Greedy;
import net.torocraft.nemesissystem.traits.logic.Heal;
import net.torocraft.nemesissystem.traits.logic.Hydrophobia;
import net.torocraft.nemesissystem.traits.logic.Inferno;
import net.torocraft.nemesissystem.traits.logic.Potion;
import net.torocraft.nemesissystem.traits.logic.Pyrophobia;
import net.torocraft.nemesissystem.traits.logic.Reflection;
import net.torocraft.nemesissystem.traits.logic.Summon;
import net.torocraft.nemesissystem.traits.logic.Teleport;
import net.torocraft.nemesissystem.util.NemesisUtil;

public class TraitHandler {

	public static final String TAG_WORSHIPING = "nemesissystem_worshiping";

	public static final Random rand = new Random();

	public static void onUpdate(NemesisEntry nemesis, EntityCreature nemesisEntity) {
		// caching to an array to avoid: java.util.ArrayList$Itr.checkForComodification
		//NemesisEntry.Trait[] traits = nemesis.getTraits().toArray(new NemesisEntry.Trait[0]);
		Greedy.decrementCooldown(nemesis, nemesisEntity);
		for (Trait trait : nemesis.getTraits()) {
			//TODO secondary traits should be used less frequently

			// TODO randomize attack timing
			onUpdate(nemesisEntity, nemesis, trait);
		}
	}

	private static void onUpdate(EntityCreature entity, NemesisEntry nemesis, Trait trait) {
		switch (trait.type) {
		case DOUBLE_MELEE:
			// TODO make the nemesis hit twice when attacking
			return;
		case FIREBALL:
			Fireball.onUpdate(entity, trait.level);
			return;
		case INFERNO:
			Inferno.onUpdate(entity, trait.level);
			return;
		case ARCHER:
			Archer.onUpdate(entity, trait.level);
			return;
		case SUMMON:
			Summon.onUpdate(entity, nemesis);
			return;
		case REFLECT:
			return;
		case POTION:
			Potion.onUpdate(entity, nemesis);
			return;
		case TELEPORT:
			Teleport.onUpdate(entity, nemesis);
			return;
		case HEAL:
			Heal.onUpdate(entity, nemesis);
			return;
		case GREEDY:
			Greedy.onUpdate(entity, nemesis, trait.level);
			return;
		case CHICKEN:
			Chicken.onUpdate(entity, trait.level);
			return;
		case GLUTTONY:
			Gluttony.onUpdate(entity, nemesis, trait.level);
			return;
		case PYROPHOBIA:
			Pyrophobia.onUpdate(entity, trait.level);
			return;
		case HYDROPHOBIA:
			Hydrophobia.onUpdate(entity, trait.level);
			return;
		case GOLD_ALLERGY:
			return;
		case WOOD_ALLERGY:
			return;
		case STONE_ALLERGY:
			return;
		}
	}

	public static void onDrops(List<EntityItem> drops, EntityCreature nemesisEntity, NemesisEntry nemesis) {
		Random rand = nemesisEntity.getRNG();
		for (Trait trait : nemesis.getTraits()) {
			switch (trait.type) {
			case DOUBLE_MELEE:

				break;
			case ARCHER:
				Archer.onDrop(drops, nemesisEntity, trait.level);
				break;
			case SUMMON:
				break;
			case REFLECT:
				break;
			case FIREBALL:
				drops.add(DeathHandler.drop(nemesisEntity, new ItemStack(Blocks.TORCH, rand.nextInt(64))));
				if (rand.nextInt(5) == 0) {
					drops.add(DeathHandler.drop(nemesisEntity, new ItemStack(Items.LAVA_BUCKET)));
				}
				// TODO HEAT:
				//Fireball.handleHeatTraitUpdate(entity, nemesis, trait);
				break;
			case POTION:
				drops.add(DeathHandler.drop(nemesisEntity,
						PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionType.REGISTRY.getRandomObject(rand))));
				break;
			case TELEPORT:
				drops.add(DeathHandler.drop(nemesisEntity, new ItemStack(Items.ENDER_PEARL, rand.nextInt(16))));
				break;
			}
		}
	}

	public static void onHurt(LivingHurtEvent event) {
		EntityCreature nemesisEntity = (EntityCreature) event.getEntity();
		NemesisEntry nemesis = NemesisUtil.loadNemesisFromEntity(nemesisEntity);
		if (nemesis == null) {
			return;
		}
		for (Trait trait : nemesis.getTraits()) {
			switch (trait.type) {
			case DOUBLE_MELEE:
				break;
			case ARCHER:
				break;
			case SUMMON:
				break;
			case REFLECT:
				Reflection.onHurt(nemesisEntity, nemesis, event.getSource(), event.getAmount());
				break;
			case FIREBALL:
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
				Allergy.onHurt(event, nemesis, trait.level);
			}
		}
	}
}
