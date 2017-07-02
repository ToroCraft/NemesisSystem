package net.torocraft.nemesissystem;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.SpecialSpawn;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SpawnHandler {

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new SpawnHandler());
	}

	//@SubscribeEvent
	public void scaleEntity(RenderLivingEvent.Pre event) {
		float scale = 2f;
		if (modelShouldBeScaled(event)) {
			GlStateManager.pushAttrib();
			GlStateManager.pushMatrix();
			GlStateManager.scale(scale, scale, scale);
		}
	}
	
	//@SubscribeEvent
	public void scaleEntity(RenderLivingEvent.Post event) {
		if (modelShouldBeScaled(event)) {
			GlStateManager.popMatrix();
			GlStateManager.popAttrib();
		}
	}

	private boolean modelShouldBeScaled(RenderLivingEvent event) {
		if (!event.getEntity().getTags().contains("nemesis")) {
			return false;
		}
		if (!(event.getRenderer().getMainModel() instanceof ModelBiped)) {
			return false;
		}
		return true;
	}

	@SubscribeEvent
	public void handleSpawn(EntityJoinWorldEvent event) {
		if (!(event.getEntity() instanceof EntityZombie)) {
			return;
		}

		if (event.getEntity().getTags().contains("nemesis")) {
			return;
		}

		event.getEntity().addTag("nemesis");

		System.out.println("Spawn: " + event.getEntity().getName() + " at " + event.getEntity().getPosition());

		System.out.println(event.getEntity().getPosition());

		SpawnUtil.convert(event.getEntity(), getNemesisForSpawn(event));
	}

	@SubscribeEvent
	public void handleSpawn(SpecialSpawn event) {
		//System.out.println("Spawn: " + event.getEntity().getName() + " at " + event.getEntity().getPosition());
		//SpawnUtil.convert(event.getEntity(), getNemesisForSpawn(event));
	}

	private Nemesis getNemesisForSpawn(EntityEvent event) {
		//TODO
		Nemesis nemesis = new Nemesis();
		nemesis.setName("TEST MOB");
		return nemesis;
	}
}
