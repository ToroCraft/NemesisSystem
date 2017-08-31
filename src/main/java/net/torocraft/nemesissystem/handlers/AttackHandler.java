package net.torocraft.nemesissystem.handlers;

import java.util.List;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.registry.NemesisEntry;
import net.torocraft.nemesissystem.util.NemesisActions;
import net.torocraft.nemesissystem.util.NemesisUtil;
import net.torocraft.torotraits.traits.Type;

public class AttackHandler {

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new AttackHandler());
	}

	@SubscribeEvent
	public void onAttacked(LivingHurtEvent event) {
		World world = event.getEntity().getEntityWorld();

		if (world.isRemote || !(event.getEntity() instanceof EntityCreature)) {
			return;
		}

		if (event.getEntity().getTags().contains(NemesisSystem.TAG_NEMESIS)) {
			orderGuardsToAttackAggressor((EntityCreature) event.getEntity(), event.getSource().getTrueSource());
		}
	}


	@SubscribeEvent
	public void onTeleportEntityHarm(LivingHurtEvent event) {
		if (!(event.getEntityLiving() instanceof EntityLiving) || !event.getEntityLiving().getTags().contains(NemesisSystem.TAG_NEMESIS)) {
			return;
		}

		EntityLiving entity = (EntityLiving) event.getEntityLiving();
		NemesisEntry nemesis = NemesisUtil.loadNemesisFromEntity(entity);
		if (nemesis == null) {
			return;
		}

		if (!nemesis.hasTrait(Type.TELEPORT)) {
			return;
		}

		World world = entity.getEntityWorld();
		if (world.rand.nextInt(2) != 0) {
			return;
		}

		List<EntityCreature> guards = NemesisUtil.findNemesisBodyGuards(world, nemesis.getId(), entity.getPosition());
		if (guards.size() < 1) {
			return;
		}
		EntityCreature teleportTarget = guards.get(world.rand.nextInt(guards.size()));

		NemesisActions.throwPearl(entity, teleportTarget);
	}

	private void orderGuardsToAttackAggressor(EntityCreature boss, Entity attacker) {
		if (attacker == null || !(attacker instanceof EntityLivingBase)) {
			return;
		}
		UUID id = boss.getEntityData().getUniqueId(NemesisSystem.NBT_NEMESIS_ID);
		NemesisUtil.findNemesisBodyGuards(boss.world, id, boss.getPosition()).forEach((EntityCreature guard) -> {
			if (boss.getRNG().nextInt(7) == 0) {
				guard.setAttackTarget((EntityLivingBase) attacker);
			}
		});
	}

}
