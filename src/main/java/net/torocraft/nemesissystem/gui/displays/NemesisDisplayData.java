package net.torocraft.nemesissystem.gui.displays;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.nemesissystem.registry.NemesisEntry;

@SideOnly(Side.CLIENT)
public class NemesisDisplayData {
	public NemesisEntry nemesis;
	public int distance;

	public NemesisDisplayData(NemesisEntry nemesis) {
		this.nemesis = nemesis;
		computeDistance();
	}

	private void computeDistance() {
		if (nemesis == null) {
			return;
		}
		EntityPlayer player = Minecraft.getMinecraft().player;
		distance = (int) Math.sqrt(player.getDistanceSqToCenter(new BlockPos(nemesis.getX(), player.posY, nemesis.getZ())));
	}

}
