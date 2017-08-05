package net.torocraft.nemesissystem.events;

import net.torocraft.nemesissystem.registry.NemesisEntry;

public class DuelEvent extends NemesisEvent {
	public final NemesisEntry loser;

	public DuelEvent(final NemesisEntry winner, final NemesisEntry loser) {
		super(winner);
		this.loser = loser;

	}

	public NemesisEntry getWinner() {
		return nemesis;
	}

	public NemesisEntry getLoser() {
		return loser;
	}
}
