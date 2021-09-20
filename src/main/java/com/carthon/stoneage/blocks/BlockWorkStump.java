package com.carthon.stoneage.blocks;

import codechicken.lib.math.MathHelper;
import codechicken.lib.raytracer.IndexedVoxelShape;
import codechicken.lib.raytracer.MultiIndexedVoxelShape;
import codechicken.lib.raytracer.VoxelShapeCache;
import codechicken.lib.vec.*;
import com.carthon.stoneage.StoneAgeConfig;
import com.carthon.stoneage.data.ModShapesProvider;
import com.carthon.stoneage.setup.ModItems;
import com.carthon.stoneage.setup.ModTileEntities;
import com.carthon.stoneage.tiles.WorkStumpTileEntity;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nullable;
import java.util.Objects;

public class BlockWorkStump extends BlockBase {

    private static final IndexedVoxelShape WORK_BODY = new IndexedVoxelShape(ModShapesProvider.WORK_STUMP, 0);
    private static final IndexedVoxelShape[][] SLOTS = new IndexedVoxelShape[4][9];
    public static final Transformation[] slotsT = new Transformation[9];
    private static final VoxelShape[] SHAPES = new VoxelShape[4];
//    public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;

    static {
        int slots = 0;
        for(int i = 2; i >= 0; i--)
            for(int j = 0; j < 3; j++){
                slotsT[slots] = new Translation((3/15D) + ((4D / 15D) * i), 14.5D/16D,
                        (3/15D) + ((4D / 15D) * j));
                slots++;
            }
        for (int rot = 0; rot < 4; rot++){
            for(int slot = 0; slot < 9; slot++){
                Cuboid6 cuboid = new Cuboid6(-2 / 16D, 0, -2 / 16D, 1 / 16D, 1 / 16D, 1 / 16D);
                cuboid.apply(new Translation(1/16D,0,1/16D));
                cuboid.apply(slotsT[slot]);
                cuboid.apply(new Rotation((-90 * rot) * MathHelper.torad, Vector3.Y_POS).at(new Vector3(0.5, 0, 0.5)));
                SLOTS[rot][slot] = new IndexedVoxelShape(VoxelShapeCache.getShape(cuboid), slot + 1);
            }
            ImmutableSet.Builder<IndexedVoxelShape> cuboids = ImmutableSet.builder();
            cuboids.add(SLOTS[rot]);
            cuboids.add(WORK_BODY);
            SHAPES[rot] = new MultiIndexedVoxelShape(WORK_BODY, cuboids.build());
        }
    }

