package net.torocraft.nemesissystem.handlers;

import net.minecraft.entity.EntityCreature;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.nemesissystem.NemesisSystem;

public class SetAttackTargetHandler {

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new SetAttackTargetHandler());
	}

	@SubscribeEvent
	public void stopBodyGuardsFromAttackingNemeses(LivingSetAttackTargetEvent event) {
		World world = event.getEntity().getEntityWorld();

		if (world.isRemote) {
			return;
		}

		if (event.getTarget() == null || !(event.getEntity() instanceof EntityCreature)) {
			return;
		}

		if (!event.getEntity().getTags().contains(NemesisSystem.TAG_BODY_GUARD)) {
			return;
		}

		if (event.getTarget().getTags().contains(NemesisSystem.TAG_NEMESIS)) {
			((EntityCreature) event.getEntityLiving()).setAttackTarget(null);
		}
	}

}
