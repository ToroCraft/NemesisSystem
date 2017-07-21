package net.torocraft.nemesissystem.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.nemesissystem.util.nbt.NbtField;
import net.torocraft.nemesissystem.util.nbt.NbtSerializer;
import scala.tools.nsc.doc.model.Trait;

public class Nemesis {

	/**
	 * the range of the nemesis's domain, setting to 50 would make a 100x100 block domain
	 */
	private static final int RANGE = 50;

	public enum Trait {DOUBLE_MELEE, ARROW, SUMMON, REFLECT, HEAT, POTION, TELEPORT, FIREBALL, HEAL}

	public enum Weakness {HYDROPHOBIA, PYROPHOBIA, WOOD_ALLERGY, GOLD_ALLERGY, STONE_ALLERGY, GREEDY, GLUTTONY, CHICKEN } // AMOROUS, DANCE, PLASMOPHOBIA, ICHTHYOPHOBIA, ANIMAL_LOVER

	public enum Strength {}

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
	private Long lastSpawned;

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

	@NbtField
	private UUID entityUuid;

	@NbtField(genericType = Trait.class)
	private List<Trait> traits = new ArrayList<>();

	@NbtField(genericType = Strength.class)
	private List<Strength> strengths = new ArrayList<>();

	@NbtField(genericType = Weakness.class)
	private List<Weakness> weaknesses = new ArrayList<>();

	@NbtField(genericType = LogEntry.class)
	private List<LogEntry> history = new ArrayList<>();

	@NbtField
	private int dimension;

	@NbtField
	private int isChild;

	//TODO spawned check

	private transient boolean isDead;

	@NbtField
	private NonNullList<ItemStack> handInventory = NonNullList.withSize(2, ItemStack.EMPTY);

	@NbtField
	private NonNullList<ItemStack> armorInventory = NonNullList.withSize(4, ItemStack.EMPTY);

	public void register(World world) {
		NemesisRegistryProvider.get(world).register(this);
	}

	@Override
	public String toString() {
		return getNameAndTitle();

//				name + " the " + title + " (" + (unloaded == null ? "LOADED" : "UNLOADED") + " " + (isSpawned() ? "SPAWNED" : "NOT_SPAWNED")
//				+ " level:" + level + " loc:" + x + "," + z + ") " + mob + " "
//				+ traits.get(0);
	}

	public void readFromNBT(NBTTagCompound c) {
		NbtSerializer.read(c, this);
	}

	public void writeToNBT(NBTTagCompound c) {
		NbtSerializer.write(c, this);
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
		if (name == null) {
			return "Nobody";
		}
		return name;
	}

	public String getNameAndTitle() {
		return getName() + " the " + getTitle();
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

	public List<Weakness> getWeaknesses() { return weaknesses; }

	public void setWeaknesses(List<Weakness> weaknesses) { this.weaknesses = weaknesses; }

	public List<Strength> getStrengths() { return strengths; }

	public void setStrengths(List<Strength> strengths) { this.strengths = strengths; }

	public String getTitle() {
		if (title == null) {
			return "Unknown";
		}
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isDead() {
		return isDead;
	}

	public boolean isSpawned() {
		return spawned != null;
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

	public int getRange() {
		return RANGE;
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

	public void setHandInventory(NonNullList<ItemStack> handInventory) {
		this.handInventory = handInventory;
	}

	public void setArmorInventory(NonNullList<ItemStack> armorInventory) {
		this.armorInventory = armorInventory;
	}

	public UUID getEntityUuid() {
		return entityUuid;
	}

	public void setEntityUuid(UUID entityUuid) {
		this.entityUuid = entityUuid;
	}

	public Long getLastSpawned() {
		return lastSpawned;
	}

	public void setLastSpawned(Long lastSpawned) {
		this.lastSpawned = lastSpawned;
	}

	public int getIsChild() { return isChild; }

	public boolean isChild() { return isChild == 1; }

	public void setChild(int child) { isChild = child; }
}
