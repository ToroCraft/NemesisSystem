package net.torocraft.nemesissystem.traits.logic;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.registry.Nemesis;

public class Summon {
	public static void handleSummonTraitUpdate(EntityLiving entity, Nemesis nemesis) {
		World world = entity.world;
		Random rand = entity.getRNG();

		EntityLivingBase target = entity.getAttackTarget();

		if (target == null) {
			return;
		}

		if (!entity.getEntitySenses().canSee(target)) {
			return;
		}

		if (rand.nextInt(3) != 0) {
			return;
		}

		int summonedCount = entity.world.getEntitiesWithinAABB(EntityMob.class, around(entity.getPosition(), 40), Summon::isSummonedMob).size();

		if (summonedCount > (nemesis.getLevel() * 2)) {
			return;
		}

		int roll = rand.nextInt(100);

		EntityMob mob;

		if (roll < 45) {
			mob = new EntitySkeleton(world);
		} else if (roll < 90) {
			mob = new EntityZombie(world);
		} else {
			mob = new EntityWitch(world);
		}

		mob.setPosition(entity.posX, entity.posY, entity.posZ);
		mob.addTag(NemesisSystem.TAG_SUMMONED_MOB);
		world.spawnEntity(mob);
	}


	private static boolean isSummonedMob(Entity e) {
		return e.getTags().contains(NemesisSystem.TAG_SUMMONED_MOB);
	}


	private static AxisAlignedBB around(BlockPos pos, int radius) {
		return new AxisAlignedBB(pos).grow(radius, radius, radius);
	}

}
