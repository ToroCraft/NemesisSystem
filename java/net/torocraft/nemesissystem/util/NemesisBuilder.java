package net.torocraft.nemesissystem.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.registry.NemesisRegistryProvider;
import net.torocraft.nemesissystem.traits.Trait;

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

	public static Nemesis build(World world, String mob, boolean isChild, int dimension, int level, int x, int z) {
		Nemesis nemesis = new Nemesis();

		nemesis.setId(UUID.randomUUID());
		nemesis.setName(getUniqueName(world));
		nemesis.setTitle(getUniqueTitle(world));

		nemesis.setLevel(level);
		nemesis.setMob(mob);
		nemesis.setChild(isChild ? 1 : 0);
		nemesis.setX(x);
		nemesis.setZ(z);
		nemesis.setDimension(dimension);

		nemesis.setTraits(new ArrayList<>());

		/*
		 * add a strength
		 */
		nemesis.getTraits().add(new Trait(Trait.STRENGTHS[rand.nextInt(Trait.STRENGTHS.length)], 1));
		//nemesis.getTraits().add(Trait.TELEPORT);

		/*
		 * add a weakness
		 */
		nemesis.getTraits().add(new Trait(Trait.WEAKNESSES[rand.nextInt(Trait.WEAKNESSES.length)], 1));
		//nemesis.getWeaknesses().add(Weakness.GREEDY);

		nemesis.getHandInventory().set(0, new ItemStack(MELEE_WEAPONS[rand.nextInt(MELEE_WEAPONS.length)]));
		setOffhandItem(nemesis);
		setArmor(nemesis);
		NemesisUtil.enchantEquipment(nemesis);

		return nemesis;
	}

	private static String getUniqueTitle(World world) {
		List<Nemesis> nemeses = NemesisRegistryProvider.get(world).list();
		String title = getRandomTitle();
		while (!isUniqueTitle(title, nemeses)) {
			title = getRandomTitle();
		}
		return title;
	}

	private static String getRandomTitle() {
		return NameBuilder.TITLES[rand.nextInt(NameBuilder.TITLES.length)];
	}

	private static boolean isUniqueTitle(String title, List<Nemesis> nemeses) {
		for (Nemesis nemesis : nemeses) {
			if (nemesis.getTitle().equals(title)) {
				return false;
			}
		}
		return true;
	}

	private static String getUniqueName(World world) {
		String name = NameBuilder.build();
		if (NemesisRegistryProvider.get(world).getByName(name) == null) {
			return name;
		}
		return getUniqueName(world);
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
		switch (nemesis.getTraits().get(0).type) {
		case FIRE:
			offhand = new ItemStack(Items.LAVA_BUCKET);
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
			// TODO color shield
			offhand = new ItemStack(Items.SHIELD);
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
