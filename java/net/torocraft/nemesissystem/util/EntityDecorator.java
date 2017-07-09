package net.torocraft.nemesissystem.util;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.registry.Nemesis;

public class EntityDecorator {

	/**
	 * Convert the provided entity into the given nemesis
	 *
	 * @param entity the entity to be converted into a nemesis
	 * @param nemesis the nemesis to create
	 */
	public static void decorate(EntityLiving entity, Nemesis nemesis) {
		entity.addTag(NemesisSystem.TAG_NEMESIS);
		entity.getEntityData().setUniqueId(NemesisSystem.NBT_NEMESIS_ID, nemesis.getId());

		entity.setCustomNameTag(nemesis.getName() + " the " + nemesis.getTitle());

		ItemStack helmet = nemesis.getArmorInventory().get(EntityEquipmentSlot.HEAD.getIndex());

		entity.setItemStackToSlot(EntityEquipmentSlot.HEAD, helmet);
		entity.setItemStackToSlot(EntityEquipmentSlot.CHEST, nemesis.getArmorInventory().get(EntityEquipmentSlot.CHEST.getIndex()));
		entity.setItemStackToSlot(EntityEquipmentSlot.LEGS, nemesis.getArmorInventory().get(EntityEquipmentSlot.LEGS.getIndex()));
		entity.setItemStackToSlot(EntityEquipmentSlot.FEET, nemesis.getArmorInventory().get(EntityEquipmentSlot.FEET.getIndex()));

		entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, nemesis.getHandInventory().get(EntityEquipmentSlot.MAINHAND.getIndex()));
		entity.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, nemesis.getHandInventory().get(EntityEquipmentSlot.OFFHAND.getIndex()));

		for (IAttributeInstance attribute : entity.getAttributeMap().getAllAttributes()) {
			if (attribute.getAttribute() == SharedMonsterAttributes.ATTACK_DAMAGE) {
				attribute.setBaseValue(attribute.getAttributeValue() * nemesis.getLevel());
			}

			if (attribute.getAttribute() == SharedMonsterAttributes.ATTACK_SPEED) {
				attribute.setBaseValue(attribute.getAttributeValue() * (nemesis.getLevel() / 4));
			}

			if (attribute.getAttribute() == SharedMonsterAttributes.MAX_HEALTH) {
				attribute.setBaseValue(attribute.getAttributeValue() * nemesis.getLevel());
				entity.setHealth(entity.getMaxHealth());
			}
		}
	}

}
