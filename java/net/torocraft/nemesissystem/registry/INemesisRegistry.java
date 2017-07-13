package net.torocraft.nemesissystem.registry;

import java.util.List;
import java.util.UUID;
import net.minecraft.entity.EntityCreature;

public interface INemesisRegistry {
	void unload(UUID id);

	Nemesis getById(UUID id);

	Nemesis getByName(String name);

	void load(EntityCreature entity, UUID id);

	void register(Nemesis nemesis);

	void promote(UUID id);

	void duel(Nemesis opponentOne, Nemesis opponentTwo);

	void setDead(UUID id, String slayerName);

	List<Nemesis> list();

	void clear();

	void markDirty();
}
