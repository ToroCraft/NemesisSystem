package net.torocraft.nemesissystem.journeymap;

import java.awt.geom.Point2D;
import journeymap.client.api.display.IOverlayListener;
import journeymap.client.api.display.PolygonOverlay;
import journeymap.client.api.model.MapPolygon;
import journeymap.client.api.model.ShapeProperties;
import journeymap.client.api.model.TextProperties;
import journeymap.client.api.util.UIState;
import net.minecraft.util.math.BlockPos;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.registry.Nemesis;

public class NemesisDomainOverlayCreator {

	public static PolygonOverlay create(Nemesis nemesis) {
		if (nemesis == null) {
			return null;
		}
		String displayId = nemesis.getId().toString();

		// TODO get dimension from nemesis
		PolygonOverlay overlay = new PolygonOverlay(NemesisSystem.MODID, displayId, 0, style(), shape(nemesis));
		overlay.setOverlayGroupName("Nemesis");
		overlay.setLabel(nemesis.getNameAndTitle());
		overlay.setTextProperties(text());
		overlay.setOverlayListener(new SlimeChunkListener(overlay));

		return overlay;
	}

	private static MapPolygon shape(Nemesis nemesis) {
		int x = nemesis.getX();
		int y = 100;
		int z = nemesis.getZ();
		int range = 50;
		BlockPos sw = new BlockPos(x - 50, y, z + 50);
		BlockPos se = new BlockPos(x + 50, y, z + 50);
		BlockPos ne = new BlockPos(x + 50, y, z - 50);
		BlockPos nw = new BlockPos(x - 50, y, z - 50);
		return new MapPolygon(sw, se, ne, nw);
	}

	private static TextProperties text() {
		TextProperties text = new TextProperties();
		text.setBackgroundColor(0x000022);
		text.setBackgroundOpacity(.5f);
		text.setColor(0x00ff00);
		text.setOpacity(1f);
		text.setMinZoom(2);
		text.setFontShadow(true);
		return text;
	}

	private static ShapeProperties style() {
		ShapeProperties style = new ShapeProperties();
		style.setStrokeWidth(2);
		style.setStrokeColor(0xc00000);
		style.setStrokeOpacity(.7f);
		style.setFillColor(0xc00000);
		style.setFillOpacity(.4f);
		return style;
	}

	static class SlimeChunkListener implements IOverlayListener {

		SlimeChunkListener(final PolygonOverlay overlay) {

		}

		@Override
		public void onActivate(UIState uiState) {

		}

		@Override
		public void onDeactivate(UIState uiState) {

		}

		@Override
		public void onMouseMove(UIState uiState, Point2D.Double mousePosition, BlockPos blockPosition) {

		}

		@Override
		public void onMouseOut(UIState uiState, Point2D.Double mousePosition, BlockPos blockPosition) {

		}

		@Override
		public boolean onMouseClick(UIState uiState, Point2D.Double mousePosition, BlockPos blockPosition, int button, boolean doubleClick) {
			// TODO check if a nemesis was clicked on and open the GUI
			return true;
		}

	}
}
