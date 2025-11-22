package com.xxinvictus.meka_pmmo.util;

import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Thread-local context tracking for Mekanism recipe processing.
 * This allows mixins to share context information about which machine
 * is currently processing a recipe.
 */
public class ProcessingContext {
    private static final ThreadLocal<BlockEntity> CURRENT_MACHINE = new ThreadLocal<>();

    /**
     * Set the current machine processing a recipe.
     * Should be called at the start of recipe processing.
     * 
     * @param machine The block entity (tile entity) processing the recipe
     */
    public static void setCurrentMachine(BlockEntity machine) {
        CURRENT_MACHINE.set(machine);
    }

    /**
     * Get the current machine processing a recipe.
     * 
     * @return The block entity, or null if not in a processing context
     */
    public static BlockEntity getCurrentMachine() {
        return CURRENT_MACHINE.get();
    }

    /**
     * Clear the processing context.
     * MUST be called after recipe processing to prevent memory leaks.
     */
    public static void clear() {
        CURRENT_MACHINE.remove();
    }

    /**
     * Check if we're currently in a processing context.
     * 
     * @return true if a machine context is set
     */
    public static boolean hasContext() {
        return CURRENT_MACHINE.get() != null;
    }
}
