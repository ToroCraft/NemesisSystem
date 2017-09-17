package net.torocraft.nemesissystem.events;

import net.minecraft.world.World;
import net.torocraft.nemesissystem.registry.NemesisEntry;

public class DuelEvent extends NemesisEvent {
	public final NemesisEntry loser;

	public DuelEvent(final World world, final NemesisEntry winner, final NemesisEntry loser) {
		super(world, winner);
		this.loser = loser;

	}

	public NemesisEntry getWinner() {
		return nemesis;
	}

	public NemesisEntry getLoser() {
		return loser;
	}
}
