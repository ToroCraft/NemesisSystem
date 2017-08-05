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

	protected List<NemesisEntry> nemeses = new ArrayList<>();

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

	public static List<NemesisEntry> readNemesesFromNBT(NBTTagCompound c) {
		NBTTagList nbtNemeses = loadNbtList(c);
		ArrayList<NemesisEntry> nemeses = new ArrayList<>();
		for (int i = 0; i < nbtNemeses.tagCount(); i++) {
			NemesisEntry nemesis = new NemesisEntry();
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

	public static void writeNemesesToNBT(NBTTagCompound c, List<NemesisEntry> nemeses) {
		NBTTagList nbtNemeses = new NBTTagList();
		for (NemesisEntry nemesis : nemeses) {
			if (!nemesis.isDead()) {
				NBTTagCompound compound = new NBTTagCompound();
				nemesis.writeToNBT(compound);
				nbtNemeses.appendTag(compound);
			}
		}
		c.setTag(NBT_NEMESES, nbtNemeses);
	}

}
