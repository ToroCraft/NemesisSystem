package net.torocraft.nemesissystem.discovery;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.torocraft.nemesissystem.util.DiscoveryUtil;
import net.torocraft.nemesissystem.util.nbt.NbtField;
import net.torocraft.nemesissystem.util.nbt.NbtSerializer;

public class PlayerDiscoveries {

	@NbtField(genericType = PlayerDiscoveries.class)
	private Map<String, NemesisDiscovery> discoveries = new HashMap<>();

	public void add(NemesisDiscovery newDiscovery) {
		String id = newDiscovery.getNemesisId().toString();
		NemesisDiscovery existingDiscovery = discoveries.get(id);
		discoveries.put(id, merge(existingDiscovery, newDiscovery));
	}

	public static NemesisDiscovery merge(NemesisDiscovery discovery1, NemesisDiscovery discovery2) {
		NemesisDiscovery discovery = new NemesisDiscovery(discovery1.getNemesisId());
		discovery.setName(discovery1.isName() || discovery2.isName());
		discovery.setLocation(discovery1.isLocation() || discovery2.isLocation());
		discovery.getTraits().addAll(discovery1.getTraits());
		discovery.getTraits().addAll(discovery2.getTraits());
		return discovery;
	}

	public static PlayerDiscoveries get(EntityPlayer player) {
		PlayerDiscoveries discoveries = new PlayerDiscoveries();
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
	public NemesisDiscovery getDiscovery(@Nullable UUID nemesisId) {
		if (nemesisId == null) {
			return new NemesisDiscovery(UUID.randomUUID());
		}
		NemesisDiscovery d = discoveries.get(nemesisId.toString());
		if (d == null) {
			return new NemesisDiscovery(nemesisId);
		}
		return d;
	}

	public Map<String, NemesisDiscovery> getDiscoveries() {
		return discoveries;
	}

	public void setDiscoveries(Map<String, NemesisDiscovery> discoveries) {
		this.discoveries = discoveries;
	}
}

