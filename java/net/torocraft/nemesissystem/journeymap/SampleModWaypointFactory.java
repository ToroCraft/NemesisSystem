package net.torocraft.nemesissystem.journeymap;

import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.ModWaypoint;
import journeymap.client.api.model.MapImage;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.torocraft.nemesissystem.NemesisSystem;

public class SampleModWaypointFactory {
	/**
	 * ExampleMod will create a waypoint for the bed slept in at the provided coordinates.
	 */
	static ModWaypoint createBedWaypoint(IClientAPI jmAPI, BlockPos bedLocation, int dimension) {
		ModWaypoint bedWaypoint = null;
		try {
			// Icon for waypoint
			MapImage bedIcon = new MapImage(new ResourceLocation("examplemod:images/bed.png"), 32, 32)
					.setAnchorX(16)
					.setAnchorY(32);

			// Waypoint itself
			bedWaypoint = new ModWaypoint(NemesisSystem.MODID, "bed_" + dimension, "Handy Locations", "Bed",
					bedLocation, bedIcon, 0xffffff, true, Integer.MIN_VALUE);

			// Add or update
			jmAPI.show(bedWaypoint);

		} catch (Throwable t) {
			System.out.println(t.getMessage());
		}

		return bedWaypoint;
	}
}
