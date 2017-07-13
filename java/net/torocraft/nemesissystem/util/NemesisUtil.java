package net.torocraft.nemesissystem.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import java.util.stream.Collectors;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.torocraft.nemesissystem.NemesisConfig;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.events.NemesisEvent;
import net.torocraft.nemesissystem.registry.INemesisRegistry;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.registry.Nemesis.Trait;
import net.torocraft.nemesissystem.registry.NemesisRegistryProvider;

public class NemesisUtil {

	private static final Random rand = new Random();

	public static String getEntityType(Entity entityIn) {
		EntityEntry entry = EntityRegistry.getEntry(entityIn.getClass());
		if (entry == null) {
			return "";
		}
		return entry.getRegistryName().toString();
	}

	public static boolean isNemesisClassEntity(Entity entity) {
		// TODO blacklist

		// TODO whitelist

		return entity instanceof EntityMob;
	}

	public static void setFollowSpeed(EntityCreature bodyGuard, double followSpeed) {
		EntityAIMoveTowardsRestriction ai = null;
		for (EntityAITaskEntry entry : bodyGuard.tasks.taskEntries) {
			if (entry.action instanceof EntityAIMoveTowardsRestriction) {
				ai = (EntityAIMoveTowardsRestriction) entry.action;
			}
		}
		if (ai == null) {
			System.out.println("guard ai not found");
			return;
		}
		//not sure field_75433_e is the correct name for EntityAIMoveTowardsRestriction.movementSpeed
		ObfuscationReflectionHelper.setPrivateValue(EntityAIMoveTowardsRestriction.class, ai, followSpeed, "field_75433_e", "movementSpeed");
	}

	public static void promoteRandomNemesis(EntityCreature entity, INemesisRegistry registry, List<Nemesis> nemeses) {
		if (nemeses == null || nemeses.size() < 1) {
			return;
		}
		registry.promote(nemeses.get(entity.getRNG().nextInt(nemeses.size())).getId());
	}

	public static Nemesis createAndRegisterNemesis(EntityCreature entity, BlockPos nemesisLocation) {
		Nemesis nemesis = NemesisBuilder.build(getEntityType(entity), entity.dimension, 1, nemesisLocation.getX(), nemesisLocation.getZ());
		NemesisRegistryProvider.get(entity.world).register(nemesis);
		return nemesis;
	}

	public static BlockPos getRandomLocationAround(EntityCreature entity) {
		int distance = 1000 + entity.getRNG().nextInt(4000);
		int degrees = entity.getRNG().nextInt(360);
		int x = distance * (int) Math.round(Math.cos(Math.toRadians(degrees)));
		int z = distance * (int) Math.round(Math.sin(Math.toRadians(degrees)));
		BlockPos here = entity.getPosition();
		return new BlockPos(here.getX() + x, here.getY(), here.getZ() + z);
	}

	public static void handleRandomPromotions(World world, EntityCreature entity) {
		INemesisRegistry registry = NemesisRegistryProvider.get(world);

		List<Nemesis> nemeses = registry.list();
		nemeses.removeIf(Nemesis::isDead);

		if (nemeses.size() >= (NemesisConfig.NEMESIS_LIMIT / 2)) {
			return;
		}

		promoteRandomNemesis(entity, registry, nemeses);
		createAndRegisterNemesis(entity, getRandomLocationAround(entity));
	}

	public static void enchantEquipment(Nemesis nemesis) {
		if (nemesis == null) {
			return;
		}
		enchantItems(nemesis.getArmorInventory());
		enchantItems(nemesis.getHandInventory());
	}

	public static void enchantItems(List<ItemStack> items) {
		for (ItemStack item : items) {
			if (rand.nextBoolean()) {
				enchantItem(item);
			}
		}
	}

	public static void enchantItem(ItemStack item) {
		if(!improveEnchants(item)){
			addNewEnchantment(item);
		}
	}

