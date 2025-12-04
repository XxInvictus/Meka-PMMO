package com.xxinvictus.meka_pmmo;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

/**
 * Configuration for Meka-PMMO mod.
 * Currently minimal, ready for future config options like:
 * - Enable/disable XP for automated systems
 * - XP multiplier
 * - Debug logging toggle
 */
@Mod.EventBusSubscriber(modid = MekaPMMO.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue ENABLE_DEBUG_LOGGING = BUILDER
            .comment("Enable debug logging for Mekanism XP integration")
            .define("enableDebugLogging", false);

    private static final ForgeConfigSpec.BooleanValue ENABLE_ENERGIZED_SMELTER_XP = BUILDER
            .comment("Enable XP rewards from Energized Smelter smelting operations")
            .define("enableEnergizedSmelterXP", true);

    private static final ForgeConfigSpec.BooleanValue ENABLE_DIGITAL_MINER_XP = BUILDER
            .comment("Enable XP rewards from Digital Miner mining operations")
            .define("enableDigitalMinerXP", true);

    private static final ForgeConfigSpec.BooleanValue ENABLE_DIGITAL_MINER_SKILL_REQUIREMENTS = BUILDER
            .comment("Enable PMMO skill requirement checks for Digital Miner (prevents mining blocks without sufficient skill)")
            .define("enableDigitalMinerSkillRequirements", true);

    static final ForgeConfigSpec SPEC = BUILDER.build();

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
    }
}
