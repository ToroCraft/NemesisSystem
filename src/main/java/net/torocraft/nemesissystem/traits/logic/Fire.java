package net.torocraft.nemesissystem.traits.logic;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.torocraft.nemesissystem.registry.Nemesis;

public class Fire {

	public static void onUpdate(EntityLiving entity, int level) {
		World world = entity.world;

		if (world.getTotalWorldTime() % 40 != 0) {
			return;
		}

		EntityLivingBase target = entity.getAttackTarget();

		if (target == null) {
			return;
		}

		if (!entity.getEntitySenses().canSee(target)) {
			return;
		}

		if (level > 3) {
			bigFireball(entity, world, target);
		} else {
			for (int i = 0; i < MathHelper.clamp(level, 1, 3); i++) {
				smallFireball(entity, target);
			}
		}
	}

	private static void smallFireball(EntityLivingBase entity, EntityLivingBase target) {
		double distanceSq = entity.getDistanceSqToEntity(target);

		double dx = target.posX - entity.posX;
		double dy = target.getEntityBoundingBox().minY + (double) (target.height / 2.0F) - (entity.posY + (double) (entity.height / 2.0F));
		double dz = target.posZ - entity.posZ;

		float f = MathHelper.sqrt(MathHelper.sqrt(distanceSq)) * 0.5F;

		EntitySmallFireball entitysmallfireball = new EntitySmallFireball(entity.world, entity, dx + entity.getRNG().nextGaussian() * (double) f, dy,
				dz + entity.getRNG().nextGaussian() * (double) f);
		entitysmallfireball.posY = entity.posY + (double) (entity.height / 2.0F) + 0.5D;
		entity.world.spawnEntity(entitysmallfireball);
		entity.world.playEvent(null, 1018, new BlockPos((int)entity.posX, (int)entity.posY, (int)entity.posZ), 0);
	}

	private static void bigFireball(EntityLivingBase entity, World world, EntityLivingBase target) {
		Vec3d vec3d = entity.getLook(1.0F);
		double d2 = target.posX - (entity.posX + vec3d.x * 4.0D);
		double d3 = target.getEntityBoundingBox().minY + (double) (target.height / 2.0F) - (0.5D + entity.posY + (double) (entity.height / 2.0F));
		double d4 = target.posZ - (entity.posZ + vec3d.z * 4.0D);

		EntityLargeFireball fireball = new EntityLargeFireball(world, entity, d2, d3, d4);
		fireball.explosionPower = 1;
		fireball.posX = entity.posX + vec3d.x * 4.0D;
		fireball.posY = entity.posY + (double) (entity.height / 2.0F) + 0.5D;
		fireball.posZ = entity.posZ + vec3d.z * 4.0D;
		world.spawnEntity(fireball);
		world.playEvent(null, 1016, new BlockPos(entity), 0);
	}

	public static void handleHeatTraitUpdate(EntityLiving entity, Nemesis nemesis) {
		World world = entity.world;
		int heatDistance = 8;

		List<EntityPlayer> playersToCook = world
				.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(entity.getPosition()).grow(heatDistance, heatDistance, heatDistance));
		for (EntityPlayer player : playersToCook) {
			if (entity.getEntitySenses().canSee(player)) {
				player.setFire(10);
			}
		}
	}
}
