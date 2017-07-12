package net.torocraft.nemesissystem.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.entity.EntityCreature;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.MinecraftForge;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.events.NemesisEvent;
import net.torocraft.nemesissystem.util.NemesisUtil;

public class NemesisRegistry extends WorldSavedData {
	public static final String NAME = NemesisSystem.MODID + ":NemesisSaveData";

	private static final String NBT_NEMESES = "nemeses";

	private Random rand = new Random();

	//TODO add nemesis log (to the nemesis object)

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

	public void unload(UUID id) {
		// TODO add a get() method use it instead of these duplicated loops
		for (Nemesis nemesis : nemeses) {
			if (id.equals(nemesis.getId())) {
				nemesis.setLoaded(null);
				System.out.println("Unloaded: " + nemesis);
				markDirty();
				return;
			}
		}
		System.out.println("Nemesis ID[" + id + "] was not found and could not be unloaded!");
	}

	public void load(EntityCreature entity, UUID id) {
		for (Nemesis nemesis : nemeses) {
			if (id.equals(nemesis.getId())) {
				nemesis.setLoaded(entity.getEntityId());
				System.out.println("Loaded: " + nemesis);
				markDirty();
				return;
			}
		}
		System.out.println("Nemesis ID[" + id + "] was not found and could not be loaded!");
	}

	public void register(Nemesis nemesis) {
		nemeses.add(nemesis);
		markDirty();
		MinecraftForge.EVENT_BUS.post(new NemesisEvent.Register(nemesis));
		System.out.println(nemesis.getNameAndTitle() + " has established rule of " + nemesis.getX() + "," + nemesis.getZ());
	}

	public void promote(UUID id) {
		for (Nemesis nemesis : nemeses) {
			if (id.equals(nemesis.getId())) {
				promote(nemesis);
				markDirty();
				return;
			}
		}
		System.out.println("Nemesis ID[" + id + "] was not found and could not be promoted!");
	}

	public void duel(Nemesis opponentOne, Nemesis opponentTwo) {
		Nemesis victor;
		Nemesis loser;

		int attack1 = 0;
		int attack2 = 0;

		while (attack1 == attack2) {
			attack1 = rand.nextInt(opponentOne.getLevel()) + rand.nextInt(3);
			attack2 = rand.nextInt(opponentOne.getLevel()) + rand.nextInt(3);
		}

		if (attack1 > attack2) {
			victor = opponentOne;
			loser = opponentTwo;
		} else {
			victor = opponentTwo;
			loser = opponentOne;
		}

		setDead(loser.getId(), victor.getNameAndTitle());
		promote(victor.getId());

		// TODO log

		MinecraftForge.EVENT_BUS.post(new NemesisEvent.Duel(victor, loser));

		System.out.println(victor.getNameAndTitle() + " defeated " + loser.getNameAndTitle() + " in a fight to the death!");

	}

	private void promote(Nemesis nemesis) {
		nemesis.setLevel(nemesis.getLevel() + 1);

		System.out.println(nemesis.getNameAndTitle() + " has been promoted to level " + nemesis.getLevel());

		NemesisUtil.enchantEquipment(nemesis);

		//TODO low chance to add trait

		MinecraftForge.EVENT_BUS.post(new NemesisEvent.Promotion(nemesis));
	}

	/**
	 * Remove nemesis from registry
	 */
	public void setDead(UUID id, String slayerName) {
		if (id == null) {
			return;
		}
		for (Nemesis nemesis : nemeses) {
			if (id.equals(nemesis.getId())) {
				nemesis.setDead(true);
				System.out.println(nemesis.getNameAndTitle() + " has been slain");
				MinecraftForge.EVENT_BUS.post(new NemesisEvent.Death(nemesis, slayerName));
				markDirty();
				return;
			}
		}
		System.out.println("Nemesis ID[" + id + "] was not found and could not be marked as dead!");
	}

	public List<Nemesis> list() {
		return new ArrayList<>(nemeses);
	}

	public Nemesis getById(UUID id) {
		if (id == null) {
			return null;
		}
		for (Nemesis nemesis : nemeses) {
			if (id.equals(nemesis.getId())) {
				return nemesis;
			}
		}
		return null;
	}

	public void clear() {
		nemeses.clear();
		markDirty();
	}

	public Nemesis getByName(String name) {
		for (Nemesis nemesis : nemeses) {
			if (name.equals(nemesis.getName())) {
				return nemesis;
			}
		}
		return null;
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
