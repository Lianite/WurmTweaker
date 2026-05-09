# TASK-004: Creatures

## Goal
JSON-driven creature customization using the `CreatureTemplateBuilder` API provided by Ago's modloader modsupport package. Supports both registering new creatures (id > 119) and overriding vanilla creatures.

## Status
COMPLETE

## Deliverables

- [x] `src/main/java/org/gotti/wurmtweaker/creatures/CreatureDefinition.java` — nested POJO matching JSON schema
- [x] `src/main/java/org/gotti/wurmtweaker/creatures/CreatureHandler.java` — `ContentHandler<CreatureDefinition>`
- [x] `data/creatures/base/*.json.example` — verified example files for all ~120 vanilla creature templates
- [x] New creatures registered via `ModCreatures.addCreature()` during `Initable.init()`
- [x] Vanilla creature overrides applied via `applyPostInit()` from `onServerStarted()`

## Architecture

### Two-Phase Apply

Creature processing is split across two mod lifecycle hooks:

1. **`init()`** — `JsonLoader.loadType("creature")` scans data dir; for each `CreatureDefinition`, `CreatureHandler.apply()` is called. New creatures (id > 119) are registered via `ModCreatures.addCreature()` at this point, before server boot.

2. **`onServerStarted()`** — `CreatureHandler.applyPostInit()` iterates all pending defs. For every def (new and vanilla), `applySetters()` applies public-API fields. For vanilla-only defs, `applyReflectedFinalFields()` overrides private final fields (speed, moveRate, damage values, sounds) via reflection.

### JSON Schema

All combat fields are nested under a `combat` key. A full creature definition looks like:

```json
{
  "json-type": "creature",
  "id": 200,
  "name": "Mud Crab",
  "plural": "Mud Crabs",
  "longDesc": "A large crab covered in dried mud.",
  "modelName": "model.creature.crab",
  "bodyType": 8,
  "vision": 5,
  "sex": 0,
  "types": [7, 13, 16],
  "sizeInCentimeters": { "high": 50, "long": 80, "wide": 100 },
  "sounds": {
    "deathMale": "sound.death.crab",
    "deathFemale": "sound.death.crab",
    "hitMale": "sound.combat.hit.crab",
    "hitFemale": "sound.combat.hit.crab"
  },
  "movement": { "speed": 0.8, "moveRate": 1200 },
  "combat": {
    "baseCombatRating": 5.0,
    "maxGroupAttackSize": 3,
    "maxHuntDistance": 10,
    "aggressivity": 20,
    "alignment": -10.0,
    "damageType": 0,
    "armor": {
      "naturalArmour": 0.5,
      "armourType": "ArmourTemplate.ARMOUR_TYPE_CHAIN"
    },
    "attacks": {
      "legacy": {
        "hand": 4.0, "handString": "claw",
        "kick": 0.0, "bite": 6.0, "head": 0.0, "breath": 0.0
      }
    }
  },
  "drops": [92, 140],
  "meatMaterial": 74,
  "maxAge": 100,
  "skills": {
    "100": 5.0, "101": 5.0, "102": 15.0, "103": 20.0,
    "104": 25.0, "105": 15.0, "106": 1.0, "10052": 10.0
  }
}
```

## Supported Fields

### Identity
| Field | Type | Notes |
|---|---|---|
| `json-type` | String | Must be `"creature"` |
| `id` | Integer | Required. ≤119 = vanilla override; >119 = new creature |
| `name` | String | Required for new creatures |
| `plural` | String | Defaults to `name + "s"` for new creatures |
| `longDesc` | String | Creature description |
| `modelName` | String | Required for new creatures |
| `bodyType` | Byte | |
| `sex` | Byte | 0=male, 1=female |
| `vision` | Short | |

### Size
Nested under `sizeInCentimeters`: `high`, `long`, `wide` (all Short).

### Sounds
Nested under `sounds`: `deathMale`, `deathFemale`, `hitMale`, `hitFemale` (all String).

### Movement
Nested under `movement`: `speed` (Float), `moveRate` (Integer).

### Combat
Nested under `combat`:
| Field | Type | Notes |
|---|---|---|
| `baseCombatRating` | Float | |
| `bonusCombatRating` | Float | |
| `maxGroupAttackSize` | Integer | |
| `maxHuntDistance` | Integer | |
| `aggressivity` | Integer | |
| `alignment` | Float | Positive=white, Negative=black |
| `damageType` | Integer | Maps to `setCombatDamageType(byte)` |
| `armor.naturalArmour` | Float | |
| `armor.armourType` | String | `"ArmourTemplate.ARMOUR_TYPE_*"` |
| `combatMoves` | int[] | Can also be set at top level |

