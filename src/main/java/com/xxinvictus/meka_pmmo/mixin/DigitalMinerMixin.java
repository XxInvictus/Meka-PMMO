package com.xxinvictus.meka_pmmo.mixin;

import com.xxinvictus.meka_pmmo.Config;
import mekanism.common.tile.machine.TileEntityDigitalMiner;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.UUID;

/**
 * Mixin to intercept Digital Miner's block breaking operations.
 * Fires BlockEvent.BreakEvent so PMMO's BreakHandler grants XP to the owner.
 */
@Mixin(value = TileEntityDigitalMiner.class, remap = false)
public abstract class DigitalMinerMixin {
    
    /**
     * Inject at HEAD of getDrops() to fire BreakEvent before drops are calculated.
     * 
     * IMPORTANT: We fire the event at the MINER's position (where machine is placed),
     * NOT at the mined block's position. This allows PMMO's chunk tracking to find
     * the owner and grant them XP, similar to how FurnaceHandler works.
     */
    @Inject(
        method = "getDrops(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)Ljava/util/List;",
        at = @At("HEAD"),
        remap = false,
        require = 0  // Optional - won't crash if method not found
    )
    private void onGetDrops(
        ServerLevel level,
        BlockState state,
        BlockPos minedBlockPos,  // This is the MINED block position (remote)
        CallbackInfoReturnable<List<ItemStack>> cir
    ) {
        try {
            // Check if feature is enabled (runtime check as fallback)
            if (!Config.enableDigitalMinerXP) {
                return;
            }
            
            // Only process on server side
            if (level == null || level.isClientSide()) {
                return;
            }
            
            // Get the Digital Miner tile entity
            TileEntityDigitalMiner self = (TileEntityDigitalMiner) (Object) this;
            
            // Get the owner UUID
            UUID ownerUUID = self.getOwnerUUID();
            if (ownerUUID == null) {
                return; // No owner, no XP
            }
            
            // Get the MINER's position (NOT the mined block's position)
            BlockPos minerPos = self.getBlockPos();
            
            // Get or create the player
            ServerPlayer player = level.getServer().getPlayerList().getPlayer(ownerUUID);
            if (player == null) {
                // Player is offline - create temporary player for event
                // PMMO's FurnaceHandler does the same thing
                var profileCache = level.getServer().getProfileCache();
                var profile = profileCache.get(ownerUUID);
                if (profile.isEmpty()) {
                    return; // Can't find player profile
                }
                player = new ServerPlayer(level.getServer(), level, profile.get());
            }
            
            // Fire BlockEvent.BreakEvent at the MINER's position
            // This is the key - PMMO's chunk tracking will find the owner at minerPos
            BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(level, minerPos, state, player);
            
            // Post the event to Forge's event bus
            // PMMO's BreakHandler will catch this and grant XP
            MinecraftForge.EVENT_BUS.post(event);
            
            // Note: We don't check if event was canceled - we're just notifying PMMO
            // The Digital Miner already did its own permission checks
            
        } catch (Exception e) {
            // Silently fail to prevent crashes
            // Mining will continue normally even if XP grant fails
            org.apache.logging.log4j.LogManager.getLogger("MekaPMMO")
                .warn("Failed to fire BreakEvent for Digital Miner XP", e);
        }
    }
}
