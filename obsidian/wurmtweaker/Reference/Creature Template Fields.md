---
type: reference
tags:
  - reference
  - creatures
related:
  - "[[Creature Types]]"
  - "[[Skill IDs]]"
  - "[[Spawn Tiles]]"
  - "[[TASK-004 Creatures]]"
  - "[[Creature Template Decisions]]"
---

# Creature Template Fields

Full JSON schema for WurmTweaker creature definitions. Derived from `CreatureTemplate.java`, `CreatureTemplateCreator.java`, `CreatureTemplateBuilder.java` (modsupport), and `CreatureHandler.java`.

## Source Files

| File | Role |
|---|---|
| `refs/CreatureTemplate.java` | Authoritative field definitions and `assignTypes()` mapping |
| `refs/CreatureTemplateCreator.java` | All 119 vanilla template registrations with exact constructor calls |
| `refs/EncounterType.java` / `refs/Encounter.java` | Spawn system internals |
| `src/.../creatures/CreatureHandler.java` | Apply logic, defaults, and reflection targets |

## New Creature vs. Existing Creature

Two mutually exclusive use cases share the same `"json-type": "creature"` schema.

### Editing a vanilla creature (id ≤ 119)

- Only `id` is required. Every other field is optional — omitted fields are left unchanged.
- Two-pass application:
  - **Pass 1 (init):** `ModCreatures.addCreature()` is called only for new creatures. Vanilla creatures skip this entirely.
  - **Pass 2 (onServerStarted):** `applySetters()` runs for all creatures. Fields with public setters are applied directly. For vanilla creatures, `applyReflectedFinalFields()` also runs to override `private final` fields (speed, moveRate, aggressivity, naturalArmour, legacy damage values, sounds) via reflection.
- The `spawns` array is **ignored** for vanilla creatures — it is only processed inside `ModCreature.addEncounters()`, which only runs for new creatures.

### Registering a new custom creature (id ≥ 120)

- `id`, `name`, and `modelName` are required. Missing either `name` or `modelName` logs a warning and skips the entry.
- `plural` defaults to `name + "s"` if omitted.
- `longDesc` defaults to `"A " + name + "."` if omitted.
- Every other field has a default (listed per-field below).
- Template is built fresh via `CreatureTemplateBuilder`, then `addEncounters()` registers spawn entries.

### Quick-reference: required fields by use case

| Field | Vanilla override (id ≤ 119) | New creature (id ≥ 120) |
|---|---|---|
| `json-type` | Required | Required |
| `id` | Required | Required |
| `name` | — | **Required** |
| `modelName` | — | **Required** |
| All other fields | Optional | Optional (each has a default) |

---

## Identity

### `id` · Integer · Required (both paths)

Template ID.

- **1–119** — vanilla creature. Only overrides are applied; the template itself is not reconstructed.
- **≥ 120** — new custom creature. Full registration via modloader. IDs above 119 are safe for custom use.

---

### `name` · String · Required for new creatures

Display name shown in-game ("a Hobgoblin", combat messages, etc.).

---

### `plural` · String · Optional

Plural form used in group and area messages. Default: `name + "s"`.

---

### `longDesc` · String · Optional

Examine tooltip / description. Default: `"A " + name + "."`.

---

### `modelName` · String · Required for new creatures

Client model path using Wurm's dot-separated namespace. Example values from vanilla:

```
model.creature.humanoid.goblin.standard
model.creature.humanoid.troll.standard
model.creature.humanoid.troll.king
model.creature.quadraped.horse
model.creature.quadraped.horse.hell
model.creature.quadraped.horse.hell.foal
```

An existing model can be reused for a new creature type — the client scales it to match `sizeInCentimeters`.

**Horse and colour variant models:** when `isHorse: true` or type flag `64` is set, `Creature.java` assembles the client model path as:

```
[modelName] + '.' + colourName.toLowerCase().replaceAll(" ", "") + '.' + sex + calendarSuffix
```

- **Dot separator** between the base model name, colour, and sex — not direct concatenation.
- **Spaces stripped** from the colour name before assembly (`"piebald pinto"` → `piebaldpinto`).
- **Sex suffix** is `male` or `female`.
- **Calendar suffix** is empty under normal circumstances; only non-empty during special calendar events (e.g. Halloween skins).

| `modelName` | Colour name | Sex | Resolved client path |
|---|---|---|---|
| `model.creature.quadraped.horse` | `grey` | male | `model.creature.quadraped.horse.grey.male` |
| `model.creature.quadraped.horse` | `piebald pinto` | female | `model.creature.quadraped.horse.piebaldpinto.female` |
| `model.creature.quadraped.horse.hell` | `ash` | male | `model.creature.quadraped.horse.hell.ash.male` |
| `model.creature.quadraped.horse.hell` | `nightshade` | female | `model.creature.quadraped.horse.hell.nightshade.female` |

