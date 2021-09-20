package com.carthon.stoneage.tiles;

import com.carthon.stoneage.StoneAgeConfig;
import com.carthon.stoneage.blocks.BlockClayKiln;
import com.carthon.stoneage.recipes.ClayKilnRecipes;
import com.carthon.stoneage.setup.ModTileEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;


public class ClayKilnTileEntity extends InventoryHelperTileEntity implements ITickableTileEntity, ISidedInventory {
    private final IItemHandlerModifiable itemHandler = new ItemStackHandler(1);
    private final RecipeWrapper itemHandlerWrapper = new RecipeWrapper(itemHandler);
    private ItemStack recipeOutput = ItemStack.EMPTY;
    private ItemStack lastItem = ItemStack.EMPTY;
    private double lastCookTime;
    private double temp;
    private boolean active;
    private double progress;
    public ClayKilnTileEntity() {
        super(2, ModTileEntities.CLAY_KILN_TILE_ENTITY_TYPE.get());
        temp = 0;
        active = false;
    }

    public ItemStack getRecipeOutput() {
        return recipeOutput;
    }

    @Override
    public int getContainerSize() {
        return 2;
    }

    private ClayKilnRecipes getRecipe(ItemStack item){
        World world = getLevel();
        assert world != null;
        itemHandler.setStackInSlot(0, item);
        ClayKilnRecipes result = world.getRecipeManager()
                .getRecipeFor(ClayKilnRecipes.CLAY_KILN_RECIPES, itemHandlerWrapper, world).orElse(null);
        if(result != null && !result.getResultItem().isEmpty()){
            recipeOutput = result.assemble(null);
            lastCookTime = result.getCookTime();
        }
        markForUpdate();
        return result;
    }

    public ActionResultType startCooking(ItemStack stack, World worldIn, PlayerEntity player){
        getInput().setStackInSlot(0, new ItemStack(stack.getItem(), 1, stack.getTag()));
        if(lastItem != null && stack != lastItem && recipeOutput.isEmpty()){
            ClayKilnRecipes recipe = getRecipe(stack);
        }
        setProgress(lastCookTime);
        if(player == null || !player.isCreative())
            stack.shrink(1);
        markForUpdate();
        return ActionResultType.sidedSuccess(!worldIn.isClientSide);
    }

    @Override
    public void tick() {
        if(findFire() && getTemp() <= 200){
            setTemp(getTemp() + 1);
        }
        if(!findFire() && getTemp() > 0){
            setTemp(getTemp() - 1);
        }
        World world = getLevel();
        BlockPos pos = getBlockPos();
        if(!active && temp >= 200){
            assert world != null;
            ((BlockClayKiln) getBlockState().getBlock()).setState(world, pos, true);
            markForUpdate();
        } else if(temp <= 50 && active){
            assert world != null;
            ((BlockClayKiln) getBlockState().getBlock()).setState(world, pos, false);
            markForUpdate();
        }

        if(active && !getInput().getStackInSlot(0).isEmpty()) {
            if(getProgress() > 0)
                setProgress(getProgress() - 0.1F);
            else {
                if(!recipeOutput.isEmpty()
                        && recipeOutput != getInput().getStackInSlot(0)){
                    getInput().setStackInSlot(1, recipeOutput);
                    getInput().getStackInSlot(0).shrink(1);
                    recipeOutput = ItemStack.EMPTY;
                    setProgress(0);
                    markForUpdate();
                } else {
                    startCooking(getInput().getStackInSlot(0), world, null);
                }
            }
        }
    }



    private boolean findFire(){
        World world = getLevel();
        if(world != null)
            return world.getBlockState(getBlockPos().below()).getBlock() == Blocks.FIRE
                    || isFireSource(getLevel().getBlockState(getBlockPos().below()).getBlock());
        else
            return false;
    }

    private boolean isFireSource(Block block) {
        List<Block> blockList = new ArrayList<>();
        for(int blocks = 0; blocks < StoneAgeConfig.FIRE_SOURCES.size(); blocks++){
            String entry = StoneAgeConfig.FIRE_SOURCES.get(blocks).trim();
            Block outBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(entry));
            blockList.add(outBlock);
        }
        return blockList.contains(block);
    }

    public void setProgress(double progress) {
        this.progress = progress;
        markForUpdate();
    }

    public double getProgress() {
        return progress;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        temp = nbt.getDouble("temp");
        progress = nbt.getDouble("progress");
        active = nbt.getBoolean("active");
        rotation = nbt.getInt("rotation");
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        nbt.putDouble("temp", temp);
        nbt.putDouble("progress", progress);
        nbt.putBoolean("active", active);
        nbt.putInt("rotation", rotation);
        return nbt;
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = new CompoundNBT();
        return save(nbt);
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        save(nbt);
        return new SUpdateTileEntityPacket(getBlockPos(), 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        load(getBlockState(), packet.getTag());
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return getItems().get(index);
    }

    @Override
    public void setItem(int p_70299_1_, ItemStack p_70299_2_) {

    }

    public void setTemp(double temp) {
        this.temp = temp;
        markForUpdate();
    }

    public double getTemp() {
        return temp;
    }

    public void setActive(boolean active) {
        this.active = active;
        markForUpdate();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[]{ 0, 1 };
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, Direction direction) {
        return index == 0 && getItem(0).getCount() < getMaxStackSize() && getItem(1).isEmpty();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return index == 1;
    }

    @Override
    public void clearContent() {

    }
}
