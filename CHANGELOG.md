# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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

[1.0.1]: https://github.com/XxInvictus/Meka-PMMO/releases/tag/1.20.1-1.0.1
[1.0.0]: https://github.com/XxInvictus/Meka-PMMO/releases/tag/1.20.1-1.0.0
