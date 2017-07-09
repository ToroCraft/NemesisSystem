package net.torocraft.nemesissystem.handlers;

import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.util.NemesisUtil;

public class AttackHandler {

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new AttackHandler());
	}

	@SubscribeEvent
	public void onAttacked(LivingAttackEvent event) {

		World world = event.getEntity().getEntityWorld();

		if (world.isRemote || !(event.getEntity() instanceof EntityCreature)) {
			return;
		}

		if (event.getEntity().getTags().contains(NemesisSystem.TAG_NEMESIS)) {
			orderGuardsToAttackAggressor((EntityCreature) event.getEntity(), event.getSource().getTrueSource());
		}
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
