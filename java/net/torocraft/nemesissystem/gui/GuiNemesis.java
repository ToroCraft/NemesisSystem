package net.torocraft.nemesissystem.gui;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.torocraft.nemesissystem.gui.displays.GuiDisplay;
import net.torocraft.nemesissystem.gui.displays.NemesisDisplay;
import net.torocraft.nemesissystem.gui.displays.NemesisDisplayData;
import net.torocraft.nemesissystem.network.MessageOpenNemesisGui;

public class GuiNemesis extends GuiScreen {

	private static final int HEIGHT = 230;
	private static final int WIDTH = 300;

	private int offsetX;
	private int offsetY;
	private int buttonY;

	private GuiButton buttonNext;
	private GuiButton buttonPrevious;
	private GuiButton buttonClose;
	private String currentPage = "";
	private int page = 0;
	private int lastPage = 3;

	private List<NemesisDisplayData> nemeses;

	private final Minecraft mc = Minecraft.getMinecraft();

	private final List<NemesisDisplay> itemDisplays = new ArrayList<>(6);

	public GuiNemesis() {
		for (int i = 0; i < 4; i++) {
			NemesisDisplay display = new NemesisDisplay(this);
			display.setPosition(5, 5 + (48 * i));
			itemDisplays.add(display);
		}
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();

	}

	private void setPage(int page) {

		if(nemeses == null){
			nemeses = MessageOpenNemesisGui.NEMESES.stream().map(NemesisDisplayData::new).collect(toList());
		}

		this.page = page;

		for (int i = (page * 4); i < ((page + 1) * 4); i++) {
			if (nemeses.size() > i) {
				itemDisplays.get(i % 4).setData(nemeses.get(i));
			}else{
				itemDisplays.get(i % 4).setData(null);
			}
		}

		updatePager();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {

		drawRect(0, 0, width, height, 0xd0000000);
		GlStateManager.translate(offsetX, offsetY, 0);
		drawRect(0, 0, WIDTH, HEIGHT, 0xd0ffffff);
		itemDisplays.forEach(GuiDisplay::draw);
		GlStateManager.translate(-offsetX, -offsetY, 0);
		super.drawScreen(mouseX, mouseY, partialTicks);

		drawCenteredString(fontRenderer, currentPage, (WIDTH - 78) + offsetX, buttonY + 5, 0x00FFFFFF);


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
		System.out.println("INIT GUI");
		offsetX = (width - WIDTH) / 2;
		offsetY = (height - HEIGHT) / 2;
		buttonY = offsetY + HEIGHT - 25;

		buttonClose = new GuiButton(0, 5 + offsetX, buttonY, 60, 20, I18n.format("gui.close"));
		buttonNext = new GuiButton(0, (WIDTH - 65) + offsetX, buttonY, 60, 20, I18n.format("gui.next"));
		buttonPrevious = new GuiButton(0, (WIDTH - 150) + offsetX, buttonY, 60, 20, I18n.format("gui.previous"));


		buttonList.add(buttonClose);
		buttonList.add(buttonNext);
		buttonList.add(buttonPrevious);
		setPage(page);
	}

	private void updatePager() {
		currentPage = (page + 1) + "/" + (lastPage + 1);
		buttonPrevious.enabled = page != 0;
		buttonNext.enabled = page < lastPage;
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button == buttonClose) {
			//Main.packetHandler.sendToServer(...);
			closeGui();
		} else if (button == buttonNext) {
			setPage(++page);
		} else if (button == buttonPrevious) {
			setPage(--page);
		}
	}

	private void closeGui() {
		mc.displayGuiScreen(null);
		if (mc.currentScreen == null) {
			mc.setIngameFocus();
		}
	}

}
