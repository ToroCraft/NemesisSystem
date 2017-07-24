package net.torocraft.nemesissystem.gui.displays;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityCreature;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.util.EntityDecorator;
import net.torocraft.nemesissystem.util.SpawnUtil;

public class NemesisEntityDisplay extends EntityDisplay implements GuiDisplay {

	private final Minecraft mc = Minecraft.getMinecraft();

	public void setNemesis(NemesisDisplayData data) {
		if (data == null) {
			setEntity(null);
		} else {
			setNemesis(data.nemesis);
		}
	}

	public void setNemesis(Nemesis nemesis) {
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

	private EntityCreature createEntity(Nemesis nemesis) {
		try {
			return SpawnUtil.getEntityFromString(mc.world, nemesis.getMob());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
