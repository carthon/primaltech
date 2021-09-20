package com.carthon.stoneage.setup;

import com.carthon.stoneage.StoneAge;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

public class ModItemGroups extends ItemGroup{
    private final Supplier<ItemStack> iconSupplier;
    public static  final ModItemGroups MOD_ITEM_GROUPS = new ModItemGroups(StoneAge.MOD_ID,
            () -> new ItemStack(ModBlocks.WORK_STUMP.get().asItem()));

    public ModItemGroups(String label, final Supplier<ItemStack> supplier) {
        super(label);
        iconSupplier = supplier;
    }

    @Override
    public ItemStack makeIcon() {
        return iconSupplier.get();
    }
}
