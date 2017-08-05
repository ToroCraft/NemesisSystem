package net.torocraft.nemesissystem.util;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.torocraft.nemesissystem.discovery.NemesisDiscovery;
import net.torocraft.nemesissystem.discovery.NemesisDiscovery.Type;
import net.torocraft.nemesissystem.discovery.NemesisKnowledge;
import net.torocraft.nemesissystem.discovery.PlayerKnowledgeBase;
import net.torocraft.nemesissystem.registry.NemesisEntry;
import net.torocraft.nemesissystem.registry.NemesisRegistryProvider;

public class DiscoveryUtil {

	public static final String NBT_DISCOVERY = "nemesissystem_discovery";
	public static final String NBT_PLAYER_DISCOVERIES = "nemesissystem_player_discoveries";

	private static Random rand = new Random();

	public static NemesisKnowledge getGetPlayerKnowledgeOfNemesis(EntityPlayer player, UUID nemesisId) {
		return PlayerKnowledgeBase.get(player).getKnowledgeOfNemesis(nemesisId);
	}

	public static void newDiscovery(EntityPlayer player, NemesisDiscovery discovery) {
		PlayerKnowledgeBase knowledgeBase = PlayerKnowledgeBase.get(player);
		knowledgeBase.add(discovery);
		knowledgeBase.save(player);
	}

	public static ItemStack getRandomDiscoveryBook(EntityPlayer player) {
		return buildDiscoveryBook(DiscoveryUtil.getRandomDiscovery(player.world));
	}

	public static ItemStack buildDiscoveryBook(NemesisDiscovery discovery) {
		ItemStack book = new ItemStack(Items.WRITTEN_BOOK, 1);
		NBTTagCompound discoveryNbt = new NBTTagCompound();
		discovery.writeToNBT(discoveryNbt);

		NBTTagCompound bookNbt = new NBTTagCompound();
		bookNbt.setTag(NBT_DISCOVERY, discoveryNbt);
		book.setTagCompound(bookNbt);

		return book;
	}

	/**
	 * Get a random discovery for a random nemesis
	 */
	public static NemesisDiscovery getRandomDiscovery(World world) {
		return getRandomDiscovery(getRandomNemesis(world));
	}

	private static NemesisEntry getRandomNemesis(World world) {
		List<NemesisEntry> nemeses = NemesisRegistryProvider.get(world).list();
		return nemeses.get(rand.nextInt(nemeses.size()));
	}

	/**
	 * Get a random discovery about the given nemesis
	 */
	public static NemesisDiscovery getRandomDiscovery(NemesisEntry nemesis) {
		NemesisDiscovery discovery = new NemesisDiscovery();
		discovery.nemesisId = nemesis.getId();

		int infoCount = 2 + nemesis.getTraits().size();

		int roll = rand.nextInt(infoCount);

		if (roll == 0) {
			discovery.type = Type.NAME;
			return discovery;
		}

		if (roll == 1) {
			discovery.type = Type.LOCATION;
			return discovery;
		}

		discovery.type = Type.TRAIT;
		discovery.index = roll - 2;
		return discovery;
	}

	private static int getRandomTraitIndex(NemesisEntry nemesis) {
		return rand.nextInt(nemesis.getTraits().size());
	}

}
