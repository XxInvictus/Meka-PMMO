package com.xxinvictus.meka_pmmo.mixin;

import com.xxinvictus.meka_pmmo.Config;
import mekanism.common.tile.machine.TileEntityDigitalMiner;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

/**
 * Mixin to intercept Digital Miner's block breaking operations.
 * Fires BlockEvent.BreakEvent so PMMO's BreakHandler grants XP to the owner
 * and enforces skill requirements.
 * 
 * This mixin can be disabled via config (mekapmmo.enableDigitalMinerXP=false).
 */
@Mixin(value = TileEntityDigitalMiner.class, remap = false)
public abstract class DigitalMinerMixin {
    
    /**
     * Inject at HEAD of canMine() to fire our own BreakEvent before Mekanism's validation.
     * 
     * This method is called BEFORE drops are calculated, which is the correct order.
     * We fire the event with the owner as the player for both XP and skill requirement checks.
     * 
     * If PMMO cancels the event (skill requirements not met), we return false immediately
     * which causes the Digital Miner to skip this block without mining it.
     */
    @Inject(
        method = "canMine",
        at = @At("HEAD"),
        cancellable = true,
        remap = false,
        require = 0  // Optional - won't crash if method not found
    )
    private void onCanMineHead(
        BlockState state,
        BlockPos pos,
        CallbackInfoReturnable<Boolean> cir
    ) {
        try {
            // Check if feature is enabled (runtime check as fallback)
            if (!Config.enableDigitalMinerXP) {
                return;
            }
            
            // Get the Digital Miner tile entity
            TileEntityDigitalMiner self = (TileEntityDigitalMiner) (Object) this;
            
            // Get the level from the tile entity
            if (self.getLevel() == null || !(self.getLevel() instanceof ServerLevel)) {
                return;
            }
            
            ServerLevel level = (ServerLevel) self.getLevel();
            
            // Only process on server side
            if (level.isClientSide()) {
                return;
            }
            
            // Get the owner UUID
            UUID ownerUUID = self.getOwnerUUID();
            if (ownerUUID == null) {
                return; // No owner, no XP
            }
            
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
            
            // Fire BlockEvent.BreakEvent with the mined block's position and state
            // PMMO's BreakHandler uses:
            // - event.getPlayer() for player (we provide the owner)
            // - event.getPos() for skill requirement checks
            // - event.getState() for XP calculation
            BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(level, pos, state, player);
            
            // Post the event to Forge's event bus
            // PMMO's BreakHandler will catch this and grant XP (and check skill requirements)
            MinecraftForge.EVENT_BUS.post(event);
            
            // Check if PMMO canceled the event due to skill requirements
            // Only enforce if the config option is enabled
            if (Config.enableDigitalMinerSkillRequirements && event.isCanceled()) {
                // Player doesn't meet skill requirements - prevent mining
                // Return false immediately to stop the entire mining operation
                cir.setReturnValue(false);
                
                if (Config.enableDebugLogging) {
                    org.apache.logging.log4j.LogManager.getLogger("MekaPMMO")
                        .debug("Digital Miner: Blocked mining {} at {} - owner {} does not meet PMMO skill requirements", 
                            state.getBlock().getName().getString(), pos, ownerUUID);
                }
                return;
            }
            
            if (Config.enableDebugLogging) {
                org.apache.logging.log4j.LogManager.getLogger("MekaPMMO")
                    .debug("Digital Miner XP: Fired BreakEvent for {} at {} (owner: {})", 
                        state.getBlock().getName().getString(), pos, ownerUUID);
            }
            
        } catch (Exception e) {
            // Silently fail to prevent crashes
            // Mining will continue normally even if XP grant fails
            org.apache.logging.log4j.LogManager.getLogger("MekaPMMO")
                .warn("Failed to fire BreakEvent for Digital Miner XP", e);
        }
    }
}
