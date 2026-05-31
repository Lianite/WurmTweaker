# Item Template Fields

Full reference for WurmTweaker item definitions. Covers all supported JSON fields, required vs optional, and the difference between modifying an existing item and creating a new one.

---

## Choosing a Path: Modify or Create

Every item definition must have exactly one of these three keys. Which key is present determines what happens:

| JSON Key | Path | When to use |
|---|---|---|
| `templateId` | Modification | Modify an existing item by numeric ID |
| `templateName` | Modification | Modify an existing item by name (e.g. `"longsword"`) |
| `identifier` | Creation | Register a brand-new item |

For **modification**: only include the fields you want to change. Every field you omit is left at its vanilla value.

For **creation**: all required fields (marked below) must be present. Any missing required fields are logged and the item is skipped — the server will not crash.

→ Full vanilla item ID and name table: `data/items/references/base-game-items.md`

---

## New Item ID Lifecycle

This section is important for anyone creating new items.

When `identifier` is present, the modloader assigns a persistent numeric ID and stores it in its internal database:

- IDs count **down from 22767** — the first custom item registered gets 22767, the next gets 22766, and so on
- The mapping from your `identifier` string to its numeric ID is written once and never changes
- On every subsequent server restart, the same `identifier` resolves back to the same numeric ID

After the item is successfully created on first boot, WurmTweaker writes the assigned ID back to your JSON file as `"assignedTemplateId"`. On subsequent restarts the write-back is skipped because the value is already present.

| Field | Purpose |
|---|---|
| `identifier` | The stable string key you choose (e.g. `"myplugin:mysword"`). The game engine never sees this string — it is only used to look up the numeric ID in the database. |
| `assignedTemplateId` | Auto-written by WurmTweaker after first creation. This is the actual numeric ID the game uses everywhere. Use this value when referencing your custom item in other definitions — creature drop tables, `crushsTo`, `grows`, `harvestsTo`, etc. Do not set this manually. |

> **Never rename `identifier` after a server has run with it.** If you rename it, the modloader treats the new string as a brand-new item and assigns a different ID. The old ID is unregistered and any items already in the world with the old ID become broken. `assignedTemplateId` is the safe value to copy and use elsewhere.

---

## Core Fields

For the **creation path**, all of these are required. For the **modification path**, include only the ones you want to change.

---

### `name` · String · Required for creation

Display name in singular form. Shown in inventory, examine text, and the event window.

---

### `plural` · String · Required for creation

Display name in plural form. Used in stack descriptions and some UI contexts.

---

### `size` · Integer · Required for creation

Physical size category.

| Value | Size |
|---|---|
| 1 | TINY |
| 2 | SMALL |
| 3 | MEDIUM |
| 4 | LARGE |
| 5 | HUGE |

→ See `data/items/references/item-sizes.md` for how size affects gameplay.

---

### `imageNumber` · Integer · Required for creation

Icon ID displayed in the client UI. Valid range: **0–1679**.

The icon system uses 7 sheets of 240 icons each:

| Range | Contents |
|---|---|
| 0–239 | Body parts, special/fallback icons |
| 240–479 | Containers, jewelry, tools, potions |
| 480–719 | Food, crops, raw materials, ore, metal lumps |
| 720–959 | Tools, some weapons |
| 960–1199 | Armor pieces, shields |
| 1200–1439 | Weapons |
| 1440–1679 | Overflow resources and newer items |

Values outside 0–1679 are undefined. When in doubt, use `60` (the question mark / fallback icon).

> **Note:** For certain vanilla items (fishing reels, cooked meat, paintings), the game ignores the template's `imageNumber` and substitutes its own value at runtime. Setting `imageNumber` on those templates has no visible effect.

---

### `behaviourType` · Integer · Required for creation

Controls which right-click action handler manages player interaction with this item.

**For custom items, use `1`** (the default item behaviour) unless the item specifically needs one of the specialized handlers listed below. Any value with no registered handler will cause an error the moment a player tries to interact with the item.

**Common values for custom items:**

| Value | Handler | Use for |
|---|---|---|
| 1 | Item behaviour | Most items — the standard handler |
| 2 | Item pile | Stackable piles |
| 16 | Vegetable | Plant/crop items |
| 18 | Fire | Fire sources |
| 28 | Corpse | Corpse items |
| 41 | Vehicle | Rideable vehicles |
| 55 | Planter | Herb/flower planters |

