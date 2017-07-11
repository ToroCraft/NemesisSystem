package net.torocraft.nemesissystem.journeymap;

import java.util.EnumSet;
import javax.annotation.ParametersAreNonnullByDefault;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.DisplayType;
import journeymap.client.api.event.ClientEvent;
import journeymap.client.api.event.DeathWaypointEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.torocraft.nemesissystem.NemesisSystem;

import static journeymap.client.api.event.ClientEvent.Type.*;

@ParametersAreNonnullByDefault
@journeymap.client.api.ClientPlugin
public class ExampleJourneymapPlugin implements IClientPlugin
{
	// API reference
	private IClientAPI jmAPI = null;

	// Forge listener reference
	private ForgeEventListener forgeEventListener;

	/**
	 * Called by JourneyMap during the init phase of mod loading.  The IClientAPI reference is how the mod
	 * will add overlays, etc. to JourneyMap.
	 *
	 * @param jmAPI     Client API implementation
	 */
	@Override
	public void initialize(final IClientAPI jmAPI)
	{
		// Set ClientProxy.SampleModWaypointFactory with an implementation that uses the JourneyMap IClientAPI under the covers.
		this.jmAPI = jmAPI;

		// Register listener for forge events
		forgeEventListener = new ForgeEventListener(jmAPI);
		MinecraftForge.EVENT_BUS.register(forgeEventListener);

		// Subscribe to desired ClientEvent types from JourneyMap
		this.jmAPI.subscribe(getModId(), EnumSet.of(DEATH_WAYPOINT, MAPPING_STARTED, MAPPING_STOPPED));

		System.out.println("Initialized " + getClass().getName());
	}

	/**
	 * Used by JourneyMap to associate a modId with this plugin.
	 */
	@Override
	public String getModId()
	{
		return NemesisSystem.MODID;
	}

	/**
	 * Called by JourneyMap on the main Minecraft thread when a {@link journeymap.client.api.event.ClientEvent} occurs.
	 * Be careful to minimize the time spent in this method so you don't lag the game.
	 * <p>
	 * You must call {@link IClientAPI#subscribe(String, EnumSet)} at some point to subscribe to these events, otherwise this
	 * method will never be called.
	 * <p>
	 * If the event type is {@link journeymap.client.api.event.ClientEvent.Type#DISPLAY_UPDATE},
	 * this is a signal to {@link journeymap.client.api.IClientAPI#show(journeymap.client.api.display.Displayable)}
	 * all relevant Displayables for the {@link journeymap.client.api.event.ClientEvent#dimension} indicated.
	 * (Note: ModWaypoints with persisted==true will already be shown.)
	 *
	 * @param event the event
	 */
	@Override
	public void onEvent(ClientEvent event)
	{
		try
		{
			switch (event.type)
			{
			case MAPPING_STARTED:
				onMappingStarted(event);
				break;

			case MAPPING_STOPPED:
				onMappingStopped(event);
				break;

			case DEATH_WAYPOINT:
				onDeathpoint((DeathWaypointEvent) event);
				break;
			}
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
	}

	/**
	 * When mapping has started, generate a bunch of random overlays.
	 *
	 * @param event client event
	 */
	void onMappingStarted(ClientEvent event)
	{
		// Create a bunch of random Image Overlays around the player
		if (jmAPI.playerAccepts(NemesisSystem.MODID, DisplayType.Image))
		{
			BlockPos pos = Minecraft.getMinecraft().player.getPosition();
			//SampleImageOverlayFactory.create(jmAPI, pos, 5, 256, 128);
		}

		// Create a bunch of random Marker Overlays around the player
		if (jmAPI.playerAccepts(NemesisSystem.MODID, DisplayType.Marker))
		{
			BlockPos pos = Minecraft.getMinecraft().player.getPosition();
			//SampleMarkerOverlayFactory.create(jmAPI, pos, 64, 256);
		}

		// Create a waypoint for the player's bed location.  The ForgeEventListener
		// will keep it updated if the player sleeps elsewhere.
		if (jmAPI.playerAccepts(NemesisSystem.MODID, DisplayType.Waypoint))
		{
			BlockPos pos = Minecraft.getMinecraft().player.getBedLocation();
			SampleModWaypointFactory.createBedWaypoint(jmAPI, pos, event.dimension);
		}

		// Slime chunk Polygon Overlays are created by the ForgeEventListener
		// as chunks load, so no need to do anything here.
	}

	/**
	 * When mapping has stopped, remove all overlays
	 *
	 * @param event client event
	 */
	void onMappingStopped(ClientEvent event)
	{
		// Clear everything
		jmAPI.removeAll(NemesisSystem.MODID);
	}

	/**
	 * Do something when JourneyMap is about to create a Deathpoint.
	 */
	void onDeathpoint(DeathWaypointEvent event)
	{
		// Could cancel the event, which would prevent the Deathpoint from actually being created.
		// For now, don't do anything.
	}

}
