package net.torocraft.nemesissystem.entities.zombie;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderZombieNemesis extends RenderZombie {

	public static void init() {
		RenderingRegistry.registerEntityRenderingHandler(EntityZombieNemesis.class, RenderZombieNemesis::new);
	}

	public RenderZombieNemesis(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	protected void preRenderCallback(EntityZombie entity, float partialTickTime) {
		float scale = ((EntityZombieNemesis) entity).getNemesisScale();
		GlStateManager.scale(scale, scale, scale);
	}
}
