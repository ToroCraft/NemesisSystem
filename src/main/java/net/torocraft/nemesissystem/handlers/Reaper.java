package net.torocraft.nemesissystem.handlers;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.torocraft.nemesissystem.registry.INemesisRegistry;
import net.torocraft.nemesissystem.registry.NemesisEntry;
import net.torocraft.nemesissystem.registry.NemesisRegistryProvider;
import net.torocraft.nemesissystem.util.NemesisUtil;

public class Reaper {

	private static final long MAX_UNLOAD_TIME = 1000;

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new Reaper());
	}

	@SubscribeEvent
	public void cleanUp(WorldTickEvent event) {
		if (event.world.getTotalWorldTime() % 20 != 0) {
			return;
		}
		despawnLogic(event, NemesisRegistryProvider.get(event.world));
	}

	private void despawnLogic(WorldTickEvent event, INemesisRegistry registry) {
		registry.list().forEach((NemesisEntry nemesis) -> {
			if (nemesis.isSpawned()) {
				if (shouldBeDespawned(event.world, nemesis)) {
					despawnNemesis(event.world, registry, nemesis);
				} else {
					unloadNemesis(event.world, registry, nemesis);
				}
			}
		});
	}

	private void despawnNemesis(World world, INemesisRegistry registry, NemesisEntry nemesis) {
		// TODO increase NemesisEntry level every time they spawn but are not killed
		Entity entity = world.getEntityByID(nemesis.getSpawned());
		if (entity != null) {
			entity.setDead();
			world.removeEntity(entity);
		}
		nemesis.setSpawned(null);
		registry.update(nemesis);
	}

	private boolean shouldBeDespawned(World world, NemesisEntry nemesis) {
		if (nemesis.isLoaded()) {
			return false;
		}
		return (world.getTotalWorldTime() - nemesis.getUnloaded()) > MAX_UNLOAD_TIME;
	}

	private void unloadNemesis(World world, INemesisRegistry registry, NemesisEntry nemesis) {
		Entity entity = world.getEntityByID(nemesis.getSpawned());

		if (nemesis.isLoaded() && entity == null) {
			nemesis.setUnloaded(world.getTotalWorldTime());
			registry.update(nemesis);
			return;
		}

		if (entity == null) {
			return;
		}

		boolean playersNear = NemesisUtil.findPlayersAround(world, entity.getPosition(), 100).size() > 0;

		if (nemesis.isLoaded() && !playersNear) {
			nemesis.setUnloaded(world.getTotalWorldTime());
			registry.update(nemesis);
			return;
		}

		if (!nemesis.isLoaded() && playersNear) {
			nemesis.setUnloaded(null);
			registry.update(nemesis);
			return;
		}
	}

}
