# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.2.0] - 2025-12-04

### Added
- `enableEnergizedSmelterXP` config option (default: true) - control Energized Smelter XP rewards
- Configuration support for disabling Energized Smelter XP feature

### Changed
- Extended `MekaPMMOMixinPlugin` to conditionally disable Energized Smelter mixins based on configuration
- Extended system property initialization to include `mekapmmo.enableEnergizedSmelterXP`
- Updated startup logging to show Energized Smelter XP status

### Technical Details
- Mixin plugin now checks both `CachedRecipeMixin` and `OneInputCachedRecipeMixin` for conditional loading
- Runtime config check added in `SmeltTranslationHandler` as fallback
- Two-layer disable system prevents unnecessary bytecode modification when feature is disabled

## [1.1.0] - 2025-12-04

### Added
- Digital Miner XP integration - grants MINING XP to machine owner when blocks are mined
- `enableDigitalMinerXP` config option (default: true) - control Digital Miner XP rewards
- `DigitalMinerMixin` to intercept mining operations and fire BlockEvent.BreakEvent
- `MekaPMMOMixinPlugin` for conditional mixin loading based on configuration
- Two-layer disable system: early mixin prevention via plugin + runtime fallback checks
- Support for offline player XP attribution using PMMO's chunk tracking system

### Changed
- Mixins can now be selectively disabled before loading to prevent unnecessary bytecode modification
- System properties set during early mod initialization to control mixin application
- Updated startup logging to show status of Digital Miner XP feature
- Enhanced configuration system with detailed comments about restart requirements

### Technical Details
- Mixin plugin checks system property `mekapmmo.enableDigitalMinerXP` during mixin application
- Runtime config check in `DigitalMinerMixin` as fallback layer
- Digital Miner fires `BlockEvent.BreakEvent` at miner's position (not mined block position)
- Leverages PMMO's existing `BreakHandler` and chunk-based player tracking
- Zero performance overhead when features are disabled via config

## [1.0.1] - 2025-11-25

### Fixed
- Fixed ClassCastException crash when using non-smelting Mekanism machines (e.g., Electrolytic Separator)
- Added proper type checking before casting recipes to ItemStackToItemStackRecipe

### Changed
- Refactored recipe type checking into `isSupportedRecipe()` method for easier extension
- Added documentation for future machine support (Crusher, Enrichment Chamber, etc.)
- Improved error handling with specific ClassCastException catch for unsupported recipe types

## [1.0.0] - 2025-11-22

### Added
- Initial release of Mekanism-PMMO compatibility mod
- Energized Smelter now grants XP through PMMO when processing smelting recipes
- Mixin-based integration to intercept Mekanism's recipe processing
- `SmeltTranslationHandler` with signature: `handleSmeltOperation(input, output, level, pos)`
- Automatic firing of PMMO's `FurnaceBurnEvent` with correct input/output items, level, and position

### Technical Details
- Mixin injection into `OneInputCachedRecipe.finishProcessing()` to capture recipe completion
- Reflection-based tile entity context tracking via `CachedRecipe.canHolderFunction`
- Input ItemStack captured before consumption using HEAD injection for accurate XP calculation
- ThreadLocal-based `ProcessingContext` for passing tile entity references between mixins

### Dependencies
- Minecraft: 1.20.1
- Forge: 47.4.0
- Mekanism: 10.4.16.80
- PMMO: 1.20.1-1.7.39

[1.2.0]: https://github.com/XxInvictus/Meka-PMMO/releases/tag/1.20.1-1.2.0
[1.1.0]: https://github.com/XxInvictus/Meka-PMMO/releases/tag/1.20.1-1.1.0
[1.0.1]: https://github.com/XxInvictus/Meka-PMMO/releases/tag/1.20.1-1.0.1
[1.0.0]: https://github.com/XxInvictus/Meka-PMMO/releases/tag/1.20.1-1.0.0
