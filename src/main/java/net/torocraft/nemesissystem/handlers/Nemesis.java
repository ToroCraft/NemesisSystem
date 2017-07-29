package net.torocraft.nemesissystem.handlers;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.events.*;
import net.torocraft.nemesissystem.registry.LogEntry;

public class Nemesis {

	private static final String PREFIX = "notifications";

	@SubscribeEvent
	public void promotion(PromotionEvent ev) {
		send("promotion", title(ev), ev.getNemesis().getLevel());
		ev.getNemesis().addToHistory(LogEntry.PROMOTION(ev.getNemesis().getLevel()));
	}

	@SubscribeEvent
	public void demotion(DemotionEvent ev) {
		send("demotion", title(ev), ev.getNemesis().getLevel(), ev.getSlayerName());
		ev.getNemesis().addToHistory(LogEntry.PROMOTION(ev.getNemesis().getLevel()));
	}

	@SubscribeEvent
	public void duel(DuelEvent ev) {
		send("duelIfCrowded", title(ev), ev.getLoser().getNameAndTitle());
		ev.getWinner().addToHistory(LogEntry.DUEL_WIN(ev.getLoser().getNameAndTitle()));
		ev.getLoser().addToHistory(LogEntry.DUEL_LOSS(ev.getWinner().getNameAndTitle()));
	}

	@SubscribeEvent
	public void register(RegisterEvent ev) {
		send("register", title(ev), ev.getNemesis().getX(), ev.getNemesis().getZ());
		ev.getNemesis().addToHistory(LogEntry.CREATION(ev.getNemesis().getX(), ev.getNemesis().getZ()));
	}

	@SubscribeEvent
	public void death(DeathEvent event) {
		send("death", title(event), event.getSlayerName());
		event.getNemesis().addToHistory(LogEntry.DIED(event.getSlayerName()));
	}

	@SubscribeEvent
	public void death(SpawnEvent event) {
		send("spawned", title(event));
		event.getNemesis().addToHistory(LogEntry.SPAWNED());
	}

	private String title(NemesisEvent event) {
		return event.getNemesis().getNameAndTitle();
	}

	private void send(String translationKey, Object... args) {
		TextComponentTranslation m = new TextComponentTranslation(PREFIX  + "." + translationKey, args);
		m.setStyle(new Style());
		m.getStyle().setColor(TextFormatting.RED);
		sendGlobalMessage(m);
	}

	private void sendGlobalMessage(ITextComponent message) {
		if (NemesisSystem.SERVER == null) {
			return;
		}
		try {
			NemesisSystem.SERVER.getPlayerList().getPlayers().forEach(player -> player.sendMessage(message));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
