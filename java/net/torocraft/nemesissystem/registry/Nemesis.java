package net.torocraft.nemesissystem.registry;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.nemesissystem.util.NbtField;

public class Nemesis {

	private static final int RANGE_SQ = 100 * 100;

	public enum Trait {DOUBLE_MELEE, ARROW, SUMMON, REFLECT, HEAT, POTION, SHIELD, TELEPORT, FIREBALL, HEAL}

	private static final String NBT_NAME = "name";
	private static final String NBT_LEVEL = "level";
	private static final String NBT_MOB = "mob";
	private static final String NBT_X = "x";
	private static final String NBT_Z = "z";
	private static final String NBT_ARMOR = "armor";
	private static final String NBT_HANDS = "hands";
	private static final String NBT_ID = "id";
	private static final String NBT_TRAITS = "traits";
	private static final String NBT_TITLE = "title";
	private static final String NBT_UNLOADED = "unloaded";
	private static final String NBT_DIMENSION = "dimension";
	private static final String NBT_SPAWNED = "spawned";

	/**
	 * the chunk the entity is in is loaded
	 */
	@NbtField
	private Long unloaded;

	/**
	 * the entity ID when this nemesis is spawned
	 */
	@NbtField
	private Integer spawned;

	@NbtField
	private String title;

	@NbtField
	private String name;

	@NbtField
	private int level;

	@NbtField
	private String mob;

	@NbtField
	private int x;

	@NbtField
	private int z;

	@NbtField
	private UUID id;

	@NbtField(genericType = Trait.class)
	private List<Trait> traits;

	@NbtField(genericType = LogEntry.class)
	private List<LogEntry> history;

	@NbtField
	private int dimension;

	//TODO spawned check

	private transient boolean isDead;

	private NonNullList<ItemStack> handInventory = NonNullList.withSize(2, ItemStack.EMPTY);
	private NonNullList<ItemStack> armorInventory = NonNullList.withSize(4, ItemStack.EMPTY);

	public void register(World world) {
		NemesisRegistryProvider.get(world).register(this);
	}

	@Override
	public String toString() {
		return name + " the " + title + " (" + (unloaded == null ? "LOADED" : "UNLOADED") + " " + (isSpawned() ? "SPAWNED" : "NOT_SPAWNED")
				+ " level:" + level + " loc:" + x + "," + z + ") " + mob + " "
				+ traits.get(0);
	}

	public void readFromNBT(NBTTagCompound c) {
		handInventory = NonNullList.withSize(2, ItemStack.EMPTY);
		armorInventory = NonNullList.withSize(4, ItemStack.EMPTY);
		name = c.getString(NBT_NAME);
		level = c.getInteger(NBT_LEVEL);
		mob = c.getString(NBT_MOB);
		x = c.getInteger(NBT_X);
		z = c.getInteger(NBT_Z);
		id = c.getUniqueId(NBT_ID);
		title = c.getString(NBT_TITLE);
		dimension = c.getInteger(NBT_DIMENSION);
		if (unloaded != null) {
			unloaded = c.getLong(NBT_UNLOADED);
		} else {
			c.removeTag(NBT_UNLOADED);
		}

		if (spawned != null) {
			unloaded = c.getLong(NBT_UNLOADED);
		} else {
			c.removeTag(NBT_UNLOADED);
		}
		spawned = c.getInteger(NBT_SPAWNED);
		readTraits(c);
		loadAllItems(NBT_HANDS, c, handInventory);
		loadAllItems(NBT_ARMOR, c, armorInventory);
	}

	public NBTTagCompound writeToNBT(NBTTagCompound c) {
		c.setString(NBT_NAME, name);
		c.setInteger(NBT_LEVEL, level);
		c.setString(NBT_MOB, mob);
		c.setInteger(NBT_X, x);
		c.setInteger(NBT_Z, z);
		c.setUniqueId(NBT_ID, id);
		c.setString(NBT_TITLE, title);
		c.setInteger(NBT_DIMENSION, dimension);
		unloaded = null;
		if (c.hasKey(NBT_UNLOADED)) {
			c.setLong(NBT_UNLOADED, unloaded);
		}
		c.setInteger(NBT_SPAWNED, spawned);
		writeTraits(c);
		saveAllItems(NBT_HANDS, c, handInventory);
		saveAllItems(NBT_ARMOR, c, armorInventory);
		return c;
	}

