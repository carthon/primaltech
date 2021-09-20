package com.carthon.stoneage.data;

import com.carthon.stoneage.StoneAge;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemTagsProvider extends net.minecraft.data.ItemTagsProvider {

    public ModItemTagsProvider(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, ExistingFileHelper existingFileHelper) {
        super(dataGenerator, blockTagProvider, StoneAge.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(){
        //getOrCreateBuilder(ModTags.Blocks.ORES_SILVER).add(ModBlocks.SILVER_ORE.get());
        //copy(ModTags.Blocks.ORES_SILVER, ModTags.Items.ORES_SILVER);
        //copy(Tags.Blocks.ORES, Tags.Items.ORES);
        //getOrCreateBuilder(ModTags.Items.INGOTS_SILVER).add(ModItems.SILVER_INGOT.get());
    }
}
