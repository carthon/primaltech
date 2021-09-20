package com.carthon.stoneage;

import codechicken.lib.config.ConfigTag;
import codechicken.lib.config.StandardConfigFile;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Created by covers1624 on 28/10/19.
 */
public class StoneAgeConfig {
    private static ConfigTag config;

    public static int STUMP_HITS;
    public static int STUMP_HEALTH;
    public static int CHOPS_LEFT;
    public static int CHARCOAL_FLAMMABILITY;
    public static boolean TOOL_DAMAGE;
    public static double KILN_EXPERIENCE;
    public static List<String> FIRE_SOURCES;

    public static void load() {
        if (config != null) {
            throw new IllegalStateException("Tried to load config more than once.");
        }
        config = new StandardConfigFile(Paths.get("./config/StoneAge.cfg")).load();
//        ConfigSyncManager.registerSync(new ResourceLocation("enderstorage:config"), config);
        STUMP_HITS = config.getTag("stumpHits")//
                .setComment("Number of hits with stone on Work Stump to craft items.")//
                .setDefaultInt(10)
                .getInt();
        TOOL_DAMAGE = config.getTag("toolDamage")//
                .setComment("Item gets damage while using tree stump for chopping")//
                .setDefaultBoolean(true)
                .getBoolean();
        STUMP_HEALTH = config.getTag("workStumpHealth")//
                .setComment("Number of items you can craft before the Work stump breaks (-1 wont break).")//
                .setDefaultInt(10)//
                .getInt();
        CHOPS_LEFT = config.getTag("chopsLeft")//
                .setComment("Number of chops to craft in Tree stump")//
                .setDefaultInt(10)
                .getInt();
        KILN_EXPERIENCE = config.getTag("clayKilnExperience")//
                .setComment("Experience obtained from using clay kiln")//
                .setDefaultDouble(5)
                .getDouble();
        FIRE_SOURCES = config.getTag("fireSources")//
                .setComment("Blocks considered as fire sources")//
                .setDefaultStringList(Arrays.asList("minecraft:fire"))
                .getStringList();
        CHARCOAL_FLAMMABILITY = config.getTag("charcoalFlammability")//
                .setComment("Chance that fire will consume this block. 300 being a 100%% chance, 0, being a 0%% chance")//
                .setDefaultInt(0)
                .getInt();

//        ResourceLocation personalItemName = new ResourceLocation(personalItemTag.getString());
//        if (ForgeRegistries.ITEMS.containsKey(personalItemName)) {
//            personalItem = new ItemStack(ForgeRegistries.ITEMS.getValue(personalItemName));
//        } else {
//            StoneAge.LOGGER.warn("Failed to load PersonaItem '{}', does not exist. Using default.", personalItemName);
//            personalItemTag.resetToDefault();
//            personalItem = new ItemStack(Items.DIAMOND);
//        }
        config.save();
    }
}