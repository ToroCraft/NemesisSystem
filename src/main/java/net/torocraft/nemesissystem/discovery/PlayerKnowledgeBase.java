package net.torocraft.nemesissystem.discovery;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.torocraft.nemesissystem.util.DiscoveryUtil;
import net.torocraft.nemesissystem.util.nbt.NbtField;
import net.torocraft.nemesissystem.util.nbt.NbtSerializer;

/**
 * This class holds all of the discovered nemesis knowledge
 * for a given player
 */
public class PlayerKnowledgeBase {

	@NbtField(genericType = PlayerKnowledgeBase.class)
	private Map<String, NemesisKnowledge> discoveries = new HashMap<>();

	public void add(NemesisDiscovery discovery) {
		NemesisKnowledge knowledge = discoveries.get(discovery.nemesisId.toString());
		if (knowledge == null) {
			knowledge = new NemesisKnowledge(discovery.nemesisId);
		}
		switch (discovery.type) {
		case NAME:
			knowledge.name = true;
			break;
		case LOCATION:
			knowledge.location = true;
			break;
		case TRAIT:
			if (knowledge.traits == null) {
				knowledge.traits = new HashSet<>();
			}
			knowledge.traits.add(discovery.index);
			break;
		}
		discoveries.put(discovery.nemesisId.toString(), knowledge);
	}

	public static NemesisKnowledge merge_OLD(NemesisKnowledge discovery1, NemesisKnowledge discovery2) {
		NemesisKnowledge discovery = new NemesisKnowledge(discovery1.getNemesisId());
		discovery.setName(discovery1.isName() || discovery2.isName());
		discovery.setLocation(discovery1.isLocation() || discovery2.isLocation());
		discovery.getTraits().addAll(discovery1.getTraits());
		discovery.getTraits().addAll(discovery2.getTraits());
		return discovery;
	}

	public static PlayerKnowledgeBase get(EntityPlayer player) {
		PlayerKnowledgeBase discoveries = new PlayerKnowledgeBase();
		discoveries.readFromPlayer(player);
		return discoveries;
	}

	public void readFromPlayer(EntityPlayer player) {
		NBTTagCompound c = player.getEntityData().getCompoundTag(DiscoveryUtil.NBT_PLAYER_DISCOVERIES);
		NbtSerializer.read(c, this);
	}

	public void writeToPlayer(EntityPlayer player) {
		NBTTagCompound c = new NBTTagCompound();
		NbtSerializer.write(c, this);
		player.getEntityData().setTag(DiscoveryUtil.NBT_PLAYER_DISCOVERIES, c);
	}

	@Nonnull
	public NemesisKnowledge getDiscovery(@Nullable UUID nemesisId) {
		if (nemesisId == null) {
			return new NemesisKnowledge(UUID.randomUUID());
		}
		NemesisKnowledge d = discoveries.get(nemesisId.toString());
		if (d == null) {
			return new NemesisKnowledge(nemesisId);
		}
		return d;
	}

	public Map<String, NemesisKnowledge> getDiscoveries() {
		return discoveries;
	}

	public void setDiscoveries(Map<String, NemesisKnowledge> discoveries) {
		this.discoveries = discoveries;
	}
}