The variant model files must already exist in the client either being vanilla models or added via serverpacks.

**Standard horse colour names** (13 entries, from `CreatureTemplateCreator.java`):
`grey`, `brown`, `gold`, `black`, `white`, `piebald pinto`, `blood bay`, `ebony black`, `skewbald pinto`, `gold buckskin`, `black silver`, `appaloosa`, `chestnut`

**Hell horse colour names** (8 entries):
`ash`, `cinder`, `envious`, `shadow`, `pestilential`, `nightshade`, `incandescent`, `molten`

The trait-bit-to-index mapping is identical for both — see `colourNames` below.

---

## Body

### `bodyType` · Byte · Optional (default: `0`)

Determines body slot layout, hitbox shape, and which equipment slots are available. `0` is the standard humanoid layout. Other values correspond to animal and dragon body forms — only change this if you have a source-confirmed value for the body type you need.

---

### `sex` · Byte · Optional (default: `0`)

```
0 = male / no sex distinction
1 = female
```

Affects which sound variants play on death/hit, and which breeding template IDs are evaluated at runtime. Most creatures use `0`.

---

### `vision` · Short · Optional (default: `50` for new creatures)

Detection radius in tiles. The creature will become aware of players within this range.

- Goblin, Troll: `5` (short sight — must be nearby to trigger)
- Default new creature: `50` — intentionally high; set explicitly for hostile creatures to avoid them detecting players from across the map

---

### `meatMaterial` · Byte · Optional (default: `0` — no meat)

Material byte of the meat item produced when the corpse is butchered. `0` = no meat. `81` = humanoid / monster flesh (used by goblins, trolls, most hostile humanoids).

---

### `maxAge` · Integer · Optional

Maximum age cap before the creature begins to die naturally. Units are server time ticks.
- Goblin: `100`
- Troll: `300`
- Omitting leaves the constructor default unchanged (varies by template)

---

## Types Array

### `types` · int[] · Optional (default: `[]` for new creatures)

Array of type flag IDs. Each ID sets one or more boolean fields in `CreatureTemplate.assignTypes()`. Multiple IDs stack; order does not matter. Type `31` is absent from the source switch — do not use it.

→ Full table: [[Creature Types]] / `data/creatures/references/creatureTypes.md`

**Commonly used flag combinations for hostile humanoids:**

| ID | Name | Purpose |
|---|---|---|
| 6 | Aggressive toward humans | Attacks on sight |
| 7 | Moves locally | Stays near spawn area rather than wandering |
| 8 | Moves globally | Roams across zones (cannot combine with type 7) |
| 13 | Hunter | Actively pursues prey rather than waiting |
| 16 | Monster | Counted against the server's monster population cap |
| 18 | Regenerates health | Slowly recovers HP when not in combat |
| 29 | Carnivore | Eats meat; influences AI hunger behaviour |
| 30 | Climber | Can navigate steep terrain and hillsides |
| 32 | Dominatable | Players can attempt to tame/dominate |
| 34 | Cave dweller | Prefers underground spawn tiles |
| 36 | Detects invisible | Can perceive hidden/stealthed players |
| 40 | Breaks fences | Destroys fence structures when pathing |
| 45 | Can open doors | Navigates through doors |
| 60 | Epic mission slayable | Valid target for Epic mission kill objectives |
| 61 | Epic mission traitor | Valid target for Epic mission traitor objectives |

---

## Size

### `sizeInCentimeters` · Object · Optional

Physical dimensions applied to the client renderer and collision system.

| Field | Type | Default | Notes |
|---|---|---|---|
| `high` | Short | `100` | Height in cm. Goblin = 130, Hobgoblin = 170, Troll = 230 |
| `long` | Short | `100` | Body length (front to back) in cm |
| `wide` | Short | `100` | Shoulder width in cm |

Reusing an existing model and increasing these values scales the mesh to match. There is no hard upper limit, but values above ~300 cm produce oversized creatures that may clip terrain.

---

## Sounds

### `sounds` · Object · Optional

All four fields default to `""` (silent) if omitted. Female variants fall back to their male counterpart if the male is set but the female is not.

| Field | Fallback | Notes |
|---|---|---|
| `deathMale` | `""` | Sound path played on male creature death |
| `deathFemale` | `deathMale` | Sound path played on female creature death |
| `hitMale` | `""` | Sound path played when male creature takes a hit |
| `hitFemale` | `hitMale` | Sound path played when female creature takes a hit |

