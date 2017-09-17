package net.torocraft.nemesissystem.events;

import net.minecraft.entity.EntityCreature;
import net.minecraft.world.World;
import net.torocraft.nemesissystem.registry.NemesisEntry;

public class SpawnEvent extends NemesisEvent {

	public final EntityCreature entity;

	public SpawnEvent(final World world, final NemesisEntry nemesis, final EntityCreature entity) {
		super(world, nemesis);
		this.entity = entity;
	}
}
