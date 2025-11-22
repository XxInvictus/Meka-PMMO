package com.xxinvictus.meka_pmmo;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

/**
 * Main mod class for Meka-PMMO.
 * Provides XP integration between Mekanism machines and Project MMO (PMMO)
 * via mixin-based interception of recipe processing.
 */
@Mod(MekaPMMO.MODID)
public class MekaPMMO
{
    public static final String MODID = "meka_pmmo";
    private static final Logger LOGGER = LogUtils.getLogger();

    public MekaPMMO(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        
        LOGGER.info("=".repeat(50));
        LOGGER.info("Meka-PMMO initialized - Mekanism XP integration active");
        LOGGER.info("Mixin config should be loaded from: mixins.meka_pmmo.json");
        LOGGER.info("=".repeat(50));
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        LOGGER.info("Meka-PMMO common setup complete");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        LOGGER.info("Meka-PMMO: Server starting - XP integration ready");
    }
}
