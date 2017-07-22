package net.torocraft.nemesissystem.gui.displays;

import java.io.IOException;

public interface GuiDisplay {

	void draw(float mouseX, float mouseY);

	void setPosition(int x, int y);

	void clicked(int mouseX, int mouseY, int mouseButton);

}
