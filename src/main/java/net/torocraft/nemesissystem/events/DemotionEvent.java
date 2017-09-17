package net.torocraft.nemesissystem.events;

import net.minecraft.world.World;
import net.torocraft.nemesissystem.registry.NemesisEntry;

public class DemotionEvent extends NemesisEvent {
	public final String slayerName;

	public DemotionEvent(final World world, final NemesisEntry nemesis, final String slayerName) {
		super(world, nemesis);
		this.slayerName = slayerName;
	}

	public Object getSlayerName() {
		return slayerName;
	}
}
