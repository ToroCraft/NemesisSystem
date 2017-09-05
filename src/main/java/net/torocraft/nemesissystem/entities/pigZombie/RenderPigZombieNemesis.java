package net.torocraft.nemesissystem.entities.pigZombie;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPigZombie;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderPigZombieNemesis extends RenderPigZombie {

	public static void init() {
		RenderingRegistry.registerEntityRenderingHandler(EntityPigZombieNemesis.class, RenderPigZombieNemesis::new);
	}

	public RenderPigZombieNemesis(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	protected void preRenderCallback(EntityPigZombie entity, float partialTickTime) {
		float scale = ((EntityPigZombieNemesis) entity).getNemesisScale();
		GlStateManager.scale(scale, scale, scale);
	}
}
