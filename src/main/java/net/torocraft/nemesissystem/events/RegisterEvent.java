package net.torocraft.nemesissystem.events;

import net.torocraft.nemesissystem.registry.Nemesis;

public class RegisterEvent extends NemesisEvent {
	public RegisterEvent(final Nemesis nemesis) {
		super(nemesis);
	}
}
