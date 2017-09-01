package net.torocraft.nemesissystem.handlers;

import java.util.Random;
import java.util.UUID;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.datafix.fixes.EntityId;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.util.NemesisUtil;
import net.torocraft.torotraits.api.BehaviorApi;

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
		}
	}

	private void handleBodyGuardUpdate(LivingUpdateEvent event) {
		if (!(event.getEntity() instanceof EntityCreature)) {
			return;
		}

		EntityCreature bodyGuard = (EntityCreature) event.getEntity();
		World world = bodyGuard.world;

		if (bodyGuard.getTags().contains(DeathHandler.TAG_RONIN)) {
			flee(bodyGuard);
			return;
		}

		UUID id = bodyGuard.getEntityData().getUniqueId(NemesisSystem.NBT_NEMESIS_ID);

		if (id == null) {
			bodyGuard.addTag(DeathHandler.TAG_RONIN);
			return;
		}

		EntityLiving nemesisEntity = NemesisUtil.findNemesisAround(world, id, bodyGuard.getPosition(), 100);

		if (nemesisEntity == null) {
			bodyGuard.addTag(DeathHandler.TAG_RONIN);
			return;
		}

		followNemesisBoss(bodyGuard, nemesisEntity);
	}

	private void flee(EntityCreature bodyGuard) {
		// TODO USE FLEEING_SPEED_MODIFIER
		bodyGuard.removeTag(NemesisSystem.TAG_BODY_GUARD);
		bodyGuard.setAttackTarget(null);
		dropItemsInHands(bodyGuard);
		BehaviorApi.setFollowSpeed(bodyGuard, 2);
		int distance = 1000;
		int degrees = bodyGuard.getRNG().nextInt(360);
		int x = distance * (int) Math.round(Math.cos(Math.toRadians(degrees)));
		int z = distance * (int) Math.round(Math.sin(Math.toRadians(degrees)));
		BlockPos from = bodyGuard.getPosition();
		BlockPos to = new BlockPos(from.getX() + x, from.getY(), from.getZ() + z);
		bodyGuard.setHomePosAndDistance(to, 50);
	}

	private void dropItemsInHands(EntityCreature bodyGuard) {
		Random rand = bodyGuard.getRNG();
		for(ItemStack item : bodyGuard.getHeldEquipment()){
			if (!item.isEmpty()) {
				EntityItem e = DeathHandler.damageAndDrop(bodyGuard, item);
				e.setVelocity(rand.nextDouble() - 0.5, rand.nextDouble() / 1.5, rand.nextDouble() - 0.5);
				bodyGuard.world.spawnEntity(e);
			}
		}
		bodyGuard.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
		bodyGuard.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY);
	}

	private void followNemesisBoss(EntityCreature bodyGuard, EntityLiving nemesisEntity) {
		bodyGuard.setHomePosAndDistance(nemesisEntity.getPosition(), 20);
	}

}
