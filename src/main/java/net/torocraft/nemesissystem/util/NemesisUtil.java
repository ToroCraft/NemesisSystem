package net.torocraft.nemesissystem.util;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.registry.NemesisRegistryProvider;

public class NemesisUtil {

	public static final Random rand = new Random();

	public static String getEntityType(Entity entityIn) {
		EntityEntry entry = EntityRegistry.getEntry(entityIn.getClass());
		if (entry == null) {
			return "";
		}
		return entry.getRegistryName().toString();
	}

	public static boolean isNemesisClassEntity(Entity entity) {
		// TODO blacklist

		// TODO whitelist

		return entity instanceof EntityMob;
	}

	public static BlockPos getRandomLocationAround(EntityCreature entity) {
		int distance = 1000 + entity.getRNG().nextInt(4000);
		int degrees = entity.getRNG().nextInt(360);
		int x = distance * (int) Math.round(Math.cos(Math.toRadians(degrees)));
		int z = distance * (int) Math.round(Math.sin(Math.toRadians(degrees)));
		BlockPos here = entity.getPosition();
		return new BlockPos(here.getX() + x, here.getY(), here.getZ() + z);
	}

	public static void enchantEquipment(Nemesis nemesis) {
		if (nemesis == null) {
			return;
		}
		enchantItems(nemesis.getArmorInventory());
		enchantItems(nemesis.getHandInventory());
	}

	public static void enchantItems(List<ItemStack> items) {
		if (items == null) {
			return;
		}
		for (ItemStack item : items) {
			if (rand.nextBoolean()) {
				enchantItem(item);
			}
		}
	}

	public static void enchantItem(ItemStack item) {
		if (item == null || item.isEmpty()) {
			return;
		}
		if (!improveEnchants(item)) {
			addNewEnchantment(item);
		}
	}

	private static boolean improveEnchants(ItemStack item) {
		boolean improved = false;
		Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(item);

		if (enchantments.isEmpty()) {
			return false;
		}

		for (Entry<Enchantment, Integer> enchant : enchantments.entrySet()) {
			if (shouldImproveEnchantment(rand, enchant.getKey(), enchant.getValue())) {
				enchantments.put(enchant.getKey(), enchant.getValue() + 1);
				improved = true;
			}
		}

		if (improved) {
			EnchantmentHelper.setEnchantments(enchantments, item);
		}

		return improved;
	}

	private static boolean shouldImproveEnchantment(Random rand, Enchantment enchantment, Integer level) {
		return level < enchantment.getMaxLevel() && rand.nextBoolean();
	}

	private static void addNewEnchantment(ItemStack item) {
		EnchantmentHelper.addRandomEnchantment(rand, item, 1, true);
		removeDuplicateEnchantments(item);
	}

	private static void removeDuplicateEnchantments(ItemStack item) {
		EnchantmentHelper.setEnchantments(EnchantmentHelper.getEnchantments(item), item);
	}

	public static boolean isBodyGuard(EntityLiving searchEntity, UUID id) {
		return searchEntity.getTags().contains(NemesisSystem.TAG_BODY_GUARD)
				&& id.equals(searchEntity.getEntityData().getUniqueId(NemesisSystem.NBT_NEMESIS_ID));
	}

	public static boolean isNemesis(EntityLiving searchEntity) {
		return searchEntity.getTags().contains(NemesisSystem.TAG_NEMESIS);
	}

	public static boolean isNemesis(EntityLiving searchEntity, UUID id) {
		return isNemesis(searchEntity) && id.equals(searchEntity.getEntityData().getUniqueId(NemesisSystem.NBT_NEMESIS_ID));
	}

	public static List<EntityCreature> findNemesisBodyGuards(World world, UUID id, BlockPos position) {
		int distance = 100;

		return world.getEntitiesWithinAABB(EntityCreature.class, nearByBox(position, distance),
				(EntityCreature searchEntity) -> isBodyGuard(searchEntity, id)
		);
	}

	public static EntityLiving findNemesisAround(World world, UUID id, BlockPos position) {
		int distance = 50;

		List<EntityLiving> entities = world.getEntitiesWithinAABB(EntityLiving.class, nearByBox(position, distance),
				(EntityLiving searchEntity) -> isNemesis(searchEntity, id)
		);

		if (entities.size() < 1) {
			return null;
		}

		return entities.get(0);
	}

	public static List<EntityPlayer> findPlayersAround(World world, BlockPos position, int distance) {
		return world.getEntitiesWithinAABB(EntityPlayer.class, nearByBox(position, distance));
	}

	public static AxisAlignedBB nearByBox(BlockPos position, int radius) {
		return new AxisAlignedBB(position).grow(radius, radius, radius);
	}

	public static Nemesis loadNemesisFromEntity(Entity nemesisEntity) {
		if (nemesisEntity == null) {
			return null;
		}
		UUID id = nemesisEntity.getEntityData().getUniqueId(NemesisSystem.NBT_NEMESIS_ID);
		return NemesisRegistryProvider.get(nemesisEntity.getEntityWorld()).getById(id);
	}

	public static String romanize(int i) {
		switch (i) {
		case 1:
			return "I";
		case 2:
			return "II";
		case 3:
			return "III";
		case 4:
			return "IV";
		case 5:
			return "V";
		case 6:
			return "VI";
		case 7:
			return "VII";
		case 8:
			return "VIII";
		case 9:
			return "IX";
		case 10:
			return "X";
		}
		return Integer.toString(i, 10);
	}
}
