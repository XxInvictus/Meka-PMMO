package com.xxinvictus.meka_pmmo.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * Mixin plugin to conditionally disable mixins based on configuration.
 * This allows us to skip loading DigitalMinerMixin if the feature is disabled,
 * saving performance and preventing unnecessary bytecode modification.
 */
public class MekaPMMOMixinPlugin implements IMixinConfigPlugin {
    
    @Override
    public void onLoad(String mixinPackage) {
        // Early initialization - config not loaded yet
    }
    
    @Override
    public String getRefMapperConfig() {
        return null;
    }
    
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        // Conditionally disable DigitalMinerMixin if config disabled
        if (mixinClassName.equals("com.xxinvictus.meka_pmmo.mixin.DigitalMinerMixin")) {
            // Check system property set during early mod loading
            String enabled = System.getProperty("mekapmmo.enableDigitalMinerXP", "true");
            boolean shouldApply = Boolean.parseBoolean(enabled);
            
            if (!shouldApply) {
                org.apache.logging.log4j.LogManager.getLogger("MekaPMMO")
                    .info("DigitalMinerMixin disabled via config - skipping mixin application");
            }
            
            return shouldApply;
        }
        return true; // Apply all other mixins
    }
    
    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }
    
    @Override
    public List<String> getMixins() {
        return null;
    }
    
    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
    
    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
