package net.torocraft.nemesissystem.gui;

import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiNemesisList extends GuiScreen {

	private GuiButton buttonClose;

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}

	@Override
	public void initGui() {
		buttonClose = new GuiButton(0, this.width / 2 - 100, this.height / 2 - 24, "Close");
		buttonList.add(buttonClose);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button == buttonClose) {
			//Main.packetHandler.sendToServer(...);
			closeGui();
		}
	}

	private void closeGui() {
		mc.displayGuiScreen(null);
		if (mc.currentScreen == null) {
			mc.setIngameFocus();
		}
	}

}
