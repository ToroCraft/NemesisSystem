package net.torocraft.nemesissystem.events;

import net.torocraft.nemesissystem.registry.NemesisEntry;

public class RegisterEvent extends NemesisEvent {
	public RegisterEvent(final NemesisEntry nemesis) {
		super(nemesis);
	}
}
