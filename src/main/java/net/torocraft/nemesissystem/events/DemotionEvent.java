package net.torocraft.nemesissystem.events;

import net.torocraft.nemesissystem.registry.Nemesis;

public class DemotionEvent extends NemesisEvent {
	protected final String slayerName;

	public DemotionEvent(final Nemesis nemesis, final String slayerName) {
		super(nemesis);
		this.slayerName = slayerName;
	}

	public Object getSlayerName() {
		return slayerName;
	}
}
