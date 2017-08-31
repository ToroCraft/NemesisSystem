package net.torocraft.nemesissystem.proxy;

import java.io.File;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.torocraft.nemesissystem.NemesisConfig;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.handlers.AttackHandler;
import net.torocraft.nemesissystem.handlers.DeathHandler;
import net.torocraft.nemesissystem.handlers.DiscoveryHandler;
import net.torocraft.nemesissystem.handlers.DropHandler;
import net.torocraft.nemesissystem.handlers.LootHandler;
import net.torocraft.nemesissystem.handlers.NemesisHandler;
import net.torocraft.nemesissystem.handlers.Reaper;
import net.torocraft.nemesissystem.handlers.SetAttackTargetHandler;
import net.torocraft.nemesissystem.handlers.SpawnHandler;
import net.torocraft.nemesissystem.handlers.UpdateHandler;
import net.torocraft.nemesissystem.network.MessageOpenNemesisDetailsGui;
import net.torocraft.nemesissystem.network.MessageOpenNemesisDetailsGuiRequest;
import net.torocraft.nemesissystem.network.MessageOpenNemesisGui;
import net.torocraft.nemesissystem.network.MessageOpenNemesisGuiRequest;
import net.torocraft.nemesissystem.network.MessageSyncNemesis;
import net.torocraft.nemesissystem.network.MessageSyncNemesisRequest;
import net.torocraft.torotraits.ToroTraits;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent e) {
		checkToroTraitsVersion();
		initConfig(e.getSuggestedConfigurationFile());
		MinecraftForge.EVENT_BUS.register(new NemesisHandler());
	}

	private void checkToroTraitsVersion() {
		if (ToroTraits.API_VERSION < NemesisSystem.MIN_TOROTRAIT_VERSION) {
			throw new IllegalArgumentException("A newer version of ToroTraits Mod is required");
		}

		if (ToroTraits.COMPAT_VERSION != NemesisSystem.COMPAT_VERSION) {
			throw new IllegalArgumentException("The included ToroTraits Mod is not compatible with this version of " + NemesisSystem.MODNAME);
		}
	}

	public void init(FMLInitializationEvent e) {
		LootHandler.init();
		SpawnHandler.init();
		UpdateHandler.init();
		AttackHandler.init();
		Reaper.init();
		DeathHandler.init();
		DiscoveryHandler.init();
		SetAttackTargetHandler.init();
		DropHandler.init();
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
		MessageOpenNemesisGuiRequest.init(packetId++);
		MessageOpenNemesisDetailsGui.init(packetId++);
		MessageOpenNemesisDetailsGuiRequest.init(packetId++);
		MessageSyncNemesis.init(packetId++);
		MessageSyncNemesisRequest.init(packetId++);
		MessageOpenNemesisGui.init(packetId++);
	}

	public void openGui(int modGuiId) {

	}

	public EntityPlayer getPlayer() {
		return null;
	}

}
