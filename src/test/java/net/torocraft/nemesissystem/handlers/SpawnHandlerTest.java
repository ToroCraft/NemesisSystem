package net.torocraft.nemesissystem.handlers;

import net.minecraft.util.math.BlockPos;
import net.torocraft.nemesissystem.registry.NemesisEntry;
import org.junit.Test;


public class SpawnHandlerTest {
	@Test
	public void buffAmount() throws Exception {

		NemesisEntry nemesis = new NemesisEntry();
		nemesis.setX(0);
		nemesis.setZ(0);
		nemesis.setLevel(8);

		BlockPos pos = new BlockPos(200, 0, 0);

		System.out.println(SpawnHandler.determineBuffAmount(nemesis, pos));

	}

}