package net.torocraft.nemesissystem.registry;

import java.util.HashMap;
import java.util.Map;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.torotraits.nbt.NbtField;

public class LogEntry {

	@NbtField
	private LogType type;

	@NbtField(genericType = String.class)
	private Map<String, String> details;

	@NbtField
	private long date;

	public LogEntry() {

	}

	private LogEntry(LogType type, Map<String, String> details) {
		this.type = type;
		this.details = details;
		if (NemesisSystem.SERVER != null) {
			this.date = NemesisSystem.SERVER.getWorld(0).getTotalWorldTime();
		}
	}

	public enum LogType {
		KILLED, DIED, DUEL_WIN, DUEL_LOSS, PROMOTION, CREATION, FLED, SPAWNED
	}

	public static LogEntry KILLED(String victimName) {
		Map<String, String> details = new HashMap<>();
		details.put("victim", victimName);
		return new LogEntry(LogType.KILLED, details);
	}

	public static LogEntry DIED(String killerName) {
		Map<String, String> details = new HashMap<>();
		details.put("killer", killerName);
		return new LogEntry(LogType.DIED, details);
	}

	public static LogEntry DUEL_WIN(String loserName) {
		Map<String, String> details = new HashMap<>();
		details.put("opponent", loserName);
		return new LogEntry(LogType.DUEL_WIN, details);
	}

	public static LogEntry DUEL_LOSS(String winnerName) {
		Map<String, String> details = new HashMap<>();
		details.put("opponent", winnerName);
		return new LogEntry(LogType.DUEL_LOSS, details);
	}

	public static LogEntry PROMOTION(int newLevel) {
		Map<String, String> details = new HashMap<>();
		details.put("newLevel", String.valueOf(newLevel));
		return new LogEntry(LogType.PROMOTION, details);
	}

	public static LogEntry CREATION(int x, int z) {
		Map<String, String> details = new HashMap<>();
		details.put("domainX", String.valueOf(x));
		details.put("domainZ", String.valueOf(z));
		return new LogEntry(LogType.CREATION, details);
	}

	public static LogEntry FLED(String lastPlayerName) {
		Map<String, String> details = new HashMap<>();
		details.put("opponent", lastPlayerName);
		return new LogEntry(LogType.FLED, details);
	}

	public static LogEntry SPAWNED() {
		Map<String, String> details = new HashMap<>();
		return new LogEntry(LogType.SPAWNED, details);
	}

	public LogType getType() {
		return type;
	}

	public Map<String, String> getDetails() {
		return details;
	}

	public void setType(LogType type) {
		this.type = type;
	}

	public void setDetails(Map<String, String> details) {
		this.details = details;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public long getDate() {
		return date;
	}
}
