package net.torocraft.nemesissystem.events;

import net.minecraft.entity.player.EntityPlayer;
import net.torocraft.nemesissystem.discovery.NemesisDiscovery;
import net.torocraft.nemesissystem.registry.NemesisEntry;

public class DiscoveryEvent extends NemesisEvent {

	public final NemesisDiscovery discovery;
	public final EntityPlayer player;

	public DiscoveryEvent(final NemesisEntry nemesis, final NemesisDiscovery discovery, final EntityPlayer player) {
		super(nemesis);
		this.discovery = discovery;
		this.player = player;
	}
}
