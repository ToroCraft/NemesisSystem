package net.torocraft.nemesissystem.entities.skeleton;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSkeleton;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderSkeletonNemesis extends RenderSkeleton {

	public static void init() {
		RenderingRegistry.registerEntityRenderingHandler(EntitySkeletonNemesis.class, RenderSkeletonNemesis::new);
	}

	public RenderSkeletonNemesis(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	protected void preRenderCallback(AbstractSkeleton entity, float partialTickTime) {
		float scale = ((EntitySkeletonNemesis) entity).getNemesisScale();
		GlStateManager.scale(scale, scale, scale);
	}
}