**Values to avoid:** 3, 12, 13, 14, and 19 have no registered handler and will throw an error on interaction.

---

### `combatDamage` · Integer · Required for creation

Base damage contribution when this item is used as a weapon (old combat path). The formula is `QL × combatDamage / 10000`, so a value of `40` at QL100 adds 0.4 damage.

For items that will never be used in combat, set this to `0`. For dedicated weapons, this works alongside the `weapon` object (see Optional Fields) — both systems can be active simultaneously.

---

### `decayTime` · Long · Required for creation

Minimum server-seconds between decay events. Larger values mean less frequent decay checks.

**Key values:**

| Value | Meaning |
|---|---|
| `9223372036854775807` | Never decays (Long.MAX_VALUE) |
| `1382400` | 16-day mark — items with values at or above this get a 16-day grace period before any decay can occur |
| `28800` | Food sentinel — triggers dynamic recalculation based on QL and damage state |
| Any value below `3600` | Bypasses the 16-day grace period; can decay immediately from creation |

**The food sentinel (`28800`):** items with this exact value use a dynamic interval at runtime. Undamaged food gets a very long effective interval (scales with QL, minimum ~16 days). Once damaged, the interval drops dramatically (8 hours at low QL). Using (consuming) the item resets the decay clock.

**Material and rarity affect decay rate** — crystal items decay very slowly (×0.1), crude items very quickly (×10.0). High-nutrition food decays faster than low-nutrition food. Salt, rarity, and protective containers all slow decay further.

---

### `primarySkill` · Integer · Required for creation

Skill ID applied as a secondary modifier when this item is used as a crafting tool. Use `-10` if the item is not a crafting tool.

This is not the main skill checked during crafting — it is an additional modifier that travels with the item. A hammer would carry the smithing skill ID; a saw would carry carpentry.

→ See `data/skills/references/skills.md` for the full skill ID table.

---

### `modelName` · String · Required for creation

3D model path sent to the client for rendering. The server does not validate this string — it is passed directly to the client, which looks it up in its model files. An invalid path will result in no model or a placeholder.

If you leave this empty, the game stores the literal string `"UNSET"` and the client will receive that.

---

### `difficulty` · Float · Required for creation

Crafting difficulty. Affects how hard it is for players to create, improve, and repair this item — higher values require higher skill for the same success rate. This value is used directly in the crafting skill check formula.

**Vanilla reference values:**

| Item | Difficulty |
|---|---|
| Rope, kindling | 1.0 |
| Plank, shaft | 3.0 |
| Rake | 10.0 |
| Hatchet | 11.0 |
| Shovel, saw | 20.0 |
| Mooring rope | 30.0 |
| Cordage rope | 40.0 |
| Halter rope | 70.0 |
| Steel glove, body parts | 200.0 (effectively un-craftable) |

---

### `weight` · Integer · Required for creation

Starting weight in grams. All items created from this template begin at this weight.

**Side effect:** if `weight > 2000`, the item's `fragmentAmount` (archaeology fragment count) is automatically set using the formula `max(3, weight / 750)`, capped at 127. If you also set `fragmentAmount` explicitly, that value will take effect — but for heavy items, be aware the auto-formula runs first.

**Vanilla reference values:**

| Item | Weight (g) |
|---|---|
| Wheat / grain | 300 |
| Rope | 500 |
| Shaft | 1,000 |
| Kindling | 1,500 |
| Plank, shovel | 2,000 |
| Hatchet | 2,500 |
| Log | 24,000 |

---

### `material` · Integer · Required for creation

Default material type. Drives decay rate, model appearance, repair tool requirements, and more.

→ See `data/items/references/item-materials.md` for the full ID table.

---

### `value` · Integer · Required for creation

Base coin value in iron coins. 100 iron = 1 silver, 10,000 iron = 1 gold.

The effective in-game value scales with QL² — a QL50 item is worth 25% of the same item at QL100, not 50%. Damage further reduces the value linearly. Items valued above 5,000 iron will show a discard-confirmation prompt.

`isPurchased` must also be `true` for this value to be used in trader transactions.

**Vanilla reference values:**

