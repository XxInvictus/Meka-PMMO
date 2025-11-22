[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
# Meka-PMMO

A compatibility bridge between Mekanism and Project MMO (PMMO) that enables XP rewards from Mekanism machines.

## Overview

Meka-PMMO provides seamless integration between Mekanism's machines and PMMO's skill system. Players now earn XP when using Mekanism's Energized Smelter, just as they would with vanilla furnaces.

### The Problem

By default, Mekanism machines do not grant experience points when processing recipes. This is a known limitation - Mekanism uses custom slot systems that bypass vanilla's XP granting mechanisms, and their recipe wrappers don't preserve experience data.

### The Solution

Meka-PMMO uses advanced mixin-based interception to:

- Capture recipe completion events from Mekanism machines
- Extract the correct input and output items
- Fire PMMO's `FurnaceBurnEvent` with accurate context data
- Enable XP rewards without modifying either Mekanism or PMMO

## Features

✅ **Energized Smelter XP** - Players receive XP when smelting recipes complete  
✅ **PMMO Integration** - Works with PMMO's skill system and configuration  
✅ **Non-invasive** - Uses mixins, no modification to Mekanism or PMMO required  
✅ **Accurate Tracking** - Captures actual input items before consumption  
✅ **Server-side Only** - XP processing happens server-side as expected

## Installation

1. Download the latest release from [Releases](https://github.com/XxInvictus/Meka-PMMO/releases)
2. Place the JAR file in your `mods` folder
3. Ensure you have the required dependencies installed (see below)
4. Launch Minecraft and enjoy automatic XP from Mekanism machines!

## Dependencies

| Mod | Version | Required |
|-----|---------|----------|
| Minecraft | 1.20.1 | ✅ |
| Forge | 47.4.0+ | ✅ |
| Mekanism | 10.4.16.80+ | ✅ |
| PMMO | 1.20.1-1.7.39+ | ✅ |

## How It Works

Meka-PMMO uses Forge's Mixin system to intercept Mekanism's recipe processing:

1. **Context Capture** - `CachedRecipeMixin` captures the tile entity (machine) processing the recipe
2. **Input Tracking** - `OneInputCachedRecipeMixin` captures the input ItemStack before it's consumed
3. **Event Firing** - When `finishProcessing()` completes, `SmeltTranslationHandler` fires PMMO's `FurnaceBurnEvent`
4. **XP Granting** - PMMO receives the event and grants appropriate XP based on its configuration

### Technical Details

- **Reflection-based** tile entity access via `CachedRecipe.canHolderFunction`
- **HEAD injection** to capture input before consumption
- **TAIL injection** to fire event after successful processing
- **ThreadLocal** context passing between mixins
- **Zero patches** to either Mekanism or PMMO source code

## Configuration

Meka-PMMO itself requires no configuration. XP amounts and skill assignments are controlled entirely by PMMO's configuration files.

To customize XP rewards:

1. Configure PMMO's smelting XP values in PMMO's config
2. Adjust PMMO's skill requirements as desired
3. Meka-PMMO will automatically use those settings

## Compatibility

### Supported Machines

- ✅ Energized Smelter

### Planned Support

- 🔄 Other single-input Mekanism machines (Crusher, Enrichment Chamber, etc.)
- 🔄 Multi-input recipes (Combiner, Chemical Infuser, etc.)

## Development

### Building from Source

```bash
git clone https://github.com/XxInvictus/Meka-PMMO.git
cd Meka-PMMO
./gradlew build
```

The compiled JAR will be in `build/libs/`

### Project Structure

```text
src/main/java/com/xxinvictus/meka_pmmo/
├── MekaPMMO.java                    # Main mod class
├── Config.java                      # Configuration handler
├── handler/
│   └── SmeltTranslationHandler.java # PMMO event bridge
├── mixin/
│   ├── CachedRecipeMixin.java       # Tile entity context capture
│   └── OneInputCachedRecipeMixin.java # Recipe completion interception
└── util/
    └── ProcessingContext.java       # ThreadLocal context storage
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

### Development Setup

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE.txt](LICENSE.txt) file for details.

## Credits

**Author:** XxInvictus

**Inspiration:** Frustration at not being able to automate and get PMMO skills from Mekanism machines.

**Special Thanks:**

- The Mekanism team for their amazing tech mod
- The PMMO team for the skill progression system
- The Forge team for the Mixin framework

## Support

- 🐛 **Bug Reports:** [Issue Tracker](https://github.com/XxInvictus/Meka-PMMO/issues)
- 💬 **Questions:** Open a discussion on GitHub
- 📖 **Documentation:** See [IMPLEMENTATION_README.md](IMPLEMENTATION_README.md) for technical details

## Changelog

See [CHANGELOG.md](CHANGELOG.md) for a detailed version history.

---

**Note:** This mod uses mixins to integrate with Mekanism and PMMO. While thoroughly tested, please report any compatibility issues on the issue tracker.
