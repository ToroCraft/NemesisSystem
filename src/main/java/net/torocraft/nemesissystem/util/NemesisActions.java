package net.torocraft.nemesissystem.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.torocraft.nemesissystem.NemesisConfig;
import net.torocraft.nemesissystem.events.DeathEvent;
import net.torocraft.nemesissystem.events.DemotionEvent;
import net.torocraft.nemesissystem.events.DuelEvent;
import net.torocraft.nemesissystem.events.PromotionEvent;
import net.torocraft.nemesissystem.registry.INemesisRegistry;
import net.torocraft.nemesissystem.registry.NemesisEntry;
import net.torocraft.nemesissystem.registry.NemesisRegistryProvider;
import net.torocraft.nemesissystem.traits.Trait;
import net.torocraft.nemesissystem.traits.Type;

public class NemesisActions {

	public static void promote(World world, NemesisEntry nemesis) {
		if (nemesis.getLevel() >= 10) {
			return;
		}
		nemesis.setLevel(nemesis.getLevel() + 1);
		NemesisUtil.enchantEquipment(nemesis);
		if (shouldGainAdditionalTrait(nemesis)) {
			addAdditionalStrength(nemesis);
		} else {
			upgradeTrait(nemesis);
		}
		NemesisRegistryProvider.get(world).update(nemesis);
		MinecraftForge.EVENT_BUS.post(new PromotionEvent(nemesis));
	}

	private static boolean shouldGainAdditionalTrait(NemesisEntry nemesis) {
		return NemesisUtil.rand.nextInt(10 * nemesis.getTraits().size()) == 0;
	}

	private static void addAdditionalStrength(NemesisEntry nemesis) {
		List<Type> availableTraits = Arrays.stream(Type.values())
				.filter(t -> !nemesis.hasTrait(t))
				.filter(t -> t.isStrength())
				.collect(Collectors.toList());

		if (availableTraits.size() < 1) {
			return;
		}

		Type type = availableTraits.get(NemesisUtil.rand.nextInt(availableTraits.size()));
		nemesis.getTraits().add(new Trait(type, 1));
	}

	private static void upgradeTrait(NemesisEntry nemesis) {
		for (Trait trait : nemesis.getTraits()) {
			if (trait.type.isStrength() && upgradeTrait(trait)) {
				return;
			}
		}
	}

	private static void intensifyWeakness(NemesisEntry nemesis) {
		for (Trait trait : nemesis.getTraits()) {
			if (trait.type.isWeakness() && upgradeTrait(trait)) {
				return;
			}
		}
	}

	private static boolean upgradeTrait(Trait t) {
		int max = t.type.getMaxLevel();
		if (t.level >= max) {
			return false;
		}
		t.level++;
		return true;
	}

	public static void demote(World world, NemesisEntry nemesis, String slayerName) {
		nemesis.setLevel(nemesis.getLevel() - 1);
		if (nemesis.getLevel() < 1) {
			kill(world, nemesis, slayerName);
		} else {
			if (NemesisUtil.rand.nextInt(4) == 0) {
				intensifyWeakness(nemesis);
			}
			NemesisRegistryProvider.get(world).update(nemesis);
			MinecraftForge.EVENT_BUS.post(new DemotionEvent(nemesis, slayerName));
		}
	}

	public static NemesisEntry createAndRegisterNemesis(EntityCreature entity, BlockPos nemesisLocation) {
		boolean isChild = false;
		if (entity instanceof EntityZombie) {
			isChild = entity.isChild();
		}
		NemesisEntry nemesis = NemesisBuilder
				.build(entity.getEntityWorld(), NemesisUtil.getEntityType(entity), isChild, entity.dimension, 1, nemesisLocation.getX(),
						nemesisLocation.getZ());
		NemesisRegistryProvider.get(entity.world).register(nemesis);
		return nemesis;
	}

	public static void kill(World world, NemesisEntry nemesis, String slayerName) {
		nemesis.setSpawned(null);
		nemesis.setDead(true);
		NemesisRegistryProvider.get(world).update(nemesis);
		MinecraftForge.EVENT_BUS.post(new DeathEvent(nemesis, slayerName));
	}

	public static void duel(World world, NemesisEntry opponentOne, NemesisEntry opponentTwo) {
		NemesisEntry victor;
		NemesisEntry loser;

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
		MinecraftForge.EVENT_BUS.post(new DuelEvent(victor, loser));
	}

	public static void duelIfCrowded(World world, NemesisEntry exclude, boolean onlyIfCrowded) {
		List<NemesisEntry> nemeses = NemesisRegistryProvider.get(world).list();
		nemeses.removeIf(NemesisEntry::isDead);

		if (onlyIfCrowded && nemeses.size() < NemesisConfig.NEMESIS_LIMIT) {
			return;
		}

		nemeses.removeIf(NemesisEntry::isLoaded);
		if (exclude != null) {
			nemeses.removeIf((NemesisEntry n) -> n.getId().equals(exclude.getId()));
		}

		if (nemeses.size() < 2) {
			return;
		}

		//TODO factor in distance, the closer the nemeses the more likely they should be to duelIfCrowded

		// get the weaklings
		Collections.shuffle(nemeses);
		nemeses.sort(Comparator.comparingInt(NemesisEntry::getLevel));
		duel(world, nemeses.get(0), nemeses.get(1));
	}

	public static void promoteRandomNemesis(EntityCreature entity, INemesisRegistry registry, List<NemesisEntry> nemeses) {
		if (nemeses == null || nemeses.size() < 1) {
			return;
		}
		promote(entity.world, nemeses.get(entity.getRNG().nextInt(nemeses.size())));
	}

	public static void handleRandomPromotions(World world, EntityCreature entity) {
		INemesisRegistry registry = NemesisRegistryProvider.get(world);

		List<NemesisEntry> nemeses = registry.list();
		nemeses.removeIf(NemesisEntry::isDead);

		if (nemeses.size() >= (NemesisConfig.NEMESIS_LIMIT / 2)) {
			return;
		}

		promoteRandomNemesis(entity, registry, nemeses);
		createAndRegisterNemesis(entity, NemesisUtil.getRandomLocationAround(entity));
	}

	public static void throwPearl(EntityLiving entity, EntityLivingBase target) {
		World world = entity.getEntityWorld();
		EntityEnderPearl pearl = new EntityEnderPearl(world, entity);

		double dX = target.posX - entity.posX;
		double dY = target.getEntityBoundingBox().minY + (double) (target.height / 3.0F) - pearl.posY;
		double dZ = target.posZ - entity.posZ;

		double distanceSq = dX * dX + dY * dY + dZ * dZ;

		if (distanceSq < 20) {
			return;
		}

		double levelDistance = MathHelper.sqrt(dX * dX + dZ * dZ);

		pearl.setThrowableHeading(dX, dY + levelDistance * 0.20000000298023224D, dZ, 1.6F,
				(float) (14 - world.getDifficulty().getDifficultyId() * 4));

		entity.playSound(SoundEvents.ENTITY_ENDERPEARL_THROW, 1.0F, 1.0F / (world.rand.nextFloat() * 0.4F + 0.8F));

		world.spawnEntity(pearl);
	}
}