| Item | value (iron) |
|---|---|
| Wheat | 10 |
| Log | 20 |
| Shovel | 100 |
| Satchel | 200 |
| Rope | 1,000 |
| Steel glove | 10,000 |

---

### `isPurchased` · Boolean · Required for creation

Whether the item participates in the NPC trader economy.

When `true`:
- The item is registered in the supply/demand pricing system
- NPC traders will buy it from players
- The item's `value` is used in trade transactions

When `false`: traders ignore the item entirely and it has no economy price.

---

### `descriptions` · Object · Optional for creation

Quality descriptors and examine text. If omitted, the game uses generic defaults.

`superb`, `normal`, `bad`, and `rotten` are **short quality descriptors** incorporated into the item's displayed name (e.g. "a good longsword", "a poor longsword"). Keep these short — one or two words.

`long` is the full examine text shown in the event window when a player examines the item.

```json
"descriptions": {
  "superb": "superb",
  "normal": "good",
  "bad":    "ok",
  "rotten": "poor",
  "long":   "A long and slender sword."
}
```

---

### `dimensions` · Object · Optional for creation

Physical size in centimeters. Defaults to `0, 0, 0` if omitted.

```json
"dimensions": { "x": 5, "y": 80, "z": 1 }
```

The three values are sorted ascending when stored — input order doesn't matter. `{ "x": 60, "y": 3, "z": 10 }` is stored identically to `{ "x": 3, "y": 10, "z": 60 }`.

Dimensions serve two roles:

1. **As an item being placed in a container:** the total volume (`x × y × z`) is checked against the container's available space.
2. **As a container holding other items:** by default the usable internal volume equals its own `x × y × z`. Override this independently with `containerSize` (see below).

**Vanilla reference values (cm):**

| Item | x | y | z |
|---|---|---|---|
| Hatchet | 3 | 10 | 60 |
| Shaft | 3 | 7 | 100 |
| Plank | 3 | 5 | 200 |
| Shovel | 2 | 20 | 100 |
| Satchel | 20 | 30 | 30 |
| Log | 20 | 20 | 200 |

---

### `bodySpaces` · Integer Array · Required for creation

Body slot IDs where the item can be worn or equipped. Use an empty array `[]` for items that cannot be equipped.

→ See `data/items/references/body-spaces.md` for the full slot ID table.

---

### `itemTypes` · Integer Array · Required for creation

Type flags that define what the item is and what can be done with it. Each number enables one or more capabilities — the item being hollow (container), a weapon, food, armour, etc. These flags drive every right-click option and interaction in the game.

→ See `data/items/references/item-types.md` for the full constant table.

---

## Optional Fields

Include only the fields relevant to your item.

---

### `containerSize` · Object · Optional

Sets the internal capacity of a container independently from its physical size. **Must be paired with `itemType 180` in `itemTypes` — neither works without the other.**

```json
"containerSize": { "x": 10, "y": 10, "z": 10 }
```

Without `itemType 180`, the game ignores `containerSize` entirely and uses the item's own physical dimensions as its internal volume. Without `containerSize`, `itemType 180` has no dimensions to work with.

Values are sorted ascending when stored, same as `dimensions`.

**Vanilla examples:**

| Item | Physical dims (cm) | Container dims (cm) |
|---|---|---|
| Forge | 82 × 122 × 390 | 41 × 61 × 210 |
| Square table | 10 × 60 × 60 | 15 × 60 × 60 |
| Large anvil | 30 × 30 × 50 | 40 × 80 × 150 |

---

### `maxItemCount` · Integer · Optional

Maximum number of items the container can hold. Default: `-1` (no limit). Any value of `-1` means the constraint is not enforced.

**Vanilla examples:**

| Item | maxItemCount |
|---|---|
| Lunchbox | 3 |
| Picnic basket | 3 |
| Alchemist's cupboard | 11 |
| Storage unit | 6 |

---

### `maxItemWeight` · Integer · Optional

Maximum total weight of contents the container can hold, in grams. Default: `-1` (no limit).

Both `maxItemCount` and `maxItemWeight` are enforced independently — you can set either, both, or neither.

**Vanilla examples:**

| Item | maxItemWeight |
|---|---|
| Lunchbox | 2,000g |
| Picnic basket | 2,200g |

---

### `nutrition` · Object · Optional

Explicit nutrition values for the item when consumed as food.

