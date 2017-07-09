package net.torocraft.nemesissystem.events;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.torocraft.nemesissystem.registry.Nemesis;

public class NemesisEvent extends Event {

    public NemesisEvent() {

    }

    public static class Promotion extends NemesisEvent {
        protected final Nemesis nemesis;

        public Promotion(final Nemesis nemesis) {
           // super(world);
            this.nemesis = nemesis;
        }

        public Nemesis getNemesis() {
            return nemesis;
        }
    }

    public static class Duel extends NemesisEvent {
        protected final Nemesis winner;
        protected final Nemesis loser;

        public Duel(final Nemesis winner, final Nemesis loser) {
            //super(world);
            this.winner = winner;
            this.loser = loser;
        }

        public Nemesis getWinner() {
            return winner;
        }

        public Nemesis getLoser() {
            return loser;
        }
    }

    public static class Register extends NemesisEvent {
        protected final Nemesis nemesis;

        public Register(final Nemesis nemesis) {
            //super(world);
            this.nemesis = nemesis;
        }

        public Nemesis getNemesis() {
            return nemesis;
        }
    }

    public static class Death extends NemesisEvent {
        protected final Nemesis nemesis;
        protected final String slayerName;

        public Death(final Nemesis nemesis, final String slayerName) {
           // super(world);
            this.nemesis = nemesis;
            this.slayerName = slayerName;
        }

        public Nemesis getNemesis() {
            return nemesis;
        }

        public String getSlayerName() { return slayerName; }
    }
}
