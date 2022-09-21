package com.carthon.stoneage.setup;

import com.carthon.stoneage.StoneAge;
import com.carthon.stoneage.data.DataPackGeneration;
import com.carthon.stoneage.tiles.TreeStumpTileEntity;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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
    }
    @SubscribeEvent
    public void onPlayerBreakBlock(PlayerInteractEvent.LeftClickBlock event){
        TileEntity tileEntity = event.getWorld().getBlockEntity(event.getPos());
        if (tileEntity instanceof TreeStumpTileEntity)        {
            TreeStumpTileEntity treeStumpTileEntity = (TreeStumpTileEntity) event.getWorld().getBlockEntity(event.getPos());
//            BlockTreeStump block = (BlockTreeStump) event.getWorld().getBlockState(event.getPos()).getBlock();
            //Intentar mejorar el dar un solo click para craftear de forma mas intuitiva
            event.setCanceled(treeStumpTileEntity.getItem(0).getItem() != Items.AIR);
            treeStumpTileEntity.chop(event.getPlayer(), event.getWorld(), event.getPos());
        }
    }
}