package net.torocraft.nemesissystem.discovery;

import java.util.UUID;
import net.minecraft.nbt.NBTTagCompound;
import net.torocraft.nemesissystem.util.NemesisUtil;
import net.torocraft.torotraits.nbt.NbtField;
import net.torocraft.torotraits.nbt.NbtSerializer;

/**
 * This class represents a piece of information that has been discovered about an nemesis.
 */
public class NemesisDiscovery {

	public enum Type {LOCATION, TRAIT}

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
			return "";
		}

		if (Type.LOCATION.equals(type)) {
			return "Location";
		}

		if (Type.TRAIT.equals(type)) {
			return "Trait " + NemesisUtil.romanize(index);
		}

		return type + " [" + index + "]";
	}
}
