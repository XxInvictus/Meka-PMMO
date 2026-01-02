package com.xxinvictus.meka_pmmo.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.xxinvictus.meka_pmmo.util.ProcessingContext;
import mekanism.api.recipes.cache.CachedRecipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BooleanSupplier;

/**
 * Mixin to capture the processing context (which machine is processing a recipe).
 * This sets a ThreadLocal reference that can be accessed by OneInputCachedRecipeMixin.
 */
@Mixin(value = CachedRecipe.class, remap = false)
public abstract class CachedRecipeMixin {
    private static final ConcurrentHashMap<Class<?>, Field[]> CAPTURE_FIELDS_CACHE = new ConcurrentHashMap<>();

    /**
     * Wrap process() so we can set/clear context regardless of early returns.
     *
     * Context is set by wrapping the canHolderFunction.getAsBoolean() call.
     */
    @WrapMethod(method = "process", remap = false, require = 0)
    private void wrapProcess(Operation<Void> original) {
        try {
            original.call();
        } finally {
            ProcessingContext.clear();
        }
    }

    /**
     * Wrap the holder check so we can recover the captured BlockEntity from the supplier instance.
     * This avoids depending on CachedRecipe's field names.
     */
    @WrapOperation(
        method = "process",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/function/BooleanSupplier;getAsBoolean()Z"
        ),
        remap = false,
        require = 0
    )
    private boolean captureProcessingContext(
        BooleanSupplier supplier,
        Operation<Boolean> original
    ) {
        try {
            BlockEntity machine = extractBlockEntity(supplier);
            if (machine != null) {
                ProcessingContext.setCurrentMachine(machine);
            }
        } catch (Exception ignored) {
            // Best-effort only
        }
        return original.call(supplier);
    }

    private static BlockEntity extractBlockEntity(BooleanSupplier holderFunction) {
        if (holderFunction == null) {
            return null;
        }
        try {
            Field[] fields = CAPTURE_FIELDS_CACHE.computeIfAbsent(holderFunction.getClass(), supplierClass -> {
                Field[] declared = supplierClass.getDeclaredFields();
                for (Field field : declared) {
                    try {
                        field.setAccessible(true);
                    } catch (Exception ignored) {
                        // Best-effort only
                    }
                }
                return declared;
            });

            for (Field field : fields) {
                Object fieldValue = field.get(holderFunction);
                if (fieldValue instanceof BlockEntity blockEntity) {
                    return blockEntity;
                }
            }
        } catch (Exception ignored) {
            // Best-effort only
        }
        return null;
    }
}
