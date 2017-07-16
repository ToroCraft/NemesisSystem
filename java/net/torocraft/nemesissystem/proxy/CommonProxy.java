package net.torocraft.nemesissystem.proxy;

import java.io.File;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.torocraft.nemesissystem.NemesisConfig;
import net.torocraft.nemesissystem.events.NemesisEventHandlers;
import net.torocraft.nemesissystem.handlers.Attack;
import net.torocraft.nemesissystem.handlers.Death;
import net.torocraft.nemesissystem.handlers.Reaper;
import net.torocraft.nemesissystem.handlers.SetAttackTarget;
import net.torocraft.nemesissystem.handlers.Spawn;
import net.torocraft.nemesissystem.handlers.Update;
import net.torocraft.nemesissystem.network.MessageHealAnimation;
import net.torocraft.nemesissystem.network.MessageOpenNemesisGui;
import net.torocraft.nemesissystem.network.MessageOpenNemesisGuiRequest;
import net.torocraft.nemesissystem.network.MessageSyncNemesis;
import net.torocraft.nemesissystem.network.MessageSyncNemesisRequest;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent e) {
		initConfig(e.getSuggestedConfigurationFile());
		MinecraftForge.EVENT_BUS.register(new NemesisEventHandlers());
	}

	public void init(FMLInitializationEvent e) {
		Spawn.init();
		Update.init();
		Attack.init();
		Reaper.init();
		Death.init();
		SetAttackTarget.init();
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
		MessageSyncNemesis.init(packetId++);
		MessageSyncNemesisRequest.init(packetId++);
	}

}
