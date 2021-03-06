package net.torocraft.nemesissystem.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.torocraft.nemesissystem.NemesisConfig;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.registry.NemesisEntry;
import net.torocraft.nemesissystem.registry.NemesisRegistryProvider;
import net.torocraft.torotraits.api.SpawnApi;

public class NemesisUtil {

	public static final Random rand = new Random();

	public static boolean isNemesisClassEntity(Entity entity) {
		String entityType = SpawnApi.getEntityString(entity);
		return Arrays.asList(NemesisConfig.MOB_WHITELIST).contains(entityType);
	}

	public static BlockPos getRandomNemesisLocation(World world, BlockPos around) {
		BlockPos pos = null;
		for(int attempt = 0; attempt < 20; attempt++){
			pos = getRandomLocation(around);
			if(isValidNemesisLocation(world, pos)){
				return pos;
			}
		}
		return pos;
	}

	private static boolean isValidNemesisLocation(World world, BlockPos pos) {
		BlockPos scan = new BlockPos(pos.getX(), world.getActualHeight(), pos.getZ());
		while(scan.getY() > 0){
			if (!world.isAirBlock(scan)) {
				return world.getBlockState(scan).isOpaqueCube();
			}
			scan = scan.down();
		}
		return false;
	}

	public static BlockPos getRandomLocation(BlockPos around) {
		int fifthRadius = NemesisConfig.NEMESIS_SETTLE_RADIUS / 5;
		int distance = fifthRadius + rand.nextInt(fifthRadius * 4);
		double radians = Math.toRadians(rand.nextDouble() * 360);
		int x = (int) (distance * Math.cos(radians));
		int z = (int) (distance * Math.sin(radians));
		BlockPos out = new BlockPos(around.getX() + x, around.getY(), around.getZ() + z);
		return out;
	}

	public static void enchantEquipment(NemesisEntry nemesis) {
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

	public static EntityLiving findNemesisAround(World world, UUID id, BlockPos position, int distance) {
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

	public static NemesisEntry loadNemesisFromEntity(Entity nemesisEntity) {
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
