package net.torocraft.nemesissystem;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.SpecialSpawn;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SpawnHandler {

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new SpawnHandler());
	}

	@SubscribeEvent
	public void handleSpawn(SpecialSpawn event) {
		System.out.println("Spawn: " + event.getEntity().getName() + " at " + event.getEntity().getPosition());
		SpawnUtil.convert(event.getEntity(), getNemesisForSpawn(event));
	}

	private Nemesis getNemesisForSpawn(SpecialSpawn event) {
		//TODO
		Nemesis nemesis = new Nemesis();
		nemesis.setName("TEST MOB");
		return nemesis;
	}
}
