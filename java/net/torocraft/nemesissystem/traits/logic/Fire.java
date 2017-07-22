package net.torocraft.nemesissystem.traits.logic;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.torocraft.nemesissystem.registry.Nemesis;

public class Fire {

	public static void onUpdate(EntityLiving entity, Nemesis nemesis) {
		World world = entity.world;
		Random rand = entity.getRNG();

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

		// TODO what is this?
		world.playEvent(null, 1015, new BlockPos(entity), 0);

		double d1 = 4.0D;
		Vec3d vec3d = entity.getLook(1.0F);
		double d2 = target.posX - (entity.posX + vec3d.x * 4.0D);
		double d3 = target.getEntityBoundingBox().minY + (double) (target.height / 2.0F) - (0.5D + entity.posY + (double) (entity.height / 2.0F));
		double d4 = target.posZ - (entity.posZ + vec3d.z * 4.0D);

		// TODO and this?
		world.playEvent(null, 1016, new BlockPos(entity), 0);

		EntityLargeFireball entitylargefireball = new EntityLargeFireball(world, entity, d2, d3, d4);
		entitylargefireball.explosionPower = 1;
		entitylargefireball.posX = entity.posX + vec3d.x * 4.0D;
		entitylargefireball.posY = entity.posY + (double) (entity.height / 2.0F) + 0.5D;
		entitylargefireball.posZ = entity.posZ + vec3d.z * 4.0D;
		world.spawnEntity(entitylargefireball);
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
