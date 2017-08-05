package net.torocraft.nemesissystem.events;

import net.minecraft.entity.EntityCreature;
import net.torocraft.nemesissystem.registry.NemesisEntry;

public class SpawnEvent extends NemesisEvent {

	public final EntityCreature entity;

	public SpawnEvent(final NemesisEntry nemesis, final EntityCreature entity) {
		super(nemesis);
		this.entity = entity;
	}
}
