package net.torocraft.nemesissystem.handlers;

import static net.torocraft.nemesissystem.NemesisSystem.TAG_BODY_GUARD;
import static net.torocraft.nemesissystem.NemesisSystem.TAG_NEMESIS;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.nemesissystem.util.DiscoveryUtil;

public class DropHandler {

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new DropHandler());
    }

    @SubscribeEvent
    public void dropBook(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof EntityMob)) {
            return;
        }

        if (event.getEntity().getTags().contains(TAG_NEMESIS)) {
            return;
        }

        if (!(event.getSource().getTrueSource() instanceof EntityPlayer)) {
            return;
        }

        World world = event.getEntity().getEntityWorld();
        int chanceBounds = 30;

        if (event.getEntity().getTags().contains(TAG_BODY_GUARD)) {
            chanceBounds = 10;
        }

        if (world.rand.nextInt(chanceBounds) != 0) {
            return;
        }

        BlockPos position = event.getEntity().getPosition();
        event.getDrops().add(new EntityItem(world, position.getX(), position.getY(), position.getZ(), DiscoveryUtil.createUnreadBook()));
    }
}
