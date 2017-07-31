package net.torocraft.nemesissystem.util;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.torocraft.nemesissystem.discovery.NemesisDiscovery;
import net.torocraft.nemesissystem.discovery.NemesisKnowledge;
import net.torocraft.nemesissystem.discovery.PlayerKnowledgeBase;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.registry.NemesisRegistryProvider;

public class DiscoveryUtil {

	public static final String NBT_DISCOVERY = "nemesissystem_discovery";
	public static final String NBT_PLAYER_DISCOVERIES = "nemesissystem_player_discoveries";

	private static Random rand = new Random();

	private static int chance = 5;

	public static NemesisKnowledge getDiscoveriesFor(EntityPlayer player, UUID nemesisId) {
		return PlayerKnowledgeBase.get(player).getDiscovery(nemesisId);
	}

	public static void newDiscovery(EntityPlayer player, NemesisDiscovery discovery) {
		PlayerKnowledgeBase discoveries = PlayerKnowledgeBase.get(player);
		// TODO implement
		//discoveries.add(discovery);
		discoveries.writeToPlayer(player);
	}

	// TODO change to discovery
	public static NemesisKnowledge buildRandomDiscovery(World world) {
		List<Nemesis> nemeses = NemesisRegistryProvider.get(world).list();
		Nemesis nemesis = getRandomNemesis(nemeses);
		NemesisKnowledge discovery = new NemesisKnowledge(nemesis.getId());
		setRandomInformation(discovery, nemesis);
		return discovery;
	}

	private static Nemesis getRandomNemesis(List<Nemesis> nemeses) {
		return nemeses.get(rand.nextInt(nemeses.size()));
	}

	// TODO change to discovery
	public static void setRandomInformation(NemesisKnowledge discovery, Nemesis nemesis) {
		boolean hasAddedInfo = false;

		if (!discovery.isName()) {
			if (rand.nextInt(chance) == 0) {
				discovery.setName(true);
				hasAddedInfo = true;
			}
		}
		if (!discovery.isLocation()) {
			if (rand.nextInt(chance) == 0) {
				discovery.setLocation(true);
				hasAddedInfo = true;
			}
		}
		if (discovery.getTraits().size() < nemesis.getTraits().size()) {
			if (rand.nextInt(chance) == 0) {
				discovery.getTraits().add(getRandomTraitIndex(nemesis));
				hasAddedInfo = true;
			}
		}

		if (!hasAddedInfo) {
			setRandomInformation(discovery, nemesis);
		}
	}

	private static int getRandomTraitIndex(Nemesis nemesis) {
		return rand.nextInt(nemesis.getTraits().size());
	}

}
