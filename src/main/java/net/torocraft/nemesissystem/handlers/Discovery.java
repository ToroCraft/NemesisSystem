package net.torocraft.nemesissystem.handlers;

import static net.torocraft.nemesissystem.util.DiscoveryUtil.NBT_DISCOVERY;
import static net.torocraft.nemesissystem.util.DiscoveryUtil.NBT_PLAYER_DISCOVERIES;

import com.google.common.collect.Streams;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.nemesissystem.discovery.NemesisDiscovery;
import net.torocraft.nemesissystem.util.DiscoveryUtil;

public class Discovery {

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new Discovery());
	}


	// TODO write valid signed book NBT

	// TODO refactor to use store the type of discovery instead of a discovery object

	// TODO detect if this was already known

	// TODO notify player of the discovery and open the GUI to the new info (hi-light the new info)

	@SubscribeEvent
	public void readBook(PlayerInteractEvent event) {
		ItemStack item = event.getItemStack();
		if (isDiscoveryBook(item)) {
			DiscoveryUtil.newDiscovery(event.getEntityPlayer(), getDiscoveryFromBook(item));
		}
	}

	private NemesisDiscovery getDiscoveryFromBook(ItemStack item) {
		NemesisDiscovery discovery = new NemesisDiscovery(null);
		discovery.readFromNBT(item.getTagCompound().getCompoundTag(NBT_DISCOVERY));
		return discovery;
	}

	private NemesisDiscovery map(NBTBase tag) {
		NemesisDiscovery d = new NemesisDiscovery(null);
		d.readFromNBT((NBTTagCompound) tag);
		return d;
	}

	private boolean isDiscoveryBook(ItemStack item) {
		if (item == null || item.isEmpty()) {
			return false;
		}
		return item.getItem() == Items.WRITTEN_BOOK && item.hasTagCompound() && item.getTagCompound().hasKey(NBT_DISCOVERY);
	}
}
