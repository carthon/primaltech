package com.carthon.stoneage.data;

import com.carthon.stoneage.StoneAge;
import com.carthon.stoneage.StoneAgeConfig;
import com.carthon.stoneage.recipes.WoodCuttingRecipeBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static com.carthon.stoneage.StoneAge.LOGGER;


public class DataPackGeneration {

    private static final String DATA = "/data/";
    private static final String CHOPPING_BLOCK_RECIPE_PATH = DATA + StoneAge.MOD_ID + "/recipes/tree_stump";

    public static void prepareStoneAgeDataPack(FMLServerAboutToStartEvent event) {
        if (event != null && event.getServer() != null
                //&& Boolean.TRUE.equals(Configs.SERVER.plankDataPackGeneration.get())) {
                ) {

            String dataPackPath = getDataPackPath(event.getServer());
            if(prepareStoneAgeDataPack(dataPackPath)) { //data pack created or was already existing
                if(clearDirectory(dataPackPath + "/" + StoneAge.MOD_ID+  "/"+ CHOPPING_BLOCK_RECIPE_PATH)) {
                    //generate recipe data
                    generate(dataPackPath);
                }
            }
        }
    }

    private static void generate(String choppingDataPackPath) {
        ResourceLocation tagLog = ItemTags.LOGS.getName();
        Map<ResourceLocation, Item> logs = ForgeRegistries.ITEMS.getEntries().stream().filter(e -> e.getValue().getTags().contains(tagLog))
                .collect(Collectors.toMap(e ->e.getKey().location(), Entry::getValue));

        //the log to plank map
        Map<Item,Item> logPlankMap = new HashMap<>();
        logs.forEach((logResourceLocation, value) -> {
            String possiblePlankName = logResourceLocation.getPath().replace("stripped_", "").replace("_log", "").replace("_wood", "").concat("_planks");
            ResourceLocation plankSearch = new ResourceLocation(logResourceLocation.getNamespace(), possiblePlankName);
            Item foundPlank = ForgeRegistries.ITEMS.getValue(plankSearch);
            if (foundPlank != null && !Items.AIR.equals(foundPlank)) {
                logPlankMap.put(value, foundPlank);
            }
        });

        for (Entry<Item, Item> logPlankEntry : logPlankMap.entrySet()) {
            String fileNameWithoutSuffix = logPlankEntry.getKey().getRegistryName().getNamespace() +
                    "_" +
                    logPlankEntry.getValue().getRegistryName().getPath() +
                    "_from_"+
                    logPlankEntry.getKey().getRegistryName().getPath();

            //recipe serialization for manual chopping
            WoodCuttingRecipeBuilder.woodCuttingRecipe(Ingredient.of(logPlankEntry.getKey()), logPlankEntry.getValue(), StoneAgeConfig.CHOPS_LEFT)
                    .addCriterion("has_oak", hasItem(Items.OAK_LOG))
                    .build(recipe -> serializeAndSave(recipe, choppingDataPackPath  + "/" + StoneAge.MOD_ID+  "/"+ CHOPPING_BLOCK_RECIPE_PATH, "manual_"+fileNameWithoutSuffix), new ResourceLocation(StoneAge.MOD_ID, "manual_chopping/" + fileNameWithoutSuffix)); }

    }

    /**
     * Generates JSON files for the DataPack.
     */
    private static void serializeAndSave(IFinishedRecipe recipe, String path, String fileNameWithoutSuffix) {
        File file = new File(path + "/" + fileNameWithoutSuffix + ".json");

        try (FileWriter fileWriter = new FileWriter(file.getPath());){
            fileWriter.write(recipe.serializeRecipe().toString());
        } catch (IOException e) {
            LOGGER.debug("DataPack Generator - \"" + file.getPath() + "\" could not created" ); //NOSONAR
            LOGGER.debug(e.getLocalizedMessage());
        }

    }


    private static InventoryChangeTrigger.Instance hasItem(IItemProvider... providers) {
        return hasItem(Arrays.stream(providers).map(provider -> ItemPredicate.Builder.item().build()).toArray(ItemPredicate[]::new));
    }

