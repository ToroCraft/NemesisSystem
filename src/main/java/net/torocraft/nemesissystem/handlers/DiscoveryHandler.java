package net.torocraft.nemesissystem.handlers;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.discovery.NemesisDiscovery;
import net.torocraft.nemesissystem.discovery.NemesisKnowledge;
import net.torocraft.nemesissystem.events.DiscoveryEvent;
import net.torocraft.nemesissystem.network.MessageOpenNemesisDetailsGui;
import net.torocraft.nemesissystem.registry.NemesisEntry;
import net.torocraft.nemesissystem.registry.NemesisRegistryProvider;
import net.torocraft.nemesissystem.util.DiscoveryUtil;

public class DiscoveryHandler {

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new DiscoveryHandler());
	}

	// TODO notify player of the discovery and open the GUI to the new info (hi-light the new info)

	@SubscribeEvent
	public void readBook(RightClickItem event) {

		if (event.getWorld().isRemote) {
			return;
		}

		ItemStack item = event.getItemStack();

		if (!DiscoveryUtil.isDiscoveryBook(item)) {
			return;
		}

		NemesisDiscovery discovery = DiscoveryUtil.readBook(event.getWorld(), item);

		if (discovery == null) {
			System.out.println("ERROR: No discovery found in book");
			return;
		}

		DiscoveryUtil.newDiscovery(event.getEntityPlayer(), discovery);
		NemesisEntry nemesis = NemesisRegistryProvider.get(event.getWorld()).getById(discovery.nemesisId);

		if (nemesis == null) {
			System.out.println("ERROR: Nemesis not found");
			return;
		}

		NemesisKnowledge knowledge = DiscoveryUtil.getGetPlayerKnowledgeOfNemesis(event.getEntityPlayer(), nemesis.getId());

		if (knowledge == null) {
			knowledge = new NemesisKnowledge();
		}

		MinecraftForge.EVENT_BUS.post(new DiscoveryEvent(nemesis, discovery, event.getEntityPlayer()));
		NemesisSystem.NETWORK.sendTo(new MessageOpenNemesisDetailsGui(nemesis, knowledge), (EntityPlayerMP) event.getEntityPlayer());
		event.setCanceled(true);
	}

}
