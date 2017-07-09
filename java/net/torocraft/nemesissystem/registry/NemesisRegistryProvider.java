package net.torocraft.nemesissystem.registry;

import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;

public class NemesisRegistryProvider {

	public static NemesisRegistry get(World world) {
		MapStorage storage = world.getPerWorldStorage();
		NemesisRegistry instance = (NemesisRegistry) storage.getOrLoadData(NemesisRegistry.class, NemesisRegistry.NAME);
		if (instance == null) {
			instance = new NemesisRegistry(world);
			storage.setData(NemesisRegistry.NAME, instance);
		}
		return instance;
	}

}
