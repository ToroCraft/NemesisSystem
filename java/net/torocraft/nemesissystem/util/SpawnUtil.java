package net.torocraft.nemesissystem.util;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class SpawnUtil {

	public static void spawn(World world, String mob, BlockPos pos, int spawnRadius) {
		Entity entity = getEntityForId(world, mob);
		if (!(entity instanceof EntityCreature)) {
			return;
		}
		spawnEntityLiving(world, (EntityCreature) entity, pos, spawnRadius);
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

	public static boolean spawnEntityLiving(World world, EntityCreature entity, BlockPos pos, int spawnRadius) {
		double x = pos.getX() + 0.5D;
		double y = pos.getY();
		double z = pos.getZ() + 0.5D;
		entity.setLocationAndAngles(x, y, z, MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
		entity.rotationYawHead = entity.rotationYaw;
		entity.renderYawOffset = entity.rotationYaw;
		entity.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity)), (IEntityLivingData) null);
		entity.enablePersistence();
		findAndSetSuitableSpawnLocation(entity, pos, spawnRadius);
		world.spawnEntity(entity);
		entity.playLivingSound();
		return true;
	}

	/**
	 * find the closest suitable spawn location to the given position within the provided radius and set it to the entity.
	 *
	 * @return false if no location was found
	 */
	public static boolean findAndSetSuitableSpawnLocation(EntityCreature entity, BlockPos around, int spawnRadius) {
		Random rand = entity.getRNG();

		if (spawnRadius < 1) {
			return false;
		}

		int degrees, distance, x, z;

		BlockPos pos = null;

		for (int attempt = 0; attempt < 10; attempt++) {
			distance = rand.nextInt(spawnRadius);
			degrees = rand.nextInt(360);
			x = distance * (int) Math.round(Math.cos(Math.toRadians(degrees)));
			z = distance * (int) Math.round(Math.sin(Math.toRadians(degrees)));
			if(verticalScan(entity, spawnRadius, around.add(x, 0, z))){
				return true;
			}
		}

		return false;
	}

	private static boolean verticalScan(EntityCreature entity, int radius, BlockPos posIn) {

		if (setAndCheckSpawnPosition(entity, posIn)) {
			return true;
		}

		BlockPos scanUp = new BlockPos(posIn);
		BlockPos scanDown = new BlockPos(posIn);

		for(int i = 0; i < radius; i++){

			scanUp.up();

			if (setAndCheckSpawnPosition(entity, scanUp)) {
				return true;
			}

			scanDown.down();

			if (setAndCheckSpawnPosition(entity, scanDown)) {
				return true;
			}

		}

		return false;
	}

	private static boolean setAndCheckSpawnPosition(EntityCreature entity, BlockPos posIn) {
		entity.setPosition(posIn.getX() + 0.5, posIn.getY(), posIn.getZ() + 0.5);
		return entity.getCanSpawnHere();
	}


}
