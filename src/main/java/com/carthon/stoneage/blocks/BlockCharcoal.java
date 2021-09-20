package com.carthon.stoneage.blocks;

import com.carthon.stoneage.StoneAgeConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockCharcoal extends RotatedPillarBlock {

    public BlockCharcoal(Properties properties) {
        super(properties);
    }

    @Override
    public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        return StoneAgeConfig.CHARCOAL_FLAMMABILITY;
    }

    @Override
    public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        return face.get3DDataValue() == Direction.UP.get3DDataValue();
    }
}
