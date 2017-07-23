package net.torocraft.nemesissystem.traits.logic;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.torocraft.nemesissystem.traits.TraitHandler;
import net.torocraft.nemesissystem.util.BehaviorUtil;
import net.torocraft.nemesissystem.util.NemesisUtil;

public class Inferno {

	public static void onUpdate(EntityLiving entity, int level) {
		EntityLivingBase target = entity.getAttackTarget();

		if (target == null) {
			return;
		}

		if (!entity.getEntitySenses().canSee(target)) {
			return;
		}

		roastNearByPlayers((EntityCreature) entity, level);
	}

	public static void roastNearByPlayers(EntityCreature entity, int level) {
		World world = entity.world;
		int heatDistance = 8 + (4 * level);
		getVisiblePlayers(entity, world, heatDistance)
				.stream()
				.filter((EntityPlayer p) -> oneOutOf(6))
				.forEach((EntityPlayer p) -> roast(p, level));
	}

	private static void roast(Entity entity, int level) {
		entity.setFire(2 * level);
	}

	private static boolean oneOutOf(int chance) {
		return TraitHandler.rand.nextInt(chance) == 0;
	}

	private static List<EntityPlayer> getVisiblePlayers(EntityCreature entity, World world, int distance) {
		return world.getEntitiesWithinAABB(EntityPlayer.class, NemesisUtil.nearByBox(entity.getPosition(), distance),
				(EntityPlayer p) -> BehaviorUtil.canSee(entity, p)
		);
	}

}
