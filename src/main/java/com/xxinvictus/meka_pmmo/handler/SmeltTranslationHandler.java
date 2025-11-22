package com.xxinvictus.meka_pmmo.handler;

import harmonised.pmmo.api.events.FurnaceBurnEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;

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

        // Fire PMMO's FurnaceBurnEvent to grant experience
        MinecraftForge.EVENT_BUS.post(new FurnaceBurnEvent(input, level, pos));
    }
}
