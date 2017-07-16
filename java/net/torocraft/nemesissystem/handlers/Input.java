package net.torocraft.nemesissystem.handlers;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.network.MessageOpenNemesisGuiRequest;
import net.torocraft.nemesissystem.proxy.ClientProxy;

@SideOnly(Side.CLIENT)
public class Input {

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new Input());
	}

	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public void onEvent(KeyInputEvent event) {
		KeyBinding[] keyBindings = ClientProxy.keyBindings;
		if (keyBindings[0].isPressed()) {
			// TODO convert this to a toggle so that it opens and closes the gui
			NemesisSystem.NETWORK.sendToServer(new MessageOpenNemesisGuiRequest());
		}
	}
}
