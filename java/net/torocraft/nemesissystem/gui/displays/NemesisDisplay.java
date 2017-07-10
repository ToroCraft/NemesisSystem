package net.torocraft.nemesissystem.gui.displays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
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
	private Nemesis nemesis;
	private final FontRenderer fontRenderer = mc.fontRenderer;
	private final GuiScreen gui;

	public NemesisDisplay(GuiScreen gui) {
		this.gui = gui;
		EntityZombie entity = new EntityZombie(mc.world);
		entityDisplay.setPosition(0, 4);
	}

	public void setNemesis(Nemesis nemesis) {
		this.nemesis = nemesis;
		if(nemesis == null){
			entityDisplay.setEntity(null);
			return;
		}

		EntityCreature entity = createEntity(nemesis);
		if (entity == null) {
			return;
		}
		EntityDecorator.decorate(entity, nemesis);
		entityDisplay.setEntity(entity);
	}

	private EntityCreature createEntity(Nemesis nemesis) {
		try{
			return (EntityCreature) SpawnUtil.getEntityFromString(mc.world, nemesis.getMob());
		}catch (Exception e){
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
		if(nemesis != null){
			drawNemesisInfo();
		}
		drawNemesisModel();
	}

	private void drawLevelIcons(int x, int y) {
		if(nemesis == null){
			return;
		}
		mc.renderEngine.bindTexture(Gui.ICONS);
		for(int i = 0; i < 10; i++){
			heartContainer(x + (i * 9), y);
		}
		for(int i = 0; i < nemesis.getLevel(); i++){
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

		fontRenderer.drawString(nemesis.getNameAndTitle(), 0, 0, 0xffffffff);
		drawLevelIcons(0, 10);

		// TODO level

		// TODO location

		// TODO traits

		// TODO journal

		// TODO distance

		GlStateManager.translate(-51, -4, 0);
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
