package net.torocraft.nemesissystem.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.torocraft.nemesissystem.network.MessageOpenNemesisGui;
import net.torocraft.nemesissystem.registry.Nemesis;

public class GuiNemesis extends GuiScreen {

	private GuiButton buttonClose;

	private final Minecraft mc = Minecraft.getMinecraft();

	private final List<NemesisDisplay> itemDisplays = new ArrayList<>(6);

	public GuiNemesis() {
		for (int i = 0; i < 4; i++) {
			NemesisDisplay display = new NemesisDisplay();
			display.setPosition(5, 5 + (47 * i));
			itemDisplays.add(display);
		}
	}

	private void setPage(int page) {
		List<Nemesis> nemeses = MessageOpenNemesisGui.NEMESES;
		// TODO wire in page logic
		for (int i = 0; i < 4; i++) {
			if (nemeses.size() > i) {
				itemDisplays.get(i).setNemesis(nemeses.get(i));
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawRect(0, 0, width, height, 0xa0000000);
		itemDisplays.forEach(GuiDisplay::draw);
		super.drawScreen(mouseX, mouseY, partialTicks);

		// TODO sort

		// TODO summary info

		// TODO pager buttons

		// TODO center on page (figure out the size of gui)

	}

	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}

	@Override
	public void initGui() {
		buttonClose = new GuiButton(0, 50, 210, 60, 20, "Close");
		buttonList.add(buttonClose);

		System.out.println("**** INIT GUI ****");

		setPage(0);
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

	private void drawNemesis() {
		// move to a GUI class?
	}

}
