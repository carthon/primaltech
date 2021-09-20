package com.carthon.stoneage.blockstates;

import net.minecraft.util.IStringSerializable;

public enum RockVariation implements IStringSerializable {
    TINY("tiny", 1),
    SMALL("small", 3),
    MEDIUM("medium", 4),
    LARGE("large", 6);

    private final Object[] values;

    RockVariation(Object... vals){
        this.values = vals;
    }
    public String VARIATION(){

        return (String) this.values[0];
    }

    public int AMOUNT() {
        return (int) this.values[1];
    }

    @Override
    public String getSerializedName() {
        return (String) this.values[0];
    }
}
