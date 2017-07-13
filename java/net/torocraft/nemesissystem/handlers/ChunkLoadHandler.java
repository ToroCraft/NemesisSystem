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
import net.torocraft.nemesissystem.util.NemesisUtil;

public class ChunkLoadHandler {

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new ChunkLoadHandler());
	}

	//@SubscribeEvent TODO https://github.com/ToroCraft/NemesisSystem/issues/18
	public void cleanUp(ChunkEvent.Load event) {
		findNemesesInChunk(event.getWorld(), event.getChunk()).forEach(NemesisUtil::unLoadNemesis);
	}

	//@SubscribeEvent TODO https://github.com/ToroCraft/NemesisSystem/issues/18
	public void cleanUp(ChunkEvent.Unload event) {
		findNemesesInChunk(event.getWorld(), event.getChunk()).forEach(NemesisUtil::unLoadNemesis);
	}

	private static List<EntityCreature> findNemesesInChunk(World world, Chunk chunk) {
		int x = chunk.x * 16;
		int z = chunk.z * 16;
		AxisAlignedBB chunkBox = new AxisAlignedBB(x, 0, z, x + 16, world.getActualHeight(), z + 16);
		return world.getEntitiesWithinAABB(EntityCreature.class, chunkBox, (EntityLiving searchEntity) -> NemesisUtil.isNemesis(searchEntity));
	}

}
