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
import net.torocraft.nemesissystem.NemesisConfig;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.discovery.NemesisKnowledge;
import net.torocraft.nemesissystem.gui.displays.NemesisDisplay;
import net.torocraft.nemesissystem.gui.displays.NemesisDisplayData;
import net.torocraft.nemesissystem.gui.displays.NemesisEntityDisplay;
import net.torocraft.nemesissystem.network.MessageOpenNemesisGuiRequest;
import net.torocraft.nemesissystem.registry.NemesisEntry;
import net.torocraft.nemesissystem.util.NemesisUtil;
import net.torocraft.torotraits.traits.Trait;

public class GuiNemesisDetails extends GuiScreen {

	public enum DisplayType {NAME, LOCATION, TRAIT}

	private static final String UNKNOWN_VALUE = "????";
	private static final ResourceLocation INVENTORY_BACKGROUND = new ResourceLocation(NemesisSystem.MODID, "textures/gui/nemesis_details_gui.png");
	private static final int HEIGHT = 230;
	private static final int WIDTH = 256;

	private int offsetX;
	private int offsetY;
	private int buttonY;

	private GuiButton buttonBack;
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

		if (nemesisData == null || nemesisData.nemesis == null) {
			return;
		}

		drawTitle();
		drawInventory(mouseX, mouseY);
		if (!NemesisConfig.DISCOVERY_ENABLED || NemesisSystem.KNOWLEDGE.name) {
			entityDisplay.draw(mouseX, mouseY);
		}
		drawNemesisInfo();

		if (hoveredItem != null) {
			renderToolTip(hoveredItem, mouseX, mouseY);
		}

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	private void drawTitle() {
		NemesisEntry n = nemesisData.nemesis;
		String s = info(DisplayType.NAME, n.getNameAndTitle()) + " - " + NemesisUtil.romanize(n.getLevel());
		drawCenteredString(fontRenderer, s, width / 2, 10 + offsetY, 0xffffff);
	}

	private String info(DisplayType type, String info) {
		return info(type, 0, info);
	}

	private String info(DisplayType type, int info) {
		return info(type, 0, Integer.toString(info, 10));
	}

	private String info(DisplayType type, int index, String info) {

		if (!NemesisConfig.DISCOVERY_ENABLED) {
			return info;
		}

		NemesisKnowledge knowledge = NemesisSystem.KNOWLEDGE;

		if (knowledge == null) {
			return UNKNOWN_VALUE;
		}

		if (DisplayType.NAME.equals(type) && knowledge.name) {
			return info;
		}

		if (DisplayType.LOCATION.equals(type) && knowledge.location) {
			return info;
		}

		if (DisplayType.TRAIT.equals(type) && knowledge.traits.contains(index)) {
			return info;
		}

		return UNKNOWN_VALUE;
	}

	private void drawNemesisInfo() {
		int x = offsetX + 109;
		int y = offsetY + 30;

		NemesisEntry nemesis = nemesisData.nemesis;

		fontRenderer
				.drawString(I18n.format("gui.distance") + ": " + info(DisplayType.LOCATION, nemesisData.distance + "m"), x, y, NemesisDisplay.grey);
		y += 10;

		fontRenderer
				.drawString(I18n.format("gui.location", info(DisplayType.LOCATION, nemesis.getX()), info(DisplayType.LOCATION, nemesis.getZ())), x, y,
						NemesisDisplay.grey);
		y += 14;

		fontRenderer.drawString(I18n.format("gui.strengths"), x, y, NemesisDisplay.grey);
		y += 10;
		Trait trait;
		//for (Trait trait : nemesis.getTraits()) {
		for (int i = 0; i < nemesis.getTraits().size(); i++) {
			trait = nemesis.getTraits().get(i);
			if (trait.type.isStrength()) {
				fontRenderer.drawString(
						"* " + info(DisplayType.TRAIT, i, I18n.format("trait." + trait.type)) + " (" + NemesisUtil.romanize(trait.level) + ")", x, y,
						NemesisDisplay.grey);
				y += 10;
			}
		}

		y += 4;

		fontRenderer.drawString(I18n.format("gui.weaknesses", nemesis.getX(), nemesis.getZ()), x, y, NemesisDisplay.grey);
		y += 10;
		for (int i = 0; i < nemesis.getTraits().size(); i++) {
			trait = nemesis.getTraits().get(i);
			if (trait.type.isWeakness()) {
				fontRenderer.drawString(
						"* " + info(DisplayType.TRAIT, i, I18n.format("trait." + trait.type)) + " (" + NemesisUtil.romanize(trait.level) + ")", x, y,
						NemesisDisplay.grey);
				y += 10;
			}
		}
	}

	private void drawBackground() {
		GlStateManager.enableAlpha();
		GlStateManager.color(0xff, 0xff, 0xff, 0xff);
		mc.getTextureManager().bindTexture(INVENTORY_BACKGROUND);
		drawTexturedModalRect(offsetX, offsetY, 0, 0, WIDTH, HEIGHT);
	}

	private void drawInventory(int mouseX, int mouseY) {
		if (!NemesisConfig.DISCOVERY_ENABLED || (NemesisSystem.KNOWLEDGE.items && nemesisData != null && nemesisData.nemesis != null)) {
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
			drawItemStack(armorSet.get(i), 86, 84 - (i * 18), mouseX, mouseY);
		}
	}

	private void drawNemesisItems(int mouseX, int mouseY) {
		if (nemesisData.nemesis.getArmorInventory() == null) {
			return;
		}
		NonNullList<ItemStack> items = nemesisData.nemesis.getHandInventory();
		for (int i = 0; i < Math.min(4, items.size()); i++) {
			drawItemStack(items.get(i), 10 + (i * 18), 106, mouseX, mouseY);
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
			if (NemesisSystem.NEMESIS == null) {
				return;
			}
			nemesisData = new NemesisDisplayData(NemesisSystem.NEMESIS);
		}

		entityDisplay.setNemesis(nemesisData);

		offsetX = (width - WIDTH) / 2;
		offsetY = (height - HEIGHT) / 2;
		buttonY = offsetY + HEIGHT - 25;

		entityDisplay.setPosition(offsetX + 14, offsetY + 36);

		buttonClose = new GuiButton(0, 5 + offsetX, buttonY, 60, 20, I18n.format("gui.close"));
		buttonBack = new GuiButton(0, (WIDTH - 65) + offsetX, buttonY, 60, 20, I18n.format("gui.back"));

		buttonList.add(buttonClose);
		buttonList.add(buttonBack);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button == buttonClose) {
			closeGui();
		} else if (button == buttonBack) {
			NemesisSystem.NETWORK.sendToServer(new MessageOpenNemesisGuiRequest());
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
