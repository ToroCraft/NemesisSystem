package net.torocraft.nemesissystem.discovery;

import java.util.UUID;
import net.minecraft.nbt.NBTTagCompound;
import net.torocraft.nemesissystem.util.nbt.NbtField;
import net.torocraft.nemesissystem.util.nbt.NbtSerializer;

/**
 * This class represents a piece of information that has been discovered about an nemesis.
 */
public class NemesisDiscovery {

	public enum Type {NAME, LOCATION, TRAIT}

	@NbtField
	public UUID nemesisId;

	@NbtField
	public Type type;

	/**
	 * this field is used to hold the index of the discovered trait (type=TRAIT)
	 */
	@NbtField
	public int index;

	public void readFromNBT(NBTTagCompound c) {
		NbtSerializer.read(c, this);
	}

	public void writeToNBT(NBTTagCompound c) {
		NbtSerializer.write(c, this);
	}

	@Override
	public String toString() {
		if (nemesisId == null) {
			return "Nobody";
		}
		return type + " [" + index + "]";
	}
}
