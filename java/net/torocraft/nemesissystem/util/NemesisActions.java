package net.torocraft.nemesissystem.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.torocraft.nemesissystem.NemesisConfig;
import net.torocraft.nemesissystem.events.NemesisEvent;
import net.torocraft.nemesissystem.registry.INemesisRegistry;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.registry.Nemesis.Trait;
import net.torocraft.nemesissystem.registry.NemesisRegistryProvider;

public class NemesisActions {

	public static void promote(World world, Nemesis nemesis) {
		nemesis.setLevel(nemesis.getLevel() + 1);
		NemesisUtil.enchantEquipment(nemesis);
		if (shouldGainAdditionalTrait(nemesis)) {
			addAdditionalTrait(nemesis);
		}
		NemesisRegistryProvider.get(world).update(nemesis);
		MinecraftForge.EVENT_BUS.post(new NemesisEvent.Promotion(nemesis));
	}

	public static void demote(World world, Nemesis nemesis, String slayerName) {
		nemesis.setLevel(nemesis.getLevel() - 1);
		if (nemesis.getLevel() < 1) {
			kill(world, nemesis, slayerName);
		}else{
			NemesisRegistryProvider.get(world).update(nemesis);
			MinecraftForge.EVENT_BUS.post(new NemesisEvent.Demotion(nemesis, slayerName));
		}
	}

	private static void addAdditionalTrait(Nemesis nemesis) {
		List<Trait> availableTraits = Arrays.stream(Trait.values())
				.filter((Trait t) -> !nemesis.getTraits().contains(t))
				.collect(Collectors.toList());

		if (availableTraits.size() < 1) {
			return;
		}

		Trait newTrait = availableTraits.get(NemesisUtil.rand.nextInt(availableTraits.size()));
		nemesis.getTraits().add(newTrait);
		System.out.println("new trait: " + newTrait);
	}

	private static boolean shouldGainAdditionalTrait(Nemesis nemesis) {
		return NemesisUtil.rand.nextInt(10 * nemesis.getTraits().size()) == 0;
	}

	public static Nemesis createAndRegisterNemesis(EntityCreature entity, BlockPos nemesisLocation) {
		Nemesis nemesis = NemesisBuilder.build(NemesisUtil.getEntityType(entity), entity.dimension, 1, nemesisLocation.getX(), nemesisLocation.getZ());
		NemesisRegistryProvider.get(entity.world).register(nemesis);
		return nemesis;
	}

	public static void kill(World world, Nemesis nemesis, String slayerName) {
		nemesis.setSpawned(null);
		nemesis.setDead(true);
		NemesisRegistryProvider.get(world).update(nemesis);
		MinecraftForge.EVENT_BUS.post(new NemesisEvent.Death(nemesis, slayerName));
	}

	public static void duel(World world, Nemesis opponentOne, Nemesis opponentTwo) {
		Nemesis victor;
		Nemesis loser;

		int attack1 = 0;
		int attack2 = 0;

		while (attack1 == attack2) {
			attack1 = NemesisUtil.rand.nextInt(opponentOne.getLevel()) + NemesisUtil.rand.nextInt(3);
			attack2 = NemesisUtil.rand.nextInt(opponentOne.getLevel()) + NemesisUtil.rand.nextInt(3);
		}

		if (attack1 > attack2) {
			victor = opponentOne;
			loser = opponentTwo;
		} else {
			victor = opponentTwo;
			loser = opponentOne;
		}

		kill(world, loser, victor.getName());
		promote(world, victor);
		MinecraftForge.EVENT_BUS.post(new NemesisEvent.Duel(victor, loser));
	}

	public static void duelIfCrowded(World world, Nemesis exclude, boolean onlyIfCrowded) {
		List<Nemesis> nemeses = NemesisRegistryProvider.get(world).list();
		nemeses.removeIf(Nemesis::isDead);

		if (onlyIfCrowded && nemeses.size() < NemesisConfig.NEMESIS_LIMIT) {
			return;
		}

		nemeses.removeIf(Nemesis::isLoaded);
		if (exclude != null) {
			nemeses.removeIf((Nemesis n) -> n.getId().equals(exclude.getId()));
		}

		if (nemeses.size() < 2) {
			return;
		}

		//TODO factor in distance, the closer the nemeses the more likely they should be to duelIfCrowded

		// get the weaklings
		Collections.shuffle(nemeses);
		nemeses.sort(Comparator.comparingInt(Nemesis::getLevel));
		duel(world, nemeses.get(0), nemeses.get(1));
	}

	public static void promoteRandomNemesis(EntityCreature entity, INemesisRegistry registry, List<Nemesis> nemeses) {
		if (nemeses == null || nemeses.size() < 1) {
			return;
		}
		promote(entity.world, nemeses.get(entity.getRNG().nextInt(nemeses.size())));
	}

	public static void handleRandomPromotions(World world, EntityCreature entity) {
		INemesisRegistry registry = NemesisRegistryProvider.get(world);

		List<Nemesis> nemeses = registry.list();
		nemeses.removeIf(Nemesis::isDead);

		if (nemeses.size() >= (NemesisConfig.NEMESIS_LIMIT / 2)) {
			return;
		}

		promoteRandomNemesis(entity, registry, nemeses);
		createAndRegisterNemesis(entity, NemesisUtil.getRandomLocationAround(entity));
	}
}
