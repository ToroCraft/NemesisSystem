package net.torocraft.nemesissystem.entities;

import net.torocraft.nemesissystem.entities.zombie.EntityZombieNemesis;
import net.torocraft.nemesissystem.entities.zombie.RenderZombieNemesis;

public class Entities {
	public static void init() {
		int entityId = 0;
		EntityZombieNemesis.init(entityId++);
	}

	public static void registerRenders() {
		RenderZombieNemesis.init();
	}
}