    private static InventoryChangeTrigger.Instance hasItem(ItemPredicate... predicates) {
        return new InventoryChangeTrigger.Instance(EntityPredicate.AndPredicate.ANY, MinMaxBounds.IntBound.ANY, MinMaxBounds.IntBound.ANY, MinMaxBounds.IntBound.ANY, predicates);
    }

    private static String getDataPackPath(MinecraftServer server) {
        String serverPath = server.getServerDirectory().getPath();
//        return server.isDedicatedServer() ? (serverPath + "/" + server.getWorldIconFile().getPath() + "/datapacks") : (serverPath + "/saves/" + server.getDataDirectory().getPath() + "/datapacks");
        return server.getWorldScreenshotFile().getParent() + "/datapacks";
    }

    /**
     * Clears the existing data in Data Pack, useful on reload or recipe changes.
     */
    private static boolean clearDirectory(String path) {
        try {
            FileUtils.cleanDirectory(new File(path));
            return true;
        } catch (IOException e) {
            LOGGER.debug("DataPack Generator - \"" + path + "\" could not be cleared" ); //NOSONAR
            LOGGER.debug(e.getLocalizedMessage());
        } catch (IllegalArgumentException e) {
            LOGGER.debug("DataPack Generator - \"" + path + "\" could not be cleared because path is not a directory or does not exist" ); //NOSONAR
            LOGGER.debug(e.getLocalizedMessage());
        }
        return false;
    }

    /**
     * Create a Horse Power Data Pack in your current world folder
     * @return boolean if the data pack folder structure was created with success
     */
    private static boolean prepareStoneAgeDataPack(String worldFolderPath){

        String path = worldFolderPath+ "/"+ StoneAge.MOD_ID;

        if(createFolder(path)
                && createPackMCMeta(path + "/pack.mcmeta")
                && createFolder(path +"/data")
                && createFolder(path + DATA + StoneAge.MOD_ID)
                && createFolder(path + DATA + StoneAge.MOD_ID + "/recipes")
                && createFolder(path + CHOPPING_BLOCK_RECIPE_PATH))
        {
            LOGGER.debug("DataPack Generator - Folder structure prepared successfully");
            return true;
        }

        LOGGER.debug("DataPack Generator - Error in folder generation");
        return false;
    }

    private static boolean createPackMCMeta(String packMcMetaFilePath) {
        File file = new File(packMcMetaFilePath);

        try {
            if(file.createNewFile()){
                LOGGER.debug("DataPack Generator - \"" + file + "\" created" ); //NOSONAR
            }else{
                LOGGER.debug("DataPack Generator - \"" + file + "\" already exists" ); //NOSONAR
            }

            if(file.length() == 0l)
            {
                writePackMCMetaFileData(file);
            }
            else{
                LOGGER.debug("DataPack Generator - \"" + file + "\" has content, seems ok" ); //NOSONAR
            }

        } catch (IOException e) {
            LOGGER.debug("DataPack Generator - \"" + file + "\" failed to create file " ); //NOSONAR
            LOGGER.debug(e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    private static void writePackMCMetaFileData(File file) throws IOException {
        try(FileWriter fileWriter = new FileWriter(file.getPath());) {
            //create JSON content
            JsonObject pack = new JsonObject();
            pack.add("description",  new JsonPrimitive(StoneAge.MOD_ID + " data pack content"));
            pack.add("pack_format",  new JsonPrimitive(Integer.valueOf(4)));

            JsonObject mcmeta = new JsonObject();
            mcmeta.add("pack", pack);

            fileWriter.write(mcmeta.toString());
            LOGGER.debug("DataPack Generator - \"" + file.getPath() + "\" successful filled file contents" ); //NOSONAR
        } catch (IOException e) {
            LOGGER.debug("DataPack Generator - \"" + file.getPath() + "\" IOException " ); //NOSONAR
            throw e;
        }
    }

    private static boolean createFolder(String path) {
        File folder = new File(path);
        if(folder.exists()) {
            LOGGER.debug("DataPack Generator - \"" + path + "\" already exists " ); //NOSONAR
            return true;
        }
        if(folder.mkdir()){
            LOGGER.debug("DataPack Generator - \"" + path + "\" created" ); //NOSONAR
            return true;
        }
        LOGGER.debug("DataPack Generator - \"" + path + "\" folder creation failed"); //NOSONAR
        return false;
    }
}