Example paths: `"sound.death.goblin"`, `"sound.combat.hit.troll"`. These are Wurm's internal audio asset paths; valid values depend on what the client ships.

Sounds are applied via reflection for **vanilla** creatures (the fields are `private final`).

---

## Movement

### `movement` · Object · Optional

Both fields are applied via reflection for **vanilla** creatures (`private final` in `CreatureTemplate`).

| Field | Type | Default | Meaning |
|---|---|---|---|
| `speed` | Float | `5.0` | Movement speed factor. **Lower = faster.** Goblin = `0.7` (very fast), Troll = `1.2` (moderate). Default `5.0` produces slow movement. Calibrate against vanilla values. |
| `moveRate` | Integer | `60` | Milliseconds between movement decision ticks. **Higher = slower reactions.** Goblin = `1500`, Troll = `1700`. Note: type flag `44` (Careful) hard-overrides `moveRate` to `100` regardless of this field. |

---

## Combat Block

### `combat` · Object · Optional

#### `combat.baseCombatRating` · Float · Optional (default: `1.0`)

Primary combat skill rating. Governs attack and defence rolls against players and other creatures. Applied via public setter; also set via reflection for vanilla creatures.

| Range | Threat level |
|---|---|
| 1.0 | Unthreatening (rat, fish) |
| 3–5 | Low threat (weak animals) |
| 6 | Light threat (goblin) |
| 9–12 | Moderate–high threat (hobgoblin, troll) |
| 15–25 | Dangerous (champion-tier, unique creatures) |
| 30+ | Boss / avatar tier |

---

#### `combat.bonusCombatRating` · Float · Optional

Added to `baseCombatRating` during combat rolls to introduce variance. Troll = `5.0`. Omitting leaves the template default of `1.0` unchanged.

---

#### `combat.maxGroupAttackSize` · Integer · Optional

Maximum number of creatures of this type that will simultaneously attack a single target. Higher values make the creature more dangerous in packs.

- Goblin: `2` — attacks in small groups
- Troll: `8` — entire pack can focus one player

---

#### `combat.maxHuntDistance` · Integer · Optional (default: `20`)

Tile radius within which the creature actively chases a target. Larger values create creatures that relentlessly pursue across a zone.

---

#### `combat.aggressivity` · Integer · Optional (default: `0` for new creatures)

Probability (0–100) that the creature initiates combat when it detects a valid target.

- `0` = fully passive; never attacks unprovoked
- `50` = attacks roughly half the time
- `94` = standard for fully hostile monsters (goblin, troll)
- `100` = always attacks

Applied via reflection for vanilla creatures (`private final` field).

---

#### `combat.alignment` · Float · Optional

Moral alignment on a −100 to +100 scale. Affects priest interactions, alignment-based combat modifiers, and kingdom-standing-based AI targeting.

- `−100` to `−50` = strongly evil (demon-tier)
- `−40` = goblin
- `−50` = troll
- `0` = neutral
- `+50` to `+100` = strongly good (Fo-aligned creatures, avatars of light)

---

#### `combat.damageType` · Integer · Optional (default: `0`)

Primary wound type applied to the creature's normal melee attacks. Cast to `byte` and stored as `combatDamageType` on the template. Governs armor wear calculations and the wound label shown in the combat log. All 11 types are defined in `Wound.java`.

| ID | Constant | In-game label | Typical use |
|---|---|---|---|
| `0` | `TYPE_CRUSH` | Bruise | Mauls, fists, blunt weapons |
| `1` | `TYPE_SLASH` | Cut | Swords, claws, sweeping attacks |
| `2` | `TYPE_PIERCE` | Hole | Spears, arrows, piercing bites — standard for goblins and trolls |
| `3` | `TYPE_BITE` | Bite | Dedicated bite attacks (dogs, spiders, etc.) |
| `4` | `TYPE_BURN` | Burn | Fire breath, hellhorse fire |
| `5` | `TYPE_POISON` | Poison | Spider venom, poison effects |
| `6` | `TYPE_INFECTION` | Infection | Dragon breath (black dragon uses type 6) |
| `7` | `TYPE_WATER` | Water | Drowning damage — no vanilla creature sets this as `combatDamageType` |
| `8` | `TYPE_COLD` | Coldburn | Cold breath (white dragon uses type 8) |
| `9` | `TYPE_INTERNAL` | Internal | Stun moves, phase, internal trauma |
| `10` | `TYPE_ACID` | Acidburn | Acid breath, acid spiders |

