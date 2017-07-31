package net.torocraft.nemesissystem.discovery;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import jline.internal.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.torocraft.nemesissystem.util.nbt.NbtField;
import net.torocraft.nemesissystem.util.nbt.NbtSerializer;

/**
 * This class holds information on what discoveries have been
 * made for a given nemesis.
 */
public class NemesisKnowledge {

	@NbtField
	public UUID nemesisId;

	/**
	 * true if the name of the nemesis is known
	 */
	@NbtField
	public boolean name;

	/**
	 * true if the location of the nemesis is known
	 */
	@NbtField
	public boolean location;

	/**
	 * each integer in this set is the index of
	 * a nemesis's trait that has been discovered
	 */
	@NbtField(genericType = Integer.class)
	public Set<Integer> traits = new HashSet<>();

	public NemesisKnowledge(@Nullable UUID nemesisId) {
		this.nemesisId = nemesisId;
	}

	public void readFromNBT(NBTTagCompound c) {
		NbtSerializer.read(c, this);
	}

	public void writeToNBT(NBTTagCompound c) {
		NbtSerializer.write(c, this);
	}

	public void setNemesisId(UUID nemesisId) {
		this.nemesisId = nemesisId;
	}

	public UUID getNemesisId() {
		return nemesisId;
	}

	public boolean isName() {
		return name;
	}

	public void setName(boolean name) {
		this.name = name;
	}

	public boolean isLocation() {
		return location;
	}

	public void setLocation(boolean location) {
		this.location = location;
	}

	public Set<Integer> getTraits() {
		return traits;
	}

	public void setTraits(Set<Integer> traits) {
		this.traits = traits;
	}
}

