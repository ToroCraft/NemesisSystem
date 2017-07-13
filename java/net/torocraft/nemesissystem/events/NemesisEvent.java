package net.torocraft.nemesissystem.events;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.torocraft.nemesissystem.registry.Nemesis;

public class NemesisEvent extends Event {

	private final Nemesis nemesis;

	public NemesisEvent(final Nemesis nemesis) {
		this.nemesis = nemesis;
	}

	public static class Promotion extends NemesisEvent {
		public Promotion(final Nemesis nemesis) {
			super(nemesis);
		}
	}

	public static class Duel extends NemesisEvent {
		protected final Nemesis loser;

		public Duel(final Nemesis winner, final Nemesis loser) {
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

	public static class Register extends NemesisEvent {
		public Register(final Nemesis nemesis) {
			super(nemesis);
		}
	}

	public static class Death extends NemesisEvent {
		protected final String slayerName;

		public Death(final Nemesis nemesis, final String slayerName) {
			super(nemesis);
			this.slayerName = slayerName;
		}

		public String getSlayerName() {
			return slayerName;
		}
	}

	public Nemesis getNemesis() {
		return nemesis;
	}
}
