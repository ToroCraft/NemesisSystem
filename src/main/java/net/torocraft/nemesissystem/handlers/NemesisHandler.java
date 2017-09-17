package net.torocraft.nemesissystem.handlers;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.nemesissystem.events.DeathEvent;
import net.torocraft.nemesissystem.events.DemotionEvent;
import net.torocraft.nemesissystem.events.DiscoveryEvent;
import net.torocraft.nemesissystem.events.DuelEvent;
import net.torocraft.nemesissystem.events.PromotionEvent;
import net.torocraft.nemesissystem.events.RegisterEvent;
import net.torocraft.nemesissystem.events.SpawnEvent;
import net.torocraft.nemesissystem.registry.LogEntry;
import net.torocraft.nemesissystem.registry.NemesisEntry;

public class NemesisHandler {

	@SubscribeEvent
	public void discovery(DiscoveryEvent ev) {

	}

	@SubscribeEvent
	public void promotion(PromotionEvent ev) {
		if (ev.world.isRemote) return;
		handle(ev.nemesis, LogEntry.PROMOTION(ev.nemesis));
	}

	@SubscribeEvent
	public void demotion(DemotionEvent ev) {
		if (ev.world.isRemote) return;
		handle(ev.nemesis, LogEntry.DEMOTION(ev.nemesis));
	}

	@SubscribeEvent
	public void duel(DuelEvent ev) {
		if (ev.world.isRemote) return;
		LogEntry.DUEL(ev.getWinner().getNameAndTitle(), ev.getLoser().getNameAndTitle()).sendNotification();
		handle(ev.nemesis, LogEntry.DUEL_WIN(ev.nemesis, ev.getLoser().getNameAndTitle()));
		handle(ev.nemesis, LogEntry.DUEL_LOSS(ev.nemesis, ev.getWinner().getNameAndTitle()));
	}

	@SubscribeEvent
	public void register(RegisterEvent ev) {
		if (ev.world.isRemote) return;
		handle(ev.nemesis, LogEntry.CREATION(ev.nemesis));
	}

	@SubscribeEvent
	public void death(DeathEvent ev) {
		if (ev.world.isRemote) return;
		handle(ev.nemesis, LogEntry.DIED(ev.nemesis, ev.getSlayerName()));
	}

	@SubscribeEvent
	public void death(SpawnEvent ev) {
		if (ev.world.isRemote) return;
		handle(ev.nemesis, LogEntry.SPAWNED(ev.nemesis));
	}

	private void handle(NemesisEntry nemesis, LogEntry log) {
		nemesis.addToHistory(log);
		log.sendNotification();
	}

}
