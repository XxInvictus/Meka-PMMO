package com.xxinvictus.meka_pmmo;

import com.mojang.logging.LogUtils;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
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

    public MekaPMMO(IEventBus modEventBus, ModContainer modContainer)
    {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        NeoForge.EVENT_BUS.register(this);

        // Register our mod's ForgeConfigSpec so that NeoForge can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        
        // Set system properties for mixin plugin based on config
        // This allows early disabling of mixins before mixin application
        // Default to true if config not yet loaded
        try {
            // Try to read config values if already loaded
            boolean enableEnergizedSmelterXP = Config.SPEC.isLoaded() ? Config.enableEnergizedSmelterXP : true;
            boolean enableDigitalMinerXP = Config.SPEC.isLoaded() ? Config.enableDigitalMinerXP : true;
            System.setProperty("mekapmmo.enableEnergizedSmelterXP", String.valueOf(enableEnergizedSmelterXP));
            System.setProperty("mekapmmo.enableDigitalMinerXP", String.valueOf(enableDigitalMinerXP));
        } catch (Exception e) {
            // Config not loaded yet, default to true
            System.setProperty("mekapmmo.enableEnergizedSmelterXP", "true");
            System.setProperty("mekapmmo.enableDigitalMinerXP", "true");
        }
        
        LOGGER.info("=".repeat(50));
        LOGGER.info("Meka-PMMO initialized - Mekanism XP integration active");
        LOGGER.info("Energized Smelter XP: " + System.getProperty("mekapmmo.enableEnergizedSmelterXP", "true").toUpperCase());
        LOGGER.info("Digital Miner XP: " + System.getProperty("mekapmmo.enableDigitalMinerXP", "true").toUpperCase());
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
