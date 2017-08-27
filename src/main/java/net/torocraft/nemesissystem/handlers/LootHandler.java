package net.torocraft.nemesissystem.handlers;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryTable;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.nemesissystem.NemesisSystem;

@SideOnly(Side.CLIENT)
public class LootHandler {

	public static final ResourceLocation LOOT_TABLE = new ResourceLocation(NemesisSystem.MODID, "nemesis_loot");

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new LootHandler());
	}

	@SubscribeEvent
	public void lootTableLoad(final LootTableLoadEvent event) {
		if (isLootTarget(event)) {

			// TODO improve roll settings

			// TODO update json loot file to have a discovery book

			String name = LOOT_TABLE.toString();
			LootEntry entry = new LootEntryTable(LOOT_TABLE, 1, 0, new LootCondition[0], name);



			RandomValueRange rolls = new RandomValueRange(0, 1);
			LootPool pool = new LootPool(new LootEntry[] { entry }, new LootCondition[0], rolls, rolls, name);
			event.getTable().addPool(pool);
		}
	}

	private boolean isLootTarget(LootTableLoadEvent event) {
		return event.getName().equals(LootTableList.CHESTS_END_CITY_TREASURE)
				|| event.getName().equals(LootTableList.CHESTS_SIMPLE_DUNGEON)
				|| event.getName().equals(LootTableList.CHESTS_VILLAGE_BLACKSMITH)
				|| event.getName().equals(LootTableList.CHESTS_ABANDONED_MINESHAFT)
				|| event.getName().equals(LootTableList.CHESTS_NETHER_BRIDGE)
				|| event.getName().equals(LootTableList.CHESTS_STRONGHOLD_LIBRARY)
				|| event.getName().equals(LootTableList.CHESTS_STRONGHOLD_CROSSING)
				|| event.getName().equals(LootTableList.CHESTS_STRONGHOLD_CORRIDOR)
				|| event.getName().equals(LootTableList.CHESTS_DESERT_PYRAMID)
				|| event.getName().equals(LootTableList.CHESTS_JUNGLE_TEMPLE)
				|| event.getName().equals(LootTableList.CHESTS_JUNGLE_TEMPLE_DISPENSER)
				|| event.getName().equals(LootTableList.CHESTS_IGLOO_CHEST)
				|| event.getName().equals(LootTableList.CHESTS_WOODLAND_MANSION);
	}

}
