package net.torocraft.nemesissystem.gui;

import net.minecraft.entity.EntityLivingBase;

public interface GuiDisplay {
	void setEntity(EntityLivingBase entity);

	void setPosition(int x, int y);

	void draw();
}
