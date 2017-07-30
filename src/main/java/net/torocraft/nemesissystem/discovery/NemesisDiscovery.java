package net.torocraft.nemesissystem.discovery;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import jline.internal.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.torocraft.nemesissystem.util.nbt.NbtField;
import net.torocraft.nemesissystem.util.nbt.NbtSerializer;

public class NemesisDiscovery {

	@NbtField
	private UUID nemesisId;

	@NbtField
	private boolean name;

	@NbtField
	private boolean location;

	@NbtField(genericType = Integer.class)
	public Set<Integer> traits = new HashSet<>();

	public NemesisDiscovery(@Nullable UUID nemesisId) {
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

