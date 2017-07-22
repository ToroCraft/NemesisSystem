package net.torocraft.nemesissystem.handlers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.nemesissystem.NemesisSystem;

public class SetAttackTarget {

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new SetAttackTarget());
	}

	@SubscribeEvent
	public void stopBodyGuardsFromAttackingNemeses(LivingSetAttackTargetEvent event) {
		World world = event.getEntity().getEntityWorld();

		if (world.isRemote) {
			return;
		}

		Entity entity = event.getEntity();

		if (event.getTarget() == null || !(event.getEntity() instanceof EntityCreature)) {
			return;
		}

		if (hasTag(entity, NemesisSystem.TAG_BODY_GUARD) || hasTag(entity, NemesisSystem.TAG_SUMMONED_MOB)) {
			if (event.getTarget().getTags().contains(NemesisSystem.TAG_NEMESIS)) {
				((EntityCreature) event.getEntityLiving()).setAttackTarget(null);
			}
		}
	}

	private static boolean hasTag(Entity e, String tag) {
		return e.getTags().contains(tag);
	}

}
