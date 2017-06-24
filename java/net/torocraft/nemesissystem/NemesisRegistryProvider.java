package net.torocraft.nemesissystem;

import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;

public class NemesisRegistryProvider {

	public static NemesisRegistry get(World world) {
		MapStorage storage = world.getMapStorage();
		NemesisRegistry instance = (NemesisRegistry) storage.getOrLoadData(NemesisRegistry.class, NemesisRegistry.NAME);
		if (instance == null) {
			instance = new NemesisRegistry();
			storage.setData(NemesisRegistry.NAME, instance);
		}
		//instance  world = world;
		return instance;
	}

}
