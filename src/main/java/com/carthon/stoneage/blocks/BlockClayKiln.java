package com.carthon.stoneage.blocks;

import com.carthon.stoneage.StoneAgeConfig;
import com.carthon.stoneage.data.ModBlockStateProperties;
import com.carthon.stoneage.setup.ModBlocks;
import com.carthon.stoneage.setup.ModTileEntities;
import com.carthon.stoneage.tiles.ClayKilnTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nullable;

public class BlockClayKiln extends BlockBase{
    public static final BooleanProperty FIRED = ModBlockStateProperties.FIRED;
    public BlockClayKiln(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override 
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(FIRED, Boolean.FALSE)
                .setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        ItemStack stack = player.getMainHandItem();
        if (worldIn.getBlockEntity(pos) instanceof ClayKilnTileEntity) {
            ClayKilnTileEntity tile = (ClayKilnTileEntity) worldIn.getBlockEntity(pos);
            assert tile != null;
            int activeSlot = !tile.getItem(1).isEmpty() ? 1 : 0;
            if (!stack.isEmpty() && tile.getItem(0).isEmpty() && tile.getItem(1).isEmpty()) {
                return tile.startCooking(stack, worldIn, player);
            } else {
                if (!worldIn.isClientSide()) {
                    ItemStack stackInSlot = tile.getItem(activeSlot);
                    if (!stackInSlot.isEmpty()) {
                        if (stackInSlot == tile.getRecipeOutput() && activeSlot == 1) {
                             splitAndSpawnExperience(worldIn, new Vector3d(pos.getX(),pos.getY(),pos.getZ()));
                        }
                        if (!player.inventory.add(stackInSlot))
                            ForgeHooks.onPlayerTossEvent(player, stackInSlot, false);
                        tile.setItem(activeSlot, ItemStack.EMPTY);
                        tile.markForUpdate();
                    }
                }
            }
        }
        return ActionResultType.sidedSuccess(!worldIn.isClientSide());
    }

    private static void splitAndSpawnExperience(World world, Vector3d pos) {
        int i = MathHelper.floor((float)1 * StoneAgeConfig.KILN_EXPERIENCE);
        float f = (float) MathHelper.frac((float)1 * StoneAgeConfig.KILN_EXPERIENCE);
        if (f != 0.0F && Math.random() < (double)f) {
            ++i;
        }

        while(i > 0) {
            int j = ExperienceOrbEntity.getExperienceValue(i);
            i -= j;
            world.addFreshEntity(new ExperienceOrbEntity(world, pos.x, pos.y, pos.z, j));
        }
    }

    @Override
    public void playerDestroy( World world, PlayerEntity player, BlockPos pos, BlockState state,
                               TileEntity tileEntity, ItemStack stack) {
        if (world.isClientSide() && !player.isCreative()) {
            ClayKilnTileEntity tile = (ClayKilnTileEntity) tileEntity;
            if (tile != null) {
                InventoryHelper.dropContents(world, pos, tile);
                for (int i = 0; i < tile.getContainerSize(); ++i) {
                    ItemStack itemstack = tile.getItem(i);
                    if (!itemstack.isEmpty())
                        tile.setItem(i, ItemStack.EMPTY);
                }
                CompoundNBT nbt = new CompoundNBT();
                tile.save(nbt);
                if (tile.isActive())
                    stack.setTag(nbt);
            }
        }
        world.playSound(null, pos, SoundEvents.STONE_BREAK, SoundCategory.BLOCKS, 1F, 1F);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntities.CLAY_KILN_TILE_ENTITY_TYPE.get().create();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FIRED);
    }

    public void setState(World world, BlockPos pos, boolean value) {
        if (!world.isClientSide()) {
            ClayKilnTileEntity tile = (ClayKilnTileEntity) world.getBlockEntity(pos);
            BlockState state = world.getBlockState(pos);
            world.setBlock(pos, ModBlocks.CLAY_KILN_BLOCK.get().defaultBlockState().setValue(HORIZONTAL_FACING,
                    state.getValue(HORIZONTAL_FACING)).setValue(FIRED, value), 3);
            if (tile != null) {
                tile.clearRemoved();
                world.setBlockEntity(pos, tile);
                tile.setActive(value);
                tile.markForUpdate();
            }
        }
    }
}