### Legacy Attack Values
Nested under `combat.attacks.legacy`:
`hand`, `kick`, `bite`, `head`, `breath` (Float); `handString`, `kickString`, `headString`, `breathString` (String); `kickPvp` (Integer).

### New Attack System
Set `combat.attacks.useNewSystem: true` and provide `combat.attacks.primary[]` and/or `combat.attacks.secondary[]`. Each entry:
```json
{
  "name": "strike",
  "identifier": "AttackIdentifier.STRIKE",
  "values": {
    "baseDamage": 8.0, "criticalChance": 0.02, "baseSpeed": 3.0,
    "attackReach": 2, "weightGroup": 1, "damageType": 1,
    "usesWeapon": false, "rounds": 3, "waitUntilNextAttack": 1.0
  }
}
```

### Types
`types` (int[]) — Wurm creature type bitmask constants (e.g., 7=ANIMAL, 13=HUNTING_CREATURE, etc.).

### Drops / Skills / Breeding
| Field | Type | Notes |
|---|---|---|
| `drops` | int[] | Item template IDs butchered from corpse |
| `meatMaterial` | Byte | |
| `skills` | Map<String,Double> | Skill ID → base value |
| `maxAge` | Integer | |
| `maxPercentOfCreatures` | Float | |
| `eggLayer` | Boolean | |
| `eggTemplateId` | Integer | |
| `childTemplateId` | Integer | |
| `mateTemplateId` | Integer | |
| `adultFemaleTemplateId` | Integer | |
| `adultMaleTemplateId` | Integer | |
| `leaderTemplateId` | Integer | |
| `keepSex` | Boolean | |

### Cosmetics / Flags
| Field | Type | Notes |
|---|---|---|
| `hasHands` | Boolean | Direct field write (no setter) |
| `isHorse` | Boolean | Direct field write (no setter) |
| `offZ` | Float | Vertical offset; direct field write |
| `onFire` | Boolean | |
| `fireRadius` | Integer | |
| `glowing` | Boolean | |
| `paintMode` | Integer | |
| `colourNames` | String[] | |
| `color` | Object | `{red, green, blue}` (Integer) |
| `noSkillGain` | Boolean | |
| `noServerSounds` | Boolean | |
| `subterranean` | Boolean | |
| `tutorial` | Boolean | |
| `corpseName` | String | |
| `denName` | String | |
| `denMaterial` | Integer | |
| `boundsValues` | float[4] | |

### Resistance / Vulnerability
All 24 fields (Float) are public direct-write on `CreatureTemplate`:
`physicalResistance`, `physicalVulnerability`, `acidResistance`, `acidVulnerability`,
`fireResistance`, `fireVulnerability`, `coldResistance`, `coldVulnerability`,
`diseaseResistance`, `diseaseVulnerability`, `pierceResistance`, `pierceVulnerability`,
`slashResistance`, `slashVulnerability`, `crushResistance`, `crushVulnerability`,
`biteResistance`, `biteVulnerability`, `poisonResistance`, `poisonVulnerability`,
`waterResistance`, `waterVulnerability`, `internalResistance`, `internalVulnerability`.

### Documentation-only (never applied)
| Field | Notes |
|---|---|
| `note` | Free-text comment stored in definition object, ignored by handler |
| `creatureAI` | Parsed but not applied — requires class-loading logic (deferred) |

## Deferred

| Item | Reason |
|---|---|
| `creatureAI` | Requires instantiating a concrete `CreatureAI` subclass by name |
| `types[]` override for vanilla | Array encodes 64 booleans in constructor; per-flag override requires type-ID-to-fieldname mapping |
| `LootPool` loot table | Modern additive API; `drops` covers legacy `itemsButchered` |

## Verification

1. **Build:** `mvn clean package` — passes with no errors
2. **New creature:** Drop a `.json` file with id > 119; confirm template is registered at init
3. **Vanilla override:** Target an existing id with modified `combat.baseCombatRating`; confirm value changed in `applyPostInit()`
4. **New attack system:** Set `useNewSystem: true` with `primary[]`/`secondary[]`; verify `isUsingNewAttacks()` returns true
5. **Resistance fields:** Set `physicalResistance` on a vanilla creature; verify direct field assignment worked
6. **Skills:** Override skills on a vanilla creature; verify via `template.getSkills().getSkill(id)`
