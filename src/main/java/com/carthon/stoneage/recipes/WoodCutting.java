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

public class WoodCutting implements IRecipe<IInventory> {
    public static final IRecipeType<WoodCutting> WOOD_CUTTING = IRecipeType.register(StoneAge.MOD_ID + ":wood_cutting");

    private final IRecipeType<?> type;
    private final ResourceLocation id;
    final String group;
    Ingredient ingredient;
    final ItemStack result;
    final int chopTimes;

    public WoodCutting(ResourceLocation id, String group,
                       Ingredient ingredient, ItemStack result, int chopTimes) {
        this.type = WOOD_CUTTING;
        this.id = id;
        this.group = group;
        this.ingredient = ingredient;
        this.result = result;
        this.chopTimes = chopTimes;
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        return this.ingredient.test(inv.getItem(0));
    }

    @Override
    public ItemStack assemble(@Nullable IInventory inv) {
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

    public int getChopTimes() {
        return chopTimes;
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
        return ModRecipeSerializer.WOODCUTTING_RECIPE_SERIALIZER.get();
    }

    @Override
    public IRecipeType<?> getType() {
        return type;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<WoodCutting> {
//        private final Serializer.IFactory<WoodCutting> factory;
//        public Serializer(@Nonnull Serializer.IFactory<WoodCutting> factory){
//            this.factory = factory;
//        }
        public Serializer(){}

        @Override
        public WoodCutting fromJson(ResourceLocation recipeId, JsonObject json) {
            String group = JSONUtils.getAsString(json, "group", "");
            JsonElement ingredientJSONElement = JSONUtils.isArrayNode(json, "base")
                    ? JSONUtils.getAsJsonArray(json, "base")
                    : JSONUtils.getAsJsonObject(json, "base");
            Ingredient ingredient = Ingredient.fromJson(ingredientJSONElement);
            ItemStack itemStack;

            if(!json.has("result")){
                throw new com.google.gson.JsonSyntaxException("Missing result, expected to find a string or object");
            }
            if(json.get("result").isJsonObject()){
                itemStack = CraftingHelper.getItemStack(JSONUtils.getAsJsonObject(json, "result"), true);
            }else{
                String resultString = JSONUtils.getAsString(json, "result");
                ResourceLocation resourceLocation = new ResourceLocation(resultString);
                itemStack = new ItemStack(Registry.ITEM.get(resourceLocation));
            }
            int chopCount = JSONUtils.getAsInt(json, "chopTimes", 5);

//            return this.factory.create(recipeId, group, ingredient, tool, itemStack, chopCount);
            return new WoodCutting(recipeId, group, ingredient, itemStack, chopCount);
        }

        @Nullable
        @Override
        public WoodCutting fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
            String group = buffer.readUtf(32767);
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            ItemStack itemstack = buffer.readItem();
            int chopCount = buffer.readVarInt();

            return new WoodCutting(recipeId, group, ingredient, itemstack, chopCount);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, WoodCutting recipe) {
            buffer.writeUtf(recipe.group);
            recipe.ingredient.toNetwork(buffer);
            buffer.writeItem(recipe.result);
            buffer.writeVarInt(recipe.chopTimes);
        }

//        public interface IFactory<T extends WoodCutting>{
//            T create(@Nonnull ResourceLocation resourceLocation, @Nonnull String group, @Nonnull Ingredient ingredient,
//                     @Nonnull Ingredient tool, @Nonnull ItemStack result, int chopTimes);
//        }
    }
}
