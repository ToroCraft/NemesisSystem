package net.torocraft.nemesissystem;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.torocraft.nemesissystem.Nemesis.Trait;

public class SpawnHandler {
	public static final UUID EMPTY_UUID = new UUID(0, 0);

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
		if (event.getEntity().getEntityData().getUniqueId(SpawnUtil.NBT_ID) != null) {
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

		if (hasId(event)) {
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

	private boolean hasId(EntityJoinWorldEvent event) {
		UUID id = event.getEntity().getEntityData().getUniqueId(SpawnUtil.NBT_ID);
		return id != null && !id.equals(EMPTY_UUID);
	}

	private boolean nemesisClassEntity(Entity entity) {
		// TODO blacklist

		// TODO whitelist

		return entity instanceof EntityMob;
	}

	private Nemesis getNemesisForSpawn(EntityEvent event) {

		// TODO check location for unspawned nemeses

		// must be in line of sight

		// must be not other nemeses in the area

		List<Nemesis> nemeses = NemesisRegistryProvider.get(event.getEntity().world).list();

		if (nemeses == null || nemeses.size() < 1) {
			return null;
		}

		String entityType = getEntityType(event.getEntity());

		System.out.println(entityType);

		nemeses.removeIf(nemesis -> !nemesis.getMob().equals(entityType));

		if (nemeses == null || nemeses.size() < 1) {
			return null;
		}

		return nemeses.get(event.getEntity().world.rand.nextInt(nemeses.size()));
	}

	public static String getEntityType(Entity entityIn) {
		EntityEntry entry = EntityRegistry.getEntry(entityIn.getClass());
		if (entry == null) {
			return "";
		}
		return entry.getRegistryName().toString();
	}
}
