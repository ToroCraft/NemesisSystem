package net.torocraft.nemesissystem.events;

import net.torocraft.nemesissystem.registry.NemesisEntry;

public class DeathEvent extends NemesisEvent {
	public final String slayerName;

	public DeathEvent(final NemesisEntry nemesis, final String slayerName) {
		super(nemesis);
		this.slayerName = slayerName;
	}

	public String getSlayerName() {
		return slayerName;
	}
}
