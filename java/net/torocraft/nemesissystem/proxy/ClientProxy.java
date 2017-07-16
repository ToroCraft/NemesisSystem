package net.torocraft.nemesissystem.proxy;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.torocraft.nemesissystem.gui.NemesisSystemGuiHandler;
import net.torocraft.nemesissystem.handlers.Input;

public class ClientProxy extends CommonProxy {
	public static KeyBinding[] keyBindings;

	@Override
	public void preInit(FMLPreInitializationEvent e) {
		super.preInit(e);
		keyBindings = new KeyBinding[1];
		keyBindings[0] = new KeyBinding("key.open_gui", 37, "key.categories.misc");
		ClientRegistry.registerKeyBinding(keyBindings[0]);
	}

	@Override
	public void init(FMLInitializationEvent e) {
		NemesisSystemGuiHandler.init();
		Input.init();
		super.init(e);
	}

	@Override
	public void postInit(FMLPostInitializationEvent e) {
		super.postInit(e);
	}

}