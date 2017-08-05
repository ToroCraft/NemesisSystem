package net.torocraft.nemesissystem.events;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.torocraft.nemesissystem.registry.NemesisEntry;

public class NemesisEvent extends Event {

	public final NemesisEntry nemesis;

	public NemesisEvent(final NemesisEntry nemesis) {
		this.nemesis = nemesis;
	}

}
