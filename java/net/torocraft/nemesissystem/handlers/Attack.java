package net.torocraft.nemesissystem.handlers;

import java.util.List;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.registry.Nemesis.Trait;
import net.torocraft.nemesissystem.util.NemesisActions;
import net.torocraft.nemesissystem.util.NemesisUtil;
import net.torocraft.nemesissystem.util.TraitsUtil;

public class Attack {

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new Attack());
	}

	private static final int ONE_SECOND = 20;

	@SubscribeEvent
	public void onAllergyHit(LivingHurtEvent event) {
		if (!(event.getEntityLiving() instanceof EntityLiving) || !event.getEntityLiving().getTags().contains(NemesisSystem.TAG_NEMESIS)) {
			return;
		}

		EntityLiving entity = (EntityLiving)event.getEntityLiving();
		Nemesis nemesis = NemesisUtil.loadNemesisFromEntity(entity);
		if (nemesis == null) {
			return;
		}

		if (!nemesis.getWeaknesses().contains(Nemesis.Weakness.WOOD_ALLERGY) && !nemesis.getWeaknesses().contains(Nemesis.Weakness.STONE_ALLERGY) && !nemesis.getWeaknesses().contains(Nemesis.Weakness.GOLD_ALLERGY)) {
			return;
		}

		Entity trueSource = event.getSource().getTrueSource();
		if (!(trueSource instanceof EntityPlayer)) {
			return;
		}

		EntityPlayer player = (EntityPlayer)trueSource;

		ItemStack heldItem = player.getHeldItem(EnumHand.MAIN_HAND);
		if (heldItem == null || heldItem.getItem() == null) {
			return;
		}

		Item item = heldItem.getItem();
		String material = null;
		if (item instanceof ItemSword) {
			material = ((ItemSword) item).getToolMaterialName();
		}
		if (item instanceof ItemTool) {
			material = ((ItemTool) item).getToolMaterialName();
		}

		if (material == null) {
			return;
		}

		if (woodAllergyApplies(nemesis, material) || goldAllergyApplies(nemesis, material) || stoneAllergyApplies(nemesis, material)) {
			entity.addPotionEffect(new PotionEffect(MobEffects.POISON, ONE_SECOND * 3, 1));
			event.setAmount(event.getAmount() * 2.0f);
		}
	}

	private boolean stoneAllergyApplies(Nemesis nemesis, String material) {
		return nemesis.getWeaknesses().contains(Nemesis.Weakness.STONE_ALLERGY) && material.equals("STONE");
	}

	private boolean goldAllergyApplies(Nemesis nemesis, String material) {
		return nemesis.getWeaknesses().contains(Nemesis.Weakness.GOLD_ALLERGY) && material.equals("GOLD");
	}

	private boolean woodAllergyApplies(Nemesis nemesis, String material) {
		return nemesis.getWeaknesses().contains(Nemesis.Weakness.WOOD_ALLERGY) && material.equals("WOOD");
	}

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
