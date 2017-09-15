package net.torocraft.nemesissystem.handlers;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.torocraft.nemesissystem.registry.NemesisEntry;
import org.junit.Assert;
import org.junit.Test;


public class SpawnHandlerTest {
	@Test
	public void buffAmount() throws Exception {

		NemesisEntry nemesis = createNemesis(8);

		BlockPos pos = new BlockPos(50, 0, 0);

		System.out.println(SpawnHandler.determineBuffAmount(nemesis, pos));

	}

	private NemesisEntry createNemesis(int level) {
		NemesisEntry nemesis = new NemesisEntry();
		nemesis.setX(0);
		nemesis.setZ(0);
		nemesis.setLevel(level);
		return nemesis;
	}

	@Test
	public void sortByHighestLevel() {
		List<NemesisEntry> nemeses = new ArrayList<>();
		nemeses.add(createNemesis(4));
		nemeses.add(createNemesis(8));
		nemeses.add(createNemesis(4));
		nemeses.add(createNemesis(10));
		nemeses.add(createNemesis(1));
		SpawnHandler.sortByHighestLevel(nemeses);

		Assert.assertEquals(10, nemeses.get(0).getLevel());
		Assert.assertEquals(8, nemeses.get(1).getLevel());
		Assert.assertEquals(4, nemeses.get(2).getLevel());
		Assert.assertEquals(4, nemeses.get(3).getLevel());
		Assert.assertEquals(1, nemeses.get(4).getLevel());
	}

	@Test
	public void determineAttackDamage() {
		Assert.assertEquals(18, (int)SpawnHandler.determineAttackDamage(10, 5));
		Assert.assertEquals(13, (int)SpawnHandler.determineAttackDamage(10, 2));
		Assert.assertEquals(11, (int)SpawnHandler.determineAttackDamage(10, 1));

	}

	@Test
	public void determineMaxHealth() {
		Assert.assertEquals(22, (int)SpawnHandler.determineMaxHealth(10, 5));
		Assert.assertEquals(15, (int)SpawnHandler.determineMaxHealth(10, 2));
		Assert.assertEquals(12, (int)SpawnHandler.determineMaxHealth(10, 1));
	}


}