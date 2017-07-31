package net.torocraft.nemesissystem.handlers;

import static net.torocraft.nemesissystem.util.DiscoveryUtil.NBT_DISCOVERY;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.nemesissystem.discovery.NemesisKnowledge;
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

	private NemesisKnowledge getDiscoveryFromBook(ItemStack item) {
		NemesisKnowledge discovery = new NemesisKnowledge(null);
		discovery.readFromNBT(item.getTagCompound().getCompoundTag(NBT_DISCOVERY));
		return discovery;
	}

	private NemesisKnowledge map(NBTBase tag) {
		NemesisKnowledge d = new NemesisKnowledge(null);
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
