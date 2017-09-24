package net.torocraft.nemesissystem;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.nemesissystem.discovery.NemesisKnowledge;
import net.torocraft.nemesissystem.discovery.PlayerKnowledgeBase;
import net.torocraft.nemesissystem.proxy.CommonProxy;
import net.torocraft.nemesissystem.registry.NemesisEntry;

@Mod(modid = NemesisSystem.MODID, version = NemesisSystem.VERSION, name = NemesisSystem.MODNAME)
public class NemesisSystem {

	public static final String MODID = "nemesissystem";
	public static final String VERSION = "1.12-1";
	public static final String MODNAME = "NemesisSystem";

	public static final String NBT_NEMESIS_ID = "torocraft_nemesis_id";
	public static final String TAG_NEMESIS = "torocraft_nemesis";
	public static final String TAG_BODY_GUARD = "nemesis_body_guard";
	public static final String TAG_SPAWNING = "nemesis_is_spawning";
	public static final String TAG_BUFFED_MOB = "nemesis_buffed_mob";
	public static final int MIN_TOROTRAIT_VERSION = 1;
	public static final int COMPAT_VERSION = 1;
	public static final String TAG_BUFF_MOB_REINFORCEMENT = "nemesis_buffed_mob_reinforcement";
	//public static final String TAG_SUMMONED_MOB = "nemesis_summoned_mob";

	@Mod.Instance(MODID)
	public static NemesisSystem INSTANCE;

	public static MinecraftServer SERVER;

	public static SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

	@SidedProxy(clientSide = "net.torocraft.nemesissystem.proxy.ClientProxy", serverSide = "net.torocraft.nemesissystem.proxy.ServerProxy")
	public static CommonProxy PROXY;

	@SideOnly(Side.CLIENT)
	public static List<NemesisEntry> NEMESES;

	@SideOnly(Side.CLIENT)
	public static PlayerKnowledgeBase KNOWLEDGE_BASE;

	@SideOnly(Side.CLIENT)
	public static NemesisEntry NEMESIS;

	@SideOnly(Side.CLIENT)
	public static NemesisKnowledge KNOWLEDGE;

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		PROXY.preInit(e);
	}

	@EventHandler
	public void init(FMLInitializationEvent e) {
		PROXY.init(e);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		PROXY.postInit(e);
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent e) {
		SERVER = e.getServer();
		e.registerServerCommand(new NemesisSystemCommand());
	}

}
