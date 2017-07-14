package net.torocraft.nemesissystem.registry;

import net.minecraft.nbt.NBTTagCompound;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class NemesisLogEntry {

    private LogType type;
    private Map<String, String> details;
    private LocalDate date;

    private static final String NBT_TYPE = "type";
    private static final String NBT_DETAILS = "details";
    private static final String NBT_DATE = "date";

    private static final String VICTIM = "victim";
    private static final String KILLER = "killer";
    private static final String OPPONENT = "opponent";
    private static final String NEW_LEVEL = "newLevel";
    private static final String DOMAIN_X = "domainX";
    private static final String DOMAIN_Z = "domainZ";


    public NemesisLogEntry() {

    }

    private NemesisLogEntry(LogType type, Map<String, String> details) {
        this.type = type;
        this.details = details;
        this.date = LocalDate.now();
    }

    public enum LogType {
        KILLED, DIED, DUEL_WIN, DUEL_LOSS, PROMOTION, CREATION, FLED
    }

    public static NemesisLogEntry KILLED(String victimName) {
        Map<String, String> details = new HashMap<>();
        details.put(VICTIM, victimName);

        return new NemesisLogEntry(LogType.KILLED, details);
    }

    public static NemesisLogEntry DIED(String killerName) {
        Map<String, String> details = new HashMap<>();
        details.put(KILLER, killerName);
        return new NemesisLogEntry(LogType.DIED, details);
    }

    public static NemesisLogEntry DUEL_WIN(String loserName) {
        Map<String, String> details = new HashMap<>();
        details.put(OPPONENT, loserName);
        return new NemesisLogEntry(LogType.DUEL_WIN, details);
    }

    public static NemesisLogEntry DUEL_LOSS(String winnerName) {
        Map<String, String> details = new HashMap<>();
        details.put(OPPONENT, winnerName);
        return new NemesisLogEntry(LogType.DUEL_LOSS, details);
    }

    public static NemesisLogEntry PROMOTION(int newLevel) {
        Map<String, String> details = new HashMap<>();
        details.put(NEW_LEVEL, String.valueOf(newLevel));
        return new NemesisLogEntry(LogType.PROMOTION, details);
    }

    public static NemesisLogEntry CREATION(int x, int z) {
        Map<String, String> details = new HashMap<>();
        details.put(DOMAIN_X, String.valueOf(x));
        details.put(DOMAIN_Z, String.valueOf(z));
        return new NemesisLogEntry(LogType.CREATION, details);
    }

    public static NemesisLogEntry FLED(String lastPlayerName) {
        Map<String, String> details = new HashMap<>();
        details.put(OPPONENT, lastPlayerName);
        return new NemesisLogEntry(LogType.FLED, details);
    }

    public LogType getType() {
        return type;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public LocalDate getDate() {
        return date;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound c) {
        c.setString(NBT_TYPE, type.toString());
        c.setString(NBT_DATE, date.toString());

        NBTTagCompound nbtDetails = new NBTTagCompound();
        details.forEach((key, value) -> {
            nbtDetails.setString(key, value);
        });
        c.setTag(NBT_DETAILS, nbtDetails);

        return c;
    }

    public void readFromNBT(NBTTagCompound c) {
        type = LogType.valueOf(c.getString(NBT_TYPE));
        date = LocalDate.parse(c.getString(NBT_DATE));

        details = new HashMap<>();
        addDetailsByKey(c, VICTIM, details);
        addDetailsByKey(c, KILLER, details);
        addDetailsByKey(c, OPPONENT, details);
        addDetailsByKey(c, NEW_LEVEL, details);
        addDetailsByKey(c, DOMAIN_X, details);
        addDetailsByKey(c, DOMAIN_Z, details);
    }

    private void addDetailsByKey(NBTTagCompound c, String key, Map<String, String> details) {
        if (!c.getString(key).isEmpty()) {
            details.put(key, c.getString(key));
        }
    }
}
