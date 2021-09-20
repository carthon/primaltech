package com.carthon.stoneage.setup;

import com.carthon.stoneage.recipes.ClayKilnRecipes;
import com.carthon.stoneage.recipes.WoodCutting;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Supplier;

public class ModRecipeSerializer {
    public static final RegistryObject<IRecipeSerializer<WoodCutting>> WOODCUTTING_RECIPE_SERIALIZER =
            register("wood_cutting", WoodCutting.Serializer::new);

    public static final RegistryObject<IRecipeSerializer<ClayKilnRecipes>> CLAY_KILN_RECIPE_SERIALIZER =
            register("clay_kiln", ClayKilnRecipes.Serializer::new);

    public static void register(){}

    static <S extends IRecipeSerializer<T>, T extends IRecipe<?>> RegistryObject<S> register(String key, Supplier<S> recipeSerializer) {
        return Registration.RECIPE_SERIALIZER_TYPE.register(key, recipeSerializer);
    }
}
