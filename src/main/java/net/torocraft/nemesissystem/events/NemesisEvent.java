package net.torocraft.nemesissystem.events;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.torocraft.nemesissystem.registry.NemesisEntry;

public class NemesisEvent extends Event {

	public final NemesisEntry nemesis;
	public final World world;

	public NemesisEvent(final World world, final NemesisEntry nemesis) {
		this.nemesis = nemesis;
		this.world = world;
	}

}
