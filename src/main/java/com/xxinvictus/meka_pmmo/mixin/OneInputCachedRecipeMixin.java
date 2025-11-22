package com.xxinvictus.meka_pmmo.mixin;

import com.xxinvictus.meka_pmmo.handler.SmeltTranslationHandler;
import mekanism.api.recipes.cache.OneInputCachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.ItemStackToItemStackRecipe;
import mekanism.common.recipe.impl.SmeltingIRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to intercept Mekanism's recipe completion process.
 * This targets the finishProcessing method in OneInputCachedRecipe,
 * which is called when a recipe successfully completes.
 */
@Mixin(value = OneInputCachedRecipe.class, remap = false)
public abstract class OneInputCachedRecipeMixin<RECIPE extends ItemStackToItemStackRecipe> {
    
    // Store the input before it's consumed
    private ItemStack capturedInput = ItemStack.EMPTY;

    /**
     * Inject at HEAD to capture the input ItemStack BEFORE it's consumed
     */
    @Inject(
        method = "finishProcessing(I)V",
        at = @At("HEAD"),
        remap = false,
        require = 0
    )
    private void captureInput(int operations, CallbackInfo ci) {
        try {
            java.lang.reflect.Field inputHandlerField = this.getClass().getDeclaredField("inputHandler");
            inputHandlerField.setAccessible(true);
            Object inputHandler = inputHandlerField.get(this);
            
            if (inputHandler != null) {
                java.lang.reflect.Field slotField = inputHandler.getClass().getDeclaredField("val$slot");
                slotField.setAccessible(true);
                Object slot = slotField.get(inputHandler);
                
                if (slot != null) {
                    java.lang.reflect.Method getStackMethod = slot.getClass().getMethod("getStack");
                    ItemStack stack = (ItemStack) getStackMethod.invoke(slot);
                    if (!stack.isEmpty()) {
                        capturedInput = stack.copy(); // Copy to preserve the item data
                    }
                }
            }
        } catch (Exception e) {
            // Silently fail - will fall back to recipe ingredients
        }
    }

    /**
     * Inject at the TAIL of finishProcessing to handle smelting operations
     * after the recipe has completed successfully.
     * 
     * This approach allows us to capture input/output data and forward it
     * to our SmeltTranslationHandler for XP processing.
     */
    @Inject(
        method = "finishProcessing(I)V",
        at = @At("TAIL"),
        remap = false,
        require = 0
    )
    private void onFinishProcessing(int operations, CallbackInfo ci) {
        // Cast to access the recipe
        @SuppressWarnings("unchecked")
        CachedRecipe<ItemStackToItemStackRecipe> self = (CachedRecipe<ItemStackToItemStackRecipe>) (Object) this;
        ItemStackToItemStackRecipe recipe = self.getRecipe();
        
        // Only process smelting recipes
        if (!(recipe instanceof SmeltingIRecipe)) {
            return;
        }

        // Get the tile entity context
        BlockEntity tileEntity = getProcessingTileEntity();
        if (tileEntity == null) {
            return;
        }

        Level level = tileEntity.getLevel();
        BlockPos pos = tileEntity.getBlockPos();
        
        if (level == null || pos == null) {
            return;
        }

        // Get recipe output
        ItemStack output = recipe.getResultItem(level.registryAccess());
        
        // Use the captured input from HEAD injection
        ItemStack input = capturedInput;
        if (input.isEmpty()) {
            // Fallback to recipe ingredients
            if (!recipe.getIngredients().isEmpty() && !recipe.getIngredients().get(0).isEmpty()) {
                ItemStack[] matchingStacks = recipe.getIngredients().get(0).getItems();
                if (matchingStacks.length > 0) {
                    input = matchingStacks[0];
                }
            }
        }
        
        // Call our handler with the smelting data
        SmeltTranslationHandler.handleSmeltOperation(input, output, level, pos);
    }

    /**
     * Helper method to get the tile entity processing this recipe.
     * Uses ThreadLocal context tracking set by CachedRecipeMixin.
     */
    private BlockEntity getProcessingTileEntity() {
        return com.xxinvictus.meka_pmmo.util.ProcessingContext.getCurrentMachine();
    }
}
