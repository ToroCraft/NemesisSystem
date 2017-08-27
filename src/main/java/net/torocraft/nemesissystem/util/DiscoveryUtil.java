package net.torocraft.nemesissystem.util;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.world.World;
import net.torocraft.nemesissystem.discovery.NemesisDiscovery;
import net.torocraft.nemesissystem.discovery.NemesisDiscovery.Type;
import net.torocraft.nemesissystem.discovery.NemesisKnowledge;
import net.torocraft.nemesissystem.discovery.PlayerKnowledgeBase;
import net.torocraft.nemesissystem.registry.NemesisEntry;
import net.torocraft.nemesissystem.registry.NemesisRegistryProvider;

public class DiscoveryUtil {

	public static final String NBT_DISCOVERY = "nemesissystem_discovery";
	public static final String NBT_UNREAD_DISCOVERY = "nemesissystem_unread_discovery";
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

	public static boolean isDiscoveryBook(ItemStack item) {
		if (item == null || item.isEmpty()) {
			return false;
		}
		return item.getItem() == Items.WRITTEN_BOOK && hasDiscoveryTag(item);
	}

	private static boolean hasDiscoveryTag(ItemStack item) {
		if (!item.hasTagCompound()) {
			return false;
		}
		return item.getTagCompound().hasKey(NBT_DISCOVERY) || item.getTagCompound().hasKey(NBT_UNREAD_DISCOVERY);
	}

	public static ItemStack createUnreadBook() {
		ItemStack book = new ItemStack(Items.WRITTEN_BOOK, 1);
		NBTTagCompound bookNbt = new NBTTagCompound();
		bookNbt.setTag(NBT_UNREAD_DISCOVERY, new NBTTagInt(1));
		book.setTagCompound(bookNbt);
		return book;
	}

	public static NemesisDiscovery readBook(World world, ItemStack book) {
		if (isUnreadBook(book)) {
			setDiscoveryToBook(book, DiscoveryUtil.getRandomDiscovery(world));
		}
		NemesisDiscovery discovery = new NemesisDiscovery();
		discovery.readFromNBT(book.getTagCompound().getCompoundTag(NBT_DISCOVERY));
		return discovery;
	}

	private static boolean isUnreadBook(ItemStack book) {
		return  book.getTagCompound().hasKey(NBT_UNREAD_DISCOVERY);
	}

	public static ItemStack setDiscoveryToBook(ItemStack book, NemesisDiscovery discovery) {
		NBTTagCompound bookNbt = book.getTagCompound();
		bookNbt.removeTag(NBT_UNREAD_DISCOVERY);
		bookNbt.setTag(NBT_DISCOVERY, nbt(discovery));
		book.setTagCompound(bookNbt);
		return book;
	}

	private static NBTTagCompound nbt(NemesisDiscovery discovery) {
		NBTTagCompound discoveryNbt = new NBTTagCompound();
		discovery.writeToNBT(discoveryNbt);
		return discoveryNbt;
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