	private static boolean improveEnchants(ItemStack item) {
		boolean improved = false;
		Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(item);

		if(enchantments.isEmpty()){
			return false;
		}

		for (Entry<Enchantment, Integer> enchant : enchantments.entrySet()) {
			if(shouldImproveEnchantment(rand, enchant.getKey(), enchant.getValue())){
				enchantments.put(enchant.getKey(), enchant.getValue() + 1);
				improved = true;
			}
		}

		if(improved){
			EnchantmentHelper.setEnchantments(enchantments, item);
		}

		return improved;
	}

	private static boolean shouldImproveEnchantment(Random rand, Enchantment enchantment, Integer level) {
		return level < enchantment.getMaxLevel() && rand.nextBoolean();
	}

	private static void addNewEnchantment(ItemStack item) {
		EnchantmentHelper.addRandomEnchantment(rand, item, 1, true);
		removeDuplicateEnchantments(item);
	}

	private static void removeDuplicateEnchantments(ItemStack item) {
		EnchantmentHelper.setEnchantments(EnchantmentHelper.getEnchantments(item), item);
	}

	public static boolean isBodyGuard(EntityLiving searchEntity, UUID id) {
		return searchEntity.getTags().contains(NemesisSystem.TAG_BODY_GUARD)
				&& id.equals(searchEntity.getEntityData().getUniqueId(NemesisSystem.NBT_NEMESIS_ID));
	}

	public static boolean isNemesis(EntityLiving searchEntity) {
		return searchEntity.getTags().contains(NemesisSystem.TAG_NEMESIS);
	}

	public static boolean isNemesis(EntityLiving searchEntity, UUID id) {
		return isNemesis(searchEntity) && id.equals(searchEntity.getEntityData().getUniqueId(NemesisSystem.NBT_NEMESIS_ID));
	}

	public static List<EntityCreature> findNemesisBodyGuards(World world, UUID id, BlockPos position) {
		int distance = 100;

		return world.getEntitiesWithinAABB(EntityCreature.class, new AxisAlignedBB(position).grow(distance, distance, distance),
				(EntityCreature searchEntity) -> isBodyGuard(searchEntity, id)
		);
	}

	public static EntityLiving findNemesisAround(World world, UUID id, BlockPos position) {
		int distance = 50;

		List<EntityLiving> entities = world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(position).grow(distance, distance, distance),
				(EntityLiving searchEntity) -> isNemesis(searchEntity, id)
		);

		if (entities.size() < 1) {
			return null;
		}

		return entities.get(0);
	}

	public static void duel(World world, Nemesis exclude, boolean onlyIfCrowded) {
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

		//TODO factor in distance, the closer the nemeses the more likely they should be to duel

		// get the weaklings
		Collections.shuffle(nemeses);
		nemeses.sort(Comparator.comparingInt(Nemesis::getLevel));
		NemesisRegistryProvider.get(world).duel(nemeses.get(0), nemeses.get(1));
	}

	public static void promote(Nemesis nemesis) {
		nemesis.setLevel(nemesis.getLevel() + 1);
		enchantEquipment(nemesis);
		if (shouldGainAdditionalTrait(nemesis)) {
			addAdditionalTrait(nemesis);
		}
		MinecraftForge.EVENT_BUS.post(new NemesisEvent.Promotion(nemesis));
	}

	private static void addAdditionalTrait(Nemesis nemesis) {
		List<Trait> availableTraits = Arrays.asList(Trait.values())
				.stream()
				.filter((Trait t) -> !nemesis.getTraits().contains(t))
				.collect(Collectors.toList());

		if (availableTraits.size() < 1) {
			return;
		}

		Trait newTrait = availableTraits.get(rand.nextInt(availableTraits.size()));
		nemesis.getTraits().add(newTrait);
		System.out.println("new trait: " + newTrait);
	}

	private static boolean shouldGainAdditionalTrait(Nemesis nemesis) {
		return rand.nextInt(10 * nemesis.getTraits().size()) == 0;
	}

	public static Nemesis loadNemesisFromEntity(Entity nemesisEntity) {
		UUID id = nemesisEntity.getEntityData().getUniqueId(NemesisSystem.NBT_NEMESIS_ID);
		return NemesisRegistryProvider.get(nemesisEntity.getEntityWorld()).getById(id);
	}
}
