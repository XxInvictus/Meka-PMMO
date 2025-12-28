package com.xxinvictus.meka_pmmo.handler;

import com.xxinvictus.meka_pmmo.Config;
import harmonised.pmmo.api.events.FurnaceBurnEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Main handler for processing intercepted Mekanism smelting operations.
 * This class receives data from mixin injections and can forward it to PMMO
 * or other integration systems for XP rewards.
 */
public class SmeltTranslationHandler {

    /**
     * Process a smelting operation from Mekanism machines.
     * 
     * @param input The input item that was consumed
     * @param output The output item that was produced
     * @param level The world/level where the operation occurred
     * @param pos The block position of the machine performing the operation
     */
    public static void handleSmeltOperation(ItemStack input, ItemStack output, Level level, BlockPos pos) {
        // Check if feature is disabled via config (runtime check as fallback)
        if (!Config.enableEnergizedSmelterXP) {
            return;
        }
        
        // Validate inputs
        if (input == null || output == null || level == null || pos == null) {
            return;
        }
        
        if (input.isEmpty() || output.isEmpty()) {
            return;
        }
        
        // Only process on server side
        if (level.isClientSide()) {
            return;
        }

        if (Config.enableDebugLogging) {
            org.apache.logging.log4j.LogManager.getLogger("MekaPMMO")
                .debug("Processing Energized Smelter operation: {} -> {} at {}", 
                      input.getDescriptionId(), output.getDescriptionId(), pos);
        }

        // Fire PMMO's FurnaceBurnEvent to grant experience
        // Wrapped in try-catch to handle potential PMMO API changes gracefully
        try {
            FurnaceBurnEvent event = new FurnaceBurnEvent(input, output, level, pos);
            NeoForge.EVENT_BUS.post(event);
        } catch (NoSuchMethodError e) {
            // PMMO's FurnaceBurnEvent constructor signature changed
            org.apache.logging.log4j.LogManager.getLogger("MekaPMMO")
                .error("FurnaceBurnEvent constructor signature mismatch. PMMO version may be incompatible. " +
                       "Expected: FurnaceBurnEvent(ItemStack, Level, BlockPos)", e);
        } catch (NoClassDefFoundError e) {
            // FurnaceBurnEvent class no longer exists in PMMO
            org.apache.logging.log4j.LogManager.getLogger("MekaPMMO")
                .error("FurnaceBurnEvent class not found. PMMO may have removed this API. " +
                       "Meka-PMMO needs to be updated for this PMMO version.", e);
        } catch (Exception e) {
            // Any other unexpected error
            org.apache.logging.log4j.LogManager.getLogger("MekaPMMO")
                .error("Unexpected error while posting FurnaceBurnEvent to PMMO", e);
        }
    }
}
