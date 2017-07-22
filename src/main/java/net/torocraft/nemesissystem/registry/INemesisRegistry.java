package net.torocraft.nemesissystem.registry;

import java.util.List;
import java.util.UUID;
import net.minecraft.entity.EntityCreature;

public interface INemesisRegistry {

	void register(Nemesis nemesis);

	/**
	 * To remove a nemesis, update it with setDead(true)
	 */
	void update(Nemesis nemesis);

	Nemesis getById(UUID id);

	Nemesis getByName(String name);

	List<Nemesis> list();

	void clear();

	void markDirty();

	/*

	void unload(UUID id);

	void load(EntityCreature entity, UUID id);

	void promote(UUID id);

	void duelIfCrowded(Nemesis opponentOne, Nemesis opponentTwo);

	void setDead(UUID id, String slayerName);

	*/
}
