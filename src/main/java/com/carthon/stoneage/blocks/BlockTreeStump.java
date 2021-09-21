package com.carthon.stoneage.blocks;

import com.carthon.stoneage.data.ModShapesProvider;
import com.carthon.stoneage.setup.ModTileEntities;
import com.carthon.stoneage.tiles.TreeStumpTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.SlabType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Objects;

public class BlockTreeStump extends BlockBase {
    public static final EnumProperty<SlabType> TYPE = BlockStateProperties.SLAB_TYPE;

    public BlockTreeStump(Properties properties) {
        super(properties, ModShapesProvider.TREE_STUMP);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        TileEntity t = worldIn.getBlockEntity(pos);
        VoxelShape shape = SHAPE.get(shapeIndex).get(state.getValue(HORIZONTAL_FACING));
        if(shape != null){
            if(t instanceof TreeStumpTileEntity){
                TreeStumpTileEntity tile = (TreeStumpTileEntity) t;
                shape = SHAPE.get(shapeIndex).get(Direction.from3DDataValue(tile.rotation));
            }
        return shape;
        } else{
            return VoxelShapes.block();
        }
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntities.TREE_STUMP_TILE_ENTITY_TYPE.get().create();
    }

    @Override
    public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClientSide() && !player.isCreative()) {
            TreeStumpTileEntity tile = (TreeStumpTileEntity) world.getBlockEntity(pos);
            if (tile != null) {
                InventoryHelper.dropContents(world, pos, tile);
                ItemStack itemstack = tile.getItem(0);
                if (!itemstack.isEmpty())
                    tile.setItem(0, ItemStack.EMPTY);
                CompoundNBT nbt = new CompoundNBT();
                tile.save(nbt);
                ItemStack stack = new ItemStack(Item.BY_BLOCK.get(this), 1, nbt);
                if (tile.getDamage() > 0)
                    stack.setTag(nbt);
            }
        }
        world.playSound(null, pos, SoundEvents.WOOD_BREAK, SoundCategory.BLOCKS, 1F, 1F);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(TYPE);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(!world.isClientSide()){
            TreeStumpTileEntity tileEntity = (TreeStumpTileEntity) world.getBlockEntity(pos);
            if(tileEntity != null){
                tileEntity.blockActivated(player);
            }
        }
        return ActionResultType.sidedSuccess(!world.isClientSide());
    }

    @Override
    public void attack(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        if(!worldIn.isClientSide()){
            TreeStumpTileEntity tileEntity = (TreeStumpTileEntity) worldIn.getBlockEntity(pos);
            assert tileEntity != null;
            if(tileEntity.getItems().isEmpty() && tileEntity.getDamage() <= 0){
                worldIn.playSound(null, pos, SoundEvents.WOOD_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
                worldIn.destroyBlock(pos, true, player);
            }
            tileEntity.onBlockLeftClicked(player);
        }
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (!world.isClientSide() && stack.hasTag()) {
            TreeStumpTileEntity tile = (TreeStumpTileEntity) world.getBlockEntity(pos);
            if (tile != null) {
                if (Objects.requireNonNull(stack.getTag()).hasUUID("damage")) {
                    tile.setDamage(stack.getTag().getInt("damage"));
                    tile.markForUpdate();
                }
            }
        }
        TileEntity tile = world.getBlockEntity(pos);
        if(tile instanceof TreeStumpTileEntity)
            ((TreeStumpTileEntity) tile).onPlaced(placer);
    }
}
