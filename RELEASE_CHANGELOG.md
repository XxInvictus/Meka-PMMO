# Meka-PMMO Release Notes

<!-- markdownlint-disable MD024 -->

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

This file is intended to be user-facing and concise (compared to CHANGELOG.md),
but it is still a complete changelog containing the full release history.

## [Unreleased]

### Added

- TBD

### Changed

- TBD

### Deprecated

- TBD

### Removed

- TBD

### Fixed

- TBD

### Security

- TBD

## [1.3.1] - 2025-12-04

### Bug Fixes

**Digital Miner Skill Requirements** 🐛

- Fixed issue where skill requirement enforcement would not work if Digital Miner XP was disabled
- The Digital Miner will now correctly respect skill requirements regardless of XP setting
- Both features can now be independently enabled/disabled as intended

---

## [1.3.0] - 2025-12-04

### What's New

**Digital Miner Skill Requirements** 🛡️

- The Digital Miner now respects your PMMO skill levels! If you don't have the required mining level for a block, the miner will skip it and move to the next one.
- New config option: `enableDigitalMinerSkillRequirements` (default: enabled) - toggle whether the Digital Miner checks your skills

**Improvements**

- Digital Miner XP rewards are now more accurate - XP is calculated based on the actual block being mined
- Better player tracking for XP rewards

### Configuration

You can customize the mod's behavior in the config file:

- `enableDigitalMinerXP` - Control if Digital Miner grants PMMO mining XP (default: on)
- `enableDigitalMinerSkillRequirements` - Control if Digital Miner respects skill requirements (default: on)
- `enableEnergizedSmelterXP` - Control if Energized Smelter grants PMMO smelting XP (default: on)

---

## [1.2.0] - 2025-12-04

### What's New

**Energized Smelter Configuration** ⚙️

- New config option to disable Energized Smelter XP rewards if you prefer
- Toggle `enableEnergizedSmelterXP` in the config file (default: enabled)

---

## [1.1.0] - 2025-12-04

### What's New

**Digital Miner Integration** ⛏️

- Mekanism's Digital Miner now grants you PMMO mining XP for every block it mines!
- XP is awarded to the machine's owner automatically
- New config option: `enableDigitalMinerXP` (default: enabled)

---

## [1.0.1] - 2025-11-25

### Bug Fixes

- Fixed crash when using non-smelting Mekanism machines like the Electrolytic Separator
- Improved compatibility with different machine types

---

## [1.0.0] - 2025-11-22

### Initial Release

**Energized Smelter Integration** 🔥

- Mekanism's Energized Smelter now grants PMMO smelting XP just like vanilla furnaces!
- Works seamlessly with your existing PMMO progression

**Compatibility**

- Minecraft 1.20.1
- Forge 47.4.0+
- Mekanism 10.4.16.80+
- PMMO 1.20.1-1.7.39+
