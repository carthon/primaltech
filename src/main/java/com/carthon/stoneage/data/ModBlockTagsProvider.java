package com.carthon.stoneage.data;

import com.carthon.stoneage.StoneAge;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockTagsProvider extends net.minecraft.data.BlockTagsProvider {
    public ModBlockTagsProvider(DataGenerator generatorIn, ExistingFileHelper existingFileHelper) {
        super(generatorIn, StoneAge.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(){
        //getOrCreateBuilder(ModTags.Blocks.ORES_SILVER).add(ModBlocks.SILVER_ORE.get());
        //getOrCreateBuilder(Tags.Blocks.ORES).add(ModBlocks.SILVER_ORE.get());
    }
}
