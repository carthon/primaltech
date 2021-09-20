package com.carthon.stoneage.data.loot;

import com.carthon.stoneage.setup.ModBlocks;
import com.carthon.stoneage.setup.Registration;
import net.minecraft.block.Block;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraftforge.fml.RegistryObject;

import java.util.stream.Collectors;

public class ModBlockLootTables extends BlockLootTables {

    @Override
    protected void addTables() {
        dropSelf(ModBlocks.WORK_STUMP.get());
        dropSelf(ModBlocks.TREE_STUMP.get());
        dropSelf(ModBlocks.ROCK_BLOCK.get());
        dropSelf(ModBlocks.CLAY_KILN_BLOCK.get());
        dropSelf(ModBlocks.CHARCOAL_BLOCK.get());
        //registerLootTable(Blocks.DIAMOND_ORE, droppingItemWithFortune(Blocks.DIAMOND_ORE, Items.DIAMOND));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return Registration.BLOCKS.getEntries().stream()
                .map(RegistryObject::get).collect(Collectors.toList());
    }
}
