package net.torocraft.nemesissystem.handlers;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.registry.Nemesis.Trait;
import net.torocraft.nemesissystem.registry.NemesisRegistryProvider;
import net.torocraft.nemesissystem.util.NemesisUtil;

public class DeathHandler {

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new DeathHandler());
	}

	@SubscribeEvent
	public void onDrops(LivingDropsEvent event) {
		World world = event.getEntity().getEntityWorld();

		if (world.isRemote || !(event.getEntity() instanceof EntityCreature)) {
			return;
		}

		if (event.getEntity().getTags().contains(NemesisSystem.TAG_NEMESIS)) {
			handleNemesisDrops(event.getDrops(), (EntityCreature) event.getEntity());
		}
	}

	@SubscribeEvent
	public void onDeath(LivingDeathEvent event) {

		System.out.println("death event");

		World world = event.getEntity().getEntityWorld();

		if (world.isRemote) {
			return;
		}

		Entity slayer = event.getSource().getTrueSource();

		if (event.getEntity() instanceof EntityPlayer && slayer instanceof EntityCreature) {
			System.out.println("player death event");
			handlePlayerDeath((EntityPlayer) event.getEntity(), (EntityCreature) slayer);
			return;
		}

		if (!(event.getEntity() instanceof EntityCreature)) {
			return;
		}

		if (event.getEntity().getTags().contains(NemesisSystem.TAG_NEMESIS)) {
			System.out.println("nemesis death event");
			handleNemesisDeath((EntityCreature) event.getEntity(), slayer);
		}
	}

	@SubscribeEvent
	public void dropExperience(LivingExperienceDropEvent event) {
		if (!event.getEntity().getTags().contains(NemesisSystem.TAG_NEMESIS)) {
			return;
		}

		Nemesis nemesis = NemesisUtil.loadNemesisFromEntity((EntityCreature) event.getEntity());
		//TODO determine some kind of formula for scaling the amount of experience received
		event.setDroppedExperience(event.getOriginalExperience() * (nemesis.getLevel() + 1));
	}

	private void handlePlayerDeath(EntityPlayer player, EntityCreature slayer) {
		if (slayer == null) {
			return;
		}
		Nemesis nemesis = NemesisUtil.loadNemesisFromEntity(slayer);

		if (nemesis == null) {
			if (NemesisUtil.isNemesisClassEntity(slayer)) {
				Nemesis newNemesis = NemesisUtil.createAndRegisterNemesis(slayer, slayer.getPosition());
				nemesisDuelIfCrowed(slayer.world, newNemesis);
			}
		} else {
			NemesisRegistryProvider.get(slayer.world).promote(nemesis.getId());
		}
	}

	private void nemesisDuelIfCrowed(World world, Nemesis exclude) {
		List<Nemesis> nemeses = NemesisRegistryProvider.get(world).list();
		nemeses.removeIf(Nemesis::isDead);

		if (nemeses.size() < NemesisSystem.NEMESIS_LIMIT) {
			return;
		}

		nemeses.removeIf(Nemesis::isLoaded);
		if (exclude != null) {
			nemeses.removeIf((Nemesis n) -> n.getId().equals(exclude.getId()));
		}

		if (nemeses.size() < 2) {
			//not enough unloaded nemeses to duel, limit will be exceeded
			return;
		}

		//TODO factor in distance, the closer the nemeses the more likely they should be to duel

		// get the weaklings
		Collections.shuffle(nemeses);
		nemeses.sort(Comparator.comparingInt(Nemesis::getLevel));
		NemesisRegistryProvider.get(world).duel(nemeses.get(0), nemeses.get(1));
	}

	private void handleNemesisDrops(List<EntityItem> drops, EntityCreature nemesisEntity) {
		Nemesis nemesis = NemesisUtil.loadNemesisFromEntity(nemesisEntity);
		Random rand = nemesisEntity.getRNG();

		if (nemesis == null) {
			return;
		}

		drops.add(drop(nemesisEntity, new ItemStack(Items.DIAMOND, rand.nextInt(nemesis.getLevel()))));

		ItemStack specialDrop = nemesisEntity.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
		specialDrop.setStackDisplayName("Property of " + nemesisEntity.getName());
		drops.add(drop(nemesisEntity, specialDrop));

		for (Trait trait : nemesis.getTraits()) {
			switch (trait) {
			case DOUBLE_MELEE:

				break;
			case ARROW:
				drops.add(drop(nemesisEntity, new ItemStack(Items.ARROW, rand.nextInt(64))));
				break;
			case SUMMON:
				break;
			case REFLECT:
				break;
			case HEAT:
				drops.add(drop(nemesisEntity, new ItemStack(Blocks.TORCH, rand.nextInt(64))));
				if (rand.nextInt(5) == 0) {
					drops.add(drop(nemesisEntity, new ItemStack(Items.LAVA_BUCKET)));
				}
				break;
			case POTION:
				// TODO drop random potions
				break;
			case SHIELD:
				break;
			case TELEPORT:
				drops.add(drop(nemesisEntity, new ItemStack(Items.ENDER_PEARL, rand.nextInt(16))));
				break;
			}
		}

	}

	private EntityItem drop(EntityCreature entity, ItemStack stack) {
		return new EntityItem(entity.getEntityWorld(), entity.posX, entity.posY, entity.posZ, stack);
	}

	private void handleNemesisDeath(EntityCreature nemesisEntity, Entity attacker) {
		Nemesis nemesis = NemesisUtil.loadNemesisFromEntity(nemesisEntity);

		if (nemesis == null) {
			System.out.println("nemesis not found on death, can't deregister");
			return;
		}

		NemesisRegistryProvider.get(nemesisEntity.world).unload(nemesis.getId());

		if (attacker == null || !(attacker instanceof EntityLivingBase)) {
			System.out.println("nemesis was not killed by entity");
			return;
		}

		NemesisRegistryProvider.get(nemesisEntity.world).setDead(nemesis.getId());

		NemesisUtil.findNemesisBodyGuards(nemesisEntity.world, nemesis.getId(), nemesisEntity.getPosition())
				.forEach((EntityCreature guard) -> guard.setAttackTarget(null));

		// TODO post message

		// TODO log death

	}
}
