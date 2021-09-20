package com.carthon.stoneage.recipes;

import com.carthon.stoneage.StoneAge;
import com.carthon.stoneage.setup.ModRecipeSerializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClayKilnRecipes implements IRecipe<IInventory> {
    public static final IRecipeType<ClayKilnRecipes> CLAY_KILN_RECIPES = IRecipeType.register(StoneAge.MOD_ID + ":clay_kiln");

    private final IRecipeType<?> type;
    private final ResourceLocation id;
    final String group;
    Ingredient ingredient;
    final ItemStack result;
    final int cookTime;

    public ClayKilnRecipes(ResourceLocation id, String group,
                       Ingredient ingredient, ItemStack result, int cookTime) {
        this.type = CLAY_KILN_RECIPES;
        this.id = id;
        this.group = group;
        this.ingredient = ingredient;
        this.result = result;
        this.cookTime = cookTime;
    }
    @Override
    public boolean matches(IInventory inv, World worldIn) {
        return this.ingredient.test(inv.getItem(0));
    }

    @Override
    public ItemStack assemble(IInventory inv) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return result;
    }

    public int getCookTime() {
        return cookTime;
    }

    public NonNullList<Ingredient> getIngredients() {
        return Stream.of(ingredient).collect(Collectors.toCollection(NonNullList::create));
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeSerializer.CLAY_KILN_RECIPE_SERIALIZER.get();
    }

    @Override
    public IRecipeType<?> getType() {
        return type;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ClayKilnRecipes> {
        public Serializer(){}

        @Override
        public ClayKilnRecipes fromJson(ResourceLocation recipeId, JsonObject json) {
            String group = JSONUtils.getAsString(json, "group", "");
            JsonElement ingredientJSONElement = JSONUtils.isArrayNode(json, "ingredient")
                    ? JSONUtils.getAsJsonArray(json, "ingredient")
                    : JSONUtils.getAsJsonObject(json, "ingredient");
            Ingredient ingredient = Ingredient.fromJson(ingredientJSONElement);
            ItemStack itemStack;
            int cookTime;
            if(!json.has("result")){
                throw new com.google.gson.JsonSyntaxException("Missing result, expected to find a string or object");
            }
            if(json.get("result").isJsonObject()){
                itemStack = CraftingHelper.getItemStack(JSONUtils.getAsJsonObject(json, "result"), true);
                cookTime = JSONUtils.getAsInt((JsonObject)json.get("result"), "cookTime", 5);
            }else{
                String resultString = JSONUtils.getAsString(json, "result");
                ResourceLocation resourceLocation = new ResourceLocation(resultString);
                cookTime = 5;
                StoneAge.LOGGER.info("Cooktime for recipe " + recipeId + " set to 5. Cooktime is not inside JsonObject");
                itemStack = new ItemStack(Registry.ITEM.get(resourceLocation));
            }

            return new ClayKilnRecipes(recipeId, group, ingredient, itemStack, cookTime);
        }

        @Nullable
        @Override
        public ClayKilnRecipes fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
            String group = buffer.readUtf(32767);
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            ItemStack itemstack = buffer.readItem();
            int cookTime = buffer.readVarInt();

            return new ClayKilnRecipes(recipeId, group, ingredient, itemstack, cookTime);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, ClayKilnRecipes recipe) {
            buffer.writeUtf(recipe.group);
            buffer.writeVarInt(recipe.cookTime);
            recipe.ingredient.toNetwork(buffer);
            buffer.writeItem(recipe.result);
        }
    }
}
