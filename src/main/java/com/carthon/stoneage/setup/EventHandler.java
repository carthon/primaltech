package com.carthon.stoneage.setup;

import com.carthon.stoneage.StoneAge;
import com.carthon.stoneage.data.DataPackGeneration;
import net.minecraft.command.CommandSource;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;

@Mod.EventBusSubscriber(modid = StoneAge.MOD_ID)
public class EventHandler {
    @SubscribeEvent
    public static void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        //Add Reload Listener for the Plank Recipe Generator
        DataPackGeneration.prepareStoneAgeDataPack(event);
//        ((IReloadableResourceManager) event.getServer().getDataPackRegistries().getFunctionReloader()).addReloadListener(
//                resourceManager -> DataPackGeneration.prepareStoneAgeDataPack(event)
//        );
    }

    @SubscribeEvent
    public static void onServerStarted(FMLServerStartedEvent event){
        //Reload to make sure Plank Recipes are available
//        if(Boolean.TRUE.equals(true)){
//            ((IReloadableResourceManager) event.getServer().getDataPackRegistries().getFunctionReloader())
//                    .getResource();
//        }
    }
}
