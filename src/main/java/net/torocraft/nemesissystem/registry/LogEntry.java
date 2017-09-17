package net.torocraft.nemesissystem.registry;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.torotraits.nbt.NbtField;

public class LogEntry {

	public enum EventType {
		DIED, DUEL_WIN, DUEL, DUEL_LOSS, DEMOTION, PROMOTION, CREATION, FLED, SPAWNED
	}

	private static final String PREFIX = "nemesis_event";

	@NbtField
	public EventType type;

	@NbtField(genericType = String.class)
	public List<String> parameters;

	@NbtField
	public long date;

	public LogEntry() {

	}

	public TextComponentTranslation getTextComponentTranslation() {
		TextComponentTranslation m = new TextComponentTranslation(PREFIX + "." + type, parameters.toArray());
		m.setStyle(new Style());
		m.getStyle().setColor(TextFormatting.RED);
		return m;
	}

	public void sendNotification() {
		sendGlobalMessage(getTextComponentTranslation());
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

	private LogEntry(EventType type, List<String> details) {
		this.type = type;
		this.parameters = details;
		if (NemesisSystem.SERVER != null) {
			this.date = NemesisSystem.SERVER.getWorld(0).getTotalWorldTime();
		}
	}

	public static LogEntry DIED(NemesisEntry nemesis, String killerName) {
		List<String> details = new ArrayList<>();
		details.add(nemesis.getName());
		details.add(killerName);
		return new LogEntry(EventType.DIED, details);
	}

	public static LogEntry DUEL(String winnerName, String loserName) {
		List<String> details = new ArrayList<>();
		details.add(winnerName);
		details.add(loserName);
		return new LogEntry(EventType.DUEL, details);
	}

	public static LogEntry DUEL_WIN(NemesisEntry nemesis, String loserName) {
		List<String> details = new ArrayList<>();
		details.add(nemesis.getName());
		details.add(loserName);
		return new LogEntry(EventType.DUEL_WIN, details);
	}

	public static LogEntry DUEL_LOSS(NemesisEntry nemesis, String winnerName) {
		List<String> details = new ArrayList<>();
		details.add(nemesis.getName());
		details.add(winnerName);
		return new LogEntry(EventType.DUEL_LOSS, details);
	}

	public static LogEntry PROMOTION(NemesisEntry nemesis) {
		List<String> details = new ArrayList<>();
		details.add(nemesis.getName());
		details.add(String.valueOf(nemesis.getLevel()));
		return new LogEntry(EventType.PROMOTION, details);
	}

	public static LogEntry DEMOTION(NemesisEntry nemesis) {
		List<String> details = new ArrayList<>();
		details.add(nemesis.getName());
		details.add(String.valueOf(nemesis.getLevel()));
		return new LogEntry(EventType.PROMOTION, details);
	}

	public static LogEntry CREATION(NemesisEntry nemesis) {
		List<String> details = new ArrayList<>();
		details.add(nemesis.getName());
		details.add(String.valueOf(nemesis.getX()));
		details.add(String.valueOf(nemesis.getZ()));
		return new LogEntry(EventType.CREATION, details);
	}

	public static LogEntry FLED(NemesisEntry nemesis, String lastPlayerName) {
		List<String> details = new ArrayList<>();
		details.add(nemesis.getName());
		details.add(lastPlayerName);
		return new LogEntry(EventType.FLED, details);
	}

	public static LogEntry SPAWNED(NemesisEntry nemesis) {
		List<String> details = new ArrayList<>();
		details.add(nemesis.getName());
		return new LogEntry(EventType.SPAWNED, details);
	}
}
