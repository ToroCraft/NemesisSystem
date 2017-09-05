package net.torocraft.nemesissystem.entities.zombieVillager;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderZombieVillager;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderZombieVillagerNemesis extends RenderZombieVillager {

	public static void init() {
		RenderingRegistry.registerEntityRenderingHandler(
				EntityZombieVillagerNemesis.class, RenderZombieVillagerNemesis::new);
	}

	public RenderZombieVillagerNemesis(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	protected void preRenderCallback(EntityZombieVillager entity, float partialTickTime) {
		float scale = ((EntityZombieVillagerNemesis) entity).getNemesisScale();
		GlStateManager.scale(scale, scale, scale);
	}
}
