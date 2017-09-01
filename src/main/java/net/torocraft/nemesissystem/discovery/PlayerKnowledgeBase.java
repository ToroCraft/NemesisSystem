package net.torocraft.nemesissystem.discovery;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.torocraft.nemesissystem.util.DiscoveryUtil;
import net.torocraft.torotraits.nbt.NbtField;
import net.torocraft.torotraits.nbt.NbtSerializer;

/**
 * This class holds all of the discovered nemesis knowledgeMap
 * for a given player
 */
public class PlayerKnowledgeBase {

	@NbtField(genericType = NemesisKnowledge.class)
	public Map<String, NemesisKnowledge> knowledgeMap;

	public void add(NemesisDiscovery newDiscovery) {
		if (newDiscovery == null || newDiscovery.nemesisId == null) {
			return;
		}

		if (knowledgeMap == null) {
			knowledgeMap = new HashMap<>();
		}

		NemesisKnowledge currentKnowledge = knowledgeMap.get(newDiscovery.nemesisId.toString());

		if (currentKnowledge == null) {
			currentKnowledge = new NemesisKnowledge();
			currentKnowledge.nemesisId = newDiscovery.nemesisId;
		}

		currentKnowledge.name = true;

		switch (newDiscovery.type) {
		case LOCATION:
			currentKnowledge.location = true;
			break;
		case ITEMS:
			currentKnowledge.items = true;
		case TRAIT:
			if (currentKnowledge.traits == null) {
				currentKnowledge.traits = new HashSet<>();
			}
			currentKnowledge.traits.add(newDiscovery.index);
			break;
		}

		this.knowledgeMap.put(newDiscovery.nemesisId.toString(), currentKnowledge);
	}

	public static PlayerKnowledgeBase get(EntityPlayer player) {
		PlayerKnowledgeBase knowledgeBase = new PlayerKnowledgeBase();
		NBTTagCompound knowledgeBaseNbt = player.getEntityData().getCompoundTag(DiscoveryUtil.NBT_PLAYER_DISCOVERIES);
		NbtSerializer.read(knowledgeBaseNbt, knowledgeBase);
		if (knowledgeBase.knowledgeMap == null) {
			knowledgeBase.knowledgeMap = new HashMap<>();
		}
		return knowledgeBase;
	}

	public void save(EntityPlayer player) {
		NBTTagCompound c = new NBTTagCompound();
		NbtSerializer.write(c, this);
		player.getEntityData().setTag(DiscoveryUtil.NBT_PLAYER_DISCOVERIES, c);
	}

	public NemesisKnowledge getKnowledgeOfNemesis(@Nullable UUID nemesisId) {
		if (nemesisId == null || knowledgeMap == null) {
			return null;
		}
		return knowledgeMap.get(nemesisId.toString());
	}

	public static PlayerKnowledgeBase load(NBTTagCompound c) {
		PlayerKnowledgeBase knowledge = new PlayerKnowledgeBase();
		NbtSerializer.read(c, knowledge);
		return knowledge;
	}

	public static NBTTagCompound save(PlayerKnowledgeBase knowledge) {
		NBTTagCompound c = new NBTTagCompound();
		NbtSerializer.write(c, knowledge);
		return c;
	}

}

