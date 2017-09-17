package net.torocraft.nemesissystem.events;

import net.minecraft.world.World;
import net.torocraft.nemesissystem.registry.NemesisEntry;

public class DeathEvent extends NemesisEvent {
	public final String slayerName;

	public DeathEvent(final World world, final NemesisEntry nemesis, final String slayerName) {
		super(world, nemesis);
		this.slayerName = slayerName;
	}

	public String getSlayerName() {
		return slayerName;
	}
}
