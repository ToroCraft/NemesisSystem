package net.torocraft.nemesissystem.entities.Stray;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderStray;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderStrayNemesis extends RenderStray {

	public static void init() {
		RenderingRegistry.registerEntityRenderingHandler(EntityStrayNemesis.class, RenderStrayNemesis::new);
	}

	public RenderStrayNemesis(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	protected void preRenderCallback(AbstractSkeleton entity, float partialTickTime) {
		float scale = ((EntityStrayNemesis) entity).getNemesisScale();
		GlStateManager.scale(scale, scale, scale);
	}
}
