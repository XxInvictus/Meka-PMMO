package com.xxinvictus.meka_pmmo.mixin;

import com.xxinvictus.meka_pmmo.util.ProcessingContext;
import mekanism.api.recipes.cache.CachedRecipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to capture the processing context (which machine is processing a recipe).
 * This sets a ThreadLocal reference that can be accessed by OneInputCachedRecipeMixin.
 */
@Mixin(value = CachedRecipe.class, remap = false)
public abstract class CachedRecipeMixin {

    /**
     * Inject at the HEAD of process() to capture the tile entity context.
     */
    @Inject(
        method = "process",
        at = @At("HEAD"),
        remap = false,
        require = 0
    )
    private void captureProcessingContext(CallbackInfo ci) {
        // Access canHolderFunction to get the tile entity
        try {
            CachedRecipe<?> self = (CachedRecipe<?>) (Object) this;
            
            java.lang.reflect.Field holderField = CachedRecipe.class.getDeclaredField("canHolderFunction");
            holderField.setAccessible(true);
            Object holderFunction = holderField.get(self);
            
            if (holderFunction != null) {
                // The canHolderFunction is a lambda that captures the tile entity
                // Find the BlockEntity in its fields
                java.lang.reflect.Field[] funcFields = holderFunction.getClass().getDeclaredFields();
                
                for (java.lang.reflect.Field field : funcFields) {
                    field.setAccessible(true);
                    Object fieldValue = field.get(holderFunction);
                    if (fieldValue instanceof BlockEntity blockEntity) {
                        ProcessingContext.setCurrentMachine(blockEntity);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            // Silently fail - context won't be available but won't crash
        }
    }

    /**
     * Inject at RETURN to clear the context after processing.
     * This prevents memory leaks.
     */
    @Inject(
        method = "process",
        at = @At("RETURN"),
        remap = false,
        require = 0
    )
    private void clearProcessingContext(CallbackInfo ci) {
        ProcessingContext.clear();
    }
}