    public BlockWorkStump(Properties properties) {
        super(properties);
    }
    
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
        TileEntity t = worldIn.getBlockEntity(pos);
        VoxelShape shape = WORK_BODY;
        if(t instanceof WorkStumpTileEntity){
            WorkStumpTileEntity tile = (WorkStumpTileEntity) t;
            shape = SHAPES[tile.rotation];
        }
        return shape;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntities.WORK_STUMP_TILE_ENTITY_TYPE.get().create();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.isClientSide() && !player.isCreative()) {
            WorkStumpTileEntity tile = (WorkStumpTileEntity) world.getBlockEntity(pos);
            if (tile != null) {
                InventoryHelper.dropContents(world, pos, tile);
                for (int i = 0; i < tile.getContainerSize(); ++i) {
                    ItemStack itemstack = tile.getItem(i);
                    if (!itemstack.isEmpty())
                        tile.setItem(i, ItemStack.EMPTY);
                }
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
    public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);
        if (!world.isClientSide() && stack.hasTag()) {
            WorkStumpTileEntity tile = (WorkStumpTileEntity) world.getBlockEntity(pos);
            if (tile != null) {
                if (Objects.requireNonNull(stack.getTag()).hasUUID("damage")) {
                    tile.setDamage(stack.getTag().getInt("damage"));
                    tile.markForUpdate();
                }
            }
        }
        TileEntity tile = world.getBlockEntity(pos);
        if(tile instanceof  WorkStumpTileEntity)
            ((WorkStumpTileEntity) tile).onPlaced(placer);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        ItemStack stack = player.getMainHandItem();
        Item craftTool = ModItems.SHAPED_ROCK.get();
        int damage = StoneAgeConfig.STUMP_HEALTH;

        if (world.getBlockEntity(pos) instanceof WorkStumpTileEntity) {
            WorkStumpTileEntity tile = (WorkStumpTileEntity) world.getBlockEntity(pos);
            int slotClicked = hit.subHit - 1;
            if(slotClicked >= 0 && slotClicked < 9) {
                if (stack.getItem() != craftTool) {
                    if (!stack.isEmpty() && tile.getItem(slotClicked).isEmpty()) {
                        if (!world.isClientSide) {
                            tile.setItem(slotClicked, stack.split(1));
                            tile.setStrikes(0);
                            tile.markForUpdate();
                            return ActionResultType.sidedSuccess(!world.isClientSide());
                        }
                    } else {
                        ItemStack stack2 = tile.getItem(slotClicked);
                        if (!stack2.isEmpty()) {
                            if (!player.inventory.add(stack2))
                                ForgeHooks.onPlayerTossEvent(player, stack2, false);
                            tile.setItem(slotClicked, ItemStack.EMPTY);
                            tile.setStrikes(0);
                            tile.markForUpdate();
                        }
                    }
                } else if (!stack.isEmpty() && stack.getItem() == craftTool) {
                    assert tile != null;
                    tile.addStrikes();
                    if (stack.isDamageableItem())
                        stack.hurtAndBreak(1, player, (item) ->
                                item.broadcastBreakEvent(player.getUsedItemHand()));
                    tile.setHit(true);
                    tile.markForUpdate();
                    if (!tile.getItems().isEmpty() && tile.getStrikes() >= StoneAgeConfig.STUMP_HITS)
                        tile.checkWorkStump();
                    if (tile.getDamage() >= damage && damage != -1) {
                        playerDestroy(world, player,pos, state, null, stack);
                        world.playSound(null, pos, SoundEvents.WOOD_BREAK, SoundCategory.BLOCKS, 1F, 1F);
                        world.destroyBlock(pos, false);
                    }
                }
            }
            else if (hit.getDirection().get3DDataValue() != 1
                    && hit.getDirection().get3DDataValue() == state.getValue(HORIZONTAL_FACING).get3DDataValue()) {
                if (!stack.isEmpty() && tile.getItem(9).isEmpty() && stack.getItem() == craftTool) {
                    if (!world.isClientSide) {
                        tile.setItem(9, stack.split(1));
                        tile.markForUpdate();
                        return ActionResultType.sidedSuccess(world.isClientSide());
                    }
                } else {
                    ItemStack stack2 = tile.getItem(9);
                    if (!stack2.isEmpty() && !world.isClientSide) {
                        if (!player.inventory.add(stack2))
                            ForgeHooks.onPlayerTossEvent(player, stack2, false);
                        tile.setItem(9, ItemStack.EMPTY);
                        tile.markForUpdate();
                        return ActionResultType.sidedSuccess(!world.isClientSide());
                    }
                }
            }
        }
        return ActionResultType.sidedSuccess(!world.isClientSide());
    }

//    @Override
//    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
//        super.createBlockStateDefinition(builder);
//        builder.add(HORIZONTAL_FACING);
//    }
//
//    @Override
//    public BlockState rotate(BlockState state, net.minecraft.util.Rotation rot) {
//        return state.with(HORIZONTAL_FACING, rot.rotate(state.get(HORIZONTAL_FACING)));
//    }
//
//    @Nullable
//    @Override
//    public BlockState getStateForPlacement(BlockItemUseContext context) {
//        return this.getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
//    }
}