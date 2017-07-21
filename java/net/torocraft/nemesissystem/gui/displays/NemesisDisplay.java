package net.torocraft.nemesissystem.gui.displays;

import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.ResourceLocation;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.network.MessageOpenNemesisDetailsGuiRequest;
import net.torocraft.nemesissystem.network.MessageOpenNemesisGui;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.util.EntityDecorator;
import net.torocraft.nemesissystem.util.SpawnUtil;

public class NemesisDisplay implements GuiDisplay {

	private static final ResourceLocation SKIN_BASIC = new ResourceLocation(NemesisSystem.MODID, "textures/gui/default_skin_basic.png");

	private static int grey = 0xff404040;
	private static int lightGrey = 0xff909090;
	private static int lighterGrey = 0xffc0c0c0;

	private final EntityDisplay entityDisplay = new EntityDisplay();
	private final Minecraft mc = Minecraft.getMinecraft();

	private int x;
	private int y;
	private float mouseX;
	private float mouseY;

	private static int WIDTH = 245;
	private static int HEIGHT = 45;

	private NemesisDisplayData data;
	private final FontRenderer fontRenderer = mc.fontRenderer;
	private final GuiScreen gui;

	public NemesisDisplay(GuiScreen gui) {
		this.gui = gui;
	}

	public void setData(NemesisDisplayData data) {
		this.data = data;
		if (data == null || data.nemesis == null) {
			entityDisplay.setEntity(null);
			return;
		}

		EntityCreature entity = createEntity(data.nemesis);
		if (entity == null) {
			return;
		}

		EntityDecorator.decorate(entity, data.nemesis);
		entityDisplay.setEntity(entity);
	}

	private EntityCreature createEntity(Nemesis nemesis) {
		try {
			return SpawnUtil.getEntityFromString(mc.world, nemesis.getMob());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
		entityDisplay.setPosition(x, y + 4);
	}

	@Override
	public void clicked(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton == 0 && data != null && data.nemesis != null && isHovering()) {
			NemesisSystem.NETWORK.sendToServer(new MessageOpenNemesisDetailsGuiRequest(data.nemesis.getId()));
		}
	}

	@Override
	public void draw(float mouseX, float mouseY) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		drawWork();
	}

	private void drawWork() {
		if (data != null) {
			drawOutline();
			drawNemesisInfo();
			drawNemesisModel();
		}
	}

	private void drawOutline() {
		int color = isHovering() ? lightGrey : lighterGrey;
		Gui.drawRect(x, y, x + WIDTH, y + 1, color);
		Gui.drawRect(x, y, x + 1, y + HEIGHT + 1, color);
		Gui.drawRect(x + WIDTH, y, x + WIDTH + 1, y + HEIGHT + 1, color);
		Gui.drawRect(x, y + HEIGHT, x + WIDTH, y + HEIGHT + 1, color);
	}

	private boolean isHovering() {
		return mouseX > x && mouseX < x + WIDTH && mouseY > y && mouseY < y + HEIGHT;
	}

	private void drawNemesisInfo() {
		if (data.nemesis != null) {
			drawTitleAndInfo(data.nemesis);
			drawTraits(data.nemesis);
		}
	}

	private void drawTitleAndInfo(Nemesis n) {
		int x = this.x + 51;
		int y = this.y + 4;
		fontRenderer.drawString(n.getNameAndTitle() + " (" + romanize(n.getLevel()) + ")", x, y, 0x0);
		fontRenderer.drawString(I18n.format("gui.distance") + ": " + data.distance, x, y + 10, grey);
	}

	private static String romanize(int i) {
		switch (i) {
		case 1:
			return "I";
		case 2:
			return "II";
		case 3:
			return "III";
		case 4:
			return "IV";
		case 5:
			return "V";
		case 6:
			return "VI";
		case 7:
			return "VII";
		case 8:
			return "VIII";
		case 9:
			return "IX";
		case 10:
			return "X";
		}
		return Integer.toString(i, 10);
	}

	private void drawTraits(Nemesis n) {
		if (n.getTraits() == null) {
			return;
		}

		int x = this.x + 51;
		int y = this.y + 24;

		for (int i = 0; i < n.getTraits().size(); i++) {
			String s = I18n.format("trait." + n.getTraits().get(i));
			fontRenderer.drawString(s, x, y, grey);
			x += fontRenderer.getStringWidth(s) + 3;
		}
	}

	private void drawNemesisModel() {
		GlStateManager.color(0xff, 0xff, 0xff, 0xff);
		entityDisplay.draw(mouseX, mouseY);
	}
}