```json
"nutrition": { "calories": 500, "carbs": 60, "fats": 20, "proteins": 30 }
```

**Defaults (if omitted):** calories 1000, carbs 150, fats 40, proteins 25.

All four values are scaled by the item's QL at runtime — the template stores the QL-100 baseline. A QL50 item returns 50% of its nutrition values.

**Nutrition tier flags** (set via `itemTypes`, not this field):

| itemType | Tier | Notes |
|---|---|---|
| 137 | No nutrition | Eaten but provides no food benefit (salt, spice, etc.) |
| 55 | Low | — |
| 74 | Medium | — |
| 75 | Good | — |
| 76 | High | Also increases decay speed — high-nutrition food rots faster |

**When to use this field:**

- **Fixed-nutrition food** (non-cooked) — set explicit values. These are the QL-100 baselines.
- **Cooking recipe outputs** — omit this field. The game calculates nutrition dynamically from the recipe ingredients.
- **Inert edibles** (salt, spice) — use `itemType 137` in `itemTypes`; this field is ignored.

---

### `dyeAmountGrams` · Integer · Optional

Grams of dye required to paint the item's primary color zone. Default: `0`.

When `0`, the required amount is auto-calculated from the item's physical dimensions:
`required = max(1, surfaceArea / 25)` where `surfaceArea = 2 × (X×Y + Y×Z + X×Z)` cm².

Only set this explicitly when the auto-formula would give wrong results — very large flat items, or items with a non-standard shape.

**Vanilla reference:**

| Item | Primary dye |
|---|---|
| Oil lamp | 20g |
| Metal torch / street lamp | 100g |
| Small crate | 1,500g |
| Large crate | 2,500g |
| Cart / rowing boat | 5,000g |
| Colossus | 65,000g |

---

### `secondaryItemName` · String · Optional

Display name for a secondary, independently-paintable zone on the item (e.g. `"seat"` on a chair, `"sail"` on a ship). **Requires `itemType 249` in `itemTypes`** — without it, no secondary dye option appears in the UI.

The name appears in the right-click dye menu as `"Dye [name]"` and in action messages. The inspect screen capitalizes the first letter automatically, but the menu uses your exact string — vanilla is inconsistent about whether to capitalize.

This is purely a dye-zone label, not a crafting ingredient name.

**Vanilla examples:**

| Item | Secondary name |
|---|---|
| Armchair | `"seat"` |
| Bed | `"covers"` |
| Cog, sailing ships | `"sail"` |
| Buoy | `"lamp"` |

---

### `dyeSecondaryAmountRequired` · Integer · Optional

Grams of dye required for the secondary color zone. Always paired with `secondaryItemName`. Default: `0`.

When `0`, the secondary dye amount is auto-calculated from the item's geometry (same formula as `dyeAmountGrams`). Only set this explicitly when the secondary zone has a non-standard dye cost.

**Vanilla example:** buoy has `secondaryItemName: "lamp"` with `dyeSecondaryAmountRequired: 312`.

---

### `fragmentAmount` · Integer · Optional

Number of archaeology fragments (pieces of template 1307) that must be found and assembled to reconstruct this item. Default: `3`. Maximum: `127`.

**Auto-assignment:** if `weight > 2000g`, the fragment count is auto-set to `max(3, weight / 750)` (capped at 127) during template construction. Setting `fragmentAmount` explicitly overrides this.

Each fragment weighs `itemWeight / fragmentAmount` grams. Higher values mean more individual digs and a longer assembly process. The combine difficulty increases as the item nears completion.

| Template weight | Auto fragment count |
|---|---|
| ≤ 2,000g | 3 (default) |
| 3,000g | 4 |
| 10,000g | 13 |
| 75,000g | 100 |
| ≥ 100,000g | 127 (cap) |

---

### `alcoholStrength` · Integer · Optional

Alcohol potency for beverage items. Default: `0`. Setting any non-zero value also marks the item as alcoholic — there is no separate flag to set.

Potency is a relative scale. At QL100, the formula is roughly `alcoholStrength × 0.3` added to the player's alcohol level (0–100). Partial containers (under 200g) apply a proportional reduction.

**Intoxication thresholds:**

