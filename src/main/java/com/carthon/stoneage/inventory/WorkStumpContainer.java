package com.carthon.stoneage.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;


public class WorkStumpContainer extends Container {

    public WorkStumpContainer() {
        super(null, 0);
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn) {
        return true;
    }
}
