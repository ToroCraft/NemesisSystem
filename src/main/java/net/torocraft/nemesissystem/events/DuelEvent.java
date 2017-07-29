package net.torocraft.nemesissystem.events;

import net.torocraft.nemesissystem.registry.Nemesis;

public class DuelEvent extends NemesisEvent {
	protected final Nemesis loser;

	public DuelEvent(final Nemesis winner, final Nemesis loser) {
		super(winner);
		this.loser = loser;

	}

	public Nemesis getWinner() {
		return getNemesis();
	}

	public Nemesis getLoser() {
		return loser;
	}
}
