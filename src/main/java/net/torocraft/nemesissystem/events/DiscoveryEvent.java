package net.torocraft.nemesissystem.events;

import net.torocraft.nemesissystem.discovery.NemesisDiscovery;
import net.torocraft.nemesissystem.registry.NemesisEntry;

public class DiscoveryEvent extends NemesisEvent {

	public final NemesisDiscovery discovery;

	public DiscoveryEvent(final NemesisEntry nemesis, final NemesisDiscovery discovery) {
		super(nemesis);
		this.discovery = discovery;
	}
}
