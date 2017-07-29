package net.torocraft.nemesissystem.events;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.math.BlockPos;
import net.torocraft.nemesissystem.registry.Nemesis;

public class SpawnEvent extends NemesisEvent {

	private final EntityCreature entity;

	public SpawnEvent(final Nemesis nemesis, final EntityCreature entity) {
		super(nemesis);
		this.entity = entity;
	}
}
