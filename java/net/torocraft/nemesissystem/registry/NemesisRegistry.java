package net.torocraft.nemesissystem.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityCreature;
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
	public void update(Nemesis nemesis) {
		if (nemesis == null) {
			return;
		}

		Nemesis oldNemesis = getById(nemesis.getId());

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
	public void register(Nemesis nemesis) {
		nemeses.add(nemesis);
		markDirty();
		if (!Minecraft.getMinecraft().world.isRemote) {
			MinecraftForge.EVENT_BUS.post(new NemesisEvent.Register(nemesis));
		}
		System.out.println(nemesis.getNameAndTitle() + " has established rule of " + nemesis.getX() + "," + nemesis.getZ());
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
