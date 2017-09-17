package net.torocraft.nemesissystem.events;

import net.minecraft.world.World;
import net.torocraft.nemesissystem.registry.NemesisEntry;

public class PromotionEvent extends NemesisEvent {
	public PromotionEvent(final World world, final NemesisEntry nemesis) {
		super(world, nemesis);
	}
}
