package com.carthon.stoneage;

import com.carthon.stoneage.blockstates.RockVariation;
import com.carthon.stoneage.proxy.ClientProxy;
import com.carthon.stoneage.proxy.IProxy;
import com.carthon.stoneage.proxy.ServerProxy;
import com.carthon.stoneage.setup.Registration;
import net.minecraft.state.EnumProperty;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(StoneAge.MOD_ID)
public class StoneAge
{
    public static final EnumProperty<RockVariation> ROCK_VARIATION = EnumProperty.create("variation", RockVariation.class);
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static IProxy proxy = DistExecutor.safeRunForDist(()-> ClientProxy::new, () -> ServerProxy::new);

    public static final String MOD_ID = "stone_age";

    public StoneAge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
//        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientSetup::new);
        Registration.register();
        StoneAgeConfig.load();

        modEventBus.addListener(this::onClientSetup);
        // Register ourselves for server and other game events we are interested in
//        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientSetup (final FMLCommonSetupEvent event){
        proxy.init();
    }
}
