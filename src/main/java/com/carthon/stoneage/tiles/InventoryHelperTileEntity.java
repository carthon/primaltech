package com.carthon.stoneage.tiles;

import codechicken.lib.math.MathHelper;
import com.carthon.stoneage.inventory.ModItemHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Objects;

public abstract class InventoryHelperTileEntity extends TileEntity implements IInventory {
    protected ModItemHandler input;

    public int rotation = 0;
    public InventoryHelperTileEntity(int invSize, TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
        input = new ModItemHandler(invSize);
    }

    public ModItemHandler getInput(){ return input; }

    public static void spawnItemStack(World world, double x, double y, double z, ItemStack stack) {
        ItemEntity entityItem = new ItemEntity(world, x, y, z, stack);
        entityItem.setDeltaMovement(Vector3d.ZERO);
        entityItem.setPickUpDelay(40);
        world.addFreshEntity(entityItem);
    }

    @Override
    public ItemStack getItem(int slot) {
        return input.getStackInSlot(slot);
    }

    public NonNullList<ItemStack> getItems() {
        return input.getContents();
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack itemstack = ItemStackHelper.removeItem(getItems(), index, count);
        if (!itemstack.isEmpty())
            this.setChanged();
        return itemstack;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if(input.getStackInSlot(index).getCount() < this.getMaxStackSize()){
            input.setStackInSlot(index, stack);
            if (stack.getCount() > this.getMaxStackSize())
                stack.setCount(this.getMaxStackSize());
        }
        this.setChanged();
    }

    public void onPlaced(LivingEntity entity){
        rotation = ((MathHelper.floor((double) (entity.yRot * 4.0F / 360.0F) + 0.5D) & 3) + 1) % 4;
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return input.isEmpty();
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        CompoundNBT invTag = tag.getCompound("inv");
        input.deserializeNBT(invTag);
        rotation = tag.getInt("rotation");
        super.load(state, tag);
    }

    public void markForUpdate() {
        BlockState state = Objects.requireNonNull(this.getLevel()).getBlockState(this.getBlockPos());
        Objects.requireNonNull(this.getLevel()).sendBlockUpdated(this.getBlockPos(), state, state, 3);
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
    public CompoundNBT save(CompoundNBT tag) {
        CompoundNBT compoundNBT = this.input.serializeNBT();
        tag.putInt("rotation", rotation);
        tag.put("inv", compoundNBT);
        return super.save(tag);
    }
}
