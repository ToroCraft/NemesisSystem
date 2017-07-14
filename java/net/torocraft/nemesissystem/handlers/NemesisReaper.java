package net.torocraft.nemesissystem.handlers;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.torocraft.nemesissystem.registry.INemesisRegistry;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.registry.NemesisRegistryProvider;

public class NemesisReaper {

	private static final long MAX_UNLOAD_TIME = 200;

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new NemesisReaper());
	}

	@SubscribeEvent
	public void cleanUp(WorldTickEvent event) {
		if (event.world.getTotalWorldTime() % 20 != 0) {
			return;
		}
		despawnLogic(event, NemesisRegistryProvider.get(event.world));
	}

	private void despawnLogic(WorldTickEvent event, INemesisRegistry registry) {
		registry.list().forEach((Nemesis nemesis) -> {
			if (nemesis.isSpawned()) {
				despawnNemesis(event.world, registry, nemesis);
				unloadNemesis(event.world, registry, nemesis);
			}else if(nemesis.isLoaded()){
				System.out.println("Strange, the nemesis loaded but but not spawned, IMPOSSIBLE!");
				nemesis.setUnloaded(event.world.getTotalWorldTime());
			}
		});
	}

	private void despawnNemesis(World world, INemesisRegistry registry, Nemesis nemesis) {
		if (shouldBeDespawned(world, nemesis)) {
			System.out.println(nemesis.getNameAndTitle() + " is moving on, despawning now");
			nemesis.setSpawned(null);
			registry.update(nemesis);
		}
	}

	private boolean shouldBeDespawned(World world, Nemesis nemesis) {
		if(nemesis.isLoaded()) return false;
		return (world.getTotalWorldTime() - nemesis.getUnloaded()) > MAX_UNLOAD_TIME;
	}

	private void unloadNemesis(World world, INemesisRegistry registry, Nemesis nemesis) {
		Entity entity = world.getEntityByID(nemesis.getSpawned());
		if (nemesis.isLoaded() && entity == null) {
			System.out.println("Entity is not found for nemesis, marking as unloaded");
			nemesis.setUnloaded(world.getTotalWorldTime());
			registry.update(nemesis);
		}else if (!nemesis.isLoaded() && entity != null) {
			System.out.println("Entity found for nemesis, marking as loaded");
			nemesis.setUnloaded(null);
			registry.update(nemesis);
		}
	}

}
