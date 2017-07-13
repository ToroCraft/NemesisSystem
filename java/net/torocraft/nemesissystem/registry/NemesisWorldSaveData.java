package net.torocraft.nemesissystem.registry;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.WorldSavedData;
import net.torocraft.nemesissystem.NemesisSystem;

public class NemesisWorldSaveData extends WorldSavedData {
	public static final String NAME = NemesisSystem.MODID + ":NemesisSaveData";

	private static final String NBT_NEMESES = "nemeses";

	protected List<Nemesis> nemeses = new ArrayList<>();

	public NemesisWorldSaveData() {
		super(NAME);
	}

	public NemesisWorldSaveData(String s) {
		super(s);
	}

	@Override
	public void readFromNBT(NBTTagCompound c) {
		nemeses = readNemesesFromNBT(c);
	}

	public static List<Nemesis> readNemesesFromNBT(NBTTagCompound c) {
		NBTTagList nbtNemeses = loadNbtList(c);
		ArrayList<Nemesis> nemeses = new ArrayList<>();
		for (int i = 0; i < nbtNemeses.tagCount(); i++) {
			Nemesis nemesis = new Nemesis();
			nemesis.readFromNBT(nbtNemeses.getCompoundTagAt(i));
			nemeses.add(nemesis);
		}
		return nemeses;
	}

	private static NBTTagList loadNbtList(NBTTagCompound c) {
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
		writeNemesesToNBT(c, nemeses);
		return c;
	}

	public static void writeNemesesToNBT(NBTTagCompound c, List<Nemesis> nemeses) {
		NBTTagList nbtNemeses = new NBTTagList();
		for (Nemesis nemesis : nemeses) {
			if (!nemesis.isDead()) {
				nbtNemeses.appendTag(nemesis.writeToNBT(new NBTTagCompound()));
			}
		}
		c.setTag(NBT_NEMESES, nbtNemeses);
	}

}
