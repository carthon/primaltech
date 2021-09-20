package com.carthon.stoneage.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;

public class ModItemHandler extends ItemStackHandler {
    public ModItemHandler(int size){
        super(size);
    }

    public NonNullList<ItemStack> getContents(){
        return stacks;
    }
    public boolean isEmpty(){
        return getContents().stream().allMatch(ItemStack::isEmpty);
    }
}
