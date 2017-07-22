package net.torocraft.nemesissystem;

import java.io.File;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NemesisConfig {
	public static int NEMESIS_LIMIT = 16;
	public static int test = 1500;

	private static final String CATEGORY = "NemesisSystem Settings";
	private static Configuration config;

	public static void init(File configFile) {
		if (config == null) {
			config = new Configuration(configFile);
			loadConfiguration();
		}
	}

	private static void loadConfiguration() {
		try {
			NEMESIS_LIMIT = config.getInt("NEMESIS_LIMIT", CATEGORY, 16, 4, 256, "Maximum number of nemeses in each dimension");
			config.save();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equalsIgnoreCase(NemesisSystem.MODID)) {
			loadConfiguration();
		}
	}
}