| Player alcohol level | Effect |
|---|---|
| ≥ 10 | Tipsy |
| ≥ 20 | Getting drunk |
| ≥ 30 | Drunk |
| ≥ 60 | Very drunk |
| ≥ 90 | Can barely walk |
| = 100 | Drunkard title awarded |

**Vanilla reference values:**

| Drink | alcoholStrength |
|---|---|
| Beer | 4 |
| Mead | 6 |
| Cider | 8 |
| White wine | 11 |
| Red wine | 13 |
| Rum | 22 |
| Vodka | 30 |
| Whisky / brandy | 35 |
| Moonshine | 40 |

---

### `foodGroup` · Integer · Optional

Assigns this item to a food group so it can be used as a substitutable ingredient in cooking recipes. Default: `0` (item is only matchable by its exact template ID).

When a recipe slot specifies a food group anchor (e.g. "any cereal"), the game accepts any item in that group. Use this to make a custom crop or ingredient work with existing recipes.

**Vanilla food group anchor IDs:**

| ID | Group |
|---|---|
| 1156 | any veg |
| 1157 | any cereal |
| 1158 | any herb |
| 1159 | any spice |
| 1163 | any fruit |
| 1179 | any berry |
| 1197 | any nut |
| 1198 | any cheese |
| 1199 | any mushroom |
| 1200 | any milk |
| 1201 | any fish |
| 1261 | any meat |
| 1267 | any flower |

To create a new food group category, define an anchor item with `itemType 207 + 208`, then point member items at it with `foodGroup: [anchorTemplateId]`.

---

### `crushsTo` · Integer · Optional

Template ID of the item produced when this item is crushed (right-click → Crush). Default: `0` (crush action not available).

Yield scales with how much the player holds: `floor(currentWeight / templateWeight)` outputs are created, each at the same QL as the source. The source weight is consumed proportionally. Fractional remainders are left on the source.

Note: the field name preserves the vanilla Wurm spelling.

**Vanilla examples:**

| Source item | Output |
|---|---|
| Pumpkin (1,000g) | Pumpkin seed |
| Wemp plants (700g) | Wemp fibre |
| Reed plants (500g) | Reed fibre |
| Fennel plant (300g) | Fennel |
| Sugar beet (1,000g) | Sugar |

---

### `pickSeeds` · Integer · Optional

Template ID of the seed produced when this item is hand-picked (right-click → Pick). Default: `0` (pick action not available).

Uses the same yield mechanic as `crushsTo` — yield = `floor(currentWeight / templateWeight)`, QL transferred, source consumed. The source item is ruined in the process.

Can be set alongside `crushsTo` on the same item — all four vanilla plant types that support seed picking also support crushing as a separate action.

**Vanilla examples:**

| Source item | Yield per unit |
|---|---|
| Wemp plants (700g) | 7 wemp seeds (100g each) |
| Reed plants (500g) | 5 reed seeds (100g each) |
| Fennel plant (300g) | 6 fennel seeds (50g each) |
| Sugar beet (1,000g) | 20 sugar beet seeds (50g each) |

---

### `grows` · Integer · Optional

Template ID of the plant this seed or cutting produces when grown in a planter pot. Default: `0`.

When `0`, the item is treated as growing into itself (the seed and the plant are the same template). Omit this field only if that is intentional.

Only affects planter pot behaviour. Does not affect ground-tile crops, which use a separate system.

**Vanilla examples:**

| Seed | Grows into |
|---|---|
| Fennel seed (1151) | Fennel plant (1132) |
| Paprika seed (1153) | Paprika plant (1143) |
| Turmeric seed (1154) | Turmeric plant (1144) |

---

### `harvestsTo` · Integer · Optional

Template ID of the item produced when a player harvests a planted trellis. Default: `0`.

Setting any non-zero value also enables the Harvest action on the item — there is no separate flag to set. Only affects trellis-type planted structures. Has no effect on planter pots or ground-tile crops.

> **Grape north/south split:** if `harvestsTo` is set to template ID `411` (blue grapes), the output is automatically overridden to green grapes (414) for tiles in the northern half of the map. This is hardcoded behaviour — any trellis pointing at 411 inherits the geographic split.

**Vanilla examples:**

| Trellis | Harvest output |
|---|---|
| Grape trellis (920) | Blue grapes / green grapes (north) |
| Rose trellis (1018) | Rose flower |
| Hops trellis (1274) | Hops |

