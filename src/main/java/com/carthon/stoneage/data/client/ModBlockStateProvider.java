package com.carthon.stoneage.data.client;

import com.carthon.stoneage.StoneAge;
import com.carthon.stoneage.blocks.BlockBase;
import com.carthon.stoneage.blocks.BlockRock;
import com.carthon.stoneage.blockstates.RockVariation;
import com.carthon.stoneage.data.ModBlockStateProperties;
import com.carthon.stoneage.setup.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.Property;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, StoneAge.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        BlockModelBuilder TREE_STUMP_MODEL = models()
            .withExistingParent("tree_stump_block", mcLoc("block/oak_slab"))
            .texture("top", mcLoc("block/oak_log_top"))
            .texture("side", mcLoc("block/oak_log"))
            .texture("bottom",  mcLoc("block/oak_log_top"));

        BlockModelBuilder KILN_BLOCK_MODEL = models()
            .withExistingParent("clay_kiln_block", modLoc("block/clay_kiln"))
            .texture("0", mcLoc("block/clay"))
            .texture("particle", mcLoc("block/clay"));

        BlockModelBuilder KILN_FIRED_MODEL = models()
            .withExistingParent("clay_fired_kiln_block", modLoc("block/clay_kiln"))
            .texture("0", mcLoc("block/terracotta"))
            .texture("particle", mcLoc("block/terracotta"));

        BlockModelBuilder WORK_STUMP_MODEL = models().withExistingParent("work_stump_block", modLoc("block/work_stump_block_template"))
                    .texture("particle", mcLoc("block/oak_log"))
                    .texture("0", mcLoc("block/oak_log"))
                    .texture("1", mcLoc("block/oak_log_top"))
                    .texture("2", modLoc("block/work_stump_top"));

        BlockModelBuilder CHARCOAL_BLOCK_MODEL = models().withExistingParent("charcoal_block", mcLoc("block/cube_column"))
                .texture("side", "stone_age:block/charcoal_block")
                .texture("end", "stone_age:block/charcoal_block_top");

        simpleBlockItem(ModBlocks.TREE_STUMP.get(), TREE_STUMP_MODEL);
        variantBuilderSlabLike(ModBlocks.TREE_STUMP.get(), TREE_STUMP_MODEL);

        simpleBlockItem(ModBlocks.WORK_STUMP.get(), WORK_STUMP_MODEL);
        variantBuilder4D(ModBlocks.WORK_STUMP.get(), WORK_STUMP_MODEL);

        simpleBlockItem(ModBlocks.ROCK_BLOCK.get(), rockVariationBuilder(RockVariation.MEDIUM));
        Map<RockVariation, ModelFile> rockModels = new HashMap<>();
        for(RockVariation variation : RockVariation.values()){
            rockModels.put(variation, rockVariationBuilder(variation));
        }
        variantBuilder(ModBlocks.ROCK_BLOCK.get(), rockModels, BlockRock.ROCK_VARIATION, Stream.of(RockVariation.values()));

        Map<Boolean, ModelFile> kilnModels = new HashMap<>();
        kilnModels.put(false, KILN_BLOCK_MODEL);
        kilnModels.put(true, KILN_FIRED_MODEL);
        variantBuilder(ModBlocks.CLAY_KILN_BLOCK.get(), kilnModels, ModBlockStateProperties.FIRED, ModBlockStateProperties.FIRED.getPossibleValues().stream());
        simpleBlockItem(ModBlocks.CLAY_KILN_BLOCK.get(), KILN_BLOCK_MODEL);

        simpleBlockItem(ModBlocks.CHARCOAL_BLOCK.get(), CHARCOAL_BLOCK_MODEL);
        variantBuilderColumnLike(ModBlocks.CHARCOAL_BLOCK.get(), CHARCOAL_BLOCK_MODEL);
    }

    private <T extends Comparable<T>> void variantBuilder(BlockBase block, Map<T, ModelFile> models, Property<T> prop, Stream<T> variations){
        variations.forEach(variation ->
                getVariantBuilder(block)
                        .partialState().with(prop, variation)
                        .with(BlockRock.HORIZONTAL_FACING, Direction.SOUTH)
                        .modelForState().modelFile(models.get(variation)).addModel()
                        .partialState().with(prop, variation)
                        .with(BlockRock.HORIZONTAL_FACING, Direction.WEST)
                        .modelForState().modelFile(models.get(variation)).rotationY(90).addModel()
                        .partialState().with(prop, variation)
                        .with(BlockRock.HORIZONTAL_FACING, Direction.NORTH)
                        .modelForState().modelFile(models.get(variation)).rotationY(180).addModel()
                        .partialState().with(prop, variation)
                        .with(BlockRock.HORIZONTAL_FACING, Direction.EAST)
                        .modelForState().modelFile(models.get(variation)).rotationY(270).addModel());
    }

    private BlockModelBuilder rockVariationBuilder(RockVariation variation){
        return models().withExistingParent("rock_" + variation.VARIATION() + "_model",
                modLoc("block/rock_"+ variation.VARIATION() +"_block"))
                .texture("particle", mcLoc("block/stone"))
                .texture("0", mcLoc("block/stone"));
    }

    private void variantBuilder4D(BlockBase block, ModelFile model) {
        getVariantBuilder(block)
                .partialState().with(BlockBase.HORIZONTAL_FACING, Direction.SOUTH)
                .modelForState().modelFile(model).addModel()
                .partialState().with(BlockBase.HORIZONTAL_FACING, Direction.WEST)
                .modelForState().modelFile(model).rotationY(90).addModel()
                .partialState().with(BlockBase.HORIZONTAL_FACING, Direction.NORTH)
                .modelForState().modelFile(model).rotationY(180).addModel()
                .partialState().with(BlockBase.HORIZONTAL_FACING, Direction.EAST)
                .modelForState().modelFile(model).rotationY(270).addModel();
    }

    private void variantBuilderSlabLike(BlockBase block, ModelFile model){
            getVariantBuilder(block)
                    .partialState().with(SlabBlock.TYPE, SlabType.BOTTOM).addModels(new ConfiguredModel(model))
                    .partialState().with(SlabBlock.TYPE, SlabType.TOP).addModels(new ConfiguredModel(model))
                    .partialState().with(SlabBlock.TYPE, SlabType.DOUBLE).addModels(new ConfiguredModel(model));
    }

    private void variantBuilderColumnLike(Block block, ModelFile model){
        getVariantBuilder(block)
                .partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.Y)
                .modelForState().modelFile(model).addModel()
                .partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.Z)
                .modelForState().modelFile(model).rotationX(90).addModel()
                .partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.X)
                .modelForState().modelFile(model).rotationX(90).rotationY(90).addModel();
    }
}
