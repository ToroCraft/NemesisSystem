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

public class Nemesis {

	private static final int RANGE_SQ = 100 * 100;

	public enum Trait {DOUBLE_MELEE, ARROW, SUMMON, REFLECT, HEAT, POTION, SHIELD, TELEPORT, FIREBALL, HEAL}

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

	@NbtField
	private NonNullList<ItemStack> handInventory = NonNullList.withSize(2, ItemStack.EMPTY);

	@NbtField
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

	public void setHandInventory(NonNullList<ItemStack> handInventory) {
		this.handInventory = handInventory;
	}

	public void setArmorInventory(NonNullList<ItemStack> armorInventory) {
		this.armorInventory = armorInventory;
	}
}
