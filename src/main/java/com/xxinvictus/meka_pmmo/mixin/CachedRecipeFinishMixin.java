package com.xxinvictus.meka_pmmo.mixin;

import com.xxinvictus.meka_pmmo.handler.SmeltTranslationHandler;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.MekanismRecipeType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Alternative mixin targeting the parent CachedRecipe class.
 * This should catch all recipe processing including smelting.
 */
@Mixin(value = CachedRecipe.class, remap = false)
public abstract class CachedRecipeFinishMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("MekaPMMO-CachedRecipe");

    /**
     * Inject into finishProcessing at the parent level.
     * This should fire for ALL Mekanism recipes.
     */
    @Inject(
        method = "finishProcessing(I)V",
        at = @At("HEAD"),
        remap = false,
        require = 0
    )
    private void onFinishProcessingHead(int operations, CallbackInfo ci) {
        LOGGER.info("MekaPMMO-CachedRecipe: finishProcessing called with {} operations (HEAD)", operations);
        
        // Try to get the recipe
        CachedRecipe<?> self = (CachedRecipe<?>) (Object) this;
        MekanismRecipe recipe = self.getRecipe();
        
        if (recipe == null) {
            LOGGER.warn("MekaPMMO-CachedRecipe: Recipe is null!");
            return;
        }
        
        LOGGER.info("MekaPMMO-CachedRecipe: Recipe type: {}", recipe.getClass().getName());
        
        // Check if it's a smelting recipe
        if (recipe instanceof IMekanismRecipeTypeProvider recipeTypeProvider) {
            var recipeType = recipeTypeProvider.getRecipeType();
            LOGGER.info("MekaPMMO-CachedRecipe: Recipe type from provider: {}", recipeType);
            
            if (recipeType == MekanismRecipeType.SMELTING.get()) {
                LOGGER.info("MekaPMMO-CachedRecipe: This is a SMELTING recipe!");
                
                // Get context
                BlockEntity tileEntity = com.xxinvictus.meka_pmmo.util.ProcessingContext.getCurrentMachine();
                if (tileEntity != null) {
                    Level level = tileEntity.getLevel();
                    BlockPos pos = tileEntity.getBlockPos();
                    
                    if (level != null && pos != null) {
                        // Get input/output from recipe
                        ItemStack input = ItemStack.EMPTY;
                        ItemStack output = recipe.getResultItem(level.registryAccess());
                        
                        if (!recipe.getIngredients().isEmpty() && !recipe.getIngredients().get(0).isEmpty()) {
                            ItemStack[] matchingStacks = recipe.getIngredients().get(0).getItems();
                            if (matchingStacks.length > 0) {
                                input = matchingStacks[0];
                            }
                        }
                        
                        LOGGER.info("MekaPMMO-CachedRecipe: Calling handler");
                        SmeltTranslationHandler.handleSmeltOperation(input, output, level, pos);
                    }
                }
            }
        }
    }
}
