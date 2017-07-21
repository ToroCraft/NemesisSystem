package net.torocraft.nemesissystem.events;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.registry.Nemesis.Trait;
import net.torocraft.nemesissystem.util.NemesisActions;
import net.torocraft.nemesissystem.util.NemesisUtil;

import java.util.List;

public class EventHandlers {

    @SubscribeEvent
    public void onTeleportEntityHarm(LivingHurtEvent event) {
        if (!(event.getEntityLiving() instanceof EntityLiving) || !event.getEntityLiving().getTags().contains(NemesisSystem.TAG_NEMESIS)) {
            return;
        }

        EntityLiving entity = (EntityLiving)event.getEntityLiving();
        Nemesis nemesis = NemesisUtil.loadNemesisFromEntity(entity);
        if (nemesis == null) {
            return;
        }

        if (!nemesis.getTraits().contains(Trait.TELEPORT)) {
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

}
