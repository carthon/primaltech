package com.carthon.stoneage.data;

import com.carthon.stoneage.recipes.ClayKilnRecipeBuilder;
import com.carthon.stoneage.recipes.WoodCuttingRecipeBuilder;
import com.carthon.stoneage.setup.ModBlocks;
import com.carthon.stoneage.setup.ModItems;
import net.minecraft.block.Blocks;
import net.minecraft.data.*;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(ModBlocks.WORK_STUMP.get(), 1)
                .define('#', ItemTags.LOGS)
                .define('a', ModBlocks.TREE_STUMP.get())
                .pattern(" a ")
                .pattern(" # ")
                .unlockedBy("has_item", has(ItemTags.LOGS))
                .save(consumer);
        ShapedRecipeBuilder.shaped(Blocks.COBBLESTONE, 1)
                .define('#', ModBlocks.ROCK_BLOCK.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .unlockedBy("has_item", has(ModBlocks.ROCK_BLOCK.get()))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(ModBlocks.TREE_STUMP.get(), 1)
                .requires(ItemTags.LOGS)
                .unlockedBy("has_item", has(ItemTags.LOGS))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(ModBlocks.ROCK_BLOCK.get(), 9)
                .requires(Items.COBBLESTONE)
                .unlockedBy("has_item", has(Items.COBBLESTONE))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(ModItems.SHAPED_ROCK.get(), 1)
                .requires(ModBlocks.ROCK_BLOCK.get())
                .unlockedBy("has_item", has(ModBlocks.ROCK_BLOCK.get()))
                .save(consumer);
        WoodCuttingRecipeBuilder.woodCuttingRecipeFromTag(ItemTags.PLANKS, Items.STICK, 3)
                .addCriterion("has_item", has(ModBlocks.TREE_STUMP.get()))
                .build(consumer);

        ClayKilnRecipeBuilder.clayKilnRecipe(Items.CLAY_BALL, Items.BRICK, 10, 1)
                .addCriterion("has_item", has(ModBlocks.CLAY_KILN_BLOCK.get()))
                .build(consumer);
        ClayKilnRecipeBuilder.clayKilnRecipe(ItemTags.LOGS, ModBlocks.CHARCOAL_BLOCK.get(), 20, 1)
                .addCriterion("has_item", has(ModBlocks.CLAY_KILN_BLOCK.get()))
                .build(consumer);
    }
}