**Armor wear:** types `0`–`4` (physical types) can cause 2× or 4× armor wear on hit depending on the armor type. Types `5`–`8` do not appear in `getArmourDamageModFor()` — the armor wear multiplier stays at `1.0f` for those, meaning armor degrades more slowly when struck by poison, infection, water, or cold attacks.

**Breath attacks are independent of this field.** `CombatMove`'s breath implementations (`FIREBREATH`, `ACIDBREATH`, etc.) set their own wound type directly, overriding whatever `combatDamageType` is on the template. Setting `combatDamageType = 4` (burn) on a dragon only affects its normal melee swings — the fire breath move always deals burn regardless, and the cold breath always deals cold.

---

#### `combat.combatMoves` · int[] · Optional

Array of combat move IDs unlocked for the creature during fights. Also settable at the top level as `combatMoves` — `combat.combatMoves` takes precedence if both are present.

All 11 moves are defined in `CombatMove.java`. Each has a per-tick trigger probability (`rarity`) — on any given combat tick the server rolls against this chance for each move in the creature's pool.

| ID | Constant | Name | Base Damage | Rarity | Wound Type | Effect |
|---|---|---|---|---|---|---|
| `1` | `SWEEP` | sweep | 25,000 | 0.01 (1%) | Crushing | AoE hit in a 3×3 area around the creature |
| `2` | `EARTHSHAKE` | earthshake | 23,000 | 0.013 (1.3%) | Crush | AoE hit in a 5×5 area around the creature |
| `3` | `FIREBREATH` | firebreath | 27,000 | 0.011 (1.1%) | Burn | Directional fire cone covering a 5×5 area |
| `4` | `DOUBLE_FIST` | double fist | 30,000 | 0.01 (1%) | Crush | Strikes two tiles based on the creature's facing direction |
| `5` | `STOMP` | stomp | 10,000 | 0.02 (2%) | Crush | AoE hit in a 5×5 area around the creature |
| `6` | `THROW` | throws | 5,000 | 0.05 (5%) | Crush | Throws the target to a random tile up to 10 tiles away |
| `7` | `STUN` | stuns | 24,000 | 0.1 (10%) | Internal | AoE stun + damage in a 3×3 area around the creature |
| `8` | `BASH` | bashes | 25,000 | 0.1 (10%) | Crush | Single-target stun + damage |
| `9` | `ACIDBREATH` | acidbreath | 20,000 | 0.011 (1.1%) | Acid | Directional acid cone covering a 5×5 area |
| `10` | `HELLHORSEFIRE` | firebreath | 7,000 | 0.003 (0.3%) | Burn | Same fire cone as `3` but weaker; used only by the Hell Horse |
| `11` | `PHASE` | phase | 5,000 | 0.011 (1.1%) | Internal | Stuns the target, then teleports the creature itself ~20 tiles away and breaks its own combat lock |

**Notes:**
- **`THROW` (6) and `BASH` (8)** only affect the creature's direct combat opponent — not AoE. Both skip unique creatures.
- **`PHASE` (11)** is self-affecting: the *creature* teleports away and breaks combat, not the target. The in-game alert message says "confuses you," which is misleading — it's the creature that moves.
- **`HELLHORSEFIRE` (10)** shares the same `breatheFire()` implementation as `FIREBREATH` (3) but is registered separately at much lower damage and rarity. Give it only to hellhorse-type creatures.
- **`STUN` (7) and `BASH` (8)** have 0.1 rarity — they trigger roughly 10× more often than the 0.01 moves like `SWEEP` or `DOUBLE_FIST`.
- **`ACIDBREATH` (9)** is not assigned to any vanilla creature despite being fully implemented.
- The vanilla Troll and Goblin set **no `combatMoves`** — they use the legacy and new attack systems instead. `combatMoves` and the new attack system are independent and can coexist on the same template.

**Vanilla creature assignments** (from `CreatureTemplateCreator.java`):

| Moves | Vanilla creatures |
|---|---|
| `[1]` | Eagle Spirit, Spawn of Uttacha, and others |
| `[1, 2]` | Blue Drake, White Drake |
| `[1, 2, 3]` | Red Dragon, Blue Dragon, Green Dragon, Black Dragon, White Dragon, Red Drake |
| `[1, 2, 5]` | Green Drake |
| `[1, 2, 6]` | Drake Black |
| `[1, 5, 6]` | Forest Giant |
| `[1, 7]` | Avenger of Light, Drake Spirit |
| `[4]` | Goblin Leader |
| `[4, 1]` | Troll King |
| `[4, 1, 6]` | Cyclops |
| `[7, 2, 1]` | Libila Incarnation |
| `[7, 5]` | Vynora Epiphany |
| `[8, 1]` | Fo Manifestation, Son of Nogump |
| `[8, 5, 1]` | Magranon Juggernaut |
| `[10]` | Hell Horse |
| `[11]` | Fog Spider |

