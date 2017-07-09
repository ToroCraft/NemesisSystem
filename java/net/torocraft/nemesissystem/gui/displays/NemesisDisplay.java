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

	public NemesisDisplay() {
		EntityZombie entity = new EntityZombie(mc.world);
		entityDisplay.setPosition(0, 4);
	}

	public void setNemesis(Nemesis nemesis) {
		System.out.println("set nemesis to " + nemesis);
		this.nemesis = nemesis;
		EntityCreature entity = createEntity(nemesis);
		if (entity == null) {
			System.out.println("failed to create entity from nemesis");
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
		GuiScreen.drawRect(0, 0, 280, 46, 0x60000000);
		if(nemesis != null){
			drawNemesisInfo();
		}
		drawNemesisModel();
	}

	private void drawNemesisInfo() {
		GlStateManager.translate(51, 4, 0);

		fontRenderer.drawString(nemesis.getNameAndTitle(), 0, 0, 0xffffffff);
		fontRenderer.drawString(nemesis.getNameAndTitle(), 0, 11, 0xffb0b0b0);

		// TODO level

		// TODO location

		// TODO traits

		// TODO journal

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
