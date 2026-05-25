---
id: TASK-004b
title: Creatures — Java Support for Redesigned JSON Schema
status: Complete
phase: 2
tags:
  - task
  - complete
related:
  - "[[TASK-004 Creatures]]"
  - "[[Creature Template Decisions]]"
---

# TASK-004b: Creatures — Java Support for Redesigned JSON Schema

## Goal

Bring `CreatureDefinition.java` and `CreatureHandler.java` into full alignment with the creature JSON schema finalized during TASK-004. The JSON schema was redesigned after the initial Java implementation was written, introducing nested objects (`combat`, `armor`, `attacks`, `movement`), replacing string behavior tags with integer `types[]` arrays, and adding ~40 previously unsupported fields.

## Context

The JSON schema is defined in `data/creatures/base/*.json.example`. The Java implementation lives in `src/main/java/org/gotti/wurmtweaker/creatures/`. The current implementation handles ~15 fields using a flat POJO structure that no longer matches the JSON. See [[Creature Template Decisions]] for schema design rationale.

## Critical Files

| File | Change |
|---|---|
| `src/main/java/org/gotti/wurmtweaker/creatures/CreatureDefinition.java` | Full rewrite |
| `src/main/java/org/gotti/wurmtweaker/creatures/CreatureHandler.java` | Major expansion |
| `refs/CreatureTemplate.java` | Read-only API reference |
| `refs/CreatureTemplateCreator.java` | Read-only reference for skills/attack patterns |
| `refs/AttackAction.java` | Read-only — `AttackAction(String, AttackIdentifier, AttackValues)` |
| `refs/AttackValues.java` | Read-only — `AttackValues(float, float, float, int, int, byte, boolean, int, float)` |

## Deliverables

- [x] `CreatureDefinition.java` — restructured POJO with all nested inner classes
- [x] `CreatureHandler.java` — updated field paths + ~40 new fields applied
- [x] Build passes: `mvn clean package`

---

## Part 1: `CreatureDefinition.java` — Full Rewrite

### Remove
- `behaviors` (List\<String\>) — superseded by `types` (int[])
- All flat combat fields: `naturalArmour`, `speed`, `aggressivity`, `handDamage`, `kickDamage`, `biteDamage`, `headDamage`, `breathDamage`, `moveRate`, `maxHuntDistance`, `armourType`, `baseCombatRating`, `bonusCombatRating`, `alignment`

### Keep Unchanged
- `jsonType`, `id`, `name`, `plural`, `longDesc`, `modelName`, `bodyType`, `sex`, `vision`, `meatMaterial`, `maxAge`
- Inner classes: `SizeInCentimeters`, `Sounds`

### New Top-Level Fields

```java
int[]              types
Movement           movement
Combat             combat
int[]              drops
int[]              combatMoves          // top-level; merged with combat.combatMoves in handler
Map<String,Double> skills
Float              maxPercentOfCreatures
Boolean            hasHands
Boolean            isHorse
Boolean            keepSex
Boolean            glowing
Boolean            onFire
Integer            fireRadius
Integer            paintMode
Boolean            noSkillGain
Boolean            noServerSounds
Boolean            subterranean
Boolean            tutorial
Boolean            eggLayer
Integer            eggTemplateId
Integer            childTemplateId
Integer            mateTemplateId
Integer            adultFemaleTemplateId
Integer            adultMaleTemplateId
Integer            leaderTemplateId
String             denName
Integer            denMaterial
String             corpseName
float[]            boundsValues
Float              offZ
String[]           colourNames
Integer            colorRed, colorGreen, colorBlue
ColorDef           color                // alt form: { "red": N, "green": N, "blue": N }
String             creatureAI           // parse but do not apply — deferred
String             note                 // documentation only — never applied

// All 24 resistance/vulnerability fields (public floats on CreatureTemplate)
Float physicalResistance, acidVulnerability, fireVulnerability, fireResistance,
      coldResistance, coldVulnerability, diseaseResistance, diseaseVulnerability,
      pierceResistance, pierceVulnerability, slashResistance, slashVulnerability,
      crushResistance, crushVulnerability, biteResistance, biteVulnerability,
      poisonResistance, poisonVulnerability, waterResistance, waterVulnerability,
      acidResistance, internalResistance, internalVulnerability
```

### New Inner Classes

**`Movement`**
```java
Float   speed
Integer moveRate
```

**`Combat`**
```java
Float   baseCombatRating
Float   bonusCombatRating
Integer maxGroupAttackSize
Integer maxHuntDistance
Integer aggressivity
Float   alignment
Integer damageType       // → combatDamageType (byte) on template
Armor   armor
Attacks attacks
int[]   combatMoves      // merged with top-level combatMoves by handler
```

**`Armor`** (nested inside `Combat`)
```java
Float  naturalArmour
String armourType        // e.g. "ArmourTemplate.ARMOUR_TYPE_LEATHER"
```

**`Attacks`** (nested inside `Combat`)
```java
Boolean            useNewSystem
LegacyAttacks      legacy
List<AttackEntry>  primary
List<AttackEntry>  secondary
```

**`LegacyAttacks`** (nested inside `Attacks`)
```java
Float   hand;    String  handString
Float   kick;    String  kickString;   Integer kickPvp
Float   bite
Float   head;    String  headString
Float   breath;  String  breathString
```

