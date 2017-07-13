package net.torocraft.nemesissystem.handlers;

import java.util.List;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.registry.NemesisRegistryProvider;
import net.torocraft.nemesissystem.util.NemesisUtil;

public class NemesisReaper {

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new NemesisReaper());
	}

	@SubscribeEvent
	public void cleanUp(ChunkEvent.Load event) {
		findNemesesInChunk(event.getWorld(), event.getChunk()).forEach(NemesisReaper::setUnloaded);
	}

	@SubscribeEvent
	public void cleanUp(ChunkEvent.Unload event) {
		findNemesesInChunk(event.getWorld(), event.getChunk()).forEach(NemesisReaper::setUnloaded);
	}

	private static List<EntityCreature> findNemesesInChunk(World world, Chunk chunk) {
		int x = chunk.x * 16;
		int z = chunk.z * 16;
		AxisAlignedBB chunkBox = new AxisAlignedBB(x, 0, z, x + 16, world.getActualHeight(), z + 16);
		return world.getEntitiesWithinAABB(EntityCreature.class, chunkBox, (EntityLiving searchEntity) -> NemesisUtil.isNemesis(searchEntity));
	}

	private static void setUnloaded(EntityCreature entity) {
		Nemesis nemesis = NemesisUtil.loadNemesisFromEntity(entity);
		if (nemesis == null) {
			return;
		}
		//NemesisRegistryProvider.get(entity.world).unload(nemesis.getId());
	}

	private static void setLoaded(EntityCreature entity) {
		Nemesis nemesis = NemesisUtil.loadNemesisFromEntity(entity);
		if (nemesis == null) {
			return;
		}
		//NemesisRegistryProvider.get(entity.world).load(entity, nemesis.getId());
	}

	private static void removeEntityFromWorld(EntityCreature entity, Nemesis nemesis) {
		entity.setDead();
		// TODO this doesn't seem to work well, maybe clean them up directly on load and/or unload
		NemesisUtil.findNemesisBodyGuards(entity.world, nemesis.getId(), entity.getPosition()).forEach((EntityCreature e) -> e.setDead());
	}

}
