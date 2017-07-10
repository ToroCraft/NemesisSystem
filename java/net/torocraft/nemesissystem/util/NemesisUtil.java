package net.torocraft.nemesissystem.util;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

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
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.torocraft.nemesissystem.NemesisConfig;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.registry.NemesisRegistry;
import net.torocraft.nemesissystem.registry.NemesisRegistryProvider;

public class NemesisUtil {
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

	public static void promoteRandomNemesis(EntityCreature entity, NemesisRegistry registry, List<Nemesis> nemeses) {
		if (nemeses == null || nemeses.size() < 1) {
			return;
		}
		registry.promote(nemeses.get(entity.getRNG().nextInt(nemeses.size())).getId());
	}

	public static Nemesis createAndRegisterNemesis(EntityCreature entity, BlockPos nemesisLocation) {
		Nemesis nemesis = NemesisBuilder.build(getEntityType(entity), 1, nemesisLocation.getX(), nemesisLocation.getZ());
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
		NemesisRegistry registry = NemesisRegistryProvider.get(world);

		List<Nemesis> nemeses = registry.list();
		nemeses.removeIf(Nemesis::isDead);

		if (nemeses.size() >= (NemesisConfig.NEMESIS_LIMIT / 2)) {
			return;
		}

		promoteRandomNemesis(entity, registry, nemeses);
		createAndRegisterNemesis(entity, getRandomLocationAround(entity));
	}

	public static void enchantArmor(Nemesis nemesis) {
		if (nemesis == null) {
			return;
		}

		Random rand = new Random();
		boolean hasEnchanted = false;
		levels: for (int i = 0; i < nemesis.getLevel(); i++) {
			for (int j = 0; j < 4; j++) {
				if (rand.nextInt(10) == 7) {
					enchantPieceOfArmor(nemesis.getArmorInventory().get(j), rand);
					hasEnchanted = true;
					break levels;
				}
			}
			if (!hasEnchanted) {
				enchantPieceOfArmor(nemesis.getArmorInventory().get(rand.nextInt(4)), rand);
			}
			hasEnchanted = false;
		}
	}

	private static void enchantPieceOfArmor(ItemStack armor, Random rand) {
		Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(armor);
		if (enchantments.isEmpty()) {
			EnchantmentHelper.addRandomEnchantment(rand, armor, 1, true);
		} else {
			enchantments.forEach((enchantment, level) -> {
				enchantments.put(enchantment, level + 1);
			});
			EnchantmentHelper.setEnchantments(enchantments, armor);
		}
	}

	public static void unLoadNemesis(EntityCreature entity) {
		Nemesis nemesis = loadNemesisFromEntity(entity);
		if (nemesis == null) {
			return;
		}
		entity.setDead();
		NemesisRegistryProvider.get(entity.world).unload(nemesis.getId());

		// TODO this doesn't seem to work well, maybe clean them up directly on load and/or unload
		findNemesisBodyGuards(entity.world, nemesis.getId(), entity.getPosition()).forEach((EntityCreature e) -> e.setDead());
	}

	public static Nemesis loadNemesisFromEntity(Entity nemesisEntity) {
		UUID id = nemesisEntity.getEntityData().getUniqueId(NemesisSystem.NBT_NEMESIS_ID);
		return NemesisRegistryProvider.get(nemesisEntity.getEntityWorld()).getById(id);
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
}
