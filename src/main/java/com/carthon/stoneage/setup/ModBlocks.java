package com.carthon.stoneage.setup;

import com.carthon.stoneage.blocks.*;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {

    public static final RegistryObject<BlockWorkStump> WORK_STUMP = register("work_stump_block", () ->
            new BlockWorkStump(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD)
                            .strength(2.0F, 3.0F).harvestTool(ToolType.AXE)),
            ModItemGroups.MOD_ITEM_GROUPS);

    public static final RegistryObject<BlockTreeStump> TREE_STUMP = register("tree_stump_block", () ->
            new BlockTreeStump(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD)
                            .strength(3.0F, 3.0F)),
            ModItemGroups.MOD_ITEM_GROUPS);

    public static final RegistryObject<BlockRock> ROCK_BLOCK = register("rock_block", () ->
            new BlockRock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE)
            .strength(3.0F, 3.0F)), ModItemGroups.MOD_ITEM_GROUPS);

    public static final RegistryObject<BlockClayKiln> CLAY_KILN_BLOCK = register("clay_kiln_block", () ->
            new BlockClayKiln(AbstractBlock.Properties.of(Material.CLAY).sound(SoundType.STONE)
            .strength(1.5F, 10.0F)), ModItemGroups.MOD_ITEM_GROUPS);

    public static final RegistryObject<BlockCharcoal> CHARCOAL_BLOCK = register("charcoal_block", () ->
            new BlockCharcoal(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD)
                .harvestTool(ToolType.AXE)
                .strength(1.5F, 10.0F)), ModItemGroups.MOD_ITEM_GROUPS);

    static void register() {}

    private static <T extends Block> RegistryObject<T> registerNoItem(String name, Supplier<T> block){
        return Registration.BLOCKS.register(name, block);
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block, ItemGroup group){
        RegistryObject<T> ret = registerNoItem(name, block);
        Registration.ITEMS.register(name, () -> new BlockItem(ret.get(),
                new Item.Properties().tab(group)));
        return ret;
    }
}
