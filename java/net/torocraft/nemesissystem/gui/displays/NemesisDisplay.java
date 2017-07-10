package net.torocraft.nemesissystem.gui.displays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.util.EntityDecorator;
import net.torocraft.nemesissystem.util.SpawnUtil;

public class NemesisDisplay implements GuiDisplay {

	private static final ResourceLocation SKIN_BASIC = new ResourceLocation(NemesisSystem.MODID, "textures/gui/default_skin_basic.png");

	private final EntityDisplay entityDisplay = new EntityDisplay();
	private final Minecraft mc = Minecraft.getMinecraft();

	private float x;
	private float y;
	private NemesisDisplayData data;
	private final FontRenderer fontRenderer = mc.fontRenderer;
	private final GuiScreen gui;

	public NemesisDisplay(GuiScreen gui) {
		this.gui = gui;
		EntityZombie entity = new EntityZombie(mc.world);
		entityDisplay.setPosition(0, 4);
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
			return (EntityCreature) SpawnUtil.getEntityFromString(mc.world, nemesis.getMob());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void draw() {
		GlStateManager.pushAttrib();
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, 0);
		drawWork();
		GlStateManager.popMatrix();
		GlStateManager.popAttrib();
	}

	private void drawWork() {
		GuiScreen.drawRect(0, 0, 290, 46, 0x60000000);
		if (data != null) {
			drawNemesisInfo();
		}
		drawNemesisModel();
	}

	private void drawLevelIcons(int x, int y) {
		if (data == null) {
			return;
		}
		mc.renderEngine.bindTexture(Gui.ICONS);
		for (int i = 0; i < 10; i++) {
			heartContainer(x + (i * 9), y);
		}
		for (int i = 0; i < data.nemesis.getLevel(); i++) {
			heartFull(x + (i * 9), y);
		}

	}

	private void heartContainer(int x, int y) {
		gui.drawTexturedModalRect(x, y, 16, 0, 9, 9);
	}

	private void heartFull(int x, int y) {
		gui.drawTexturedModalRect(x, y, 16 + 36, 0, 9, 9);
	}

	private void drawNemesisInfo() {
		GlStateManager.translate(51, 4, 0);
		Nemesis n = data.nemesis;

		fontRenderer.drawString(n.getNameAndTitle(), 0, 0, 0xffffffff);
		drawLevelIcons(0, 10);
		fontRenderer.drawString(I18n.format("gui.location", n.getX(), n.getZ(), data.distance), 0, 20, 0xff404040);
		int x = 0;
		for (int i = 0; i < n.getTraits().size(); i++) {
			String s = I18n.format("trait." + n.getTraits().get(i));
			fontRenderer.drawString(s, 0, 30, 0xff404040);
			x += fontRenderer.getStringWidth(s) + 3;
		}

		// TODO journal

		// TODO gui textures
		
		GlStateManager.translate(-51, -4, 0);
	}

	private String computeDistance() {
		return "TODO";
	}

	private void drawNemesisModel() {
		GlStateManager.color(0xff, 0xff, 0xff, 0xff);
		drawEntityBackground();
		entityDisplay.draw();
	}

	private void drawEntityBackground() {
		mc.getTextureManager().bindTexture(SKIN_BASIC);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Gui.drawModalRectWithCustomSizedTexture(3 - 10, 3 - 10, 0.0f, 0.0f, 160, 60, 160, 60);
	}
}
