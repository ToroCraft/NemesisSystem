package net.torocraft.nemesissystem;

import java.util.Random;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class NemesisBuilder {

	private static Random rand = new Random();

	private static final Item[] WEAPONS = {
			Items.STONE_SHOVEL, Items.STONE_AXE, Items.STONE_HOE, Items.STONE_PICKAXE, Items.STONE_SWORD,
			Items.GOLDEN_SHOVEL, Items.GOLDEN_AXE, Items.GOLDEN_HOE, Items.GOLDEN_PICKAXE, Items.GOLDEN_SWORD,
			Items.IRON_SHOVEL, Items.IRON_AXE, Items.IRON_HOE, Items.IRON_PICKAXE, Items.IRON_SWORD,
			Items.DIAMOND_SHOVEL, Items.DIAMOND_AXE, Items.DIAMOND_HOE, Items.DIAMOND_PICKAXE, Items.DIAMOND_SWORD,
			Items.SHIELD, Items.BOW
	};

	private static final Item[] HELMETS = {
			Items.LEATHER_HELMET,
			Items.GOLDEN_HELMET,
			Items.IRON_HELMET,
			Items.DIAMOND_HELMET,
	};

	private static final Item[] CHEST_PLATES = {
			Items.LEATHER_CHESTPLATE,
			Items.GOLDEN_CHESTPLATE,
			Items.IRON_CHESTPLATE,
			Items.DIAMOND_CHESTPLATE,
	};

	private static final Item[] LEGGINGS = {
			Items.LEATHER_LEGGINGS,
			Items.GOLDEN_LEGGINGS,
			Items.IRON_LEGGINGS,
			Items.DIAMOND_LEGGINGS,
	};

	private static final Item[] BOOTS = {
			Items.LEATHER_BOOTS,
			Items.GOLDEN_BOOTS,
			Items.IRON_BOOTS,
			Items.DIAMOND_BOOTS,
	};

	public static Nemesis build(String mob, int level, int x, int z) {
		Nemesis nemesis = new Nemesis();

		//TODO random name
		nemesis.setName(NameBuilder.build());

		nemesis.setLevel(level);
		nemesis.setMob(mob);
		nemesis.setX(x);
		nemesis.setZ(z);

		//TODO random stats

		//TODO random name

		//TODO random armor/weapons
		nemesis.getHandInventory().set(0, new ItemStack(WEAPONS[rand.nextInt(WEAPONS.length)]));
		nemesis.getHandInventory().set(1, new ItemStack(WEAPONS[rand.nextInt(WEAPONS.length)]));

		nemesis.getArmorInventory().set(EntityEquipmentSlot.HEAD.getIndex(), new ItemStack(HELMETS[rand.nextInt(HELMETS.length)]));
		nemesis.getArmorInventory().set(EntityEquipmentSlot.CHEST.getIndex(), new ItemStack(CHEST_PLATES[rand.nextInt(CHEST_PLATES.length)]));
		nemesis.getArmorInventory().set(EntityEquipmentSlot.LEGS.getIndex(), new ItemStack(LEGGINGS[rand.nextInt(LEGGINGS.length)]));
		nemesis.getArmorInventory().set(EntityEquipmentSlot.FEET.getIndex(), new ItemStack(BOOTS[rand.nextInt(BOOTS.length)]));

		return nemesis;
	}

}
