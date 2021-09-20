package com.carthon.stoneage.setup;

import com.carthon.stoneage.items.ItemFireStick;
import com.carthon.stoneage.items.ShapedRock;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;

public class ModItems {
    public static final RegistryObject<Item> SHAPED_ROCK = Registration.ITEMS.register("shaped_rock", () ->
            new ShapedRock(new Item.Properties().durability(64).tab(ModItemGroups.MOD_ITEM_GROUPS)));

    public static final RegistryObject<Item> FIRE_STICK = Registration.ITEMS.register("fire_stick", () ->
            new ItemFireStick(new Item.Properties().stacksTo(1).tab(ModItemGroups.MOD_ITEM_GROUPS)));

    static void register(){}
}
