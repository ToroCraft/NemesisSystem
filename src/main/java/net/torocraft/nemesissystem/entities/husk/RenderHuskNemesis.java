package net.torocraft.nemesissystem.entities.husk;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderHusk;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderHuskNemesis extends RenderHusk {

	public static void init() {
		RenderingRegistry.registerEntityRenderingHandler(EntityHuskNemesis.class, RenderHuskNemesis::new);
	}

	public RenderHuskNemesis(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	protected void preRenderCallback(EntityZombie entity, float partialTickTime) {
		float scale = ((EntityHuskNemesis) entity).getNemesisScale();
		GlStateManager.scale(scale, scale, scale);
	}
}