For most custom hostile creatures, `[1]` or `[1, 8]` is a reasonable starting point. Reserve moves `2`–`6` for large or elite creatures. `7` and `8` are high-frequency stuns — use sparingly for creatures intended to be challenging but not frustrating.

---

### `combat.armor` · Object · Optional

#### `combat.armor.naturalArmour` · Float · Optional (default: `1.0` for new creatures)

Damage multiplier applied to all incoming hits before armor-type reduction. **Lower = more armored.**

- `1.0` = no natural armor (full damage through)
- `0.7` = goblin (30% damage absorbed)
- `0.55` = hobgoblin-tier
- `0.4` = troll (60% damage absorbed)
- `0.2` = heavily armored (dragon-adjacent)

Applied via reflection for vanilla creatures.

#### `combat.armor.armourType` · String · Optional

Armor tier used in combat damage calculations. The `ArmourTemplate.` prefix is accepted but stripped — use the constant name only or with prefix, either is valid.

Valid values (roughly ordered least to most protective):

```
ARMOUR_TYPE_NONE
ARMOUR_TYPE_CLOTH
ARMOUR_TYPE_LEATHER
ARMOUR_TYPE_STUDDED
ARMOUR_TYPE_RING (Troll King Exclusive in Vanilla)
ARMOUR_TYPE_CHAIN
ARMOUR_TYPE_PLATE
ARMOUR_TYPE_SPLINT (Not used in Vanilla)
ARMOUR_TYPE_SCALE (Not used in Vanilla)
ARMOUR_TYPE_LEATHER_DRAGON
ARMOUR_TYPE_SCALE_DRAGON
```

#### `combat.armor.resistances` · Object · Optional

Damage reduction values, one key per damage type. Each field is a Float (0.0–1.0). `0.0` = no protection; `1.0` = full immunity. Only the keys you include are applied — omitted keys leave the template value unchanged.

#### `combat.armor.vulnerabilities` · Object · Optional

Damage amplification values, one key per damage type. Each field is a Float. `0.0` = no weakness; `0.15` = 15% extra damage taken. Resistance and vulnerability for the same type are independent and can both be set.

Both objects share the same set of keys:

| Key | Damage type |
|---|---|
| `physical` | General physical attacks |
| `acid` | Acid damage |
| `fire` | Fire damage |
| `cold` | Cold / freeze damage |
| `disease` | Disease and infection |
| `pierce` | Piercing attacks |
| `slash` | Slashing / cutting attacks |
| `crush` | Crush / blunt attacks |
| `bite` | Bite attacks |
| `poison` | Poison damage |
| `water` | Water damage |
| `internal` | Internal / true damage (bypasses armor) |

```json
"armor": {
  "naturalArmour": 0.55,
  "armourType": "ARMOUR_TYPE_LEATHER",
  "resistances":     { "physical": 0.1, "fire": 0.0, "cold": 0.05 },
  "vulnerabilities": { "fire": 0.15, "slash": 0.1, "poison": 0.1 }
}
```

Only include keys you want to set — omit the rest entirely.

---

### `combat.attacks` · Object · Optional

Defines the creature's attack system. See [[Creature Template Decisions]] for full design rationale. The two systems (legacy and new) coexist in the template struct — only one is active at runtime depending on `useNewSystem`.

#### `combat.attacks.useNewSystem` · Boolean · Optional (default: `false`)

When `true`, calls `template.setUsesNewAttacks(true)` and the engine uses the `primary` and `secondary` attack lists at runtime, ignoring legacy damage values. The legacy block should still be populated even when this is `true` — the values are stored in the template and serve as documentation of the fallback.

---

#### `combat.attacks.legacy` · Object · Optional

Maps directly to the five CreatureTemplate constructor damage parameters. Always stored in the template struct regardless of `useNewSystem`. Only active at runtime when `useNewSystem` is `false`.

Applied via reflection for vanilla creatures.

