package net.torocraft.nemesissystem.registry;

import java.util.List;
import java.util.UUID;

public interface INemesisRegistry {

	void register(NemesisEntry nemesis);

	/**
	 * To remove a nemesis, update it with setDead(true)
	 */
	void update(NemesisEntry nemesis);

	NemesisEntry getById(UUID id);

	NemesisEntry getByName(String name);

	List<NemesisEntry> list();

	void clear();

	void markDirty();

	/*

	void unload(UUID id);

	void load(EntityCreature entity, UUID id);

	void promote(UUID id);

	void duelIfCrowded(NemesisEntry opponentOne, NemesisEntry opponentTwo);

	void setDead(UUID id, String slayerName);

	*/
}
