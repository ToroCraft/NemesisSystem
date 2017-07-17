package net.torocraft.nemesissystem.handlers;

import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.registry.Nemesis.Trait;
import net.torocraft.nemesissystem.util.NemesisUtil;
import net.torocraft.nemesissystem.util.TraitsUtil;

public class Attack {

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new Attack());
	}

	@SubscribeEvent
	public void onAttacked(LivingAttackEvent event) {

		World world = event.getEntity().getEntityWorld();

		//entityDebug(event);

		if (world.isRemote || !(event.getEntity() instanceof EntityCreature)) {
			return;
		}

		if (event.getEntity().getTags().contains(NemesisSystem.TAG_NEMESIS)) {
			handleTraitDefenses(event);
			orderGuardsToAttackAggressor((EntityCreature) event.getEntity(), event.getSource().getTrueSource());
		}
	}

	private void handleTraitDefenses(LivingAttackEvent event) {
		EntityCreature nemesisEntity = (EntityCreature) event.getEntity();
		Nemesis nemesis = NemesisUtil.loadNemesisFromEntity(nemesisEntity);
		if (nemesis == null) {
			return;
		}
		for(Trait trait : nemesis.getTraits()){
			switch(trait) {
			case DOUBLE_MELEE:
				break;
			case ARROW:
				break;
			case SUMMON:
				break;
			case REFLECT:
				TraitsUtil.reflectDamage(nemesisEntity, nemesis, event.getSource(), event.getAmount());
				break;
			case HEAT:
				break;
			case POTION:
				break;
			case TELEPORT:
				break;
			case FIREBALL:
				break;
			case HEAL:
				break;
			}
		}
	}

	private void entityDebug(LivingAttackEvent event) {
		try {
			if (event.getSource().getTrueSource() instanceof EntityPlayer) {
				System.out.println("-------------------------------------");
				System.out.println("ID: " + event.getEntity().getEntityId());
				for (String tag : event.getEntity().getTags()) {
					System.out.println("TAG: " + tag);
				}

				System.out.println("Data: " + event.getEntity().getEntityData());
				Nemesis nemesis = NemesisUtil.loadNemesisFromEntity(event.getEntity());
				NBTTagCompound c = new NBTTagCompound();
				if (nemesis != null) {
					nemesis.writeToNBT(c);
				}
				System.out.println("Nemesis: " + (nemesis == null ? "null" : c));
				System.out.println("-------------------------------------");
			}
		} catch (Exception e) {
			e.printStackTrace();
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
