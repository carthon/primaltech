package com.carthon.stoneage.data.client;

import com.carthon.stoneage.StoneAge;
import com.carthon.stoneage.setup.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, StoneAge.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        ModelFile itemGenerated = getExistingFile(mcLoc("item/generated"));

        builder("shaped_rock", itemGenerated, "item/shaped_rock");
        builder("fire_stick", itemGenerated, "item/fire_stick");
    }

    private void blockItemModels(Block block) {
        String name = block.getRegistryName().getPath();
        StoneAge.LOGGER.info(block.getRegistryName() + " Generated already:");
        if(!block.equals(ModBlocks.TREE_STUMP.get()))
            withExistingParent(name, modLoc("block/" + name + "_template"));
    }

    private ItemModelBuilder builder(String path, ModelFile itemGenerated, String texture) {
        return getBuilder(path).parent(itemGenerated).texture("layer0", texture);
    }
}
