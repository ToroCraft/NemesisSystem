package net.torocraft.nemesissystem.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.gui.NemesisSystemGuiHandler;
import net.torocraft.nemesissystem.handlers.InputHandler;

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
		InputHandler.init();
		super.init(e);
	}

	@Override
	public void openGui(int modGuiId) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		player.openGui(NemesisSystem.INSTANCE, modGuiId, player.world, (int) player.posX, (int) player.posY, (int) player.posZ);
	}

	@Override
	public EntityPlayer getPlayer() {
		return Minecraft.getMinecraft().player;
	}

}