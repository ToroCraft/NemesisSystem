package net.torocraft.nemesissystem.events;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.torocraft.nemesissystem.registry.Nemesis;

public class NemesisEvent extends Event {

	private final Nemesis nemesis;

	public NemesisEvent(final Nemesis nemesis) {
		this.nemesis = nemesis;
	}

	public Nemesis getNemesis() {
		return nemesis;
	}
}
