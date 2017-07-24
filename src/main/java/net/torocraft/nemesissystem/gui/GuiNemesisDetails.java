package net.torocraft.nemesissystem.gui;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.gui.displays.NemesisDisplay;
import net.torocraft.nemesissystem.gui.displays.NemesisDisplayData;
import net.torocraft.nemesissystem.gui.displays.NemesisEntityDisplay;
import net.torocraft.nemesissystem.network.MessageOpenNemesisDetailsGui;
import net.torocraft.nemesissystem.registry.Nemesis;

public class GuiNemesisDetails extends GuiScreen {

	private static final ResourceLocation INVENTORY_BACKGROUND = new ResourceLocation(NemesisSystem.MODID, "textures/gui/nemesis_details_gui.png");
	private static final int HEIGHT = 230;
	private static final int WIDTH = 256;

	private int offsetX;
	private int offsetY;
	private int buttonY;

	private GuiButton buttonNext;
	private GuiButton buttonPrevious;
	private GuiButton buttonClose;

	private NemesisDisplayData nemesisData;

	private ItemStack hoveredItem;

	private final Minecraft mc = Minecraft.getMinecraft();

	private final NemesisEntityDisplay entityDisplay = new NemesisEntityDisplay();

	public GuiNemesisDetails() {
		entityDisplay.setSize(60);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		hoveredItem = null;
		drawBackground();

		// TODO health display (using hearts)

		// TODO show attack damage

		// TODO show armor defense?  attack speed?

		// TODO back button

		drawInventory(mouseX, mouseY);
		entityDisplay.draw(mouseX, mouseY);
		drawNemesisInfo();

		if (hoveredItem != null) {
			renderToolTip(hoveredItem, mouseX, mouseY);
		}
	}

	private void drawNemesisInfo() {
		if (nemesisData.nemesis != null) {
			Nemesis n = nemesisData.nemesis;
			int x = offsetX + 105;
			int y = offsetY + 6;

			fontRenderer.drawString(n.getNameAndTitle() + " (" + NemesisDisplay.romanize(n.getLevel()) + ")", x, y, 0x0);
			y += 10;
			fontRenderer.drawString(I18n.format("gui.distance") + ": " + nemesisData.distance, x, y, NemesisDisplay.grey);
			y += 10;
			fontRenderer.drawString(I18n.format("gui.health") + ": " + "?", x, y, NemesisDisplay.grey);
			y += 10;
			fontRenderer.drawString(I18n.format("gui.location") + ": " + "?", x, y, NemesisDisplay.grey);

		}
	}


	private void drawBackground() {
		GlStateManager.enableAlpha();
		GlStateManager.color(0xff, 0xff, 0xff, 0xff);
		mc.getTextureManager().bindTexture(INVENTORY_BACKGROUND);
		drawTexturedModalRect(offsetX, offsetY, 0, 0, WIDTH, HEIGHT);
	}

	private void drawInventory(int mouseX, int mouseY) {
		if (nemesisData != null && nemesisData.nemesis != null) {
			drawNemesisArmor(mouseX, mouseY);
			drawNemesisItems(mouseX, mouseY);
		}
	}

	private void drawNemesisArmor(int mouseX, int mouseY) {
		if (nemesisData.nemesis.getArmorInventory() == null) {
			return;
		}
		NonNullList<ItemStack> armorSet = nemesisData.nemesis.getArmorInventory();
		for (int i = 0; i < Math.min(4, armorSet.size()); i++) {
			drawItemStack(armorSet.get(i), 84, 62 - (i * 18), mouseX, mouseY);
		}
	}

	private void drawNemesisItems(int mouseX, int mouseY) {
		if (nemesisData.nemesis.getArmorInventory() == null) {
			return;
		}
		NonNullList<ItemStack> items = nemesisData.nemesis.getHandInventory();
		for (int i = 0; i < Math.min(4, items.size()); i++) {
			drawItemStack(items.get(i), 8 + (i * 18), 84, mouseX, mouseY);
		}
	}

	private void drawItemStack(ItemStack stack, int x, int y, int mouseX, int mouseY) {
		GlStateManager.translate(0.0F, 0.0F, 32.0F);
		zLevel = 200.0F;
		itemRender.zLevel = 200.0F;
		itemRender.renderItemAndEffectIntoGUI(stack, x + offsetX, y + offsetY);
		zLevel = 0.0F;
		itemRender.zLevel = 0.0F;

		if (isPointInRegion(x + offsetX, y + offsetY, mouseX, mouseY)) {
			hoveredItem = stack;
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}

	@Override
	public void initGui() {

		if (nemesisData == null) {
			if (MessageOpenNemesisDetailsGui.NEMESIS == null) {
				return;
			}
			nemesisData = new NemesisDisplayData(MessageOpenNemesisDetailsGui.NEMESIS);
		}

		entityDisplay.setNemesis(nemesisData);

		offsetX = (width - WIDTH) / 2;
		offsetY = (height - HEIGHT) / 2;
		buttonY = offsetY + HEIGHT - 25;

		entityDisplay.setPosition(offsetX + 12, offsetY + 15);

		buttonClose = new GuiButton(0, 5 + offsetX, buttonY, 60, 20, I18n.format("gui.close"));
		buttonNext = new GuiButton(0, (WIDTH - 65) + offsetX, buttonY, 60, 20, I18n.format("gui.next"));
		buttonPrevious = new GuiButton(0, (WIDTH - 150) + offsetX, buttonY, 60, 20, I18n.format("gui.previous"));

		buttonList.add(buttonClose);
		buttonList.add(buttonNext);
		buttonList.add(buttonPrevious);
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

	protected boolean isPointInRegion(int rectX, int rectY, int pointX, int pointY) {
		return pointX >= rectX - 1 && pointX < rectX + 16 + 1 && pointY >= rectY - 1 && pointY < rectY + 16 + 1;
	}

}
