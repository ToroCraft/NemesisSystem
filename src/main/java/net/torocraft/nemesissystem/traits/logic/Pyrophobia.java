package net.torocraft.nemesissystem.traits.logic;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.util.BehaviorUtil;

public class Pyrophobia {
	public static void onUpdate(EntityLiving entity, Nemesis nemesis) {
		if (entity.isBurning()) {
			BlockPos targetPos = findNearbyWater(entity.world, entity, 5, 4);
			if (targetPos == null) {
				targetPos = BehaviorUtil.findRandomBlock((EntityCreature)entity);
			}
			BehaviorUtil.moveToBlock(entity, targetPos, 2.0D);
		}
	}

	private static BlockPos findNearbyWater(World worldIn, Entity entityIn, int horizontalRange, int verticalRange) {
		BlockPos blockpos = new BlockPos(entityIn);
		int x = blockpos.getX();
		int y = blockpos.getY();
		int z = blockpos.getZ();
		float f = (float)(horizontalRange * horizontalRange * verticalRange * 2);
		BlockPos blockPos = null;
		BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

		for (int l = x - horizontalRange; l <= x + horizontalRange; ++l) {
			for (int i = y - verticalRange; i <= y + verticalRange; ++i) {
				for (int j = z - horizontalRange; j <= z + horizontalRange; ++j) {
					mutableBlockPos.setPos(l, i, j);
					IBlockState iblockstate = worldIn.getBlockState(mutableBlockPos);

					if (iblockstate.getMaterial() == Material.WATER) {
						float f1 = (float)((l - x) * (l - x) + (i - y) * (i - y) + (j - z) * (j - z));

						if (f1 < f) {
							f = f1;
							blockPos = new BlockPos(mutableBlockPos);
						}
					}
				}
			}
		}

		return blockPos;
	}
}
