package net.torocraft.nemesissystem.entities.husk;

import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.entities.INemesisEntity;
import net.torocraft.nemesissystem.registry.NemesisEntry;

public class EntityHuskNemesis extends EntityHusk implements INemesisEntity {

	public static String NAME = NemesisSystem.MODID + "_husk";

	private static final String NBT_SCALE = NemesisSystem.MODID + "_scale";
	private static final DataParameter<Float> SCALE = EntityDataManager.createKey(EntityHuskNemesis.class, DataSerializers.FLOAT);

	private float scale = 1;

	public static void init(int entityId) {
		EntityRegistry.registerModEntity(
				new ResourceLocation(NemesisSystem.MODID, NAME),
				EntityHuskNemesis.class, NAME, entityId,
				NemesisSystem.INSTANCE, 60, 2,
				true, 0xFFFFFF, 0x000000);
	}

	public EntityHuskNemesis(World worldIn) {
		super(worldIn);
	}

	protected void entityInit() {
		super.entityInit();
		dataManager.register(SCALE, 1f);
	}

	@Override
	public void setNemesis(NemesisEntry nemesis) {
		setScale(scaleForLevel(nemesis.getLevel()));
	}

	@Override
	public float getEyeHeight() {
		return scale * super.getEyeHeight();
	}

	private void setScale(float scale) {
		if (scale < 1) {
			scale = 1;
		}
		dataManager.set(SCALE, scale);
		getEntityData().setFloat(NBT_SCALE, scale);
		multiplySize(scale);
		this.scale = scale;
	}

	private static float scaleForLevel(int level) {
		return 1.20f + ((float) level / 8);
	}

	public float getNemesisScale() {
		float scale = dataManager.get(SCALE);
		if (world.isRemote && scale != this.scale) {
			setScale(scale);
		}
		return scale;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		if (!world.isRemote) {
			setScale(getEntityData().getFloat(NBT_SCALE));
		}
	}

}
