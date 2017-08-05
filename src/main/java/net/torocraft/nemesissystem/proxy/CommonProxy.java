package net.torocraft.nemesissystem.proxy;

import java.io.File;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.torocraft.nemesissystem.NemesisConfig;
import net.torocraft.nemesissystem.handlers.NemesisHandler;
import net.torocraft.nemesissystem.handlers.*;
import net.torocraft.nemesissystem.network.MessageHealAnimation;
import net.torocraft.nemesissystem.network.MessageOpenNemesisDetailsGui;
import net.torocraft.nemesissystem.network.MessageOpenNemesisDetailsGuiRequest;
import net.torocraft.nemesissystem.network.MessageOpenNemesisGui;
import net.torocraft.nemesissystem.network.MessageOpenNemesisGuiRequest;
import net.torocraft.nemesissystem.network.MessageReflectDamageAnimation;
import net.torocraft.nemesissystem.network.MessageSyncNemesis;
import net.torocraft.nemesissystem.network.MessageSyncNemesisRequest;
import net.torocraft.nemesissystem.network.MessageWorshipAnimation;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent e) {
		initConfig(e.getSuggestedConfigurationFile());
		MinecraftForge.EVENT_BUS.register(new NemesisHandler());
	}

	public void init(FMLInitializationEvent e) {
		SpawnHandler.init();
		UpdateHandler.init();
		AttackHandler.init();
		Reaper.init();
		DeathHandler.init();
		DiscoveryHandler.init();
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
		MessageOpenNemesisDetailsGui.init(packetId++);
		MessageOpenNemesisDetailsGuiRequest.init(packetId++);
		MessageHealAnimation.init(packetId++);
		MessageSyncNemesis.init(packetId++);
		MessageSyncNemesisRequest.init(packetId++);
		MessageReflectDamageAnimation.init(packetId++);
		MessageWorshipAnimation.init(packetId++);
	}

}
