# Meka-PMMO Release Notes

## Version 1.3.1

### Bug Fixes

**Digital Miner Skill Requirements** 🐛
- Fixed issue where skill requirement enforcement would not work if Digital Miner XP was disabled
- The Digital Miner will now correctly respect skill requirements regardless of XP setting
- Both features can now be independently enabled/disabled as intended

---

## Version 1.3.0

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

## Version 1.2.0

### What's New

**Energized Smelter Configuration** ⚙️
- New config option to disable Energized Smelter XP rewards if you prefer
- Toggle `enableEnergizedSmelterXP` in the config file (default: enabled)

---

## Version 1.1.0

### What's New

**Digital Miner Integration** ⛏️
- Mekanism's Digital Miner now grants you PMMO mining XP for every block it mines!
- XP is awarded to the machine's owner automatically
- New config option: `enableDigitalMinerXP` (default: enabled)

---

## Version 1.0.1

### Bug Fixes
- Fixed crash when using non-smelting Mekanism machines like the Electrolytic Separator
- Improved compatibility with different machine types

---

## Version 1.0.0

### Initial Release

**Energized Smelter Integration** 🔥
- Mekanism's Energized Smelter now grants PMMO smelting XP just like vanilla furnaces!
- Works seamlessly with your existing PMMO progression

**Compatibility**
- Minecraft 1.20.1
- Forge 47.4.0+
- Mekanism 10.4.16.80+
- PMMO 1.20.1-1.7.39+
