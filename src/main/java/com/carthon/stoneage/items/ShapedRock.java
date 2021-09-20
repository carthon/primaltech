package com.carthon.stoneage.items;

import net.minecraft.item.Item;

public class ShapedRock extends Item {
    public ShapedRock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canBeDepleted() {
        return true;
    }
}
