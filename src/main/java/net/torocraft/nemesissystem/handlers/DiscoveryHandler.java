package net.torocraft.nemesissystem.handlers;

import static net.torocraft.nemesissystem.util.DiscoveryUtil.NBT_DISCOVERY;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.discovery.NemesisDiscovery;
import net.torocraft.nemesissystem.events.DiscoveryEvent;
import net.torocraft.nemesissystem.network.MessageOpenNemesisDetailsGui;
import net.torocraft.nemesissystem.registry.NemesisEntry;
import net.torocraft.nemesissystem.registry.NemesisRegistryProvider;
import net.torocraft.nemesissystem.util.DiscoveryUtil;

public class DiscoveryHandler {

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new DiscoveryHandler());
	}

	// TODO write valid signed book NBT

	// TODO refactor to use store the type of discovery instead of a discovery object

	// TODO detect if this was already known

	// TODO notify player of the discovery and open the GUI to the new info (hi-light the new info)

	@SubscribeEvent
	public void readBook(PlayerInteractEvent event) {

		if (event.getWorld().isRemote) {
			return;
		}

		ItemStack item = event.getItemStack();

		if (!isDiscoveryBook(item)) {
			return;
		}

		NemesisDiscovery discovery = getDiscoveryFromBook(item);

		if (discovery == null) {
			System.out.println("No discovery found in book");
			return;
		}

		System.out.println("Book Nemesis ID: " + discovery.nemesisId);

		DiscoveryUtil.newDiscovery(event.getEntityPlayer(), discovery);
		NemesisEntry nemesis = NemesisRegistryProvider.get(event.getWorld()).getById(discovery.nemesisId);

		if (nemesis == null) {
			System.out.println("Nemesis not found");
			return;
		}

		System.out.println("Reading DiscoveryHandler: " + discovery);

		MinecraftForge.EVENT_BUS.post(new DiscoveryEvent(nemesis, discovery, event.getEntityPlayer()));

		NemesisSystem.NETWORK.sendTo(new MessageOpenNemesisDetailsGui(nemesis), (EntityPlayerMP) event.getEntityPlayer());

		event.setCanceled(true);
	}

	private NemesisDiscovery getDiscoveryFromBook(ItemStack item) {
		NemesisDiscovery discovery = new NemesisDiscovery();
		discovery.readFromNBT(item.getTagCompound().getCompoundTag(NBT_DISCOVERY));
		return discovery;
	}

	private boolean isDiscoveryBook(ItemStack item) {
		if (item == null || item.isEmpty()) {
			return false;
		}
		return item.getItem() == Items.WRITTEN_BOOK && item.hasTagCompound() && item.getTagCompound().hasKey(NBT_DISCOVERY);
	}
}
