package net.torocraft.nemesissystem.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.entity.EntityCreature;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.WorldSavedData;
import net.torocraft.nemesissystem.NemesisSystem;

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
		// TODO add some more rolls here, or do something else fun

		Nemesis victor;
		Nemesis loser;

		// TODO there should be some weight here so that higher leveled nemeses have more chance to win a duel

		if(rand.nextBoolean()){
			victor = opponentOne;
			loser = opponentTwo;
		}else{
			victor = opponentTwo;
			loser = opponentOne;
		}

		setDead(loser.getId());
		promote(victor.getId());

		// TODO log

		// TODO announce

		System.out.println(victor.getNameAndTitle() + " defeated " + loser.getNameAndTitle() + " in a fight to the death!");

	}

	private void promote(Nemesis nemesis) {
		nemesis.setLevel(nemesis.getLevel() + 1);

		System.out.println(nemesis.getNameAndTitle() + " has been promoted to level " + nemesis.getLevel());

		//TODO add enchants

		//TODO low chance to add trait

		//TODO announce
	}

	/**
	 * Remove nemesis from registry
	 */
	public void setDead(UUID id) {
		if (id == null) {
			return;
		}
		for (Nemesis nemesis : nemeses) {
			if (id.equals(nemesis.getId())) {
				nemesis.setDead(true);
				System.out.println(nemesis.getNameAndTitle() + " has been slain");
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
			if (!nemesis.isDead()) {
				nbtNemeses.appendTag(nemesis.writeToNBT(new NBTTagCompound()));
			}
		}
		c.setTag(NBT_NEMESES, nbtNemeses);
		return c;
	}


}
