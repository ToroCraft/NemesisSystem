package net.torocraft.nemesissystem.handlers;

import java.util.UUID;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.registry.NemesisEntry;
import net.torocraft.nemesissystem.traits.TraitHandler;
import net.torocraft.nemesissystem.util.BehaviorUtil;
import net.torocraft.nemesissystem.util.NemesisUtil;

public class UpdateHandler {

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new UpdateHandler());
	}

	@SubscribeEvent
	public void livingUpdate(LivingUpdateEvent event) {
		World world = event.getEntity().getEntityWorld();

		if (world.isRemote) {
			return;
		}

		if (world.getTotalWorldTime() % 20 != 0) {
			return;
		}

		if (!(event.getEntity() instanceof EntityLiving)) {
			return;
		}

		if (event.getEntity().getTags().contains(NemesisSystem.TAG_BODY_GUARD)) {
			handleBodyGuardUpdate(event);
		} else if (event.getEntity().getTags().contains(NemesisSystem.TAG_NEMESIS)) {
			handleNemesisUpdate(event);
		}
	}

	private void handleBodyGuardUpdate(LivingUpdateEvent event) {
		if (!(event.getEntity() instanceof EntityCreature)) {
			return;
		}

		EntityCreature bodyGuard = (EntityCreature) event.getEntity();
		if (bodyGuard.getTags().contains(DeathHandler.TAG_RONIN)) {
			flee(bodyGuard);
			return;
		}

		UUID id = bodyGuard.getEntityData().getUniqueId(NemesisSystem.NBT_NEMESIS_ID);
		EntityLiving nemesisEntity = NemesisUtil.findNemesisAround(event.getEntity().world, id, event.getEntity().getPosition());

		if (nemesisEntity == null) {
			return;
		}

		followNemesisBoss(bodyGuard, nemesisEntity);
	}

	private void flee(EntityCreature bodyGuard) {
		// TODO USE FLEEING_SPEED_MODIFIER
		bodyGuard.removeTag(NemesisSystem.TAG_BODY_GUARD);
		BehaviorUtil.setFollowSpeed(bodyGuard, 2);
		int distance = 1000;
		int degrees = bodyGuard.getRNG().nextInt(360);
		int x = distance * (int) Math.round(Math.cos(Math.toRadians(degrees)));
		int z = distance * (int) Math.round(Math.sin(Math.toRadians(degrees)));
		BlockPos from = bodyGuard.getPosition();
		BlockPos to = new BlockPos(from.getX() + x, from.getY(), from.getZ() + z);
		bodyGuard.setHomePosAndDistance(to, 50);
	}

	private void followNemesisBoss(EntityCreature bodyGuard, EntityLiving nemesisEntity) {
		bodyGuard.setHomePosAndDistance(nemesisEntity.getPosition(), 2);
	}

	private void handleNemesisUpdate(LivingUpdateEvent event) {
		NemesisEntry nemesis = NemesisUtil.loadNemesisFromEntity(event.getEntity());
		if (nemesis == null || !(event.getEntity() instanceof EntityCreature)) {
			return;
		}
		TraitHandler.onUpdate(nemesis, (EntityCreature) event.getEntity());
	}

}
