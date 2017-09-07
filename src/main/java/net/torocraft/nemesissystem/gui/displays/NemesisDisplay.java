package net.torocraft.nemesissystem.gui.displays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.torocraft.nemesissystem.NemesisConfig;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.discovery.NemesisKnowledge;
import net.torocraft.nemesissystem.discovery.PlayerKnowledgeBase;
import net.torocraft.nemesissystem.gui.GuiNemesisDetails.DisplayType;
import net.torocraft.nemesissystem.network.MessageOpenNemesisDetailsGuiRequest;
import net.torocraft.nemesissystem.registry.NemesisEntry;
import net.torocraft.nemesissystem.util.NemesisUtil;
import net.torocraft.torotraits.traits.Affect;
import net.torocraft.torotraits.traits.Trait;

public class NemesisDisplay implements GuiDisplay {

	private static final String UNKNOWN_VALUE = "????";

	public static int grey = 0xff404040;
	public static int lightGrey = 0xff909090;
	public static int lighterGrey = 0xffc0c0c0;

	private final NemesisEntityDisplay entityDisplay = new NemesisEntityDisplay();
	private final Minecraft mc = Minecraft.getMinecraft();

	private int x;
	private int y;
	private float mouseX;
	private float mouseY;

	private static int WIDTH = 245;
	private static int HEIGHT = 45;

	private NemesisDisplayData data;
	private final FontRenderer fontRenderer = mc.fontRenderer;

	public NemesisDisplay() {
		entityDisplay.setSize(34);
	}

	public void setData(NemesisDisplayData data) {
		this.data = data;
		entityDisplay.setNemesis(data);
	}

	@Override
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
		entityDisplay.setPosition(x + 6, y + 7);
	}

	@Override
	public void clicked(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton == 0 && data != null && data.nemesis != null && isHovering()) {
			NemesisSystem.NETWORK.sendToServer(new MessageOpenNemesisDetailsGuiRequest(data.nemesis.getId()));
		}
	}

	@Override
	public void draw(float mouseX, float mouseY) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		drawWork();
	}

	private void drawWork() {
		if (data != null) {
			drawOutline();
			drawNemesisInfo();
			drawNemesisModel();
		}
	}

	private void drawOutline() {
		int color = isHovering() ? lightGrey : lighterGrey;
		Gui.drawRect(x, y, x + WIDTH, y + 1, color);
		Gui.drawRect(x, y, x + 1, y + HEIGHT + 1, color);
		Gui.drawRect(x + WIDTH, y, x + WIDTH + 1, y + HEIGHT + 1, color);
		Gui.drawRect(x, y + HEIGHT, x + WIDTH, y + HEIGHT + 1, color);
	}

	private boolean isHovering() {
		return mouseX > x && mouseX < x + WIDTH && mouseY > y && mouseY < y + HEIGHT;
	}

	private void drawNemesisInfo() {
		if (data.nemesis != null) {
			drawTitleAndInfo(data.nemesis);
			drawTraits(data.nemesis);
		}
	}

	private void drawTitleAndInfo(NemesisEntry n) {
		int x = this.x + 51;
		int y = this.y + 4;
		fontRenderer.drawString(info(DisplayType.NAME, n.getNameAndTitle()) + " (" + NemesisUtil.romanize(n.getLevel()) + ")", x, y, 0x0);
		fontRenderer.drawString(I18n.format("gui.distance") + ": " + info(DisplayType.LOCATION, data.distance), x, y + 10, grey);
	}

	private void drawTraits(NemesisEntry n) {
		if (n.getTraits() == null) {
			return;
		}

		int x = this.x + 51;
		int y = this.y + 24;

		boolean first = true;

		for (int i = 0; i < n.getTraits().size(); i++) {
			Trait trait = n.getTraits().get(i);
			if (trait.type.getAffect().equals(Affect.STRENGTH)) {
				if (first) {
					first = false;
				} else {
					fontRenderer.drawString(". . .", x, y, grey);
					return;
				}
				String s = info(DisplayType.TRAIT, i, I18n.format("trait." + n.getTraits().get(i).type));
				s += " (" + NemesisUtil.romanize(trait.level) + ")";
				fontRenderer.drawString(s, x, y, grey);
				x += fontRenderer.getStringWidth(s) + 3;
			}
		}
	}

	private void drawNemesisModel() {
		GlStateManager.color(0xff, 0xff, 0xff, 0xff);
		NemesisKnowledge knowledge = getNemesisKnowledge();
		if (!NemesisConfig.DISCOVERY_ENABLED || (knowledge != null && knowledge.name)) {
			entityDisplay.draw(mouseX, mouseY);
		}
	}

	private String info(DisplayType type, String info) {
		return info(type, 0, info);
	}

	private String info(DisplayType type, int info) {
		return info(type, 0, Integer.toString(info, 10));
	}

	private NemesisKnowledge getNemesisKnowledge() {
		PlayerKnowledgeBase knowledgeBase = NemesisSystem.KNOWLEDGE_BASE;

		if (knowledgeBase == null) {
			return null;
		}

		if (data == null || data.nemesis == null || data.nemesis.getId() == null) {
			return null;
		}

		return knowledgeBase.getKnowledgeOfNemesis(data.nemesis.getId());
	}

	private String info(DisplayType type, int index, String info) {

		if (!NemesisConfig.DISCOVERY_ENABLED) {
			return info;
		}

		NemesisKnowledge knowledge = getNemesisKnowledge();

		if (knowledge == null) {
			return UNKNOWN_VALUE;
		}

		if (DisplayType.NAME.equals(type) && knowledge.name) {
			return info;
		}

		if (DisplayType.LOCATION.equals(type) && knowledge.location) {
			return info;
		}

		if (DisplayType.TRAIT.equals(type) && knowledge.traits.contains(index)) {
			return info;
		}

		return UNKNOWN_VALUE;
	}
}
