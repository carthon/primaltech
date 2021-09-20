package com.carthon.stoneage.data;

import net.minecraft.block.Block;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

import java.util.stream.Stream;

public class ModShapesProvider {
    public static final VoxelShape WORK_STUMP = Stream.of(
            Block.box(11, 6, 5, 13, 10, 13),
            Block.box(4, -2, 4, 12, 5, 7),
            Block.box(4, -2, 9, 12, 4, 12),
            Block.box(9, -2, 4, 12, 5, 12),
            Block.box(4, -2, 4, 7, 5, 12),
            Block.box(3, 0, 3, 13, 6, 13),
            Block.box(1, 11, 1, 15, 16, 2),
            Block.box(2, 10, 2, 14, 15, 14),
            Block.box(1, 11, 14, 15, 16, 15),
            Block.box(14, 11, 2, 15, 16, 14),
            Block.box(1, 11, 2, 2, 16, 14),
            Block.box(3, 6, 3, 13, 10, 5),
            Block.box(3, 6, 5, 5, 10, 13)
    ).reduce((v1, v2) -> {return VoxelShapes.join(v1, v2, IBooleanFunction.OR);}).get();

    public static final VoxelShape[] ROCKS_VARIANT = {
            //Tiny
            Block.box(7, 0, 7, 12, 1, 10),
            //Small
            VoxelShapes.join(Block.box(6, 1, 5.5, 9, 2, 7.5),
                    Block.box(3, 0, 4, 10, 1, 8), IBooleanFunction.OR),
            //Medium
            VoxelShapes.join(Block.box(8, 0, 6.5, 15, 2, 10.5),
                    Block.box(9, 0, 5, 12, 1, 7), IBooleanFunction.OR),
            //Large
            Stream.of(
            Block.box(1, 0, 5, 13, 3, 12),
            Block.box(2, 3, 7, 9, 4, 11),
            Block.box(4, 0, 3, 12, 1, 13)
    ).reduce((v1, v2) -> {return VoxelShapes.join(v1, v2, IBooleanFunction.OR);}).get()

    };

    public static final VoxelShape TREE_STUMP = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
}
