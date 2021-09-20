package com.carthon.stoneage.recipes;

import com.carthon.stoneage.StoneAge;
import com.carthon.stoneage.setup.ModRecipeSerializer;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class WoodCuttingRecipeBuilder {
    private final IRecipeSerializer<WoodCutting> serializer;
    private final int count;
    private Ingredient base;
    private Item result;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();

    public WoodCuttingRecipeBuilder(IRecipeSerializer<WoodCutting> serializer, Ingredient base, IItemProvider output, int count) {
        result = output.asItem();
        this.base = base;
        this.count = count;
        this.serializer = serializer;
    }
    public static WoodCuttingRecipeBuilder woodCuttingRecipe(Ingredient base, IItemProvider output, int count) {
        return new WoodCuttingRecipeBuilder(ModRecipeSerializer.WOODCUTTING_RECIPE_SERIALIZER.get(), base, output, count);
    }

    public static WoodCuttingRecipeBuilder woodCuttingRecipeFromTag(ITag<Item> base, IItemProvider output, int count) {
        return new WoodCuttingRecipeBuilder(ModRecipeSerializer.WOODCUTTING_RECIPE_SERIALIZER.get(), Ingredient.of(base), output, count);
    }

    public WoodCuttingRecipeBuilder addCriterion(String name, ICriterionInstance criterion) {
        this.advancementBuilder.addCriterion(name, criterion);
        return this;
    }

    public void build(Consumer<IFinishedRecipe> consumer, String id) {
        ResourceLocation resourcelocation = this.result.getRegistryName();
        if ((new ResourceLocation(id)).equals(resourcelocation)) {
            throw new IllegalStateException("WoodCutting " + id + " should remove its 'save' argument");
        } else {
            this.build(consumer, new ResourceLocation(id));
        }
    }
    public void build(Consumer<IFinishedRecipe> consumer) {
        String id = WoodCutting.WOOD_CUTTING.toString() + "_" + result.getRegistryName().getPath();
        ResourceLocation resourcelocation = this.result.getRegistryName();
        if ((new ResourceLocation(id)).equals(resourcelocation)) {
            throw new IllegalStateException("WoodCutting " + id + " should remove its 'save' argument");
        } else {
            this.build(consumer, new ResourceLocation(id));
        }
    }

    public void build(Consumer<IFinishedRecipe> recipe, ResourceLocation id) {
        this.validate(id);
        this.advancementBuilder.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(IRequirementsStrategy.OR);
        assert this.result.getItemCategory() != null;
        recipe.accept(new WoodCuttingRecipeBuilder.Result(id, this.serializer, this.base, this.result, this.count,
                this.advancementBuilder, new ResourceLocation(StoneAge.MOD_ID,
                "recipes/" + this.result.getItemCategory().getRecipeFolderName() + "/" + id.getPath())));
    }

    private void validate(ResourceLocation id) {
        if (this.advancementBuilder.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id);
        }
    }

    public static class Result implements IFinishedRecipe {
        private final ResourceLocation id;
        private final Ingredient base;
        private final int count;
        private final Item output;
        private final Advancement.Builder advancementBuilder;
        private final ResourceLocation advancementId;
        private final IRecipeSerializer<?> serializer;

        public Result(ResourceLocation id, IRecipeSerializer<?> serializer, Ingredient base, Item output, int count, Advancement.Builder advancementBuilder, ResourceLocation advancementId) {
            this.id = id;
            this.serializer = serializer;
            this.base = base;
            this.output = output;
            this.count = count;
            this.advancementBuilder = advancementBuilder;
            this.advancementId = advancementId;
        }

        public void serializeRecipeData(JsonObject json) {
            json.add("base", this.base.toJson());
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("item", Registry.ITEM.getKey(this.output).toString());
            json.add("result", jsonobject);
            if (this.count > 1) {
                jsonobject.addProperty("count", this.count);
            }
        }

        /**
         * Gets the ID for the recipe.
         */
        public ResourceLocation getId() {
            return this.id;
        }

        public IRecipeSerializer<?> getType() {
            return this.serializer;
        }

        /**
         * Gets the JSON for the advancement that unlocks this recipe. Null if there is no advancement.
         */
        @Nullable
        public JsonObject serializeAdvancement() {
            return this.advancementBuilder.serializeToJson();
        }

        /**
         * Gets the ID for the advancement associated with this recipe. Should not be null if {@link #serializeAdvancement()}
         * is non-null.
         */
        @Nullable
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }
}