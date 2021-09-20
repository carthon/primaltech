package com.carthon.stoneage.tiles;

import com.carthon.stoneage.StoneAge;
import com.carthon.stoneage.inventory.WorkStumpContainer;
import com.carthon.stoneage.inventory.WorkStumpInventory;
import com.carthon.stoneage.setup.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorkStumpTileEntity extends InventoryHelperTileEntity implements ITickableTileEntity {
//    private static final int[] SLOTS = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    public float[] itemRotation = {0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F};
    public int[] itemJump = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    public int[] itemJumpPrev = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    public boolean hit = false;
    public int damage = 0;
    public int strikes = 0;
    WorkStumpInventory craftMatrix;

    public WorkStumpTileEntity(){
        super(10, ModTileEntities.WORK_STUMP_TILE_ENTITY_TYPE.get());
        craftMatrix = new WorkStumpInventory(new WorkStumpContainer(), this);
    }

    public void tick() {
        World world = getLevel();
        assert world != null;

        if (world.isClientSide()) {
            itemJumpPrev = itemJump;
        }
        if (getHit()) {
            if (world.isClientSide) {
                for(int count = 0; count < 9; count++) {
                    itemJump[count] = 1 + world.random.nextInt(5);
                    itemRotation[count] = (world.random.nextFloat() - world.random.nextFloat()) * 25F;
                }
            }
            if (!world.isClientSide)
                world.playSound(null, getBlockPos(), SoundEvents.WOOD_HIT, SoundCategory.BLOCKS, 1F, 0.75F);
            setHit(false);
        }

        if (!getHit()) {
            float jumpStep = 0.5F;
            if (world.isClientSide) {
                if (itemJump[0] > 0)
                    itemJump[0]-=jumpStep;
                if (itemJump[1] > 0)
                    itemJump[1]-=jumpStep;
                if (itemJump[2] > 0)
                    itemJump[2] -= jumpStep;
                if (itemJump[3] > 0)
                    itemJump[3] -= jumpStep;
                if (itemJump[4] > 0)
                    itemJump[4] -= jumpStep;
                if (itemJump[5] > 0)
                    itemJump[5] -= jumpStep;
                if (itemJump[6] > 0)
                    itemJump[6] -= jumpStep;
                if (itemJump[7] > 0)
                    itemJump[7] -= jumpStep;
                if (itemJump[8] > 0)
                    itemJump[8] -= jumpStep;
            }
        }
    }

    public void checkWorkStump(){
        World world = getLevel();
        //if (this instanceof TileEntityWorkStumpUpgraded ? getStrikes() >= ConfigHandler.CRAFTING_STRIKES_II : getStrikes() >= ConfigHandler.CRAFTING_STRIKES) {
        assert world != null;
        IRecipe<CraftingInventory> output = WorkStumpInventory.findRecipe(craftMatrix, world,StoneAge.proxy.getClientPlayer());
    if (getStrikes() >= 3 && output != null) {
            if (craftMatrix.getStackList().isEmpty()) {
                setStrikes(0);
                return;
            }
            NonNullList<ItemStack> stuffLeft = output.getRemainingItems(craftMatrix);

            for (int index = 0; index < 9; index++) {
                if(!stuffLeft.get(index).isEmpty())
                    setItem(index, stuffLeft.get(index));
                else
                    removeItem(index, 1);
            }
            BlockPos pos = getBlockPos();
            spawnItemStack(world, pos.getX() + 0.5D, pos.getY() + 1.3D, pos.getZ() + 0.5D, output.getResultItem());
            setDamage(getDamage() + 1);
            setStrikes(0);
        }
    }

    public boolean getHit() {
        return hit;
    }

    public void setHit(boolean isHit) {
        hit = isHit;
    }

    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        nbt.putInt("rotation", rotation);
        nbt.putInt("damage", damage);
        nbt.putInt("strikes", strikes);
        nbt.putBoolean("hit", hit);
        return nbt;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        rotation = nbt.getInt("rotation");
        damage = nbt.getInt("damage");
        strikes = nbt.getInt("strikes");
        hit = nbt.getBoolean("hit");
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
    public void onPlaced(LivingEntity entity) {
        super.onPlaced(entity);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        load(getBlockState(), packet.getTag());
    }

    @Override
    public int getContainerSize() {
        return 9;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return getItems().get(index);
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        return true;
    }

    public void setStrikes(int strikes) {
        this.strikes = strikes;
        markForUpdate();
    }

    public void addStrikes(){
        this.strikes++;
        markForUpdate();
    }

    public int getStrikes() {
        return strikes;
    }

    public void setDamage(int damage) {
        this.damage = damage;
        markForUpdate();
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public void clearContent() {

    }
}