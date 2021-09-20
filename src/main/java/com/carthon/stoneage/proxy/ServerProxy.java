package com.carthon.stoneage.proxy;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.DEDICATED_SERVER)
public class ServerProxy implements IProxy {

    @Override
    public void init() {
    }

    @Override
    public World getClientWorld() {
        throw new IllegalStateException("Only run on the client");
    }

    @Override
    public PlayerEntity getClientPlayer() {
        throw new IllegalStateException("Only run on the client");
    }

    @Override
    public void displayStoneAgeiconScreen(ItemStack stack) {

    }
}
