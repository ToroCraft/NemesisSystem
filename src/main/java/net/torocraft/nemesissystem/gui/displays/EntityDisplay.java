package net.torocraft.nemesissystem.gui.displays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class EntityDisplay implements GuiDisplay {

	private static int width = 40;
	private static int height = width;

	private int x;
	private int y;
	private float mouseX;
	private float mouseY;

	private float entityX;
	private float entityY;

	private EntityLivingBase entity;
	private float prevYawOffset;
	private float prevYaw;
	private float prevPitch;
	private float prevYawHead;
	private float prevPrevYahHead;
	private int scale = 1;

	public EntityDisplay() {
	}

	@Override
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
		updateOffsets();
	}

	private void updateOffsets() {
		if (entity == null) {
			return;
		}
		entityX = x + width / 2 + entity.width/2;
		entityY = y + height;
		if (entity instanceof EntityGhast) {
			entityY -= 10;
		}
	}

	private void updateScale() {
		if (entity == null) {
			entityY = (float) y + height;
			return;
		}
		int scaleY = MathHelper.ceil(width / entity.height);
		int scaleX = MathHelper.ceil(height / entity.width);
		scale = Math.min(scaleX, scaleY);

	}
	public void setSize(int size) {
		width = size;
		height = size;
	}

	@Override
	public void clicked(int mouseX, int mouseY, int mouseButton) {

	}

	public void setEntity(EntityLivingBase entity) {
		this.entity = entity;
		updateOffsets();
		updateScale();
	}

	@Override
	public void draw(float mouseX, float mouseY) {
		this.mouseX = mouseX - 26 - x;
		this.mouseY = mouseY - 16 - y;
		try {
			//drawDebugBackground();
			GlStateManager.color(0xff, 0xff, 0xff, 0xff);
			pushEntityRotations();
			glDraw();
			popEntityRotations();
		} catch (Throwable ignore) {
		}
	}

	@SuppressWarnings("unused")
	private void drawDebugBackground() {
		Gui.drawRect(x, y, x + width, y + height, 0xffffffff);
	}

	private void glDraw() {
		GlStateManager.enableColorMaterial();
		GlStateManager.pushMatrix();

		GlStateManager.translate(entityX, entityY, 50.0F);

		//GlStateManager.translate((float)x, (float)y, 50.0F);


		GlStateManager.scale((float) (-scale), (float) scale, (float) scale);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
		//GlStateManager.rotate(-100.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);


		GlStateManager.rotate(0.0f, 1.0F, 0.0F, 0.0F);
		//GlStateManager.rotate(-((float)Math.atan((double)(-mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);


		//RenderHelper.enableStandardItemLighting();



		entity.renderYawOffset = (float)Math.atan((double)(-mouseX / 40.0F)) * 40.0F;
		entity.rotationYaw = (float)Math.atan((double)(-mouseX / 40.0F)) * 40.0F;
		entity.rotationPitch = -((float)Math.atan((double)(-mouseY / 40.0F))) * 40.0F;

		entity.rotationPitch = reduce(entity.rotationPitch);
		entity.rotationYaw = reduce(entity.rotationYaw);
		entity.renderYawOffset = reduce(entity.renderYawOffset);

		entity.rotationYawHead = entity.rotationYaw;
		entity.prevRotationYawHead = entity.rotationYaw;
		

		GlStateManager.translate(0.0F, 0.0F, 0.0F);
		RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
		rendermanager.setPlayerViewY(180.0F);
		rendermanager.setRenderShadow(false);

		GlStateManager.enableLighting();
		for(int i = 0; i < 8; i++){
			GlStateManager.disableLight(i);
		}

		rendermanager.doRenderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
		rendermanager.setRenderShadow(true);

		GlStateManager.popMatrix();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.disableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	private static float reduce(float f) {
		return f/(2f);
	}

	private void popEntityRotations() {
		entity.renderYawOffset = prevYawOffset;
		entity.rotationYaw = prevYaw;
		entity.rotationPitch = prevPitch;
		entity.rotationYawHead = prevYawHead;
		entity.prevRotationYawHead = prevPrevYahHead;
	}

	private void pushEntityRotations() {
		prevYawOffset = entity.renderYawOffset;
		prevYaw = entity.rotationYaw;
		prevPitch = entity.rotationPitch;
		prevYawHead = entity.rotationYawHead;
		prevPrevYahHead = entity.prevRotationYawHead;
		entity.renderYawOffset = 0.0f;
		entity.rotationYaw = 0.0f;
		entity.rotationPitch = 0.0f;
		entity.rotationYawHead = 0.0f;
		entity.prevRotationYawHead = 0.0f;
	}
}
