package net.torocraft.nemesissystem.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.torocraft.nemesissystem.NemesisSystem;

public class NemesisSystemGuiHandler implements IGuiHandler {
	public static final int TUTORIAL_GUI = 0;

	public static void init() {
		NetworkRegistry.INSTANCE.registerGuiHandler(NemesisSystem.INSTANCE, new NemesisSystemGuiHandler());
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == TUTORIAL_GUI) {
			return new GuiNemesis();
		}
		return null;
	}
}
