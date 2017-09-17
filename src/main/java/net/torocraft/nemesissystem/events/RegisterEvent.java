package net.torocraft.nemesissystem.events;

import net.minecraft.world.World;
import net.torocraft.nemesissystem.registry.NemesisEntry;

public class RegisterEvent extends NemesisEvent {
	public RegisterEvent(final World world, final NemesisEntry nemesis) {
		super(world, nemesis);
	}
}
