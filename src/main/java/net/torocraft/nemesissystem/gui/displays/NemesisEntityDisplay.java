package net.torocraft.nemesissystem.gui.displays;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityCreature;
import net.torocraft.nemesissystem.registry.NemesisEntry;
import net.torocraft.nemesissystem.util.EntityDecorator;
import net.torocraft.torotraits.api.SpawnApi;

public class NemesisEntityDisplay extends EntityDisplay implements GuiDisplay {

	private final Minecraft mc = Minecraft.getMinecraft();

	public void setNemesis(NemesisDisplayData data) {
		if (data == null) {
			setEntity(null);
		} else {
			setNemesis(data.nemesis);
		}
	}

	public void setNemesis(NemesisEntry nemesis) {
		if (nemesis == null) {
			setEntity(null);
			return;
		}
		EntityCreature entity = createEntity(nemesis);
		if (entity == null) {
			return;
		}
		EntityDecorator.decorate(entity, nemesis);
		setEntity(entity);
	}

	private EntityCreature createEntity(NemesisEntry nemesis) {
		try {
			return SpawnApi.getEntityFromString(mc.world, nemesis.getMob());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
