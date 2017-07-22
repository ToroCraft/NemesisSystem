package net.torocraft.nemesissystem.events;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.registry.LogEntry;

public class NemesisEventHandlers {

	@SubscribeEvent
	public void promotion(NemesisEvent.Promotion event) {
		ITextComponent message = buildMessage("notifications.nemesis_system.promotion",
				event.getNemesis().getNameAndTitle(), event.getNemesis().getLevel());
		sendGlobalMessage(message);

		event.getNemesis().addToHistory(LogEntry.PROMOTION(event.getNemesis().getLevel()));
	}

	@SubscribeEvent
	public void demotion(NemesisEvent.Demotion event) {
		ITextComponent message = buildMessage("notifications.nemesis_system.demotion",
				event.getNemesis().getNameAndTitle(), event.getNemesis().getLevel(), event.getSlayerName());
		sendGlobalMessage(message);

		event.getNemesis().addToHistory(LogEntry.PROMOTION(event.getNemesis().getLevel()));
	}

	@SubscribeEvent
	public void duel(NemesisEvent.Duel event) {
		ITextComponent message = buildMessage("notifications.nemesis_system.duelIfCrowded",
				event.getWinner().getNameAndTitle(), event.getLoser().getNameAndTitle());
		sendGlobalMessage(message);

		event.getWinner().addToHistory(LogEntry.DUEL_WIN(event.getLoser().getNameAndTitle()));
		event.getLoser().addToHistory(LogEntry.DUEL_LOSS(event.getWinner().getNameAndTitle()));
	}

	@SubscribeEvent
	public void register(NemesisEvent.Register event) {
		ITextComponent message = buildMessage("notifications.nemesis_system.register",
				event.getNemesis().getNameAndTitle(), event.getNemesis().getX(), event.getNemesis().getZ());
		sendGlobalMessage(message);

		event.getNemesis().addToHistory(LogEntry.CREATION(event.getNemesis().getX(), event.getNemesis().getZ()));
	}

	@SubscribeEvent
	public void death(NemesisEvent.Death event) {
		ITextComponent message = buildMessage("notifications.nemesis_system.death",
				event.getNemesis().getNameAndTitle(), event.getSlayerName());
		sendGlobalMessage(message);

		event.getNemesis().addToHistory(LogEntry.DIED(event.getSlayerName()));
	}

	// TODO need an event for killing a player

	private void sendGlobalMessage(ITextComponent message) {
		try {
			NemesisSystem.SERVER.getPlayerList().getPlayers().forEach(player -> player.sendMessage(message));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ITextComponent buildMessage(String translationKey, Object... args) {
		return new TextComponentTranslation(translationKey, args);
	}
}
