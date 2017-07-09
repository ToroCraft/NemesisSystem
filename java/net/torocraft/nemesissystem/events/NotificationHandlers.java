package net.torocraft.nemesissystem.events;


import com.google.common.base.Predicates;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.processing.SupportedSourceVersion;
import java.util.List;

public class NotificationHandlers {

    @SubscribeEvent
    public void promotionNotification(NemesisEvent.Promotion event) {
        ITextComponent message = buildMessage("notifications.nemesis_system.promotion",
                event.getNemesis().getNameAndTitle(), event.getNemesis().getLevel());
        sendGlobalMessage(event.getWorld(), message);
    }

    @SubscribeEvent
    public void duelNotification(NemesisEvent.Duel event) {
        ITextComponent message = buildMessage("notifications.nemesis_system.duel",
                event.getWinner().getNameAndTitle(), event.getLoser().getNameAndTitle());
        sendGlobalMessage(event.getWorld(), message);
    }

    @SubscribeEvent
    public void registerNotification(NemesisEvent.Register event) {
        ITextComponent message = buildMessage("notifications.nemesis_system.register",
                event.getNemesis().getNameAndTitle(), event.getNemesis().getX(), event.getNemesis().getZ());
        sendGlobalMessage(event.getWorld(), message);
    }

    @SubscribeEvent
    public void deathNotification(NemesisEvent.Death event) {
        ITextComponent message = buildMessage("notifications.nemesis_system.death",
                event.getNemesis().getNameAndTitle(), event.getSlayerName());
        sendGlobalMessage(event.getWorld(), message);
    }

    private void sendGlobalMessage(World world, ITextComponent message) {
        List<EntityPlayer> players = world.getPlayers(EntityPlayer.class, Predicates.alwaysTrue());
        players.forEach(player -> {
            player.sendMessage(message);
        });
    }

    private ITextComponent buildMessage(String translationKey, Object... args) {
        return new TextComponentTranslation(translationKey, args);
    }
}
