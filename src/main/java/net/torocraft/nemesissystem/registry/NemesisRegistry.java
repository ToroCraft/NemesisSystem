package net.torocraft.nemesissystem.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraftforge.common.MinecraftForge;
import net.torocraft.nemesissystem.events.RegisterEvent;

public class NemesisRegistry extends NemesisWorldSaveData implements INemesisRegistry {

	private final Random rand = new Random();

	public NemesisRegistry() {
		super(NAME);
	}

	public NemesisRegistry(String s) {
		super(s);
	}

	@Override
	public NemesisEntry getById(UUID id) {
		if (id == null) {
			return null;
		}
		for (NemesisEntry nemesis : nemeses) {
			if (id.equals(nemesis.getId())) {
				return nemesis;
			}
		}
		return null;
	}

	@Override
	public NemesisEntry getByName(String name) {
		name = name.trim().toLowerCase();
		for (NemesisEntry nemesis : nemeses) {
			if (name.equals(nemesis.getName().trim().toLowerCase())) {
				return nemesis;
			}
		}
		return null;
	}

	@Override
	public void update(NemesisEntry nemesis) {
		if (nemesis == null) {
			return;
		}

		NemesisEntry oldNemesis = getById(nemesis.getId());

		if(oldNemesis == null){
			return;
		}

		if(oldNemesis != nemesis){
			nemeses.remove(oldNemesis);
			nemeses.add(nemesis);
		}

		markDirty();
	}

	@Override
	public void register(NemesisEntry nemesis) {
		nemeses.add(nemesis);
		markDirty();
		MinecraftForge.EVENT_BUS.post(new RegisterEvent(nemesis));
		System.out.println(nemesis.getNameAndTitle() + " has established rule of " + nemesis.getX() + "," + nemesis.getZ());
	}

	@Override
	public List<NemesisEntry> list() {
		return new ArrayList<>(nemeses);
	}

	@Override
	public void clear() {
		nemeses.clear();
		markDirty();
	}

}
