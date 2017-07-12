package net.torocraft.nemesissystem.util;

import java.util.*;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.registry.Nemesis.Trait;

public class NemesisBuilder {

	private static Random rand = new Random();

	private static final Item[] MELEE_WEAPONS = {
			Items.STONE_SHOVEL, Items.STONE_AXE, Items.STONE_HOE, Items.STONE_PICKAXE, Items.STONE_SWORD,
			Items.GOLDEN_SHOVEL, Items.GOLDEN_AXE, Items.GOLDEN_HOE, Items.GOLDEN_PICKAXE, Items.GOLDEN_SWORD,
			Items.IRON_SHOVEL, Items.IRON_AXE, Items.IRON_HOE, Items.IRON_PICKAXE, Items.IRON_SWORD,
			Items.DIAMOND_SHOVEL, Items.DIAMOND_AXE, Items.DIAMOND_HOE, Items.DIAMOND_PICKAXE, Items.DIAMOND_SWORD,
	};

	private static final Item[] HELMETS = {
			Items.LEATHER_HELMET,
			Items.GOLDEN_HELMET,
			Items.IRON_HELMET,
			Items.DIAMOND_HELMET,
			Items.CHAINMAIL_HELMET
	};

	private static final Item[] CHEST_PLATES = {
			Items.LEATHER_CHESTPLATE,
			Items.GOLDEN_CHESTPLATE,
			Items.IRON_CHESTPLATE,
			Items.DIAMOND_CHESTPLATE,
			Items.CHAINMAIL_CHESTPLATE
	};

	private static final Item[] LEGGINGS = {
			Items.LEATHER_LEGGINGS,
			Items.GOLDEN_LEGGINGS,
			Items.IRON_LEGGINGS,
			Items.DIAMOND_LEGGINGS,
			Items.CHAINMAIL_LEGGINGS
	};

	private static final Item[] BOOTS = {
			Items.LEATHER_BOOTS,
			Items.GOLDEN_BOOTS,
			Items.IRON_BOOTS,
			Items.DIAMOND_BOOTS,
			Items.CHAINMAIL_BOOTS
	};

	public static Nemesis build(String mob, int level, int x, int z) {
		Nemesis nemesis = new Nemesis();

		nemesis.setId(UUID.randomUUID());
		nemesis.setName(NameBuilder.build());
		// TODO pick a title that is not currently in use
		nemesis.setTitle(NameBuilder.TITLES[rand.nextInt(NameBuilder.TITLES.length)]);

		nemesis.setLevel(level);
		nemesis.setMob(mob);
		nemesis.setX(x);
		nemesis.setZ(z);

		nemesis.setTraits(new ArrayList<>());
		nemesis.getTraits().add(Trait.values()[rand.nextInt(Trait.values().length)]);

		nemesis.getHandInventory().set(0, new ItemStack(MELEE_WEAPONS[rand.nextInt(MELEE_WEAPONS.length)]));
		setOffhandItem(nemesis);
		setArmor(nemesis);
		NemesisUtil.enchantEquipment(nemesis);

		return nemesis;
	}

	private static void setArmor(Nemesis nemesis) {
		// TODO design armor sets
		nemesis.getArmorInventory().set(EntityEquipmentSlot.HEAD.getIndex(), new ItemStack(HELMETS[rand.nextInt(HELMETS.length)]));
		nemesis.getArmorInventory().set(EntityEquipmentSlot.CHEST.getIndex(), new ItemStack(CHEST_PLATES[rand.nextInt(CHEST_PLATES.length)]));
		nemesis.getArmorInventory().set(EntityEquipmentSlot.LEGS.getIndex(), new ItemStack(LEGGINGS[rand.nextInt(LEGGINGS.length)]));
		nemesis.getArmorInventory().set(EntityEquipmentSlot.FEET.getIndex(), new ItemStack(BOOTS[rand.nextInt(BOOTS.length)]));
	}

	private static void setOffhandItem(Nemesis nemesis) {
		ItemStack offhand;
		switch (nemesis.getTraits().get(0)) {
		case HEAT:
			offhand = new ItemStack(Blocks.TORCH);
			break;
		case ARROW:
			offhand = new ItemStack(Items.BOW);
			break;
		case POTION:
			offhand = new ItemStack(Items.POTIONITEM);
			break;
		case SUMMON:
			offhand = new ItemStack(Items.STICK);
			break;
		case REFLECT:
			offhand = ItemStack.EMPTY;
			break;
		case TELEPORT:
			offhand = new ItemStack(Items.ENDER_PEARL);
			break;
		case DOUBLE_MELEE:
			offhand = new ItemStack(MELEE_WEAPONS[rand.nextInt(MELEE_WEAPONS.length)]);
			break;
		default:
			offhand = ItemStack.EMPTY;
		}
		nemesis.getHandInventory().set(1, offhand);
	}

}
