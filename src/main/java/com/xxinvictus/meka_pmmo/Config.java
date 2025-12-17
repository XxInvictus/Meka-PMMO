package com.xxinvictus.meka_pmmo;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Configuration for Meka-PMMO mod.
 * Currently minimal, ready for future config options like:
 * - Enable/disable XP for automated systems
 * - XP multiplier
 * - Debug logging toggle
 */
@EventBusSubscriber(modid = MekaPMMO.MODID)
public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue ENABLE_DEBUG_LOGGING = BUILDER
            .comment("Enable debug logging for Mekanism XP integration")
            .translation("meka_pmmo.configuration.enableDebugLogging")
            .define("enableDebugLogging", false);

    private static final ModConfigSpec.BooleanValue ENABLE_ENERGIZED_SMELTER_XP = BUILDER
            .comment("Enable XP rewards from Energized Smelter smelting operations")
            .translation("meka_pmmo.configuration.enableEnergizedSmelterXP")
            .define("enableEnergizedSmelterXP", true);

    private static final ModConfigSpec.BooleanValue ENABLE_DIGITAL_MINER_XP = BUILDER
            .comment("Enable XP rewards from Digital Miner mining operations")
            .translation("meka_pmmo.configuration.enableDigitalMinerXP")
            .define("enableDigitalMinerXP", true);

    private static final ModConfigSpec.BooleanValue ENABLE_DIGITAL_MINER_SKILL_REQUIREMENTS = BUILDER
            .comment("Enable PMMO skill requirement checks for Digital Miner (prevents mining blocks without sufficient skill)")
            .translation("meka_pmmo.configuration.enableDigitalMinerSkillRequirements")
            .define("enableDigitalMinerSkillRequirements", true);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean enableDebugLogging;
    public static boolean enableEnergizedSmelterXP;
    public static boolean enableDigitalMinerXP;
    public static boolean enableDigitalMinerSkillRequirements;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        enableDebugLogging = ENABLE_DEBUG_LOGGING.get();
        enableEnergizedSmelterXP = ENABLE_ENERGIZED_SMELTER_XP.get();
        enableDigitalMinerXP = ENABLE_DIGITAL_MINER_XP.get();
        enableDigitalMinerSkillRequirements = ENABLE_DIGITAL_MINER_SKILL_REQUIREMENTS.get();
        
        if (enableDebugLogging) {
            org.apache.logging.log4j.LogManager.getLogger("MekaPMMO")
                .debug("Config loaded - Energized Smelter XP: {}, Digital Miner XP: {}, Digital Miner Skill Checks: {}", 
                      enableEnergizedSmelterXP, enableDigitalMinerXP, enableDigitalMinerSkillRequirements);
        }
    }
}
