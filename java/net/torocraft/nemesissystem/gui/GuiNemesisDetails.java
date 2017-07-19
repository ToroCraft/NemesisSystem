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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.torocraft.nemesissystem.NemesisConfig;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.gui.displays.GuiDisplay;
import net.torocraft.nemesissystem.gui.displays.NemesisDisplay;
import net.torocraft.nemesissystem.gui.displays.NemesisDisplayData;
import net.torocraft.nemesissystem.network.MessageOpenNemesisGui;

public class GuiNemesisDetails extends GuiScreen {

	private static final int HEIGHT = 230;
	private static final int WIDTH = 256;

	private int offsetX;
	private int offsetY;
	private int buttonY;

	private GuiButton buttonNext;
	private GuiButton buttonPrevious;
	private GuiButton buttonClose;
	private String currentPage = "";
	private int page = 0;
	private int lastPage;

	private List<NemesisDisplayData> nemeses;

	private final Minecraft mc = Minecraft.getMinecraft();

	private final List<NemesisDisplay> itemDisplays = new ArrayList<>(6);

	public GuiNemesisDetails() {
		for (int i = 0; i < 4; i++) {
			NemesisDisplay display = new NemesisDisplay(this);
			display.setPosition(5, 5 + (48 * i));
			itemDisplays.add(display);
		}
	}

	public static final ResourceLocation INVENTORY_BACKGROUND = new ResourceLocation(NemesisSystem.MODID, "textures/gui/nemesis_details_gui.png");

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		//drawRect(0, 0, width, height, 0xd0000000);
		GlStateManager.translate(offsetX, offsetY, 0);

		//drawRect(0, 0, WIDTH, HEIGHT, 0xd0ffffff);

		GlStateManager.enableAlpha();
		GlStateManager.color(0xff, 0xff, 0xff, 0xff);
		this.mc.getTextureManager().bindTexture(INVENTORY_BACKGROUND);
		drawTexturedModalRect(0, 0, 0, 0, WIDTH, HEIGHT);




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
	}

	private void updatePager() {
		currentPage = (page + 1) + "/" + (lastPage + 1);
		buttonPrevious.enabled = page != 0;
		buttonNext.enabled = page < lastPage;
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button == buttonClose) {
			closeGui();
		} else if (button == buttonNext) {

		} else if (button == buttonPrevious) {

		}
	}

	private void closeGui() {
		mc.displayGuiScreen(null);
		if (mc.currentScreen == null) {
			mc.setIngameFocus();
		}
	}

}
