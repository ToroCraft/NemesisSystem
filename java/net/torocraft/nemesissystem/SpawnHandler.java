package net.torocraft.nemesissystem;

import java.util.List;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityMob;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

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
		if (!event.getEntity().getTags().contains(SpawnUtil.NEMESIS_TAG)) {
			return false;
		}
		if (!(event.getRenderer().getMainModel() instanceof ModelBiped)) {
			return false;
		}
		return true;
	}

	// TODO handle nemesis death

	// TODO handel player death

	@SubscribeEvent
	public void handleSpawn(EntityJoinWorldEvent event) {
		if (!nemesisClassEntity(event.getEntity())) {
			return;
		}

		if (event.getEntity().getTags().contains(SpawnUtil.NEMESIS_TAG)) {
			return;
		}

		Nemesis nemesis = getNemesisForSpawn(event);

		if (nemesis == null) {
			return;
		}

		// TODO sound horn

		// TODO chat to near by players

		SpawnUtil.convert(event.getEntity(), nemesis);

		System.out.println("Spawning: " + event.getEntity().getName() + " at " + event.getEntity().getPosition());
	}

	private boolean nemesisClassEntity(Entity entity) {
		// TODO blacklist

		// TODO whitelist

		return entity instanceof EntityMob;
	}

	private Nemesis getNemesisForSpawn(EntityEvent event) {

		// TODO check location for unspawned nemeses

		List<Nemesis> nemeses = NemesisRegistryProvider.get(event.getEntity().world).list();

		if (nemeses == null || nemeses.size() < 1) {
			System.out.println("no nemeses");
			return null;
		}

		String entityType = getEntityType(event.getEntity());

		System.out.println(entityType);

		nemeses.removeIf(nemesis -> !nemesis.getMob().equals(entityType));

		if (nemeses == null || nemeses.size() < 1) {
			System.out.println("not in list");
			return null;
		}

		return nemeses.get(event.getEntity().world.rand.nextInt(nemeses.size()));
	}

	public static String getEntityType(Entity entityIn) {
		EntityEntry entry = EntityRegistry.getEntry(entityIn.getClass());
		if(entry == null){
			return "";
		}
		return entry.getRegistryName().toString();
	}
}