---

## `weapon` Object · Optional

Weapon stats for the new combat system. Only relevant for items with weapon-type flags in `itemTypes`.

```json
"weapon": {
  "damage": 5.5,
  "speed": 3.75,
  "critChance": 0.01,
  "reach": 3,
  "weightGroup": 3,
  "parryPercent": 1.0,
  "skillPenalty": 0.0,
  "damagedByMetal": false
}
```

> **Important:** any weapon without a registered `weapon` block defaults to a `skillPenalty` of `7.0` and a `speed` of `20.0` — very inaccurate and very slow. Always include the `weapon` object for items intended to be used in combat.

This system runs alongside `combatDamage` — both can be active on the same item.

**Vanilla weapon reference:**

| Weapon | damage | speed | critChance | reach | parryPercent | skillPenalty |
|---|---|---|---|---|---|---|
| Fist | 1.0 | 1.0 | 0.0 | 1 | 0.0 | 2.0 |
| Carving knife | 1.0 | 2.0 | 0.0 | 1 | 1.0 | 2.0 |
| Short sword | 4.0 | 3.0 | 0.10 | 2 | 1.0 | 0.0 |
| Long sword | 5.5 | 4.0 | 0.01 | 3 | 1.0 | 0.0 |
| Battle axe | 6.5 | 4.0 | 0.03 | 4 | 0.3 | 0.0 |
| Two-handed sword | 9.0 | 5.0 | 0.05 | 4 | 1.0 | 0.0 |
| Large maul | 11.0 | 6.0 | 0.03 | 4 | 1.0 | 0.0 |
| Magic hammer | 18.0 | 6.0 | 0.08 | 4 | 1.0 | 0.0 |
| Hatchet (tool) | 1.0 | 5.0 | 0.0 | 2 | 0.0 | 3.0 |
| Small bow | 0.0 | 5.0 | 0.0 | 1 | 1.0 | 9.0 |

---

### `weapon.damage` · Float

Base damage potential. Scales with QL — a QL100 item deals full damage, QL50 deals half. Vanilla range: `0.0` (bows, damage from ammunition) to `18.0` (magic hammer).

---

### `weapon.speed` · Float

Attack timer. **Higher = slower.** Hard minimum of `3.0` — no skill or enchant can go below this floor. At maximum skill, players reduce their effective timer by up to 10%.

| Speed | Examples |
|---|---|
| 1.0 | Fist |
| 2.0 | Knives |
| 3.0 | Short sword, sickle |
| 4.0–5.0 | Long sword, magic weapons |
| 5.0–6.0 | Pickaxe, shovel, large maul |

---

### `weapon.critChance` · Float

Critical hit probability against players (crits do not apply to creatures). The stored value is internally divided by 5, so `0.10` becomes a 2% base crit rate. Rarity increases this: rare ×1.1, supreme ×1.3, fantastic ×1.5.

Vanilla range: `0.0` (most tools) to `0.10` (short sword).

---

### `weapon.reach` · Integer

Ideal engagement distance — higher reach weapons prefer more space. Vanilla range: `1` (knives, fist) to `7` (spears).

| Value | Examples |
|---|---|
| 1 | Fist, knives, hammers |
| 2–3 | Short sword, long sword, pickaxe |
| 4–5 | Battle axe, two-handed sword, rake |
| 6–7 | Halberd, spears |

---

### `weapon.weightGroup` · Integer

Currently has no effect in the game engine. Define any value freely.

---

### `weapon.parryPercent` · Float

Probability of parrying an incoming non-critical hit. `1.0` = always parries when in position; `0.0` = cannot parry. Vanilla range: `0.0` (hatchet, bows) to `1.0` (swords, mauls, spears).

---

### `weapon.skillPenalty` · Double

Accuracy penalty when wielding this weapon. Subtracted from the player's combat rating — higher values make it harder to land hits. Dedicated weapons use `0.0`; tools used as weapons typically use `2.0–5.0`; bows use `9.0`.

---

### `weapon.damagedByMetal` · Boolean · Default `false`

When `true`, the weapon takes structural damage when it contacts a metal parry weapon during combat. Use `true` for wooden, cloth, or organic weapons not designed to block metal. All bows, wooden tools, and crude improvised weapons use `true` in vanilla.
