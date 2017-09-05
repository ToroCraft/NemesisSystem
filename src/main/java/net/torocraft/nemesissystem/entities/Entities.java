package net.torocraft.nemesissystem.entities;

import net.minecraft.entity.monster.EntityZombieVillager;
import net.torocraft.nemesissystem.entities.Stray.EntityStrayNemesis;
import net.torocraft.nemesissystem.entities.Stray.RenderStrayNemesis;
import net.torocraft.nemesissystem.entities.husk.EntityHuskNemesis;
import net.torocraft.nemesissystem.entities.husk.RenderHuskNemesis;
import net.torocraft.nemesissystem.entities.pigZombie.EntityPigZombieNemesis;
import net.torocraft.nemesissystem.entities.pigZombie.RenderPigZombieNemesis;
import net.torocraft.nemesissystem.entities.skeleton.EntitySkeletonNemesis;
import net.torocraft.nemesissystem.entities.skeleton.RenderSkeletonNemesis;
import net.torocraft.nemesissystem.entities.zombie.EntityZombieNemesis;
import net.torocraft.nemesissystem.entities.zombie.RenderZombieNemesis;
import net.torocraft.nemesissystem.entities.zombieVillager.EntityZombieVillagerNemesis;
import net.torocraft.nemesissystem.entities.zombieVillager.RenderZombieVillagerNemesis;

public class Entities {

	public static void init() {
		int entityId = 0;
		EntityZombieNemesis.init(entityId++);
		EntityPigZombieNemesis.init(entityId++);
		EntityZombieVillagerNemesis.init(entityId++);
		EntityHuskNemesis.init(entityId++);
		EntitySkeletonNemesis.init(entityId++);
		EntityStrayNemesis.init(entityId++);
	}

	public static void registerRenders() {
		RenderZombieNemesis.init();
		RenderPigZombieNemesis.init();
		RenderZombieVillagerNemesis.init();
		RenderHuskNemesis.init();
		RenderSkeletonNemesis.init();
		RenderStrayNemesis.init();
	}
}