| Field | Type | Default | Notes |
|---|---|---|---|
| `hand` | Float | `0.0` | Claw / hand damage. `0.0` = no hand attack |
| `handString` | String | `"hit"` | Wound label in combat log (e.g. `"claw"`, `"slash"`) |
| `kick` | Float | `0.0` | Kick damage |
| `kickString` | String | `"kick"` | Wound label for kick |
| `kickPvp` | Integer | — | PvP-specific kick damage modifier |
| `bite` | Float | `0.0` | Bite damage. `0.0` = no bite attack |
| `head` | Float | `0.0` | Headbutt damage |
| `headString` | String | `"headbutt"` | Wound label for headbutt |
| `breath` | Float | `0.0` | Breath attack damage |
| `breathString` | String | — | Wound label for breath attack |

All damage values are raw floats — higher = more damage per hit. `0.0` disables that attack slot.

---

#### `combat.attacks.primary` / `combat.attacks.secondary` · AttackEntry[] · Optional

Attack lists used when `useNewSystem: true`. Primary attacks are rolled on every combat tick. Secondary attacks are a separate, less frequent slot — typically reserved for special or charged moves with higher damage.

Each entry is an **AttackEntry** object:

| Field | Type | Notes |
|---|---|---|
| `name` | String | Display name used in combat log messages |
| `identifier` | String | `AttackIdentifier` enum constant. The `AttackIdentifier.` prefix is optional. |
| `values` | Object | `AttackValues` — see below |

**Observed `identifier` values** (enum is resolved via reflection; only confirmed values from vanilla and current mods):

```
MAUL    — bludgeoning blow
STRIKE  — standard melee strike
KICK    — kick attack
BITE    — bite attack
CLAW    — claw/slash attack
SLASH   — slash attack
```

**AttackValues fields:**

| Field | Type | Notes |
|---|---|---|
| `baseDamage` | Double | Base damage per successful hit. Higher = more damage. Goblin-tier: 3–6, troll-tier: 7–12 |
| `criticalChance` | Double | Critical hit probability (0.0–1.0). `0.04` = 4%, `0.10` = 10% |
| `baseSpeed` | Double | Base attack speed. **Lower = attacks faster.** Goblin-tier: 4–5, troll-tier: 6–7 |
| `attackReach` | Integer | Tile reach of the attack. `1` = adjacent only, `2–3` = short reach, higher for special attacks |
| `weightGroup` | Integer | Weight class used for combo chaining logic. `0`–`2` observed in vanilla |
| `damageType` | Integer | Wound type (same values as `combat.damageType`: 0=crush, 1=slash, 2=pierce, 3=bite) |
| `usesWeapon` | Boolean | `true` = attack uses the creature's equipped weapon stats |
| `rounds` | Integer | Number of combat rounds the attack occupies |
| `waitUntilNextAttack` | Double | Delay multiplier before the next attack can fire. Higher = longer cooldown between attacks |

---

## Drops

### `drops` · int[] · Optional

Item template IDs butchered from the corpse. **Replaces** the default drop list entirely — not additive. To keep vanilla drops, omit this field.

---

## `combatMoves` · int[] · Optional

Top-level fallback. Ignored if `combat.combatMoves` is also set. Functionally identical.

---

## Skills

### `skills` · Object (Map\<String, Double\>) · Optional

Starting skill values for the creature. Keys are skill IDs as strings; values are the starting level (1.0–99.0 typical range).

→ Full skill ID table: [[Skill IDs]] / `data/creatures/references/skills.md`

**Most useful for combat creatures:**

| Key | Characteristic | Effect on creature |
|---|---|---|
| `"100"` | Mind Logic | Decision-making quality |
| `"101"` | Mind Speed | Reaction time |
| `"102"` | Body Strength | Raw attack power |
| `"103"` | Body Stamina | Endurance / HP pool |
| `"104"` | Body Control | Accuracy and evasion |
| `"105"` | Soul Strength | Will / resistance |
| `"106"` | Soul Depth | Spiritual power |
| `"10052"` | Weaponless Fighting | Unarmed combat skill |
| `"10064"` | Huge Club | Large blunt weapon proficiency |

Omitting a skill leaves it at the creature's template default (all characteristics default to their initial values from `CreatureTemplateCreator`).

For editing vanilla creatures: only the keys you include are overridden.

---

## Population Control

### `maxPercentOfCreatures` · Float · Optional

Maximum fraction of the total server creature population this species may occupy, expressed as a decimal (0.0–1.0). The spawn system stops spawning new creatures of this type once the global cap is reached.

| Value | Rarity |
|---|---|
| `0.01` | Very rare — 1% of all world creatures |
| `0.04` | Uncommon — 4% |
| `0.06` | Common — 6% (goblin, troll defaults) |
| `0.10` | Abundant |

---

## Behavior Flags

All Boolean, all Optional. Omitting any flag leaves the template's existing value unchanged.

