package com.carthon.stoneage.proxy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IProxy {

    void init();
    World getClientWorld();
    PlayerEntity getClientPlayer();

    void displayStoneAgeiconScreen(ItemStack stack);
}
