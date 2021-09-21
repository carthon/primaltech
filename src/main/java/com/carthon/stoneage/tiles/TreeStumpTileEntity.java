package com.carthon.stoneage.tiles;

import com.carthon.stoneage.StoneAgeConfig;
import com.carthon.stoneage.recipes.WoodCutting;
import com.carthon.stoneage.setup.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TreeStumpTileEntity extends InventoryHelperTileEntity implements ISidedInventory {
    private int damage = 20;
    private final IItemHandlerModifiable itemHandler = new ItemStackHandler(1);
    private final RecipeWrapper itemHandlerWrapper = new RecipeWrapper(itemHandler);

    private int chopsLeft = 0;
    private int totalChops = 0;

    private ItemStack recipeResult = ItemStack.EMPTY;

    public TreeStumpTileEntity() {
        super(1, ModTileEntities.TREE_STUMP_TILE_ENTITY_TYPE.get());
    }


    public int getDamage() {
        return damage;
    }
    public void setDamage(int dmg){
        damage = dmg;
    }

    public void onBlockLeftClicked(@Nonnull PlayerEntity player){
//        chop(player, getLevel(), getBlockPos());
    }

    public void chop(@Nonnull PlayerEntity player, World world, BlockPos blockPos){
        assert world != null;
        if(player.getMainHandItem().getToolTypes().contains(ToolType.AXE) && !getInput().isEmpty()){
            if(chopsLeft > 0){
                setChops(chopsLeft - 1);
                player.causeFoodExhaustion(0.5F);
                world.playSound(null, blockPos, SoundEvents.WOOD_HIT, SoundCategory.BLOCKS, 1.0f, 1.0f);
            } else if(recipeResult != ItemStack.EMPTY){
                NonNullList<ItemStack> itemStacks = NonNullList.create();
                itemStacks.add(0, ItemStack.EMPTY);
                itemStacks.set(0, recipeResult);
                itemStacks.get(0).setCount(4);
                InventoryHelper.dropContents(world, blockPos, itemStacks);
                recipeResult = ItemStack.EMPTY;
                getInput().setStackInSlot(0, ItemStack.EMPTY);
                if(StoneAgeConfig.TOOL_DAMAGE) {
                    player.getMainHandItem().hurtAndBreak(1, player,
                            playerEntity -> playerEntity.broadcastBreakEvent(EquipmentSlotType.MAINHAND));
                }
                setDamage(getDamage() - 1);
                world.playSound(null, blockPos, SoundEvents.WOOD_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
            } else{
                return;
            }
        }
        markForUpdate();
    }

    private void setChops(int i) {
        chopsLeft = i;
        markForUpdate();
    }

    public void blockActivated(@Nonnull PlayerEntity player){
        World world = getLevel();
        assert world != null;
        ItemStack itemStack = player.getMainHandItem();
        WoodCutting recipe = getRecipe(itemStack);

        if(player.isShiftKeyDown()){
            ItemStack stack2 = getItem(0);
            if (!stack2.isEmpty()) {
                if (!player.inventory.add(stack2))
                    ForgeHooks.onPlayerTossEvent(player, stack2, false);
                setItem(0, ItemStack.EMPTY);
                markForUpdate();
            }
        } else {
            if(recipe == null && !player.getOffhandItem().isEmpty()){
                itemStack = player.getOffhandItem();
                recipe = getRecipe(itemStack);
            }
            if(getInput().getStackInSlot(0).isEmpty() && (recipe != null
            || itemStack.getToolTypes().contains(ToolType.AXE))){
                getInput().setStackInSlot(0, itemStack.split(1));
                if(recipe != null){
                    totalChops = recipe.getChopTimes();
                    setChops(recipe.getChopTimes());
                    recipeResult = recipe.assemble(null);
                }
                markForUpdate();
            }
        }
    }

    private WoodCutting getRecipe(ItemStack item){
        World world = getLevel();
        assert world != null;
        itemHandler.setStackInSlot(0, item);
        return world.getRecipeManager().getRecipeFor(WoodCutting.WOOD_CUTTING, itemHandlerWrapper, world).orElse(null);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        chopsLeft = nbt.getInt("chopLeft");
        totalChops = nbt.getInt("totalChops");
        damage = nbt.getInt("damage");
        rotation = nbt.getInt("rotation");
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        nbt.putInt("chopLeft", chopsLeft);
        nbt.putInt("totalChops", totalChops);
        nbt.putInt("damage", damage);
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
    public int getContainerSize() {
        return 1;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return getItems().get(index);
    }

    @Override
    public void clearContent() {

    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[]{ 0 };
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, @Nullable Direction direction) {
        WoodCutting recipe = getRecipe(itemStackIn);
        if(recipe != null){
            totalChops = recipe.getChopTimes();
            setChops(recipe.getChopTimes());
            recipeResult = recipe.assemble(null);
            markForUpdate();
        }
        return index == 0 && getItem(0).getCount() < getMaxStackSize();
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return false;
    }
}
