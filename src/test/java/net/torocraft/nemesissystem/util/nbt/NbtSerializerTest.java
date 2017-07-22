package net.torocraft.nemesissystem.util.nbt;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.torocraft.nemesissystem.registry.LogEntry;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.traits.Trait;
import net.torocraft.nemesissystem.traits.Type;
import org.junit.Assert;
import org.junit.Test;

public class NbtSerializerTest {
	@Test
	public void serialize() throws Exception {
		Nemesis n = new Nemesis();

		NBTTagCompound c = new NBTTagCompound();

		n.setSpawned(null);
		NbtSerializer.write(c, n);
		Assert.assertFalse(c.hasKey("spawned"));

		n.setSpawned(0);
		NbtSerializer.write(c, n);
		Assert.assertEquals(0, c.getInteger("spawned"));

		n.setSpawned(132);
		n.setZ(15);
		n.setTitle("test");
		n.setId(UUID.randomUUID());
		n.setTraits(Arrays.asList(new Trait(Type.ARROW, 2), new Trait(Type.DOUBLE_MELEE, 10)));
		n.setHistory(new ArrayList<>());
		n.getHistory().add(LogEntry.DUEL_WIN("loser"));
		n.getHistory().get(0).setDate(1001);

		n.setHandInventory(NonNullList.withSize(2, ItemStack.EMPTY));

		NbtSerializer.write(c, n);
		System.out.println(c);
		Assert.assertEquals(132, c.getInteger("spawned"));
		Assert.assertEquals(15, c.getInteger("z"));
		Assert.assertEquals("test", c.getString("title"));

		Nemesis n2 = new Nemesis();
		NbtSerializer.read(c, n2);
		Assert.assertEquals(n.getSpawned(), n2.getSpawned());
		Assert.assertEquals(n.getZ(), n2.getZ());
		Assert.assertEquals(n.getTitle(), n2.getTitle());
		Assert.assertEquals(n.getId(), n2.getId());
		Assert.assertEquals(2, n2.getTraits().size());
		Assert.assertEquals(Type.ARROW, n2.getTraits().get(0).type);
		Assert.assertEquals(Type.DOUBLE_MELEE, n2.getTraits().get(1).type);

		Assert.assertEquals(2, n2.getTraits().get(0).level);
		Assert.assertEquals(10, n2.getTraits().get(1).level);

		Assert.assertEquals(1, n2.getHistory().size());
		Assert.assertEquals(1001, n2.getHistory().get(0).getDate());

		Assert.assertEquals(2, n2.getHandInventory().size());
	}
}