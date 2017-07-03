package net.torocraft.nemesissystem;

import java.util.Random;
import net.minecraft.block.BlockBush;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class SpawnUtil {

	public static final String NEMESIS_TAG = "torocraft_nemesis";

	/**
	 * Convert the provided entity into the given nemesis
	 *
	 * @param entity the entity to be converted into a nemesis
	 * @param nemesis the nemesis to create
	 */
	public static void convert(Entity entity, Nemesis nemesis) {

		if (!(entity instanceof EntityLivingBase)) {
			return;
		}

		decorateEntity((EntityLivingBase) entity, nemesis);

	}

	private static void decorateEntity(EntityLivingBase entity, Nemesis nemesis) {

		entity.addTag(NEMESIS_TAG);

		entity.setCustomNameTag(nemesis.getName());

		// TODO add armor

		ItemStack helmet = nemesis.getArmorInventory().get(EntityEquipmentSlot.HEAD.getIndex());

		System.out.println("adding helmet: " + helmet);

		entity.setItemStackToSlot(EntityEquipmentSlot.HEAD, helmet);
		entity.setItemStackToSlot(EntityEquipmentSlot.CHEST, nemesis.getArmorInventory().get(EntityEquipmentSlot.CHEST.getIndex()));
		entity.setItemStackToSlot(EntityEquipmentSlot.LEGS, nemesis.getArmorInventory().get(EntityEquipmentSlot.LEGS.getIndex()));
		entity.setItemStackToSlot(EntityEquipmentSlot.FEET, nemesis.getArmorInventory().get(EntityEquipmentSlot.FEET.getIndex()));


		entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, nemesis.getHandInventory().get(EntityEquipmentSlot.MAINHAND.getIndex()));
		entity.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, nemesis.getHandInventory().get(EntityEquipmentSlot.OFFHAND.getIndex()));

		/*
	generic.maxHealth
		generic.knockbackResistance
		generic.movementSpeed
		generic.armor
		generic.armorToughness
		generic.followRange
		 */

		for (IAttributeInstance attribute : entity.getAttributeMap().getAllAttributes()) {
			if (attribute.getAttribute() == SharedMonsterAttributes.ATTACK_DAMAGE) {
				System.out.println("Boosting ATTACK_DAMAGE");
				attribute.setBaseValue(attribute.getAttributeValue() * 2);
			}

			if (attribute.getAttribute() == SharedMonsterAttributes.ATTACK_SPEED) {
				System.out.println("Boosting ATTACK_SPEED");
				attribute.setBaseValue(attribute.getAttributeValue() * 2);
			}

			if (attribute.getAttribute() == SharedMonsterAttributes.MOVEMENT_SPEED) {
				System.out.println("Boosting MOVEMENT_SPEED");
				attribute.setBaseValue(attribute.getAttributeValue() * 2);
			}

			if (attribute.getAttribute() == SharedMonsterAttributes.MAX_HEALTH) {
				System.out.println("Boosting MAX_HEALTH");
				attribute.setBaseValue(attribute.getAttributeValue() * 5);
				entity.setHealth(entity.getMaxHealth());
			}
		}
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

	/**
	 * TODO: WIP
	 *
	 * find the closest suitable spawn location to the given position within the provided radius.
	 *
	 * @return the suitable spawn position chosen
	 */
	@Deprecated
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

	/**
	 * TODO: WIP
	 */
	@Deprecated
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