	private void readTraits(NBTTagCompound c) {
		traits = new ArrayList<>();
		NBTTagList l = null;
		try {
			l = (NBTTagList) c.getTag(NBT_TRAITS);
		} catch (Exception ignored) {

		}
		if (l == null) {
			l = new NBTTagList();
		}

		for (int i = 0; i < l.tagCount(); i++) {
			traits.add(Trait.values()[l.getIntAt(i)]);
		}
	}

	private void writeTraits(NBTTagCompound c) {
		NBTTagList l = new NBTTagList();
		for (Trait t : traits) {
			l.appendTag(new NBTTagInt(t.ordinal()));
		}
		c.setTag(NBT_TRAITS, l);
	}

	public static void saveAllItems(String key, NBTTagCompound tag, NonNullList<ItemStack> list) {
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < list.size(); ++i) {
			ItemStack itemstack = list.get(i);

			if (!itemstack.isEmpty()) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte) i);
				itemstack.writeToNBT(nbttagcompound);
				nbttaglist.appendTag(nbttagcompound);
			}
		}

		if (!nbttaglist.hasNoTags()) {
			tag.setTag(key, nbttaglist);
		}
	}

	public static void loadAllItems(String key, NBTTagCompound tag, NonNullList<ItemStack> list) {
		NBTTagList nbttaglist = tag.getTagList(key, 10);

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;

			if (j >= 0 && j < list.size()) {
				list.set(j, new ItemStack(nbttagcompound));
			}
		}
	}

	public static class LogEntry {
		@NbtField
		private LogType type;

		private Map<String, String> details;

		@NbtField
		private long date;

		private LogEntry(LogType type, Map<String, String> details) {
			this.type = type;
			this.details = details;
			// TODO save world time instead?
			this.date = System.currentTimeMillis();
		}

		public enum LogType {
			KILLED, DIED, DUEL_WIN, DUEL_LOSS, PROMOTION, CREATION, FLED
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

		public LogType getType() {
			return type;
		}

		public Map<String, String> getDetails() {
			return details;
		}

		public long getDate() {
			return date;
		}
	}

	public void addToHistory(LogEntry logEntry) {
		if (history == null) {
			history = new ArrayList<>();
		}
		history.add(logEntry);
	}

	@SideOnly(Side.SERVER)
	public void markRegistryDirty() {
		World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(dimension);
		NemesisRegistryProvider.get(world).markDirty();
	}

	public String getName() {
		return name;
	}

	public String getNameAndTitle() {
		return name + " the " + title;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getMob() {
		return mob;
	}

	public void setMob(String mob) {
		this.mob = mob;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public NonNullList<ItemStack> getHandInventory() {
		return handInventory;
	}

	public NonNullList<ItemStack> getArmorInventory() {
		return armorInventory;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public List<Trait> getTraits() {
		return traits;
	}

	public void setTraits(List<Trait> traits) {
		this.traits = traits;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isDead() {
		return isDead;
	}

	public boolean isSpawned() {
		return spawned != 0;
	}

	public boolean isLoaded() {
		return unloaded == null;
	}

	public Long getUnloaded() {
		return unloaded;
	}

	public void setUnloaded(Long unloaded) {
		this.unloaded = unloaded;
	}

	public double getRangeSq() {
		return RANGE_SQ;
	}

	public int getDimension() {
		return dimension;
	}

	public void setDimension(int dimension) {
		this.dimension = dimension;
	}

	public Integer getSpawned() {
		return spawned;
	}

	public void setSpawned(Integer spawned) {
		this.spawned = spawned;
	}

	public void setDead(boolean dead) {
		isDead = dead;
	}

	public List<LogEntry> getHistory() {
		return history;
	}

	public void setHistory(List<LogEntry> history) {
		this.history = history;
	}
}
