package net.torocraft.nemesissystem.events;

import net.torocraft.nemesissystem.registry.Nemesis;

public class DeathEvent extends NemesisEvent {
	protected final String slayerName;

	public DeathEvent(final Nemesis nemesis, final String slayerName) {
		super(nemesis);
		this.slayerName = slayerName;
	}

	public String getSlayerName() {
		return slayerName;
	}
}
