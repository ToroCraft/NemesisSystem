package net.torocraft.nemesissystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.WorldSavedData;

public class NemesisRegistry extends WorldSavedData {
	public static final String NAME = NemesisSystem.MODID + ":NemesisSaveData";

	private static final String NBT_NEMESES = "nemeses";

	private List<Nemesis> nemeses = new ArrayList<>();

	public NemesisRegistry() {
		super(NAME);
	}

	public NemesisRegistry(String s) {
		super(s);
	}

	public Nemesis getSpawnableNemesis(String className, int chunkX, int chunkZ) {
		return null;
	}

	public void register(Nemesis nemesis) {
		nemeses.add(nemesis);
		markDirty();
		//TODO overwrite if already exists
	}

	public List<Nemesis> list() {
		return nemeses;
	}

	public void remove(Nemesis nemesis) {
		nemeses.remove(nemesis);
		markDirty();
	}

	@Override
	public void readFromNBT(NBTTagCompound c) {
		NBTTagList nbtNemeses = loadNbtList(c);
		nemeses = new ArrayList<>();
		for (int i = 0; i < nbtNemeses.tagCount(); i++) {
			Nemesis nemesis = new Nemesis();
			nemesis.readFromNBT(nbtNemeses.getCompoundTagAt(i));
			nemeses.add(nemesis);
		}
	}

	private NBTTagList loadNbtList(NBTTagCompound c) {
		NBTTagList l = null;
		try {
			l = (NBTTagList) c.getTag(NBT_NEMESES);
		} catch (Exception ignored) {

		}
		if (l == null) {
			l = new NBTTagList();
		}
		return l;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound c) {
		NBTTagList nbtNemeses = new NBTTagList();
		for (Nemesis nemesis : nemeses) {
			nbtNemeses.appendTag(nemesis.writeToNBT(new NBTTagCompound()));
		}
		c.setTag(NBT_NEMESES, nbtNemeses);
		return c;
	}
}
