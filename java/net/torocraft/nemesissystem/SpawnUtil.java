package net.torocraft.nemesissystem;

import java.util.Random;
import net.minecraft.block.BlockBush;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class SpawnUtil {

	public static void replace(Entity entity, Nemesis nemesis) {
		Entity replacementEntity = getEntityForId(entity.world, nemesis.getMob());
		if (!(entity instanceof EntityLivingBase)) {
			return;
		}
		spawnEntityLiving(entity.world, (EntityLiving) entity, entity.getPosition());
	}

	public static void spawn(World world, Nemesis nemesis, BlockPos pos) {
		// TODO add nemesis tags
		spawn(world, nemesis.getMob(), pos, 1);
	}

	public static void spawn(World world, String mob, BlockPos pos, int spawnRadius) {
		Entity entity = getEntityForId(world, mob);
		if (!(entity instanceof EntityLivingBase)) {
			return;
		}
		BlockPos spawnLocation = findSuitableSpawnLocation(world, pos, spawnRadius);
		spawnEntityLiving(world, (EntityLiving) entity, spawnLocation);
	}

	private static Entity getEntityForId(World world, String entityID) {
		String[] parts = entityID.split(":");
		String domain, entityName;
		if (parts.length == 2) {
			domain = parts[0];
			entityName = parts[1];
		} else {
			domain = "minecraft";
			entityName = entityID;
		}
		return EntityList.createEntityByIDFromName(new ResourceLocation(domain, entityName), world);
	}

	private static boolean spawnEntityLiving(World world, EntityLiving entity, BlockPos pos) {

		double x = pos.getX() + 0.5D;
		double y = pos.getY();
		double z = pos.getZ() + 0.5D;

		entity.setLocationAndAngles(x, y, z, MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
		entity.rotationYawHead = entity.rotationYaw;
		entity.renderYawOffset = entity.rotationYaw;
		entity.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity)), (IEntityLivingData) null);

		entity.enablePersistence();

		/*

		TODO

		if (entityTags != null) {
			for (String tag : entityTags) {
				entity.addTag(tag);
			}
		}

		if (helmet != null) {
			try {
				entity.setItemStackToSlot(EntityEquipmentSlot.HEAD, helmet);
			} catch (Exception e) {
				System.out.println("failed to add helment: " + e.getMessage());
			}
		}

		if (boots != null) {
			try {
				entity.setItemStackToSlot(EntityEquipmentSlot.FEET, boots);
			} catch (Exception e) {
				System.out.println("failed to add boots: " + e.getMessage());
			}
		}

		if (chestplate != null) {
			try {
				entity.setItemStackToSlot(EntityEquipmentSlot.CHEST, chestplate);
			} catch (Exception e) {
				System.out.println("failed to add chestplate: " + e.getMessage());
			}
		}

		if (leggings != null) {
			try {
				entity.setItemStackToSlot(EntityEquipmentSlot.LEGS, leggings);
			} catch (Exception e) {
				System.out.println("failed to add leggings: " + e.getMessage());
			}
		}
		*/

		world.spawnEntity(entity);
		entity.playLivingSound();
		return true;
	}

	private static BlockPos findSuitableSpawnLocation(World world, BlockPos posIn, int spawnRadius) {
		Random rand = world.rand;

		if (spawnRadius < 1) {
			return posIn;
		}

		int degrees, distance, x, z;

		BlockPos pos = null;

		for (int i = 0; i < 10; i++) {
			distance = rand.nextInt(spawnRadius);
			degrees = rand.nextInt(360);
			x = distance * (int) Math.round(Math.cos(Math.toRadians(degrees)));
			z = distance * (int) Math.round(Math.sin(Math.toRadians(degrees)));
			pos = findSurface(world, posIn, x, z);
			if (pos != null) {
				return pos;
			}
		}
		return pos;
	}

	private static BlockPos findSurface(World world, BlockPos posIn, int x, int z) {
		BlockPos pos = posIn.add(x, -3, z);
		IBlockState blockState;
		int yOffset = 0;

		boolean groundFound = false;
		boolean[] airSpace = { false, false };

		while (yOffset < 14) {
			blockState = world.getBlockState(pos);
			if (isGroundBlock(blockState)) {
				groundFound = true;
				airSpace[0] = false;
				airSpace[1] = false;

			} else if (airSpace[0] && airSpace[1] && groundFound) {
				return pos.down();

			} else if (Blocks.AIR.equals(blockState.getBlock())) {
				if (airSpace[0]) {
					airSpace[1] = true;
				} else {
					airSpace[0] = true;
				}

			}

			pos = pos.up();
			yOffset++;
		}
		return null;
	}

	private static boolean isLiquid(IBlockState blockState) {
		return blockState.getBlock() == Blocks.WATER || blockState.getBlock() == Blocks.LAVA;
	}

	private static boolean isGroundBlock(IBlockState blockState) {
		if (blockState.getBlock() == Blocks.LEAVES || blockState.getBlock() == Blocks.LEAVES2 || blockState.getBlock() == Blocks.LOG || blockState
				.getBlock() instanceof BlockBush) {
			return false;
		}
		return blockState.isOpaqueCube();
	}

}
