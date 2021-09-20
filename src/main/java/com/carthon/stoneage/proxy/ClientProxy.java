package com.carthon.stoneage.proxy;

import com.carthon.stoneage.render.ClayKilnRenderer;
import com.carthon.stoneage.render.TreeStumpEntityRenderer;
import com.carthon.stoneage.render.WorkStumpEntityRenderer;
import com.carthon.stoneage.setup.ModTileEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@OnlyIn(Dist.CLIENT)
public class ClientProxy implements IProxy {
    @Override
    public void init() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::onClientSetup);
//        modEventBus.addListener(this::stitchTextures);
//        modEventBus.addListener(this::onModelBake);
    }

    public void onClientSetup(FMLClientSetupEvent e) {
//        for (ModBlocks block : ModBlocks.values()) {
//            if (block.needsSpecialRender()) {
//                RenderUtils.setRenderLayer(block, block.getRenderType());
//            }
//        }

//        NewModBlocks.BLOCK_RENDER_TYPE_MAP.forEach((block, renderType) -> RenderTypeLookup.setRenderLayer(block, renderType.getRenderType()));
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.WORK_STUMP_TILE_ENTITY_TYPE.get(), WorkStumpEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.TREE_STUMP_TILE_ENTITY_TYPE.get(), TreeStumpEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.CLAY_KILN_TILE_ENTITY_TYPE.get(), ClayKilnRenderer::new);
//        ClientRegistry.bindTileEntityRenderer(ModTileEntities.OBSIDIAN_SKULL.get(), ObsidianSkullTileEntityRenderer::new);


        //  ClientRegistry.bindTileEntityRenderer(DarkBeaconTileEntity.class, new DarkBeaconTileEntityRenderer());
        //  ScreenManager.registerFactory(ModContainers.dark_beacon, DarkBeaconScreen::new);
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getInstance().level;
    }

    @Override
    public PlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    @Override
    public void displayStoneAgeiconScreen(ItemStack stack) {
        //Minecraft.getInstance().displayGuiScreen(new ForbiddenmiconScreen(stack));
    }

    public void onModelBake(ModelBakeEvent e) {
//        FullbrightBakedModel.invalidateCache();
//
//        for (ResourceLocation id : e.getModelRegistry().keySet()) {
//            BakedModelOverrideRegistry.BakedModelOverrideFactory factory = ClientProxy.bakedModelOverrideRegistry.get(new ResourceLocation(id.getNamespace(), id.getPath()));
//
//            if (factory != null) {
//                e.getModelRegistry().put(id, factory.create(e.getModelRegistry().get(id), e.getModelRegistry()));
//            }
//        }
    }

    public void stitchTextures(TextureStitchEvent.Pre event) {
//        if (event.getMap().getTextureLocation().equals(Atlases.SIGN_ATLAS)) {
//            event.addSprite(new ResourceLocation(StoneAge.MOD_ID, "entity/signs/edelwood"));
//            event.addSprite(new ResourceLocation(StoneAge.MOD_ID, "entity/signs/cherrywood"));
//            event.addSprite(new ResourceLocation(StoneAge.MOD_ID, "entity/signs/mysterywood"));
//        }
    }
}