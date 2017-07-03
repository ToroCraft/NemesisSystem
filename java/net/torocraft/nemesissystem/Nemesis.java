package net.torocraft.nemesissystem;

import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Nemesis {

	private static final String NBT_NAME = "name";
	private static final String NBT_LEVEL = "level";
	private static final String NBT_MOB = "mob";
	private static final String NBT_X = "x";
	private static final String NBT_Z = "z";
	private static final String NBT_ARMOR = "armor";
	private static final String NBT_HANDS = "hands";

	private String name;
	private int level;
	private String mob;
	private int x;
	private int z;

	private NonNullList<ItemStack> handInventory = NonNullList.<ItemStack>withSize(2, ItemStack.EMPTY);
	private NonNullList<ItemStack> armorInventory = NonNullList.<ItemStack>withSize(4, ItemStack.EMPTY);

	//TODO armor / attributes

	public void spawn(World world, BlockPos pos) {
		SpawnUtil.spawn(world, this, pos);
	}

	public void register(World world) {
		NemesisRegistryProvider.get(world).register(this);
	}

	@Override
	public String toString() {
		return name + " (level:" + level + " chunk:" + x + "," + z + ")";
	}

	public void readFromNBT(NBTTagCompound c) {
		handInventory = NonNullList.<ItemStack>withSize(2, ItemStack.EMPTY);
		armorInventory = NonNullList.<ItemStack>withSize(4, ItemStack.EMPTY);
		name = c.getString(NBT_NAME);
		level = c.getInteger(NBT_LEVEL);
		mob = c.getString(NBT_MOB);
		x = c.getInteger(NBT_X);
		z = c.getInteger(NBT_Z);
		loadAllItems(NBT_HANDS, c, handInventory);
		loadAllItems(NBT_ARMOR, c, armorInventory);
	}

	public NBTTagCompound writeToNBT(NBTTagCompound c) {
		c.setString(NBT_NAME, name);
		c.setInteger(NBT_LEVEL, level);
		c.setString(NBT_MOB, mob);
		c.setInteger(NBT_X, x);
		c.setInteger(NBT_Z, z);
		saveAllItems(NBT_HANDS, c, handInventory);
		saveAllItems(NBT_ARMOR, c, armorInventory);
		return c;
	}

	public static NBTTagCompound saveAllItems(String key, NBTTagCompound tag, NonNullList<ItemStack> list) {
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

		return tag;
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
}
