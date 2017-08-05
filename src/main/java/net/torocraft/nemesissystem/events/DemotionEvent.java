package net.torocraft.nemesissystem.events;

import net.torocraft.nemesissystem.registry.NemesisEntry;

public class DemotionEvent extends NemesisEvent {
	public final String slayerName;

	public DemotionEvent(final NemesisEntry nemesis, final String slayerName) {
		super(nemesis);
		this.slayerName = slayerName;
	}

	public Object getSlayerName() {
		return slayerName;
	}
}
