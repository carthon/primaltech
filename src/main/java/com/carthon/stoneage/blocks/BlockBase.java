package com.carthon.stoneage.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BlockBase extends Block{
    public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
    protected static final List<Map<Direction, VoxelShape>> SHAPE = new ArrayList<Map<Direction, VoxelShape>>();
    protected int shapeIndex = 0;

    public BlockBase(Properties properties) {
        super(properties);
        this.registerDefaultState(defaultBlockState().setValue(HORIZONTAL_FACING, Direction.NORTH));
    }
    public BlockBase(Properties properties, VoxelShape shape) {
        super(properties);
        this.registerDefaultState(defaultBlockState().setValue(HORIZONTAL_FACING, Direction.NORTH));
        shapeIndex = SHAPE.size();
        build(shape);
    }
    public  BlockBase(Properties properties, VoxelShape[] shapes){
        super(properties);
        this.registerDefaultState(defaultBlockState().setValue(HORIZONTAL_FACING, Direction.NORTH));
        shapeIndex = SHAPE.size();
        build(shapes);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, net.minecraft.util.Rotation rot) {
        return state.setValue(HORIZONTAL_FACING, rot.rotate(state.getValue(HORIZONTAL_FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return rotate(state, mirrorIn.getRotation(state.getValue(HORIZONTAL_FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HORIZONTAL_FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE.get(0).get(state.getValue(HORIZONTAL_FACING));
    }

    public void calculateShapes(Direction dir, VoxelShape shape, Map<Direction, VoxelShape> builtShape){
        int times = (dir.get3DDataValue() - Direction.NORTH.get3DDataValue() + 4) % 4;
        VoxelShape[] buffer = new VoxelShape[] { shape, VoxelShapes.empty() };
        for(int i = 0; i < times; i++){
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.or(buffer[1],
                    VoxelShapes.create(
                            new AxisAlignedBB(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)))
            );
            buffer[0] = buffer[1];
            buffer[1] = VoxelShapes.empty();
        }
        builtShape.put(dir, buffer[0]);
    }

    public void build (VoxelShape[] shapes){
        for(VoxelShape shape : shapes){
            Map<Direction, VoxelShape> builtShape = new HashMap<>();
            for(Direction direction : Direction.values()){
                calculateShapes(direction, shape, builtShape);
            }
            SHAPE.add(builtShape);
        }
    }
    public void build (VoxelShape shape){
        Map<Direction, VoxelShape> builtShape = new HashMap<>();
        for(Direction direction : Direction.values()){
            calculateShapes(direction, shape, builtShape);
        }
        SHAPE.add(builtShape);
    }
}
