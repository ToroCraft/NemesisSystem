package net.torocraft.nemesissystem.journeymap;

import static journeymap.client.api.event.ClientEvent.Type.DISPLAY_UPDATE;
import static journeymap.client.api.event.ClientEvent.Type.MAPPING_STARTED;
import static journeymap.client.api.event.ClientEvent.Type.MAPPING_STOPPED;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import javax.annotation.ParametersAreNonnullByDefault;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.Displayable;
import journeymap.client.api.event.ClientEvent;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.registry.Nemesis;

@ParametersAreNonnullByDefault
@journeymap.client.api.ClientPlugin
public class NemesisJourneymapPlugin implements IClientPlugin {

	private IClientAPI api = null;
	private List<Displayable> overlays = new ArrayList<>();

	public NemesisJourneymapPlugin() {
		Nemesis n = new Nemesis();
		n.setId(UUID.randomUUID());
		n.setName("Moab");
		n.setTitle("Tree Puncher");
		n.setX(0);
		n.setZ(0);
		overlays.add(NemesisDomainOverlayCreator.create(n));
	}

	@Override
	public void initialize(final IClientAPI api) {
		this.api = api;
		api.subscribe(getModId(), EnumSet.of(DISPLAY_UPDATE, MAPPING_STARTED, MAPPING_STOPPED));
		addOverlays();
	}

	@Override
	public String getModId() {
		return NemesisSystem.MODID;
	}

	@Override
	public void onEvent(ClientEvent event) {
		switch (event.type) {
		case MAPPING_STARTED:
		case DISPLAY_UPDATE:
			addOverlays();
			break;
		case MAPPING_STOPPED:
			onMappingStopped();
			break;
		}
	}

	private void addOverlays() {
		for (Displayable d : overlays) {
			show(d);
		}
	}

	private void show(Displayable d) {
		try {
			api.show(d);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void onMappingStopped() {
		try {
			api.removeAll(NemesisSystem.MODID);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
