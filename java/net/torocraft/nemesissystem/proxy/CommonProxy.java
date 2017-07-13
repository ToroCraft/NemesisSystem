package net.torocraft.nemesissystem.proxy;

import java.io.File;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.torocraft.nemesissystem.NemesisConfig;
import net.torocraft.nemesissystem.events.NemesisEventHandlers;
import net.torocraft.nemesissystem.handlers.AttackHandler;
import net.torocraft.nemesissystem.handlers.ChunkLoadHandler;
import net.torocraft.nemesissystem.handlers.DeathHandler;
import net.torocraft.nemesissystem.handlers.SetAttackTargetHandler;
import net.torocraft.nemesissystem.handlers.SpawnHandler;
import net.torocraft.nemesissystem.handlers.UpdateHandler;
import net.torocraft.nemesissystem.network.MessageHealAnimation;
import net.torocraft.nemesissystem.network.MessageOpenNemesisGui;
import net.torocraft.nemesissystem.network.MessageOpenNemesisGuiRequest;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent e) {
		initConfig(e.getSuggestedConfigurationFile());
		MinecraftForge.EVENT_BUS.register(new NemesisEventHandlers());
	}

	public void init(FMLInitializationEvent e) {
		SpawnHandler.init();
		UpdateHandler.init();
		AttackHandler.init();
		ChunkLoadHandler.init();
		DeathHandler.init();
		SetAttackTargetHandler.init();
		initPackets();
	}

	public void postInit(FMLPostInitializationEvent e) {

	}

	private void initConfig(File configFile) {
		NemesisConfig.init(configFile);
		MinecraftForge.EVENT_BUS.register(new NemesisConfig());
	}

	private void initPackets() {
		int packetId = 0;
		MessageOpenNemesisGui.init(packetId++);
		MessageOpenNemesisGuiRequest.init(packetId++);
		MessageHealAnimation.init(packetId++);
	}

}
