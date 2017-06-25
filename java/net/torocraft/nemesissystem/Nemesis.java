package net.torocraft.nemesissystem;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Nemesis {

	private static final String NBT_NAME = "name";
	private static final String NBT_NEMESIS_OF = "nemesisOf";
	private static final String NBT_LEVEL = "level";
	private static final String NBT_MOB = "mob";
	private static final String NBT_X = "x";
	private static final String NBT_Z = "z";

	private String name;
	private String nemesisOf;
	private int level;
	private String mob;
	private int x;
	private int z;

	//TODO armor / attributes

	public void spawn(World world, BlockPos pos) {
		SpawnUtil.spawn(world, this, pos);
	}

	public void register(World world) {
		NemesisRegistryProvider.get(world).register(this);
	}

	@Override
	public String toString() {
		return name + " nemesis of " + nemesisOf + " (level:" + level + " chunk:" + x + "," + z + ")";
	}

	public void readFromNBT(NBTTagCompound c) {
		name = c.getString(NBT_NAME);
		nemesisOf = c.getString(NBT_NEMESIS_OF);
		level = c.getInteger(NBT_LEVEL);
		mob = c.getString(NBT_MOB);
		x = c.getInteger(NBT_X);
		z = c.getInteger(NBT_Z);
	}

	public NBTTagCompound writeToNBT(NBTTagCompound c) {
		c.setString(NBT_NAME, name);
		c.setString(NBT_NEMESIS_OF, nemesisOf);
		c.setInteger(NBT_LEVEL, level);
		c.setString(NBT_MOB, mob);
		c.setInteger(NBT_X, x);
		c.setInteger(NBT_Z, z);
		return c;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNemesisOf() {
		return nemesisOf;
	}

	public void setNemesisOf(String nemesisOf) {
		this.nemesisOf = nemesisOf;
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
}
