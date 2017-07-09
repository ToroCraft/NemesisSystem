package net.torocraft.nemesissystem.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class Nemesis {

	public enum Title {}

	private static final int RANGE_SQ = 100 * 100;

	public enum Trait {DOUBLE_MELEE, ARROW, SUMMON, REFLECT, HEAT, POTION, SHIELD, TELEPORT}
	// TODO: FIREBALL, LASER, HEAL, POTION PROTECTION

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
	private static final String NBT_LOADED = "loaded";

	private String title;
	private String name;
	private int level;
	private String mob;
	private int x;
	private int z;
	private UUID id;
	private List<Trait> traits;
	private Integer loaded;

	/**
	 * This field is not persisted
	 */
	private boolean dead;

	//TODO spawned check

	private NonNullList<ItemStack> handInventory = NonNullList.withSize(2, ItemStack.EMPTY);
	private NonNullList<ItemStack> armorInventory = NonNullList.withSize(4, ItemStack.EMPTY);

	public void register(World world) {
		NemesisRegistryProvider.get(world).register(this);
	}

	@Override
	public String toString() {
		return name + " the " + title + " (" + (loaded != null ? "LOADED" : "UNLOADED" ) + " level:" + level + " loc:" + x + "," + z + ") " + mob + " " + traits.get(0);
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
		if(c.hasKey(NBT_LOADED)){
			loaded = c.getInteger(NBT_LOADED);
		}
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
		if(loaded != null){
			c.setInteger(NBT_LOADED, loaded);
		}
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
			Nemesis nemesis = new Nemesis();
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
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}

	public boolean isLoaded() {
		return loaded != null;
	}

	public Integer getLoaded() {
		return loaded;
	}

	public void setLoaded(Integer loaded) {
		this.loaded = loaded;
	}

	public double getRangeSq() {
		return RANGE_SQ;
	}
}
