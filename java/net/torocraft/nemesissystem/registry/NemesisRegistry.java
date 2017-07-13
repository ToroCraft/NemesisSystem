package net.torocraft.nemesissystem.registry;

import java.rmi.server.UID;
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

public class NemesisRegistry extends NemesisWorldSaveData implements INemesisRegistry {

	private final Random rand = new Random();

	public NemesisRegistry() {
		super(NAME);
	}

	public NemesisRegistry(String s) {
		super(s);
	}

	@Override
	public void unload(UUID id) {
		Nemesis nemesis = getById(id);
		if(nemesis == null){
			return;
		}
		nemesis.setLoaded(false);
		markDirty();
	}

	@Override
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

	@Override
	public Nemesis getByName(String name) {
		name = name.trim().toLowerCase();
		for (Nemesis nemesis : nemeses) {
			if (name.equals(nemesis.getName().trim().toLowerCase())) {
				return nemesis;
			}
		}
		return null;
	}

	@Override
	public void load(EntityCreature entity, UUID id) {
		Nemesis nemesis = getById(id);
		if(nemesis == null){
			return;
		}
		nemesis.setLoaded(true);
		nemesis.setEntityId(entity.getEntityId());
		markDirty();
	}

	@Override
	public void register(Nemesis nemesis) {
		nemeses.add(nemesis);
		markDirty();
		MinecraftForge.EVENT_BUS.post(new NemesisEvent.Register(nemesis));
		System.out.println(nemesis.getNameAndTitle() + " has established rule of " + nemesis.getX() + "," + nemesis.getZ());
	}

	@Override
	public void promote(UUID id) {
		Nemesis nemesis = getById(id);
		if(nemesis == null){
			return;
		}
		NemesisUtil.promote(nemesis);
		markDirty();
	}

	@Override
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
		MinecraftForge.EVENT_BUS.post(new NemesisEvent.Duel(victor, loser));
	}

	@Override
	public void setDead(UUID id, String slayerName) {
		Nemesis nemesis = getById(id);
		if(nemesis == null){
			return;
		}
		nemesis.setLoaded(false);
		nemesis.setEntityId(null);
		nemesis.setDead(true);
		MinecraftForge.EVENT_BUS.post(new NemesisEvent.Death(nemesis, slayerName));
		markDirty();
	}

	@Override
	public List<Nemesis> list() {
		return new ArrayList<>(nemeses);
	}

	@Override
	public void clear() {
		nemeses.clear();
		markDirty();
	}

}
