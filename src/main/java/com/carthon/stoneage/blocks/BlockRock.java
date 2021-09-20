package com.carthon.stoneage.blocks;

import com.carthon.stoneage.StoneAge;
import com.carthon.stoneage.blockstates.RockVariation;
import com.carthon.stoneage.data.ModShapesProvider;
import com.carthon.stoneage.setup.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockRock extends BlockBase {
    public static final EnumProperty<RockVariation> ROCK_VARIATION = StoneAge.ROCK_VARIATION;
    private int amount;

    public BlockRock(Properties properties) {
        super(properties, ModShapesProvider.ROCKS_VARIANT);
        amount = 0;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        int index = shapeIndex + state.getValue(ROCK_VARIATION).ordinal();
        return SHAPE.get(index).get(state.getValue(HORIZONTAL_FACING));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(ROCK_VARIATION, RockVariation.TINY);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        RockVariation nextVariation = getNextVariation(state);
        ItemStack item = player.getMainHandItem();
        amount = state.getValue(ROCK_VARIATION).AMOUNT();
        if(player.isCreative()){
            world.setBlockAndUpdate(pos, state.setValue(ROCK_VARIATION, nextVariation));
        }else{
            if(item.getItem() == this.asItem()
                    && state.getValue(ROCK_VARIATION) != RockVariation.LARGE && item.getCount() >= nextVariation.AMOUNT() - amount){
                item.split(nextVariation.AMOUNT() - amount);
                world.setBlockAndUpdate(pos, state.setValue(ROCK_VARIATION, nextVariation));
            } else {
                ItemStack drop = new ItemStack(ModBlocks.ROCK_BLOCK.get().asItem());
                drop.setCount(amount);
                ItemEntity entityItem = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), drop);
                entityItem.setDeltaMovement(new Vector3d(0, 0.1, 0));
                entityItem.setPickUpDelay(40);
                world.addFreshEntity(entityItem);
                world.destroyBlock(pos, false);
            }
        }
        return ActionResultType.sidedSuccess(!world.isClientSide());
    }

    private RockVariation getNextVariation(BlockState state){
        RockVariation variation = RockVariation.TINY;
        if (state.getValue(ROCK_VARIATION) == RockVariation.TINY) {
            variation = RockVariation.SMALL;
        }
        if (state.getValue(ROCK_VARIATION) == RockVariation.SMALL) {
            variation = RockVariation.MEDIUM;
        }
        if (state.getValue(ROCK_VARIATION) == RockVariation.MEDIUM) {
            variation = RockVariation.LARGE;
        }
        if (state.getValue(ROCK_VARIATION) == RockVariation.LARGE) {
            variation = RockVariation.TINY;
        }
        return variation;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ROCK_VARIATION);
    }
}
