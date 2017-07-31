package net.torocraft.nemesissystem.events;

import net.torocraft.nemesissystem.discovery.NemesisDiscovery;
import net.torocraft.nemesissystem.discovery.NemesisKnowledge;
import net.torocraft.nemesissystem.registry.Nemesis;

public class DiscoveryEvent extends NemesisEvent {

	private final NemesisDiscovery discovery;

	public DiscoveryEvent(final Nemesis nemesis, final NemesisDiscovery discovery) {
		super(nemesis);
		this.discovery = discovery;
	}
}