| Field | Effect |
|---|---|
| `hasHands` | `true` = can pick up, hold, and use items. Required for weapon-wielding creatures |
| `isHorse` | `true` = treated as a rideable horse; interacts with saddle and tack system |
| `keepSex` | `true` = sex is not randomised on spawn; always uses the value set in `sex` |
| `noSkillGain` | `true` = killing this creature awards no skill XP to the player |
| `noServerSounds` | `true` = suppresses server-side ambient sound events for this creature |
| `subterranean` | `true` = creature spawns and operates underground / on cave tiles |
| `tutorial` | `true` = creature is restricted to the tutorial zone |
| `glowing` | `true` = creature emits a glow effect (requires client-side support) |
| `onFire` | `true` = creature is permanently on fire |
| `fireRadius` | Integer — tile radius of the fire effect when `onFire: true` |
| `paintMode` | Integer — controls model color tinting mode. `0` = default |

---

## Breeding

All Optional. Omit entirely for non-breeding creatures.

| Field | Type | Notes |
|---|---|---|
| `eggLayer` | Boolean | `true` = creature reproduces by laying eggs rather than live birth |
| `eggTemplateId` | Integer | Item template ID of the egg. Only evaluated when `eggLayer: true` |
| `childTemplateId` | Integer | Template ID of the juvenile / offspring form |
| `mateTemplateId` | Integer | Template ID of the creature this one can breed with |
| `adultFemaleTemplateId` | Integer | Template ID the juvenile female matures into |
| `adultMaleTemplateId` | Integer | Template ID the juvenile male matures into |
| `leaderTemplateId` | Integer | Template ID of the pack leader / champion variant. Used by group AI logic (e.g. goblin leader is the goblin king template). |

---

## Den

| Field | Type | Notes |
|---|---|---|
| `denName` | String | Display name of the den item when placed in the world |
| `denMaterial` | Integer | Material byte of the den item. `15` = observed default for goblins and trolls. See Wurm material constants for other values |

---

## Corpse

### `corpseName` · String · Optional

Overrides the name of the creature's corpse item. Defaults to the creature's `name` if omitted.

---

## Bounds and Positioning

### `boundsValues` · float[] · Optional

Collision bounding box in local space: `[minX, minY, maxX, maxY]`. Must contain exactly 4 values. Applied via `template.setBoundsValues(float, float, float, float)`.

```json
"boundsValues": [-0.5, -0.5, 0.5, 0.5]
```

Smaller absolute values produce a smaller hitbox. Goblin-sized: `[-0.3, -0.3, 0.3, 0.3]`. Troll-sized: `[-0.5, -0.5, 0.5, 0.5]`.

### `offZ` · Float · Optional

Vertical model offset in meters. `0.0` = ground-level. Positive values lift the model; negative drop it. Used to correct for model origin points that don't align with the ground plane.

---

## Cosmetics

### `colourNames` · String[] · Optional

**These are not arbitrary labels.** Each string is a colour segment inserted into the client model path. `getModelColourName()` fetches the active entry, strips spaces, and lowercases it. `Creature.java` then assembles the full path as `[modelName].[colourName].[sex]` — for example, `model.creature.quadraped.horse.grey.male` or `model.creature.quadraped.horse.piebaldpinto.female`. The variant model files must exist in the client — you cannot invent new names unless the corresponding client assets exist.

**This system is only active when:**
- `isHorse: true`, **or**
- type flag `64` (Multi-color / horse colors) is in the `types` array

For all other creatures, `getColourName()` always returns `colourNameOverrides[0]` and the array has no visible effect.

**How colour selection works:** each creature instance carries a set of trait bits on its `CreatureStatus`. `getColourCode()` maps specific trait bits to an array index, which selects the colour name. The array position is fixed — you cannot reorder the meaning of positions.

**Standard horse** (13 entries):

| Array index | Trait bit | Colour name |
|---|---|---|
| 0 | *(default)* | `grey` |
| 1 | 15 | `brown` |
| 2 | 16 | `gold` |
| 3 | 17 | `black` |
| 4 | 18 | `white` |
| 5 | 24 | `piebald pinto` |
| 6 | 25 | `blood bay` |
| 7 | 23 | `ebony black` |
| 8 | 30 | `skewbald pinto` |
| 9 | 31 | `gold buckskin` |
| 10 | 32 | `black silver` |
| 11 | 33 | `appaloosa` |
| 12 | 34 | `chestnut` |

**Hell horse** (8 entries — same trait-bit mapping, fewer slots):