**`AttackEntry`** (nested inside `Attacks`)
```java
String       name
String       identifier   // e.g. "AttackIdentifier.BITE"
AttackValues values
```

**`AttackValues`** (nested inside `Attacks` — field order matches `com.wurmonline.server.creatures.AttackValues` constructor)
```java
Double  baseDamage
Double  criticalChance
Double  baseSpeed
Integer attackReach
Integer weightGroup
Integer damageType
Boolean usesWeapon
Integer rounds
Double  waitUntilNextAttack
```

**`ColorDef`**
```java
Integer red, green, blue
```

---

## Part 2: `CreatureHandler.java` — Expanded Field Application

### 2a. Remove `BEHAVIOR_FIELDS` Map
The string-tag behavior approach is replaced entirely by `types[]`. Remove the static map and all processing referencing it in `applySetters()`.

### 2b. Fix Existing `armourType` Parsing Bug
Current code fails for the `"ArmourTemplate.ARMOUR_TYPE_X"` format. Strip the prefix:
```java
String key = armourTypeString.replaceFirst("^ArmourTemplate\\.", "");
Field f = ArmourTemplate.ArmourType.class.getDeclaredField(key);
template.setArmourType((ArmourTemplate.ArmourType) f.get(null));
```

### 2c. Update `buildTemplate()` — New Creatures Only

All field reads move to nested paths. Helper null-safe accessors are recommended (`combatAggressivity(def)`, `legacyHand(def)`, etc.) to keep the builder call readable.

| Old field | New path |
|---|---|
| `def.speed` | `def.movement.speed` |
| `def.moveRate` | `def.movement.moveRate` |
| `def.aggressivity` | `def.combat.aggressivity` |
| `def.naturalArmour` | `def.combat.armor.naturalArmour` |
| `def.handDamage` | `def.combat.attacks.legacy.hand` |
| `def.kickDamage` | `def.combat.attacks.legacy.kick` |
| `def.biteDamage` | `def.combat.attacks.legacy.bite` |
| `def.headDamage` | `def.combat.attacks.legacy.head` |
| `def.breathDamage` | `def.combat.attacks.legacy.breath` |
| `def.maxHuntDistance` | `def.combat.maxHuntDistance` |
| `def.baseCombatRating` | `def.combat.baseCombatRating` |

### 2d. Update `applyReflectedFinalFields()` — Vanilla Creatures Only

Update all field reads to new nested paths (same mapping as 2c). Also add drops override:
```java
if (def.drops != null) setField(template, "butcheredItems", def.drops);
```

### 2e. Update `applySetters()` — Both Modes

**Add — new attack system:**
```java
if (def.combat != null && def.combat.attacks != null
        && Boolean.TRUE.equals(def.combat.attacks.useNewSystem)) {
    template.setUsesNewAttacks(true);
    if (def.combat.attacks.primary != null) {
        for (AttackEntry e : def.combat.attacks.primary) {
            template.addPrimaryAttack(buildAttackAction(e));
        }
    }
    if (def.combat.attacks.secondary != null) {
        for (AttackEntry e : def.combat.attacks.secondary) {
            template.addSecondaryAttack(buildAttackAction(e));
        }
    }
}
```

`buildAttackAction(AttackEntry e)`:
1. Strip `"AttackIdentifier."` prefix from `e.identifier`
2. Resolve the `AttackIdentifier` enum constant via reflection
3. Construct `AttackValues` (cast all fields to primitive types per constructor)
4. Return `new AttackAction(e.name, identifier, attackValues)`

**Add — skills:**
```java
// API: template.getSkills().learn(int skillId, float value)
if (def.skills != null) {
    try {
        Skills skills = template.getSkills();
        for (Map.Entry<String, Double> entry : def.skills.entrySet()) {
            skills.learn(Integer.parseInt(entry.getKey()), entry.getValue().floatValue());
        }
    } catch (Exception e) {
        logger.warning("WurmTweaker: could not apply skills for creature id=" + def.id + ": " + e.getMessage());
    }
}
```

---

## Deferred Items

| Item | Reason |
|---|---|
| `creatureAI` string | Requires instantiating a concrete `CreatureAI` subclass by name; class-loading approach not yet researched |
| `types[]` for vanilla creature modification | Decoded to 64 booleans in the constructor; overriding for vanilla requires a full integer-to-fieldname mapping |
| `LootPool` loot table migration | `drops` works as legacy `butcheredItems`; modern `LootPool` API is additive and separate research |

---

## Verification

1. `mvn clean package` — no compile errors
2. **New creature** — create `data/creatures/test.json` with id > 119, all nested fields populated; start server, confirm template registered with correct values
3. **Vanilla override** — target Zombie (id=69) with modified `combat.baseCombatRating` and `combat.attacks.legacy.hand`; confirm values reflect in-game
4. **New attack system** — creature with `combat.attacks.useNewSystem: true` and `primary[]`/`secondary[]`; verify `isUsingNewAttacks()` and attack list sizes
5. **Resistance fields** — set `physicalResistance` on Skeleton (id=87); log and verify
6. **Skills** — override a skill on a vanilla creature; verify with debug log
7. **Bad JSON** — malformed file is skipped with a warning, server starts normally
