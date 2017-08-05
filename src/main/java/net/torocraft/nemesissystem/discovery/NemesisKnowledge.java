package net.torocraft.nemesissystem.discovery;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import jline.internal.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.torocraft.nemesissystem.util.nbt.NbtField;
import net.torocraft.nemesissystem.util.nbt.NbtSerializer;

/**
 * This class holds information on what knowledgeMap have been
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

}

