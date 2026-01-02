package com.xxinvictus.meka_pmmo.mixin;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.xxinvictus.meka_pmmo.handler.SmeltTranslationHandler;
import mekanism.api.recipes.cache.OneInputCachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.ItemStackToItemStackRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.common.recipe.MekanismRecipeType;
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

    /**
     * Wrap the input consumption so we can capture the input ItemStack BEFORE it's used.
     *
     * This is substantially more stable than reflecting into Mekanism's handler lambdas.
     */
    @WrapOperation(
        method = "finishProcessing(I)V",
        at = @At(
            value = "INVOKE",
            target = "Lmekanism/api/recipes/inputs/IInputHandler;use(Ljava/lang/Object;I)V"
        ),
        remap = false,
        require = 0
    )
    private void captureInputBeforeUse(
        IInputHandler<?> inputHandler,
        Object input,
        int operations,
        Operation<Void> original,
        @Share("capturedInput") LocalRef<ItemStack> capturedInputRef
    ) {
        if (input instanceof ItemStack stack && !stack.isEmpty()) {
            capturedInputRef.set(stack.copy());
        }
        original.call(inputHandler, input, operations);
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
    private void onFinishProcessing(int operations, CallbackInfo ci, @Share("capturedInput") LocalRef<ItemStack> capturedInputRef) {
        try {
            // Cast to access the recipe - use raw CachedRecipe to avoid type issues
            CachedRecipe<?> self = (CachedRecipe<?>) (Object) this;
            Object recipeObj = self.getRecipe();
            
            // Check if this is a supported recipe type
            if (!isSupportedRecipe(recipeObj)) {
                return;
            }
            
            // Safe to cast now that we've checked the type
            ItemStackToItemStackRecipe recipe = (ItemStackToItemStackRecipe) recipeObj;

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
            ItemStack input = capturedInputRef.get();
            if (input == null || input.isEmpty()) {
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
        } catch (ClassCastException e) {
            // Recipe is not ItemStackToItemStackRecipe - silently ignore
            // This happens with other machine types like Electrolytic Separator
        } catch (Exception e) {
            // Log unexpected errors but don't crash
            org.apache.logging.log4j.LogManager.getLogger("MekaPMMO").error("Unexpected error in OneInputCachedRecipeMixin", e);
        }
    }
    
    /**
     * Check if the recipe is a supported type for XP rewards.
     * Add more recipe types here to extend support to other machines.
     * 
     * @param recipeObj The recipe object to check
     * @return true if the recipe type is supported
     */
    private boolean isSupportedRecipe(Object recipeObj) {
        // Check if this is an ItemStackToItemStackRecipe (the base for smelting, enriching, crushing)
        if (!(recipeObj instanceof ItemStackToItemStackRecipe recipe)) {
            return false;
        }
        
        // Check if the recipe type is smelting (Energized Smelter)
        // In Mekanism 1.21.x, we need to check the recipe type, not the class type
        if (recipe.getType() == MekanismRecipeType.SMELTING.get()) {
            return true;
        }
        
        // TODO: Add support for other machine types by checking their recipe types
        // Examples:
        // - MekanismRecipeType.CRUSHING (Crusher)
        // - MekanismRecipeType.ENRICHING (Enrichment Chamber)
        // Note: Machines that use chemicals (Compressing, Purifying, Injecting) use different recipe types
        
        return false;
    }

    /**
     * Helper method to get the tile entity processing this recipe.
     * Uses ThreadLocal context tracking set by CachedRecipeMixin.
     */
    private BlockEntity getProcessingTileEntity() {
        return com.xxinvictus.meka_pmmo.util.ProcessingContext.getCurrentMachine();
    }
}
