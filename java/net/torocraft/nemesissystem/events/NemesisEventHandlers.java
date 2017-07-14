package net.torocraft.nemesissystem.events;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.registry.NemesisLogEntry;

public class NemesisEventHandlers {

    @SubscribeEvent
    public void promotion(NemesisEvent.Promotion event) {
        ITextComponent message = buildMessage("notifications.nemesis_system.promotion",
                event.getNemesis().getNameAndTitle(), event.getNemesis().getLevel());
        sendGlobalMessage(message);

        addToHistory(event.getNemesis(), NemesisLogEntry.PROMOTION(event.getNemesis().getLevel()));
    }

    @SubscribeEvent
    public void duel(NemesisEvent.Duel event) {
        ITextComponent message = buildMessage("notifications.nemesis_system.duelIfCrowded",
                event.getWinner().getNameAndTitle(), event.getLoser().getNameAndTitle());
        sendGlobalMessage(message);

        addToHistory(event.getWinner(), NemesisLogEntry.DUEL_WIN(event.getLoser().getNameAndTitle()));
        addToHistory(event.getLoser(), NemesisLogEntry.DUEL_LOSS(event.getWinner().getNameAndTitle()));
    }

    @SubscribeEvent
    public void register(NemesisEvent.Register event) {
        ITextComponent message = buildMessage("notifications.nemesis_system.register",
                event.getNemesis().getNameAndTitle(), event.getNemesis().getX(), event.getNemesis().getZ());
        sendGlobalMessage(message);

        addToHistory(event.getNemesis(), NemesisLogEntry.CREATION(event.getNemesis().getX(), event.getNemesis().getZ()));
    }

    @SubscribeEvent
    public void death(NemesisEvent.Death event) {
        ITextComponent message = buildMessage("notifications.nemesis_system.death",
                event.getNemesis().getNameAndTitle(), event.getSlayerName());
        sendGlobalMessage(message);

        addToHistory(event.getNemesis(), NemesisLogEntry.DIED(event.getSlayerName()));
    }

    // TODO need an event for killing a player

    private void sendGlobalMessage(ITextComponent message) {
        try {
            NemesisSystem.SERVER.getPlayerList().getPlayers().forEach(player -> player.sendMessage(message));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void addToHistory(Nemesis nemesis, NemesisLogEntry entry) {
        nemesis.addToHistory(entry);
        nemesis.markRegistryDirty();
    }

    private ITextComponent buildMessage(String translationKey, Object... args) {
        return new TextComponentTranslation(translationKey, args);
    }
}
