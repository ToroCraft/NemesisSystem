package net.torocraft.nemesissystem.handlers;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.nemesissystem.NemesisSystem;

public class Render {
	public static void init() {
		MinecraftForge.EVENT_BUS.register(new Render());
	}

	@SubscribeEvent
	public void scaleEntity(RenderLivingEvent.Pre event) {
		float scale = 2f;
		if (modelShouldBeScaled(event)) {
			GlStateManager.pushAttrib();
			GlStateManager.pushMatrix();
			GlStateManager.scale(scale, scale, scale);
		}
	}

	@SubscribeEvent
	public void scaleEntity(RenderLivingEvent.Post event) {
		if (modelShouldBeScaled(event)) {
			GlStateManager.popMatrix();
			GlStateManager.popAttrib();
		}
	}

	private boolean modelShouldBeScaled(RenderLivingEvent event) {

		// TODO entity scaling
		if (true) {
			return false;
		}

		if (!event.getEntity().getTags().contains(NemesisSystem.TAG_NEMESIS)) {
			return false;
		}
		if (!(event.getRenderer().getMainModel() instanceof ModelBiped)) {
			return false;
		}
		return true;
	}
}
