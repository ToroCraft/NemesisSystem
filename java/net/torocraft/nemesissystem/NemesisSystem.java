package net.torocraft.nemesissystem;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = NemesisSystem.MODID, version = NemesisSystem.VERSION, name = NemesisSystem.MODNAME)
public class NemesisSystem {

  public static final String MODID = "nemesissystem";
  public static final String VERSION = "1.12-1";
  public static final String MODNAME = "NemesisSystem";

  @Mod.Instance(MODID)
  public static NemesisSystem INSTANCE;

  @SidedProxy(clientSide = "net.torocraft.nemesissystem.ClientProxy", serverSide = "net.torocraft.nemesissystem.ServerProxy")
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

}
