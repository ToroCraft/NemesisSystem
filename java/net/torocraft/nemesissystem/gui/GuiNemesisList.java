package net.torocraft.nemesissystem.gui;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSpectator;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;
import net.torocraft.nemesissystem.NemesisSystem;

public class GuiNemesisList extends GuiScreen {

	private GuiButton buttonClose;

	private final Minecraft mc = Minecraft.getMinecraft();

	private final EntityDisplay entityDisplay = new EntityDisplay(mc);

	private static final ResourceLocation SKIN_BASIC = new ResourceLocation(NemesisSystem.MODID, "textures/gui/default_skin_basic.png");



	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		// background

		// sort

		// draw nemeses,  6 per page?



		// pager buttons


		entityDisplay.draw();


		drawSkin();

	}

	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}

	@Override
	public void initGui() {
		buttonClose = new GuiButton(0, this.width / 2 - 100, this.height / 2 - 24, "Close");
		buttonList.add(buttonClose);

		System.out.println("**** INIT GUI ****");

		EntityZombie entity = new EntityZombie(mc.world);
		entityDisplay.setPosition(50, 50);
		entityDisplay.setEntity(entity);

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

	private void drawSkin() {

		mc.getTextureManager().bindTexture(SKIN_BASIC);

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Gui.drawModalRectWithCustomSizedTexture(3 - 10, 3 - 10, 0.0f, 0.0f, 160, 60, 160, 60);
	}

}
