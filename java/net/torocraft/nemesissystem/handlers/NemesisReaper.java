package net.torocraft.nemesissystem.handlers;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
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
	// [17:25:45] [Server thread/INFO] [STDOUT]: [net.torocraft.nemesissystem.handlers.SpawnHandler:handleSpawn:44]: spawn {torocraft_nemesis_idLeast:-8648079058090442312L,torocraft_nemesis_idMost:-8268956894229607991L}
	// [17:25:45] [Server thread/INFO] [STDOUT]: [net.torocraft.nemesissystem.handlers.SpawnHandler:handleRespawnOfNemesis:93]: Uphita the Shaman has left the battlefield
	// [17:25:45] [Server thread/INFO] [STDOUT]: [net.torocraft.nemesissystem.handlers.SpawnHandler:handleSpawn:44]: spawn {torocraft_nemesis_idLeast:-5359124288490232800L,torocraft_nemesis_idMost:-1531997445234734227L}
	// [17:25:45] [Server thread/INFO] [STDOUT]: [net.torocraft.nemesissystem.handlers.SpawnHandler:handleRespawnOfNemesis:99]: Aclsea the Bowmaster has not left the battle grounds yet!

	@SubscribeEvent
	public void cleanUp(WorldTickEvent event) {
		if (event.world.getTotalWorldTime() % 20 != 0) {
			return;
		}
		//System.out.println("server tick " + event.world.getTotalWorldTime());
		despawnLogic(event, NemesisRegistryProvider.get(event.world));
	}

	private void despawnLogic(WorldTickEvent event, INemesisRegistry registry) {
		registry.list().forEach((Nemesis nemesis) -> {
			if (nemesis.isSpawned()) {
				System.out.println("checking spawn status of " + nemesis.getNameAndTitle());
				despawnNemesis(event.world, registry, nemesis);
				unloadNemesis(event.world, registry, nemesis);
			}else if(nemesis.isLoaded()){
				System.out.println("Strange, the nemesis loaded but but not spawned, IMPOSSIBLE!  " + nemesis.getNameAndTitle());
				nemesis.setUnloaded(event.world.getTotalWorldTime());
				registry.update(nemesis);
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
		if(entity != null){
			System.out.println("Entity Found for " + nemesis.getNameAndTitle());
		}
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
