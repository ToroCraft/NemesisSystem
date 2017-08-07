package net.torocraft.nemesissystem.discovery;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.nbt.NBTTagCompound;
import net.torocraft.nemesissystem.util.nbt.NbtData;
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

	public static NemesisKnowledge load(NBTTagCompound c) {
		NemesisKnowledge knowledge = new NemesisKnowledge();
		NbtSerializer.read(c, knowledge);
		return knowledge;
	}

	public static NBTTagCompound save(NemesisKnowledge knowledge) {
		NBTTagCompound c = new NBTTagCompound();
		NbtSerializer.write(c, knowledge);
		return c;
	}

}

