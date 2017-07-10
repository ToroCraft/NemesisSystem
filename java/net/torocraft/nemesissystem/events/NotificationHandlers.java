package net.torocraft.nemesissystem.events;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.torocraft.nemesissystem.NemesisSystem;

public class NotificationHandlers {

    @SubscribeEvent
    public void promotionNotification(NemesisEvent.Promotion event) {
        ITextComponent message = buildMessage("notifications.nemesis_system.promotion",
                event.getNemesis().getNameAndTitle(), event.getNemesis().getLevel());
        sendGlobalMessage(message);
    }

    @SubscribeEvent
    public void duelNotification(NemesisEvent.Duel event) {
        ITextComponent message = buildMessage("notifications.nemesis_system.duel",
                event.getWinner().getNameAndTitle(), event.getLoser().getNameAndTitle());
        sendGlobalMessage(message);
    }

    @SubscribeEvent
    public void registerNotification(NemesisEvent.Register event) {
        ITextComponent message = buildMessage("notifications.nemesis_system.register",
                event.getNemesis().getNameAndTitle(), event.getNemesis().getX(), event.getNemesis().getZ());
        sendGlobalMessage(message);
    }

    @SubscribeEvent
    public void deathNotification(NemesisEvent.Death event) {
        ITextComponent message = buildMessage("notifications.nemesis_system.death",
                event.getNemesis().getNameAndTitle(), event.getSlayerName());
        sendGlobalMessage(message);
    }

    private void sendGlobalMessage(ITextComponent message) {
        NemesisSystem.SERVER.getPlayerList().getPlayers().forEach(player -> player.sendMessage(message));
    }

    private ITextComponent buildMessage(String translationKey, Object... args) {
        return new TextComponentTranslation(translationKey, args);
    }
}
