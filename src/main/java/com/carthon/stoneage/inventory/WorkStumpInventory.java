package com.carthon.stoneage.inventory;

import com.carthon.stoneage.StoneAge;
import com.carthon.stoneage.tiles.WorkStumpTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Util;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.Objects;

public class WorkStumpInventory extends CraftingInventory {
    private ModItemHandler inv;
    private final WorkStumpTileEntity tile;

    public WorkStumpInventory(Container container, WorkStumpTileEntity tile) {
        super(container, 3, 3);
        this.inv = tile.getInput();
        this.tile = tile;
    }

    @Override
    public int getContainerSize() {
        return 9;
    }

    @Override
    public ItemStack getItem(int slot) {
        validate(slot);
        return inv.getStackInSlot(slot);
    }

    public void validate(int slot){
        if (isValid(slot))return;
        throw new IndexOutOfBoundsException("Someone attempted to poll an outofbounds stack at slot " +
                slot + " report to them, NOT Crafting Station");
    }

    public boolean isValid(int slot){
        return slot >= 0 && slot < getContainerSize();
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public void setChanged() {
        this.tile.setChanged();
        BlockState state = Objects.requireNonNull(this.tile.getLevel()).getBlockState(this.tile.getBlockPos());
        this.tile.getLevel().sendBlockUpdated(this.tile.getBlockPos(), state, state, 3);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        validate(slot);
        ItemStack s = getItem(slot);
        if(s.isEmpty()) return ItemStack.EMPTY;
        setItem(slot, ItemStack.EMPTY);
        return s;
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        validate(slot);
        ItemStack stack = inv.extractItem(slot,count,false);
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        validate(slot);
        inv.setStackInSlot(slot, stack);
    }

    public static IRecipe<CraftingInventory> findRecipe(CraftingInventory inv, World world, PlayerEntity player){
        return world.getRecipeManager().getRecipesFor(IRecipeType.CRAFTING, inv, world).stream().flatMap(recipe -> {
            try {
                return Util.toStream(IRecipeType.CRAFTING.tryMatch(recipe, world, inv));
            } catch (Exception e) {
                StoneAge.LOGGER.error("Bad recipe found: " + recipe.getId().toString());
                StoneAge.LOGGER.error(e.getMessage());
                player.sendMessage(new TranslationTextComponent("text.crafting_station.error", recipe.getId().toString()).withStyle(TextFormatting.DARK_RED), Util.NIL_UUID);
                return null;
            }
        }).findFirst().orElse(null);
    }

    public NonNullList<ItemStack> getStackList(){
        return inv.getContents();
    }
}

