package net.torocraft.nemesissystem.handlers;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.nemesissystem.discovery.NemesisDiscovery;
import net.torocraft.nemesissystem.util.DiscoveryUtil;

import java.util.ArrayList;
import java.util.List;

import static net.torocraft.nemesissystem.util.DiscoveryUtil.NBT_DISCOVERY;
import static net.torocraft.nemesissystem.util.DiscoveryUtil.NBT_PLAYER_DISCOVERIES;

public class Discovery {

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new Discovery());
    }

    @SubscribeEvent
    public void readBook(PlayerInteractEvent event) {
        ItemStack item = event.getItemStack();
        if (item == null || item.getItem() == null || item.getItem() != Items.WRITTEN_BOOK || item.getTagCompound() == null || !item.getTagCompound().hasKey(NBT_DISCOVERY)) {
            return;
        }

        NemesisDiscovery discovery = new NemesisDiscovery(null);
        discovery.readFromNBT(item.getTagCompound().getCompoundTag(NBT_DISCOVERY));

        List<NemesisDiscovery> playerDiscoveries = new ArrayList<>();
        if (event.getEntityPlayer().getEntityData().hasKey(NBT_PLAYER_DISCOVERIES)) {
            NBTTagList discoveryTags = event.getEntityPlayer().getEntityData().getTagList(NBT_PLAYER_DISCOVERIES, Constants.NBT.TAG_COMPOUND);
            discoveryTags.forEach(tag -> {
                NemesisDiscovery d = new NemesisDiscovery(null);
                d.readFromNBT((NBTTagCompound) tag);
                playerDiscoveries.add(d);
            });
            DiscoveryUtil.merge(playerDiscoveries, discovery);
        } else {
            playerDiscoveries.add(discovery);
        }

    }
}
