package net.torocraft.nemesissystem;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.torocraft.nemesissystem.proxy.CommonProxy;

@Mod(modid = NemesisSystem.MODID, version = NemesisSystem.VERSION, name = NemesisSystem.MODNAME)
public class NemesisSystem {

	public static final String MODID = "nemesissystem";
	public static final String VERSION = "1.12-1";
	public static final String MODNAME = "NemesisSystem";
	
	public static final String NBT_NEMESIS_ID = "torocraft_nemesis_id";
	public static final String TAG_NEMESIS = "torocraft_nemesis";
	public static final String TAG_BODY_GUARD = "nemesis_body_guard";
	public static final int NEMESIS_LIMIT = 16;


	@Mod.Instance(MODID)
	public static NemesisSystem INSTANCE;

	@SidedProxy(clientSide = "net.torocraft.nemesissystem.proxy.ClientProxy", serverSide = "net.torocraft.nemesissystem.proxy.ServerProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		proxy.preInit(e);
	}

	@EventHandler
	public void init(FMLInitializationEvent e) {
		proxy.init(e);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		proxy.postInit(e);
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent e) {
		e.registerServerCommand(new NemesisSystemCommand());
	}

}
