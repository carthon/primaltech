package com.carthon.stoneage.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ModBaseContainer extends Container {
    private PlayerEntity player;
    private PlayerInventory inventory;
    public final int SIZE;

    protected ModBaseContainer (@Nullable ContainerType<?> type, int id, World world, BlockPos pos,
                           PlayerInventory inventory, PlayerEntity player, int size) {
        super(type, id);
        this.player = player;
        this.inventory = inventory;
        this.SIZE = size;
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);//[index];
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < SIZE) {
                if (!this.moveItemStackTo(itemstack1, SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, SIZE, false)) return ItemStack.EMPTY;

            if (itemstack1.getCount() <= 0) slot.set(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return itemstack;
    }
}