| Array index | Trait bit | Colour name |
|---|---|---|
| 0 | *(default)* | `ash` |
| 1 | 15 | `cinder` |
| 2 | 16 | `envious` |
| 3 | 17 | `shadow` |
| 4 | 18 | `pestilential` |
| 5 | 24 | `nightshade` |
| 6 | 25 | `incandescent` |
| 7 | 23 | `molten` |

`setColourNames()` overwrites from position 0 upward (maximum 13 entries), fills any remaining positions with `"unused"`, and sets `maxColourCount` to the array length. A creature whose trait code resolves to an index ≥ `maxColourCount` falls back to index 0.

Hell horses register only 8 entries — trait bits that would map to indices 8–12 (bits 30–34) produce index values beyond `maxColourCount` and fall back to index 0 (`ash`).

**Practical use:** only relevant if you are creating a creature type that reuses horse model variants, or if your custom client content provides named mesh variants. For most custom creatures, omit this field entirely.

### `color` · Object · Optional

Base RGB tint applied to the model. `{255, 255, 255}` = no tint (pure model colors). Values are applied to `setColorRed()`, `setColorGreen()`, `setColorBlue()` which are `int` fields on the template.

| Field | Range | Notes |
|---|---|---|
| `red` | 0–255 | Red channel |
| `green` | 0–255 | Green channel |
| `blue` | 0–255 | Blue channel |

---

## Spawns

### `spawns` · SpawnEntry[] · **New creatures only (id ≥ 120)**

Registers this creature into Wurm's encounter system via `EncounterBuilder`. Each entry adds the creature to one tile-type + elevation spawn pool. Ignored entirely for vanilla creature overrides.

| Field | Type | Default | Notes |
|---|---|---|---|
| `tile` | String | — | Tile type name. See [[Spawn Tiles]] / `data/creatures/references/spawnTiles.md` |
| `elevation` | String | — | Elevation name. Same reference |
| `count` | Integer | `1` | Number of creatures spawned per encounter trigger |
| `chance` | Integer | `1` | Relative weight in the spawn pool |

**How `chance` works:** entries within the same tile+elevation pool use cumulative weighting. An entry with `chance: 4` will appear four times as often as one with `chance: 1` when the pool selects a random encounter. This is relative to all other entries in that same pool, including vanilla entries.

→ Valid `tile` and `elevation` values: `data/creatures/references/spawnTiles.md`

> **TASK-006 note:** a standalone `"json-type": "spawn-group"` system (CDDA-style, decoupled from creature definitions) is planned for Phase 4. When that ships, `spawns` on the creature definition will be deprecated. See [[TASK-006 Spawn Groups]].

---

## Admin Fields

### `creatureAI` · String · Optional

Placeholder field for specifying a custom AI behaviour class. The field is stored on the POJO but **no handler logic currently applies it to the template**. Reserved for future use.

### `note` · String · Optional

Free-text annotation. Not applied to the template. Exists purely for admin documentation inside the JSON file.

---

## Implementation Notes

- **`private final` overrides:** speed, moveRate, aggressivity, naturalArmour, all five legacy damage values, and all four sounds are `private final` in `CreatureTemplate`. For vanilla creatures they are patched via `Field.setAccessible(true)` + stripping the `FINAL` modifier. For new creatures the `CreatureTemplateBuilder` constructor accepts these directly.
- **`drops` is `private final`:** also overridden via reflection for both new and vanilla paths (builder accepts it via `itemsButchered()`, vanilla gets `setField(template, "butcheredItems", ...)`).
- **`hasHands` and `isHorse`:** package-private fields with no public setter; always set via reflection.
- **`combat.combatMoves` vs top-level `combatMoves`:** handler checks `cbt.combatMoves` first; falls back to `def.combatMoves`. Having both in one file is valid but redundant.
- **`types` array:** passed to the `CreatureTemplateBuilder` constructor (and stored directly for vanilla). The `assignTypes()` method maps each integer to a named boolean flag on the template. Unknown IDs are silently ignored. Type `31` is absent from the switch statement in source — do not use it.
- **`AttackIdentifier` enum:** resolved at runtime via `Class.forName("com.wurmonline.server.creatures.AttackIdentifier")` and `Enum.valueOf()`. An unrecognized identifier logs a warning and skips the attack entry; it does not crash the server.
- **`armourType` lookup:** resolved via a static map in `CreatureHandler`. Unknown values log a warning and leave the template's existing armour type unchanged.
- **Vanilla creature edits apply in `onServerStarted()`:** this runs after `onItemTemplatesCreated()` and after all mod creature registrations complete, so cross-mod ordering issues are unlikely for reads. Writes are applied once; there is no re-application on reload.
