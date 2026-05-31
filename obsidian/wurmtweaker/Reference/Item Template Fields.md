---
type: reference
tags:
  - reference
  - items
related:
  - "[[TASK-005 Items]]"
  - "[[Item Types]]"
  - "[[Item Materials]]"
  - "[[Item Sizes]]"
  - "[[Skill IDs]]"
---

# Item Template Fields

Full JSON schema for WurmTweaker item definitions. Derived from `ItemTemplate.java`, `ItemTemplateBuilder.java`, and the six `ItemTemplateCreator*.java` files.

## Source Files

Item templates are defined across six creator files, all called from `ItemTemplateFactory.java`:

| File | Contents |
|---|---|
| `ItemTemplateCreator.java` | Primary batch — bulk of all items |
| `ItemTemplateCreatorContinued.java` | Overflow of general items |
| `ItemTemplateCreatorThird.java` | Third batch |
| `ItemTemplateCreatorCooking.java` | Cooking items |
| `ItemTemplateCreatorFishing.java` | Fishing items |
| `ItemTemplateCreatorKingdom.java` | Kingdom items |

The mod builder API lives in `ItemTemplateBuilder.java` (modsupport).

## Path Selection

Every item definition must have exactly one of these three keys. Which key is present determines which handler path runs:

| JSON Key | Type | Path | Notes |
|---|---|---|---|
| `templateId` | integer | Modification | Modify an existing template by numeric ID |
| `templateName` | string | Modification | Modify an existing template by `ItemList` constant name (e.g. `"longsword"`) |
| `identifier` | string | Creation | Register a brand-new template (e.g. `"myplugin:mysword"`) |

Only one key should be present per definition. If `identifier` is set, it always takes the creation path regardless of whether `templateId` or `templateName` are also present.

For **modification**: only the fields you want to change need to be in the JSON. Absent fields leave the vanilla value untouched.

For **creation**: all required fields (see below) must be present. Missing required fields are all logged at once and the item is skipped without crashing the server.

## Custom Item ID Lifecycle

This section is critical for anyone creating new items.

When `identifier` is present, `ItemTemplateBuilder` calls `IdFactory.getIdFor(identifier, IdType.ITEMTEMPLATE)`, which assigns and persists a numeric ID in the modloader's SQLite DB (`modsupport.db`):

- IDs count **down from 22767** — the first custom item registered gets 22767, the next gets 22766, and so on
- The mapping `identifier string → numeric ID` is written once and never changes
- On every subsequent restart, the same identifier string resolves back to the same ID from the DB

After the item is successfully created, WurmTweaker writes the assigned ID back to your JSON file as `"assignedTemplateId"`. On subsequent restarts the write-back is skipped because the value is already present and current.

| Field | Type | Purpose |
|---|---|---|
| `identifier` | string (input) | The stable string key the modloader uses to look up or create the numeric ID in `modsupport.db`. The game engine never sees this string — it is purely a persistence key. |
| `assignedTemplateId` | integer (output) | Auto-written by WurmTweaker on first boot. This is the actual numeric ID the game engine uses everywhere. Use this value when you need to reference your custom item in other JSON definitions — creature drop tables, `crushsTo`, `grows`, `harvestsTo`, etc. Do not set this manually; it is managed automatically. |

> **Never rename `identifier` after a server has run with it.** If you rename it, `IdFactory` treats the new string as a brand-new item and assigns a different ID. The old DB row orphans, the old ID is unregistered, and any items already in the world with the old ID become broken. `assignedTemplateId` is the safe value to copy and reference elsewhere. `identifier` is just the key that produced it.

## Core Fields

These fields are `private final` on `ItemTemplate` — they are set at construction time and require reflection to modify on existing templates. For the **creation path** all of these are required. For the **modification path** only include the ones you want to change.

---

### `name` · String · Required for creation

Display name in singular form. Shown in inventory, examine text, and the event window.

---

### `plural` · String · Required for creation

Display name in plural form. Used in stack descriptions and some UI contexts.

---

### `size` · Integer · Required for creation

Physical size category of the item.

| Value | Constant |
|---|---|
| 1 | TINY |
| 2 | SMALL |
| 3 | MEDIUM |
| 4 | LARGE |
| 5 | HUGE |

→ See [[Item Sizes]] for full context on how size affects gameplay.

---

### `imageNumber` · Integer · Required for creation

Identifies the icon displayed for this item in the client UI. Maps to `public final short imageNumber` on `ItemTemplate`. No setter exists — set once at construction via WurmTweaker's reflection helper. There is no server-side range check or validation.

The server sends this value as a `short` in the `sendItem()` packet to the client (Communicator.java:13895).

**Hard cap: 0–1679.** The shared `IconConstants` class defines `MAX_ICON_NUMBER = 1679`. Values above 1679 are undefined. Negative values are not used in vanilla.

**Icon sheet system — 7 sheets, 240 icons each (1,680 total slots):**

| Sheet index | Constant | Offset range | Contents |
|---|---|---|---|
| 0 | `SHEET_ICONS` | 0–239 | Body parts, special/fallback icons |
| 1 | `SHEET_MISC` | 240–479 | Containers, jewelry, tools, potions |
| 2 | `SHEET_RESOURCE` | 480–719 | Food, crops, raw materials, ore, metal lumps |
| 3 | `SHEET_TOOLS` | 720–959 | Tools, some weapons (blades + assembled) |
| 4 | `SHEET_ARMOR` | 960–1199 | Armor pieces, shields |
| 5 | `SHEET_WEAPONS` | 1200–1439 | Weapons |
| 6 | `SHEET_RESOURCE2` | 1440–1679 | Overflow resources and newer items |

Each sheet is 20 icons per row × 12 rows (`ICONS_PER_LINE = 20`, `ICONS_PER_SHEET = 240`).

**Key named constants from `IconConstants`:**

| Value | Constant | Notes |
|---|---|---|
| 60 | `ICON_NONE` / `ICON_ICON_QUESTIONMARK` / `ICON_ICON_UNFINISHED_ITEM` | Fallback/unknown icon — used when no dedicated icon exists |
| 241 | `ICON_CONTAINER_BACKPACK` | — |
| 242 | `ICON_CONTAINER_BAG` | — |
| 260 | `ICON_MISC_POTION` | — |
| 523 | `ICON_FOOD_MEAT_COOKED` | Used for all cooked food via a hardcoded runtime override |
| 570 | `ICON_COIN_GOLD` | — |
| 606 | `ICON_WOOD_LOG` | — |
| 1207 | `ICON_TOOL_HATCHET` | Only named weapons-sheet constant visible in server code |
| 1224 | *(unnamed)* | Used by longsword |

**Runtime overrides — `Item.getImageNumber()` (Item.java:8604–8717):**

For certain template IDs, the runtime method ignores the template's `imageNumber` entirely and returns a hardcoded value instead. Setting `imageNumber` on these templates has no effect on what the client sees:

| Template ID | Condition | Returns |
|---|---|---|
| 1310 (creature egg) | Stored creature is a dragon (template 45) | 404 |
| 1307 (painting/sculpture) | No data (blank canvas) | 1460 |
| 1307 | By material (metal, wood, clay, etc.) | 1461–1474 |
| 1346 (fishing reel) | Empty reel | 846 |
| 1346 | Reel with line, no hook | 866 |
| 1346 | Fully rigged reel | 886 |
| 387 (form item) | Any | Returns the stored template's `imageNumber` |
| 92, 368 | Not raw | 523 (cooked meat icon) |
| Any with recipe | Recipe result is not a food group | Recipe result item's `imageNumber` |
| Any with `useRealTemplateIcon` | Real template is 92 or 368 and not raw | 523 |
| Any with `useRealTemplateIcon` | Otherwise | Real template's `imageNumber` |

**What is not known from server code:** How the client maps `imageNumber` to a texture atlas position, which image files correspond to each sheet, and what happens when an `imageNumber` has no corresponding client asset — these are client-side only.

---

### `behaviourType` · Integer · Required for creation

A registry dispatch key that determines which handler manages all player interaction with this item. Maps to `private final short behaviourType` on `ItemTemplate`. No setter, no default, no validation — raw constructor assignment only.

**How it works:** `behaviourType` is a lookup key into a global `HashMap<Short, Behaviour>` maintained by the `Behaviours` singleton. Every time a player interacts with an item, the chain resolves:

```
item.getBehaviour()
  → template.getBehaviour()
  → Behaviours.getInstance().getBehaviour(behaviourType)
  → behaviours.get(type)   // throws NoSuchBehaviourException if not registered
```

The resolved `Behaviour` object drives the right-click menu and all action handling. An invalid `behaviourType` does not crash the server at load time — it only throws `NoSuchBehaviourException` the moment a player tries to interact with the item.

**Not sent to the client.** The client receives the action list produced by the handler, not the raw type value.

**Relationship to `itemTypes[]`:** These are entirely separate axes. `behaviourType` picks which handler class manages the item. `itemTypes[]` is a multi-value tag set read by the handler internally to decide which specific actions to offer. Two items can share `behaviourType = 1` (ItemBehaviour) but get completely different right-click menus because ItemBehaviour reads their `itemTypes` flags to decide what to show.

**Complete constant table — `BehaviourList.java`** (45 constants, IDs 0–61 with gaps at 3, 12, 13, 14, 19):

| ID | Constant | Use for items? |
|---|---|---|
| 0 | `noBehaviour` | No — base handler only |
| 1 | `itemBehaviour` | **Yes — default for most items** |
| 2 | `itemPileBehaviour` | Yes |
| 3 | *(no constant)* | **No — no registered handler; throws on interaction** |
| 4 | `creatureBehaviour` | No — world entity |
| 5 | `tileBehaviour` | No — world entity |
| 6 | `structureBehaviour` | No — world entity |
| 7 | `tileTreeBehaviour` | No — world entity |
| 8 | `tileGrassBehaviour` | No — world entity |
| 9 | `tileRockBehaviour` | No — world entity |
| 10 | `bodyPartBehaviour` | Yes — body part items |
| 11 | `examineBehaviour` | No — world entity |
| 12–14 | *(no constants)* | **No — no registered handler; throws on interaction** |
| 15 | `tileDirtBehaviour` | No — world entity |
| 16 | `vegetableBehaviour` | Yes |
| 17 | `tileFieldBehaviour` | No — world entity |
| 18 | `fireBehaviour` | Yes |
| 19 | *(no constant)* | **No — no registered handler; throws on interaction** |
| 20 | `wallBehaviour` | No — world entity |
| 21 | `writBehaviour` | Yes |
| 22 | `fenceBehaviour` | No — world entity |
| 23 | `unfinishedItemBehaviour` | Yes |
| 24 | `villageDeedBehaviour` | Yes |
| 25 | `villageTokenBehaviour` | Yes |
| 26 | `toyBehaviour` | Yes |
| 27 | `woundBehaviour` | Yes — wound items |
| 28 | `corpseBehaviour` | Yes |
| 29 | `traderBookBehaviour` | Yes |
| 30 | `cornucopiaBehaviour` | Yes |
| 31 | `practiceDollBehaviour` | Yes |
| 32 | `tileBorderBehaviour` | No — world entity |
| 33 | `domainItemBehaviour` | Yes |
| 34 | `hugeAltarBehaviour` | Yes |
| 35 | `artifactBehaviour` | Yes |
| 36 | `planetBehaviour` | Yes |
| 37 | `hugeLogBehaviour` | Yes |
| 38 | `caveWallBehaviour` | No — world entity |
| 39 | `caveTileBehaviour` | No — world entity |
| 40 | `WarmachineBehaviour` | Yes |
| 41 | `vehicleBehaviour` | Yes |
| 42 | `skillBehaviour` | No — skill system |
| 43 | `missionBehaviour` | No — mission system |
| 44 | `papyrusBehaviour` | Yes |
| 45 | `floorBehaviour` | No — world entity |
| 46 | `shardBehaviour` | Yes |
| 47 | `flowerpotBehaviour` | Yes |
| 48 | `gravestoneBehaviour` | Yes |
| 49 | `inventoryBehaviour` | Yes — inventory container only (template 0) |
| 50 | `ticketBehaviour` | Yes |
| 51 | `bridgePartBehaviour` | No — world entity |
| 52 | `ownershipPaperBehaviour` | Yes |
| 53 | `menuRequestBehaviour` | No — UI system |
| 54 | `tileCornerBehaviour` | No — world entity |
| 55 | `planterBehaviour` | Yes |
| 56 | `markerBehaviour` | Yes |
| 57 | `almanacBehaviour` | Yes |
| 58 | `trellisBehaviour` | Yes |
| 59 | `wagonerContractBehaviour` | Yes |
| 60 | `bridgeCornerBehaviour` | No — world entity |
| 61 | `wagonerContainerBehaviour` | Yes |

**For new custom items, use `1` (`itemBehaviour`) unless the item specifically needs one of the specialized handlers above.** Any value above 61 or in the gap set (3, 12, 13, 14, 19) has no registered handler and will throw `NoSuchBehaviourException` on first player interaction.

---

### `combatDamage` · Integer · Required for creation

Base damage component used by the **old combat path** (`CombatEngine.getWeaponDamage()`). Maps to `private final int combatDamage` on `ItemTemplate`.

**Formula (old path):** `QL * combatDamage / 10000` — this is an additive QL-scaled bonus, not the primary damage value.

**Strength interaction (old path):** multiplied by `1.0 + strength / 100.0`.

**Body part handling:** for body-part items (fist, foot, etc.) the engine routes to `creature.getCombatDamage()` instead.

**Relationship to `weapon.damage`:** these are two parallel systems. If a `weapon` object is also registered for the item, `Weapon.getModifiedDamageForWeapon()` runs a separate QL-multiplicative calculation from `weapon.damage` via `CombatHandler`. Both paths can be active simultaneously. See the `weapon` object section for the full comparison.

The `create-new.json.example` uses `40`. Non-weapon items that will never be used in combat can safely set this to `0`.

---

### `decayTime` · Long · Required for creation

The minimum number of **server-seconds** that must pass before a decay event can occur. This is a timer interval — not a damage value and not a rate. The longer the value, the less frequently decay is evaluated.

Maps to `private final long decayTime` on `ItemTemplate`. Accessors: `ItemTemplate.getDecayTime()` (ItemTemplate.java:2047), `Item.getDecayTime()` (Item.java:11097–11099).

**Key sentinel values:**

| Value | Duration | Meaning |
|---|---|---|
| `< 3600L` | < 1 hour | Bypasses the 16-day grace period; item can decay immediately from creation |
| `28800L` | 8 hours | Food sentinel — triggers dynamic interval recalculation at runtime (see below) |
| `1382400L` | 16 days | Grace period threshold |
| `Long.MAX_VALUE` (`9223372036854775807`) | Never | Item never decays; all decay logic is skipped |

**The 16-day grace period:**

Standard items (not food, not always-polled, not in trashbin, and with `decayTime >= 3600L`) do not decay at all until the item has existed for at least 1,382,400 server-seconds (16 days) past its creation date (Item.java:6311). Items with `decayTime < 3600L` bypass this grace period entirely.

**Two decay code paths:**

- **`pollOwned()` (Item.java:5865)** — runs when the item is in a player's inventory. Applies to food, `isAlwaysPoll()` items, corpses, planted flowerpots, and items in a tacklebox.
- **`poll()` (Item.java:6218, 6606)** — runs for world items (inside containers, on ground, in structures). Applies to everything not handled by `pollOwned`.

Both paths share the core calculation:

```java
int timesSinceLastUsed = (int)((WurmCalendar.currentTime - this.lastMaintained) / decayt);
```

When `timesSinceLastUsed > 0`, a decay event fires and `lastMaintained` is updated.

**The 28800L sentinel — dynamic recalculation for food (Item.java:5887–5893):**

When `decayTime == 28800L`, the stored value is replaced at runtime with a recalculated effective interval based on the item's current QL and damage:

```java
if (decayt == 28800L) {
    if (this.damage == 0.0f) {
        decayt = 1382400L + (long)(28800.0f * Math.max(1.0f, this.qualityLevel / 3.0f));
    } else {
        decayt = (long)(28800.0f * Math.max(1.0f, this.qualityLevel / 3.0f));
    }
}
```

- **Undamaged:** effective interval = `1,382,400 + (28,800 × max(1, QL/3))` — minimum ~16 days, scaling up with quality
- **Any damage > 0:** effective interval = `28,800 × max(1, QL/3)` — 8 hours at QL 1, ~8.3 days at QL 90

`Item.isRefreshedOnUse()` returns `true` when `decayTime == 28800L` (Item.java:11101–11103) — using (consuming) such an item resets `lastMaintained`, resetting the decay clock.

**Damage per decay tick — `getDamageModifier(true)` (Item.java:10752–10797):**

```java
damage += timesSinceLastUsed * Math.max(decayMin, getDamageModifier(true))
```

Base formula: `100.0f * rotMod / max(1.0f, qualityLevel * (100.0f - damage) / 100.0f) * materialMod`

Higher QL and lower existing damage both reduce per-tick damage. As the item nears 100 damage the divisor approaches zero, accelerating final-stage decay.

`rotMod` modifiers (multiplicative):

| Condition | Effect |
|---|---|
| Crude item | ×10.0 |
| Crystal item | ×0.1 |
| Food, high nutrition | +10 (or +5 if salted) |
| Food, good nutrition | +5 (or +2 if salted) |
| Food, medium nutrition | +3 (or +1.5 if salted) |
| In tacklebox | ×0.5 |
| In container template 1342 | ×0.5 |
| Rarity > 0 | ×0.9^rarity (rare ×0.9, supreme ×0.81, fantastic ×0.729) |
| Spell rot modifier | +spellRotModifier / 100 |
| Rune effect (ENCH_DECAY) | ×runeEffect |

`materialMod` (from `getMaterialDecayModifier()`, Item.java:10582–10644):

| Material | ID | Damage mod | METALLIC_ITEMS feature required |
|---|---|---|---|
| Adamantine | 56 | ×0.40 | No |
| Glimmersteel | 57 | ×0.60 | No |
| Seryll | 67 | ×0.50 | Yes |
| Gold | 7 | ×0.40 | Yes |
| Silver | 8 | ×0.70 | Yes |
| Steel | 9 | ×0.70 (×0.80 without flag) | Partial |
| Lead | 12 | ×0.80 | Yes |
| Electrum | 96 | ×0.80 | Yes |
| Oak wood | 38 | ×0.80 | Yes |
| Bone | 35 | ×0.90 | Yes |
| Tin | 34 | ×0.925 | Yes |
| Brass | 30 | ×0.95 | Yes |
| Copper | 10 | ×0.95 | Yes |
| Zinc | 13 | ×1.20 (decays faster) | Yes |
| All others | — | ×1.0 | — |

**Interval stretching — `getDecayMultiplier()` (Item.java:7438–7481):**

A multiplier applied to the effective tick count. Values > 1.0 cause the item to require more real elapsed time to trigger a decay event. Partial ticks are stored and carried over.

| Condition | Multiplier |
|---|---|
| Material is cedar wood (ID 39) | ×1.5 |
| Item is wrapped (papyrus or cloth) | ×5.0 |
| Item is salted | ×1.5 |
| In lunchbox template 1297 (better lunchbox) | ×1.5 |
| In lunchbox template 1296 (basic lunchbox) | ×1.25 |
| Liquid in container template 1117 | ×2.0 |
| Parent container decay multiplier | (propagates from parent's own rules) |

**Special decay behaviours:**

- **`destroyOnDecay` flag:** adds `timesSinceLastUsed × max(1.0, 10.0 - lunchboxMod)` damage per tick in `pollOwned`; ×10 per tick in `poll`. Items already damaged in trashbin are immediately destroyed.
- **Bulk items (`poll`):** instead of damage, bulk items lose 5% of their weight per tick. Destroyed when weight drops below the volume threshold.
- **`positiveDecay` flag:** only template 738 (goblet in barrel). Each tick increases quality rather than damage — the wine/aging mechanic.
- **Magic containers:** items inside skip damage decay unless they are a currently-burning light source or fireplace.
- **Trashbin:** effective interval is capped at `min(decayt, 28800L)` regardless of template value.
- **Saddlebag items (`SADDLEBAG_DECAY` feature flag):** items in creature saddlebags use `0.2 × getDamageModifier(true)` per tick, with a 1-in-6 random gate on non-food items.
- **Template 386 (liquid container):** `decayTime` is read from the `realTemplate` (the liquid it contains), not template 386 itself.
- **Deeded/structure reduction:** a random gate is applied for non-bulk, non-light items: `1-in-2` chance outdoors undeeded → `1-in-10` in a structure → `1-in-14` deeded in a structure.
- **Divine protection (Fo deity):** if the item owner worships an `isItemProtector()` deity with Faith ≥ 70 and Favor ≥ 35, food items have a 1-in-5 chance of skipping each decay tick entirely.

---

### `primarySkill` · Integer · Required for creation

A skill ID from the game's skill system. Maps to `private final int primarySkill` on `ItemTemplate` (ItemTemplate.java:67).

**Sentinel value: `-10`** — means the item has no associated primary skill. The accessors enforce this:

```java
public boolean hasPrimarySkill() {
    return this.primarySkill != -10;  // false = no skill
}

public int getPrimarySkill() throws NoSuchSkillException {
    if (this.primarySkill == -10) {
        throw new NoSuchSkillException("No skill needed for item " + this.name);
    }
    return this.primarySkill;
}
```

**What it does:** when a player uses this item as the active/source item in a crafting action, the creation system reads `primarySkill` and applies it as the **secondary skill modifier** in the creation formula. This is confirmed across all creation entry types:

- `CreationEntry.java:369` — `secondarySkill = skills.getSkill(realSource.getPrimarySkill())`
- `AdvancedCreationEntry.java:1079` — same pattern
- `SimpleCreationEntry.java:204` — same pattern
- `Recipe.java:1034` — `secondarySkill = skills.getSkill(activeItem.getPrimarySkill())` (cooking recipes)

**What it is not:** this is not the main skill tested for the crafting action itself — that belongs to the `CreationEntry` definition, not the item template. The template's `primarySkill` is a property that travels with the item and only activates when that item is used as a crafting source.

Use `-10` for items that are never used as crafting tools. For tool items, set this to the skill ID that matches the tool's function — a hammer would carry the smithing skill ID, a saw would carry carpentry, etc.

→ See [[Skill IDs]] for the full ID table.

---

### `modelName` · String · Required for creation

The model path string sent to the client for rendering. Maps to `private final String modelName` on `ItemTemplate` (ItemTemplate.java:70).

**Fallback:** if the value passed at construction is `null` or empty, the field is stored as the literal string `"UNSET"` (ItemTemplate.java:686). This is not a valid model — the client will receive `"UNSET"` and likely render nothing or a placeholder.

**How it reaches the client:** the server serializes the string to UTF-8 bytes and writes it directly into the network packet (Communicator.java:13882, 13939; PlayerCommunicatorQueued.java:784). The server has no further involvement. What the client does with the string — looking it up in `graphics.jar`, a serverpack, or a modloader resource — is entirely client-side.

**`Item.getModelName()` overrides:** the server sends the result of `Item.getModelName()` (Item.java:1211), not the raw template value. For most items it returns `template.getModelName()` unchanged, but several template-specific overrides exist:

| Condition | Model name sent |
|---|---|
| Template 385 (fallen tree) | `"model.fallen." + species + [".animatedfalling"] + ".seasoncycle"` — template value ignored entirely |
| Dragon armour | `template model + material string + dragon color name` |
| Template 386 (barrel/liquid container) | Model name from the contained liquid's `realTemplate`, not the barrel's own template |
| Template 854 | `template model + auxData value` |
| Template 177 (book) | `template model + contained item name + material string` |
| All other items | `template.getModelName()` verbatim |

For custom items that don't match any of these special cases, the template's `modelName` is sent to the client exactly as defined.

> **Stub** — model path format and naming conventions (e.g. `model.weapon.swordlong.`) require client-side investigation to document fully.

---

### `difficulty` · Float · Required for creation

Maps to `private final float difficulty` on `ItemTemplate` (ItemTemplate.java:71). Returned unchanged by `getDifficulty()` (ItemTemplate.java:860–861).

`difficulty` is passed directly as the `difficulty` argument to `skillCheck()` in every crafting context that uses the item as the target:

- **Creation** — SimpleCreationEntry.java:360, CreationEntry.java:400, AdvancedCreationEntry.java:503
- **Improvement** — AdvancedCreationEntry.java:559, 1196
- **Repair** — Item.java:2299
- **Cooking recipes** — Recipe.java:369 (result item's difficulty is the base, optionally combined with the target item's difficulty)

Higher difficulty makes creation, improvement, and repair all harder — a player needs higher skill to achieve the same success rate and output quality.

**Vanilla reference values:**

| Item | Difficulty | Notes |
|---|---|---|
| Rope, Kindling | 1.0 | Simplest craftable items |
| Plank, Shaft | 3.0 | Basic processed wood |
| Rake | 10.0 | Simple tools |
| Hatchet | 11.0 | — |
| Shovel, Saw | 20.0 | Mid-range tools |
| Satchel | 20.0 | — |
| Metal brush | 25.0 | — |
| Mooring rope | 30.0 | Specialty items |
| Cordage rope | 40.0 | — |
| Halter rope | 70.0 | — |
| Steel glove, body parts | 200.0 | Effectively uncraftable by normal means |

Items at `200.0` are not intended to be player-crafted — the value blocks practical creation success.

---

### `weight` · Integer · Required for creation

Starting weight of every item instance created from this template, in grams. Maps to `private final int weight` on `ItemTemplate` (ItemTemplate.java:73). Returned by `getWeightGrams()` (ItemTemplate.java:2281–2282).

When a new `Item` is instantiated, its own weight field is initialized directly from this value (Item.java:393). An item's weight can change over its lifetime (combining, consuming, splitting, etc.), but `template.getWeightGrams()` always returns the original template value.

**Automatic `fragmentAmount` side effect (ItemTemplate.java:703–704):** if `weight > 2000`, `fragmentAmount` is automatically set to `max(3, weight / 750)`, capped at 127. Items at or under 2000g keep the default fragment count of 3. This means the `fragmentAmount` field is effectively overridden by `weight` for heavier items — explicitly setting `fragmentAmount` on a heavy item will be ignored in favour of this formula.

**Vanilla reference values:**

| Item | Weight (g) |
|---|---|
| Body parts | 0 |
| Wheat / grain crops | 300 |
| Rope | 500 |
| Shaft | 1000 |
| Kindling | 1500 |
| Plank, Shovel | 2000 |
| Hatchet | 2500 |
| Log | 24000 |

---

### `material` · Integer · Required for creation

The default material of items created from this template. Maps to `private final byte material` on `ItemTemplate` (ItemTemplate.java:74). Returned by `getMaterial()` (ItemTemplate.java:837–838).

Item instances have their own `material` field (Item.java:395) initialized at creation time from the template value. `Item.getMaterial()` reads from the instance field, not the template — the two can differ (e.g., a sword template has `MATERIAL_IRON` as its default, but a specific instance smithed from steel will carry `MATERIAL_STEEL` on the instance). The template value is the fallback when no override is provided at creation.

The material byte drives every material-specific system: decay modifiers, damage modifiers, model name suffix construction, parry bonuses, repair tool requirements, and more.

**Common vanilla assignments:**

| Material | ID | Example items |
|---|---|---|
| `MATERIAL_UNDEFINED` | 0 | Generic containers — material resolved at runtime from `realTemplate` |
| `MATERIAL_STEEL` | 9 | Steel glove |
| `MATERIAL_IRON` | 11 | Hatchet, shovel, saw |
| `MATERIAL_LEATHER` | 16 | Satchel |
| `MATERIAL_COTTON` | 17 | Rags, cloth items |
| `MATERIAL_MAGIC` | 21 | Potions |
| `MATERIAL_VEGETARIAN` | 22 | Crops, food items |
| Wood variants | 14–68 range | Shaft, plank, log (species-specific) |

`MATERIAL_UNDEFINED` (0) is a sentinel for items whose effective material is determined by their contents rather than fixed at the template level.

→ See [[Item Materials]] for the full ID table.

---

### `value` · Integer · Required for creation

Base coin value in **iron coins**. Maps to `private final int value` on `ItemTemplate` (ItemTemplate.java:75). Returned by `getValue()` (ItemTemplate.java:795–796).

Coin denominations: 100 iron = 1 silver, 10,000 iron = 1 gold.

The template value is the base. `Item.getValue()` (Item.java:2359–2380) computes the effective runtime value:

| Item type | Formula |
|---|---|
| Coins and full-price items | `template.getValue()` — no scaling |
| Combinable / bulk items | `(weight / template.weight) × template.value × QL² / 10000 × (100 − damage) / 100` |
| All other items | `template.value × QL² / 10000 × (100 − damage) / 100` |

QL scaling is **quadratic** — a 50QL item is worth 25% of a 100QL item, not 50%. Damage then linearly reduces the result. If `priceAffectedByMaterial` is set on the template, the result is multiplied by a material price modifier. If rarity > 0, the result is multiplied by the rarity value directly.

The effective value is used for bank transactions (Bank.java:134), trade valuation (TradeHandler.java:215/248), shop pricing (Shop.java:262/271), and sacrifice value (MethodsReligion.java:799). A template `value > 5000` also triggers a discard-confirmation prompt (Item.java:12819).

**Vanilla reference values:**

| Item | value (iron) |
|---|---|
| Wheat / crop seeds | 10 |
| Log | 20 |
| Satchel | 200 |
| Shovel | 100 |
| Rope | 1,000 |
| Steel glove, potion | 10,000 |

---

### `isPurchased` · Boolean · Required for creation

Marks the item as part of the NPC kingdom trader economy. Maps to `private final boolean isPurchased` on `ItemTemplate` (ItemTemplate.java:76). Returned by `isPurchased()` (ItemTemplate.java:829–830).

**Three concrete effects when `true`:**

1. **Supply/demand tracking** (LocalSupplyDemand.java:158) — only `isPurchased = true` templates are registered in the local supply/demand system. Items without this flag are invisible to the economy pricing model entirely.
2. **Trader acceptance** (TradeHandler.java:395) — when a player offers items to a trader, only items where `isPurchased()` returns `true` (and the item is not an artifact and not locked) are accepted and purchased by the trader.
3. **NPC inventory restock exclusion** (TradingWindow.java:694) — when a non-player shop owner receives an item with `isPurchased = false` in a non-personal shop, it spawns a fresh copy of that item into its inventory instead of keeping the traded instance. Items with `isPurchased = true` do not trigger this path.

Setting `isPurchased = false` means traders will not buy the item and it does not participate in supply/demand pricing. Setting it to `true` without a meaningful `value` will result in the item being accepted by traders for 0 coins.

---

### `descriptions` · Object · Optional for creation

Optional for creation (builder defaults to "superb" / "good" / "ok" / "poor" / ""). Required if you want meaningful text.

`superb`, `normal`, `bad`, and `rotten` are short quality descriptors incorporated into the item's displayed name (e.g. "a good longsword", "a poor longsword"). `long` is the full examine text shown in the event window when a player examines the item.

```json
"descriptions": {
  "superb": "superb",
  "normal": "good",
  "bad":    "ok",
  "rotten": "poor",
  "long":   "A long and slender sword."
}
```

| JSON Key | ItemTemplate Field | Used as |
|---|---|---|
| `descriptions.superb` | `itemDescriptionSuperb` | Quality descriptor for high-quality items |
| `descriptions.normal` | `itemDescriptionNormal` | Quality descriptor for average items |
| `descriptions.bad` | `itemDescriptionBad` | Quality descriptor for low-quality items |
| `descriptions.rotten` | `itemDescriptionRotten` | Quality descriptor for damaged/decayed items |
| `descriptions.long` | `itemDescriptionLong` | Full examine text shown in the event window |

---

### `dimensions` · Object · Optional for creation

Physical size in centimeters. Defaults to `0, 0, 0` on creation if omitted. Maps to three separate `private final int` fields on `ItemTemplate`: `centimetersX`, `centimetersY`, `centimetersZ` (ItemTemplate.java:40–42). Accessors: `getSizeX()`, `getSizeY()`, `getSizeZ()` (ItemTemplate.java:2050–2073).

```json
"dimensions": { "x": 5, "y": 80, "z": 1 }
```

**Sorting behaviour:** the three values are sorted ascending at construction (ItemTemplate.java:676–680) — `x` is always stored as the smallest, `z` always as the largest. The order you supply them in does not matter. `{ "x": 60, "y": 3, "z": 10 }` is stored identically to `{ "x": 3, "y": 10, "z": 60 }`.

`volume = x * y * z` is computed at construction and stored separately (ItemTemplate.java:681).

**Two roles:**

1. **As an item being placed inside a container:** `Item.getVolume()` (Item.java:3716–3730) returns `x * y * z` (with optional rune modifier). This is the space the item occupies, checked against the free volume of the target container. For liquids, volume equals weight in grams instead. When placing into a liquid container specifically, each axis is checked individually (Item.java:2931) — the container must be larger than the item in all three dimensions, not just by total volume.

2. **As a container holding other items:** by default the container's usable internal volume equals its own `x * y * z`. This can be overridden independently via the `containerSize` field — see the [[#`containerSize` · Object · Optional]] section.

**Vanilla reference values:**

| Item | x | y | z | Volume (cm³) |
|---|---|---|---|---|
| Hatchet | 3 | 10 | 60 | 1,800 |
| Shaft | 3 | 7 | 100 | 2,100 |
| Plank | 3 | 5 | 200 | 3,000 |
| Shovel | 2 | 20 | 100 | 4,000 |
| Satchel | 20 | 30 | 30 | 18,000 |
| Log | 20 | 20 | 200 | 80,000 |

---

### `bodySpaces` · int[] · Required for creation

The set of body slots the item is valid for. Maps to `private final byte[] bodySpaces` on `ItemTemplate` (ItemTemplate.java:68). Delivered as `int[]` in JSON and converted to `byte[]` on apply. Accessor: `getBodySpaces()` (ItemTemplate.java:2265–2266), delegated through `Item.getBodySpaces()` (Item.java:10094–10095).

An empty array `[]` means the item cannot be equipped anywhere. Multiple entries mean the item is valid for any of the listed slots — used both for items that fit in either of two symmetric slots (e.g., either hand) and for armour that covers multiple body zones simultaneously.

When a player attempts to equip an item, the code iterates this array and checks whether any entry matches the target body part's slot ID (Item.java:2605–2606).

**Body zone IDs (wound/coverage zones, 0–34) — `BodyTemplate.java`:**

| ID | Name | ID | Name |
|---|---|---|---|
| 0 | `body` | 18 | `leftEye` |
| 1 | `head` | 19 | `rightEye` |
| 2 | `torso` | 20 | `centerEye` |
| 3 | `leftArm` | 21 | `chest` |
| 4 | `rightArm` | 22 | `topBack` |
| 5 | `leftOverArm` | 23 | `stomach` |
| 6 | `rightOverArm` | 24 | `lowerBack` |
| 7 | `leftThigh` | 25 | `crotch` |
| 8 | `rightThigh` | 26 | `leftShoulder` |
| 9 | `leftUnderArm` | 27 | `rightShoulder` |
| 10 | `rightUnderArm` | 28 | `secondHead` |
| 11 | `leftCalf` | 29 | `face` |
| 12 | `rightCalf` | 30 | `leftLeg` |
| 13 | `leftHand` | 31 | `rightLeg` |
| 14 | `rightHand` | 32 | `hip` |
| 15 | `leftFoot` | 33 | `baseOfNose` |
| 16 | `rightFoot` | 34 | `legs` |
| 17 | `neck` | | |

**Equipment slot IDs (dedicated wear slots, 35–48) — `BodyTemplate.java`:**

| ID | Name | ID | Name |
|---|---|---|---|
| 35 | `tabardSlot` | 42 | `backSlot` |
| 36 | `neckSlot` | 43 | `beltSlot` |
| 37 | `lHeldSlot` | 44 | `shieldSlot` |
| 38 | `rHeldSlot` | 45 | `capeSlot` |
| 39 | `lRingSlot` | 46 | `lShoulderSlot` |
| 40 | `rRingSlot` | 47 | `rShoulderSlot` |
| 41 | `quiverSlot` | 48 | `inventory` |

**Vanilla examples:**

| Item | bodySpaces | Meaning |
|---|---|---|
| Hatchet, log, food | `[]` | Not equippable |
| Satchel | `[2, 42]` | Torso zone or back slot |
| Steel glove | `[13, 14]` | Either hand (left or right) |
| Plate leg armour | `[7, 8, 11, 12, 30, 31]` | All leg zones |
| Long sword | `[37, 38]` | Either held slot |
| Shield | `[44]` | Shield slot only |

---

### `itemTypes` · int[] · Required for creation

A set of type tag integers that define what the item is and what can be done with it. Delivered as `int[]` in JSON and converted to `short[]` before being passed to `assignTypes()` (ItemTemplate.java:700, 904).

**The array is not stored as a field.** It is consumed immediately at construction — `assignTypes()` iterates the array and each `short` maps to a switch case that flips one or more boolean fields on the template (`hollow`, `weapon`, `weaponslash`, `shield`, `armour`, `food`, `magic`, etc.). After construction, the array is discarded. What persists are the resulting booleans, which power every `isFood()`, `isHollow()`, `isWeapon()`, `isArmour()` and similar checks across the codebase.

The array is order-independent and additive. Passing `[1, 5]` makes the item both hollow and food. There is no conflict detection — contradictory combinations are accepted without error.

Vanilla example — satchel: `[44, 24, 1, 92, 147]` — each number sets one or more booleans defining what the satchel is and which actions are available on it.

→ See [[Item Types]] for the full constant table.

---

## Optional Fields

Include only the fields relevant to your item. All optional fields are omittable on both the creation and modification paths.

---

### `containerSize` · Object · Optional

Sets the internal capacity of a container independently from its external physical size. **This field has no effect on its own** — it must be paired with `itemType 180` in the `itemTypes` array. Both must be present together.

```json
"containerSize": { "x": 10, "y": 10, "z": 10 }
```

**How the two parts interact:**

- **`itemType 180`** (ItemTemplate.java:1673–1675) sets the boolean `usesSpecifiedContainerSizes = true`. This causes `getContainerSizeX/Y/Z()` and `getContainerVolume()` to return the separately stored container dimensions instead of the item's own physical dimensions.
- **`containerSize` object** calls `setContainerSize(x, y, z)` (ItemTemplate.java:2083–2091), which stores the internal dimensions (sorted ascending, same as physical dimensions) and computes `containerVolume = x * y * z`.

Without `itemType 180`, `getContainerVolume()` always falls back to the item's own `getVolume()` regardless of whether `containerSize` was set. Without `containerSize`, `itemType 180` activates the flag but has no dimensions to return.

The values are sorted ascending at storage — the same behaviour as `dimensions`. Input order does not matter.

**Vanilla examples:**

| Item | Physical dims | Container dims |
|---|---|---|
| Forge | 82 × 122 × 390 | 41 × 61 × 210 |
| Stone oven | 100 × 121 × 390 | 45 × 45 × 210 |
| Square table | 10 × 60 × 60 | 15 × 60 × 60 |
| Round table | 10 × 60 × 60 | 40 × 150 × 150 |
| Dining table | 10 × 60 × 250 | 40 × 100 × 220 |
| Bed | 60 × 60 × 200 | 40 × 60 × 200 |
| Large anvil | 30 × 30 × 50 | 40 × 80 × 150 |

---

### `maxItemCount` · Integer · Optional

Maximum number of items the container can hold. Maps to `int maxItemCount` on `ItemTemplate` (ItemTemplate.java:342). Default: `-1` (no limit). Set via `setMaxItemCount(int)` (ItemTemplate.java:3066–3081).

A value of `-1` means the constraint is not enforced. Any value ≥ 0 is enforced.

Both `maxItemCount` and `maxItemWeight` are checked together in `Item.canHold()` (Item.java:15645–15646) — see `maxItemWeight` below for the full check.

---

### `maxItemWeight` · Integer · Optional

Maximum total weight of contents the container can hold, in grams. Maps to `int maxItemWeight` on `ItemTemplate` (ItemTemplate.java:343). Default: `-1` (no limit). Set via `setMaxItemWeight(int)` (ItemTemplate.java:3066–3081).

A value of `-1` means the constraint is not enforced. The two fields are independent — either, both, or neither can be set.

**Enforcement — `Item.canHold()` (Item.java:15645–15646):**

```java
return (this.getMaxItemCount() <= -1 || this.getItemCount() < this.getMaxItemCount())
    && (this.getMaxItemWeight() <= -1 || this.getFullWeight() - this.getWeightGrams() + target.getFullWeight() <= this.getMaxItemWeight());
```

`maxItemWeight` is checked against the container's current contents weight (excluding the container's own tare weight) plus the incoming item's full weight.

**Special case — template 1295 (food compartment):** `getMaxItemCount/Weight()` reads limits from the outer parent container rather than its own template (Item.java:15626–15641), so nested sub-containers can inherit the parent's constraints.

**Vanilla examples:**

| Item | maxItemCount | maxItemWeight |
|---|---|---|
| Lunchbox (1296) | 3 | 2,000g |
| Picnic basket (1297) | 3 | 2,200g |
| Alchemist's cupboard (1117) | 11 | (none) |
| Storage unit (1119) | 6 | (none) |
| Most containers | (none) | (none) |

---

### `nutrition` · Object · Optional

Explicit nutrition values for the item when consumed as food. Calls `setNutritionValues(calories, carbs, fats, proteins)` (ItemTemplate.java:2139–2146), which stores all four values as `short` fields and flips the internal `calcNutritionValues` flag to `false`.

```json
"nutrition": { "calories": 500, "carbs": 60, "fats": 20, "proteins": 30 }
```

**The nutrition system has two independent parts:**

**1. Nutrition tier — set via `itemTypes`, not this field:**

| itemType | Flag | Notes |
|---|---|---|
| 137 | `nonutrition` | Eaten but provides no benefit (salt, spice, etc.) |
| 55 | `lownutrition` | Low tier |
| 74 | `mediumnutrition` | Medium tier |
| 75 | `goodnutrition` | Good tier |
| 76 | `highnutrition` | High tier; also increases decay speed (Item.java:10774–10782) — high-nutrition food rots faster than lower-tier food |

These flags are mutually exclusive and are consumed by `assignTypes()` at construction. They control how the game classifies the item in the hunger/nutrition UI and in `getDamageModifier()`.

**2. Nutrition values — set by this `nutrition` object:**

| Sub-field | Default | Type |
|---|---|---|
| `calories` | 1000 | short |
| `carbs` | 150 | short |
| `fats` | 40 | short |
| `proteins` | 25 | short |

These defaults are baked into the `ItemTemplate` constructor. Omitting the `nutrition` object entirely leaves these defaults in place and keeps `calcNutritionValues = true` (see below).

**Runtime value resolution (Item.java:15212–15311):**

All four getters apply QL and rarity scaling first:

```java
percentage = currentQL / 100.0 * (1 + rarity² × 0.1)
```

A QL50 common item returns 50% of its nutrition values. A QL100 rare item returns 110%.

Then:

- If `calcNutritionValues == false` (explicit values set via this field): returns `template.value × percentage`
- If `calcNutritionValues == true` (default): checks `ItemMealData` first — populated for dynamically cooked dishes. If found, uses that data × percentage. Otherwise falls back to `template.value × percentage`.

**When to use this field:**

- **Non-cooked food with fixed nutrition** — set explicit values. The stored template values are the QL-100 baseline; the game scales them down at runtime per actual QL.
- **Cooked dishes (cooking recipe outputs)** — omit this field. Leave `calcNutritionValues = true` so `ItemMealData` can override at runtime with recipe-derived values.
- **Items that are eaten but nutritionally inert** (salt, spice) — use `itemType 137` (`nonutrition`) in `itemTypes`. The nutrition values are irrelevant in this case.

---

### `dyeAmountGrams` · Integer · Optional

Grams of dye required to paint the item's **primary color zone**. Default: `0`. Calls `setDyeAmountGrams(int primary)` (single-arg form) or `setDyeAmountGrams(int primary, int secondary)` (two-arg form, used when `dyeSecondaryAmountRequired` is also set).

**When `dyeAmountGrams == 0`:** the required amount is auto-derived from the item's physical dimensions at paint time (MethodsItems.java:7104–7118):

```java
surfaceArea = 2 * (X*Y + Y*Z + X*Z)   // cm², using item dimensions
dyeNeeded   = Math.max(1, surfaceArea / 25)
```

Action time is `Math.max(50, dyeNeeded / 50)` ticks.

**When `dyeAmountGrams > 0`:** the geometric calculation is bypassed and this value is used directly as the required amount.

Only set this explicitly when the auto-formula would be wrong — large flat items that don't need proportional dye, unusually shaped items, or items with a distinct secondary paintable component.

**Vanilla examples:**

| Item | Primary | Dimensions |
|---|---|---|
| Oil lamp (5×5×10 cm) | 20g | Small — explicit override, geometric formula would give ~4g |
| Metal torch / street lamp | 100g | — |
| Small crate (100×100×100 cm) | 1,500g | — |
| Large crate (120×120×120 cm) | 2,500g | — |
| Cart / rowing boat | 5,000g | — |
| Ship transporter | 15,000g | — |
| Colossus (500×500×2000 cm) | 65,000g | Max seen in vanilla |
| Buoy | 40g | Has secondary zone — see `secondaryItemName` |

---

### `secondaryItemName` · String · Optional

Display name for a visually distinct sub-component that supports a second, independent dye color — for example the seat of a chair or the sail of a ship. Maps to `String secondaryItemName`, default `""`.

**Dependency: `itemType 249` (`supportsSecondryColor`) is required.** Without it, no secondary dye action is exposed in the UI, even if a name is set.

Calls one of two typo-preserving setters:

| Setter | Effect |
|---|---|
| `setSecondryItem(String name)` | Sets name; zeroes `dyeSecondaryAmountRequired` |
| `setSecondryItem(String name, int grams)` | Sets name and secondary dye gram requirement together |

Use the two-argument form only when the secondary zone has a non-geometric dye cost (e.g., the buoy's lamp at 312g). Use the single-argument form when the geometry formula should handle it.

**Where the name is used:**

| Context | Usage |
|---|---|
| Right-click dye menu | `"Dye " + secondaryItemName` — raw string, case-sensitive |
| Dye action messages | `"already has colour on it's [name]."` / `"You start to dye the [item]'s [name]."` |
| Item inspect/description | `LoginHandler.raiseFirstLetter(name) + color string` — first letter always capitalized |

The inspect path capitalizes the first letter regardless of the stored value; the action menu uses the raw string. Vanilla is inconsistent — armchair stores `"seat"` (lowercase), fine high chair stores `"Seat"` (capitalized).

**Vanilla examples:**

| Item | `secondaryItemName` | Explicit secondary dye cost |
|---|---|---|
| Armchair | `"seat"` | — (geometry) |
| Fine high chair, royal lounge chaise | `"Seat"` | — (geometry) |
| Bed | `"covers"` | — (geometry) |
| Cog, sailing ships | `"sail"` | — (geometry) |
| Buoy | `"lamp"` | 312g |

---

### `dyeSecondaryAmountRequired` · Integer · Optional

Grams of dye required for the **secondary color zone**. Always paired with `secondaryItemName`. Passed as the second argument to `setSecondryItem(name, secondary)`.

**Runtime resolution (MethodsItems.java:7104–7118):**

When a player dyes the secondary zone:

- If `dyeSecondaryAmountRequired > 0`: uses this value directly
- If `dyeSecondaryAmountRequired == 0` but `dyeAmountGrams > 0`: applies `(int)(dyeAmountGrams * 0.3f)`. **Note:** `(int)0.3f` truncates to `0` in Java — this is a Wurm bug. If the primary amount is set but secondary is zero, the result is `0`, which falls through to the geometry formula below
- If result is `0`: falls back to the geometry formula (`Math.max(1, surfaceArea / 25)`) using the item's own dimensions

**Vanilla example — buoy:** primary zone `40g`, secondary zone (`"lamp"`) `312g`.

---

### `fragmentAmount` · Integer · Optional

Number of fragment pieces (template ID 1307) that must be found and assembled via archaeology to reconstruct this item. Default: `3`. Hard cap: `127` — the setter enforces this silently.

**Auto-assignment at construction (already documented in `weight`):**

If `weight > 2000g`, the constructor overrides the default immediately after `assignTypes()`:

```java
fragmentAmount = min(127, max(3, weight / 750))
```

| Template weight | Auto fragment count |
|---|---|
| ≤ 2000g | 3 (default, untouched) |
| 3,000g | 4 |
| 10,000g | 13 |
| 75,000g | 100 |
| ≥ 100,000g | 127 (cap) |

`setFragmentAmount(int)` can override this after construction and applies the same 127 cap.

**Runtime role (ItemBehaviour.java:8015–8108):**

Each archaeology find is a fragment instance of template 1307 with two values stored on the item itself:

- `auxData` — how many fragment units this piece represents (starts at 1 per freshly dug piece; accumulates as pieces are combined)
- `getRealTemplate()` — pointer back to the target template being reconstructed

When two fragments are combined: `source.auxData + target.auxData >= fragmentAmount` completes the item. If the sum overshoots, the leftover is kept as a new fragment with `auxData = newTotal - totalNeeded`.

**Fragment weight per piece:** `template.getWeightGrams() / fragmentAmount`. A fragment's actual weight scales with its `auxData` count.

**Fragment display name (Item.java:663):** `[auxData/fragmentAmount]` — e.g. `[2/5]` means this piece represents 2 of the 5 needed.

**Combine difficulty:** `min(90, baseDifficulty * (piecesHeld / total))` — gets harder as the item nears completion.

Higher `fragmentAmount` values mean more individual digs required, lighter pieces, and a longer assembly process. Lower values (minimum 3) make the item quick to reconstruct.

---

### `alcoholStrength` · Integer · Optional

Alcohol potency for beverage items. Default: `0`. Calls `setAlcoholStrength(int)`, which sets the value **and simultaneously sets the `isAlcohol` boolean flag to `true`**. There is no separate `itemType` for alcohol — this setter is the only way to mark an item as alcoholic.

**Intoxication formula (MethodsItems.java:3763–3769):**

```java
drinkMod = 1.0 + (currentQL × 0.005)
if (weightGrams < 200): drinkMod *= weightGrams / 200.0   // partial container penalty
addAlcohol = alcoholStrength × 0.2 × drinkMod
```

The result is added to the player's alcohol float (range 0–100, hard-capped at 100). QL100 drinks are ×1.5 as potent as QL1. Drinking from a container with less than 200g remaining applies a proportional reduction.

**Intoxication thresholds:**

| Player alcohol level | Effect |
|---|---|
| ≥ 10 | "You are tipsy." |
| ≥ 20 | "You are getting drunk." + giggle broadcast |
| ≥ 30 | "You are drunk." |
| ≥ 60 | "You are really really drunk." |
| ≥ 90 | "You can barely walk." + movement drunk modifier |
| ≥ 95 | "You are setting some kind of record." |
| = 100 | "Perfectly drunk" — Drunkard title awarded |

**Vanilla reference values:**

| Drink | `alcoholStrength` |
|---|---|
| Beer | 4 |
| Mead | 6 |
| Cider | 8 |
| White wine | 11 |
| Red wine | 13 |
| Rice wine | 15 |
| Gin | 20 |
| Rum | 22 |
| Vodka | 30 |
| Whisky | 35 |
| Brandy | 35 |
| Moonshine | 40 |

The value is a relative potency unit with no real-world ABV meaning. Beer (4) takes several drinks to get tipsy at any QL. Moonshine (40) at QL100 from a full container can push a player past 90 in a single drink.

---

### `foodGroup` · Integer · Optional

Assigns this item to a food group category for use as a substitutable cooking ingredient. Backs the `inFoodGroup` field on `ItemTemplate`, default `0`. Set via `setFoodGroup(int foodGroupTemplateId)`.

**`getFoodGroup()` fallback behavior:**

```java
public int getFoodGroup() {
    if (this.inFoodGroup > 0) return this.inFoodGroup;
    return this.getTemplateId();   // every item is its own group by default
}
```

If `foodGroup` is not set, the item can still be matched exactly by its own template ID in recipes — it just doesn't belong to any shared group.

**How food groups work:**

Recipes can specify either an exact template ID or a food group anchor (a template with `itemType 208`, `isFoodGroup = true`). When a recipe slot references an anchor, the system accepts any item whose `getFoodGroup()` returns that anchor's template ID. This is how "any cereal" or "any veg" recipe slots work — they match any member that has been assigned to the group.

**Anchor templates (all use `itemType 207 + 208`):**

| Template ID | Anchor name |
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

**Vanilla member examples:**

| Items | Assigned group |
|---|---|
| Wheat, barley, rye, oat | 1157 (any cereal) |
| Corn, potato, pumpkin | 1156 (any veg) |
| All mushrooms | 1199 (any mushroom) |
| All cheeses | 1198 (any cheese) |
| Blueberry, lingonberry | 1179 (any berry) |
| Cooked meat | 1261 (any meat) |
| Rosemary, nettles | 1158 (any herb) |
| All flower bouquets | 1267 (any flower) |

**For custom definitions:**

- Assign to an existing group with `setFoodGroup(groupId)` to make the item substitutable in any recipe that accepts that group.
- Omit entirely if the item should only ever be matched as a specific named ingredient.
- To create a new food group category, define an anchor template with `itemType 207 + 208`, then point member items at it with `setFoodGroup(anchorTemplateId)`.

---

### `crushsTo` · Integer · Optional

Template ID of the item produced when this item is crushed. Default: `0`. Set via `setCrushsTo(int toTemplateId)`. Note: the field name preserves the vanilla Wurm spelling.

When non-zero, enables the "Crush" action (action ID 54) on the item.

**Crush mechanic (VegetableBehaviour.java:83–151):**

Output count is derived from how many template-weight units the item currently weighs:

```java
nums = item.getWeightGrams() / template.getWeightGrams()
```

Each output is created at the same QL as the source and inserted into the performer's inventory. The source item's weight is reduced by one `templateWeight` per iteration until depleted. If inventory is full mid-crush, the remaining source weight is left intact and the partial count is returned.

**One hardcoded special case:** if `crushsTo == 745` (reed fibre), each output is explicitly set to 100g regardless of the output template's default weight.

**Crushing is blocked if:**

- The item is not owned by the performer
- The item is flagged `isProcessedFood()`
- `crushsTo == 0`

**Vanilla examples:**

| Source item | Template weight | `crushsTo` | Output |
|---|---|---|---|
| Pumpkin | 1,000g | 34 | Pumpkin seed |
| Wemp plants | 700g | 318 | Wemp fibre |
| Reed plants | 500g | 745 | Reed fibre (forced to 100g each) |
| Fennel plant | 300g | 1131 | Fennel |
| Sugar beet | 1,000g | 1139 | Sugar |

The yield scales with how much the player holds — a stack weighing 2× the template weight yields 2 outputs. Fractional remainders (partial units below one `templateWeight`) are left on the source item with no output for that last unit.

---

### `pickSeeds` · Integer · Optional

Template ID of the seed produced when this item is hand-picked. Default: `0`. Set via `setPickSeeds(int seedTemplateId)`.

When non-zero, enables the "Pick" action (action ID 55) on the item.

**Mechanic:**

Uses the identical `crush()` method as `crushsTo` (VegetableBehaviour.java:122–151) — same yield formula, same QL transfer, same inventory-space guard:

```java
nums = item.getWeightGrams() / template.getWeightGrams()
```

Each output is created at the source item's QL, source weight is decremented by one `templateWeight` per output, and the source is consumed entirely when weight hits zero.

**Differences from `crushsTo`:**

- Action ID 55 (vs. 54 for crush)
- Player messages: `"You pick the [item] for seeds, ruining it."` / `"The [item] contains almost no seeds."` (zero yield)
- The reed fibre weight override (`templateId == 745` → 100g) does **not** apply — it is hardcoded to the crush path only

**Coexistence with `crushsTo`:** both fields are independent and can be set on the same template. All four vanilla plant types that use `pickSeeds` also have `crushsTo` set — picking and crushing are separate player-initiated actions on the same item.

**Vanilla examples:**

| Source item | `pickSeeds` | Seed weight | Yield from one unit |
|---|---|---|---|
| Wemp plants (700g) | 317 | 100g/unit | 7 seeds |
| Reed plants (500g) | 744 | 100g/unit | 5 seeds |
| Fennel plant (300g) | 1151 | 50g/unit | 6 seeds |
| Sugar beet (1,000g) | 1148 | 50g/unit | 20 seeds |

Yield is weight-driven — ensure the source template weight and seed template weight produce a sensible ratio.

---

### `grows` · Integer · Optional

Template ID of the plant this seed or cutting produces when grown in a planter pot. Default: `0`. Set via `setGrows(int growsTemplateId)`.

**`getGrows()` fallback behavior:**

```java
public int getGrows() {
    if (this.grows == 0) return this.templateId;  // self-referential fallback
    return this.grows;
}
```

When `grows == 0`, the item is considered to grow into its own template. Omit this field only if the seed and the resulting plant share the same template.

**Scope:** used exclusively by `PlanterBehaviour` (planter pots). When a seed is successfully potted, the planter pot's `realTemplate` is set to `seed.getTemplate().getGrows()`. Does not affect ground-tile crops, which use a separate tile-based system.

**Vanilla examples:**

| Seed template | `grows` | Plant produced |
|---|---|---|
| Fennel seed (1151) | 1132 | Fennel plant |
| Paprika seed (1153) | 1143 | Paprika plant |
| Turmeric seed (1154) | 1144 | Turmeric plant |

---

### `harvestsTo` · Integer · Optional

Template ID of the item produced when a player harvests a planted trellis. Default: `0`. Set via `setHarvestsTo(int toTemplateId)`.

**`setHarvestsTo()` simultaneously sets `isHarvestable = true`**, which is the flag `TrellisBehaviour` checks to expose the Harvest action. There is no separate `itemType` for this — calling `setHarvestsTo()` is the only way to mark a trellis as harvestable.

**Scope:** used exclusively by `TrellisBehaviour` (planted trellis structures). Has no effect on planter pot items or ground-tile crops.

**One hardcoded special case (TrellisBehaviour.java:134–140):** if `harvestsTo == 411` (blue grapes), the output is overridden to `414` (green grapes) for tiles in the northern half of the map. This is baked into the behaviour class, not the template — setting `harvestsTo = 411` on a custom trellis inherits this geographic split automatically.

**Vanilla examples:**

| Trellis template | `harvestsTo` | Harvest output |
|---|---|---|
| Grape trellis (920) | 411 | Blue grapes (green grapes on north half of map) |
| Rose trellis (1018) | 426 | Rose flower |
| Hops trellis (1274) | 1273 | Hops |

---

## `weapon` Object · Optional

Weapon-specific stats registered via `WeaponCreator` and read by the **new combat path** (`Weapon.getModifiedDamageForWeapon()`, called from `CombatHandler`). Only relevant for items with weapon-type `itemTypes` flags.

This is a parallel system to `combatDamage`. The two paths are not mutually exclusive — both can be active on the same item.

| Aspect | `combatDamage` (old path) | `weapon.damage` (new path) |
|---|---|---|
| Where defined | `ItemTemplate` constructor | `WeaponCreator.createWeapons()` |
| Read by | `CombatEngine.getWeaponDamage()` | `Weapon.getModifiedDamageForWeapon()` |
| QL interaction | `QL * combatDamage / 10000` (additive) | `damage * QL / 100` (multiplicative) |
| Strength interaction | `1.0 + strength / 100.0` | `1.0 + strength / strengthModifier` (300 or 1000) |
| Material modifiers | Not present | Via `getMaterialDamageBonus()` etc. |
| Body part handling | Routes to `creature.getCombatDamage()` | Skips QL scaling, uses base damage only |

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

**Vanilla weapon reference table:**

| Name | Template ID | damage | speed | critChance | reach | weightGroup | parryPercent | skillPenalty | damagedByMetal |
|---|---|---|---|---|---|---|---|---|---|
| Awl | 390 | 1.0 | 3.0 | 0.0 | 1 | 1 | 0.0 | 2.0 | — |
| Leather knife | 392 | 0.5 | 2.0 | 0.0 | 1 | 1 | 0.0 | 2.0 | — |
| Carving knife | 8 | 1.0 | 2.0 | 0.0 | 1 | 1 | 1.0 | 2.0 | — |
| Butchering knife | 93 | 1.5 | 2.0 | 0.0 | 1 | 1 | 1.0 | 1.0 | — |
| Saddler's knife | 792 | 1.5 | 2.0 | 0.03 | 1 | 1 | 1.0 | 1.0 | — |
| Crude knife | 685 | 1.0 | 4.0 | 0.0 | 1 | 1 | 0.0 | 3.0 | — |
| Crude pickaxe | 687 | 1.0 | 6.0 | 0.0 | 1 | 1 | 0.0 | 5.0 | — |
| Crude shaft | 691 | 1.0 | 3.0 | 0.0 | 1 | 1 | 0.0 | 5.0 | — |
| Crude shovel | 690 | 1.0 | 6.0 | 0.0 | 1 | 1 | 0.0 | 5.0 | — |
| Branch | 688 | 1.0 | 6.0 | 0.0 | 1 | 1 | 0.0 | 3.0 | — |
| Crude axe | 1011 | 1.0 | 5.0 | 0.0 | 1 | 1 | 0.0 | 5.0 | — |
| Scissors | 394 | 0.5 | 2.0 | 0.0 | 1 | 1 | 0.0 | 2.0 | — |
| Short sword | 80 | 4.0 | 3.0 | 0.10 | 2 | 1 | 1.0 | 0.0 | — |
| Long sword | 21 | 5.5 | 4.0 | 0.01 | 3 | 3 | 1.0 | 0.0 | — |
| Two-handed sword | 81 | 9.0 | 5.0 | 0.05 | 4 | 5 | 1.0 | 0.0 | — |
| Magic sword | 336 | 15.0 | 5.0 | 0.08 | 4 | 3 | 1.0 | 0.0 | — |
| Small axe | 3 | 5.0 | 3.0 | 0.0 | 2 | 2 | 0.3 | 0.0 | — |
| Battle axe | 90 | 6.5 | 4.0 | 0.03 | 4 | 5 | 0.3 | 0.0 | — |
| Large axe | 87 | 12.0 | 6.0 | 0.05 | 5 | 5 | 0.2 | 0.0 | — |
| Sickle | 267 | 6.0 | 3.0 | 0.02 | 2 | 3 | 0.2 | 2.0 | — |
| Scythe | 268 | 9.0 | 5.0 | 0.08 | 5 | 4 | 0.2 | 2.0 | yes |
| Long spear | 705 | 8.0 | 5.0 | 0.06 | 7 | 3 | 1.0 | 0.0 | — |
| Halberd | 706 | 9.0 | 5.0 | 0.06 | 6 | 8 | 1.0 | 0.0 | — |
| Steel spear | 707 | 9.0 | 5.0 | 0.06 | 7 | 4 | 1.0 | 0.0 | — |
| Steel staff | 710 | 8.0 | 4.0 | 0.0 | 3 | 3 | 1.0 | 0.0 | — |
| Lander staff | 986 | 8.0 | 4.0 | 0.0 | 3 | 3 | 1.0 | 0.0 | — |
| Whip | 514 | 6.0 | 2.0 | 0.0 | 5 | 1 | 0.1 | 0.0 | — |
| Small maul | 291 | 4.5 | 3.0 | 0.01 | 2 | 2 | 1.0 | 0.0 | — |
| Medium maul | 292 | 8.0 | 5.0 | 0.03 | 3 | 2 | 1.0 | 0.0 | — |
| Large maul | 290 | 11.0 | 6.0 | 0.03 | 4 | 5 | 1.0 | 0.0 | — |
| Huge club | 314 | 8.0 | 6.0 | 0.01 | 4 | 6 | 1.0 | 2.0 | yes |
| Magic hammer | 337 | 18.0 | 6.0 | 0.08 | 4 | 4 | 1.0 | 0.0 | — |
| Sceptre | 340 | 17.0 | 6.0 | 0.08 | 3 | 3 | 1.0 | 0.0 | — |
| Staff (basic) | 711 | 2.0 | 3.0 | 0.0 | 2 | 3 | 1.0 | 0.0 | yes |
| Steel crowbar | 1115 | 4.5 | 3.0 | 0.01 | 2 | 2 | 1.0 | 0.0 | — |
| Belaying pin | 567 | 2.0 | 3.0 | 0.0 | 1 | 1 | 1.0 | 2.0 | yes |
| Fist (body part) | 14 | 1.0 | 1.0 | 0.0 | 1 | 1 | 0.0 | 2.0 | — |
| Foot (body part) | 19 | 1.0 | 2.0 | 0.0 | 1 | 1 | 0.0 | 3.0 | — |
| Small bow | 447 | 0.0 | 5.0 | 0.0 | 1 | 5 | 1.0 | 9.0 | yes |
| Medium bow | 448 | 0.0 | 5.0 | 0.0 | 1 | 5 | 1.0 | 9.0 | yes |
| Large bow | 449 | 0.0 | 5.0 | 0.0 | 1 | 5 | 1.0 | 9.0 | yes |
| Short bow (N) | 459 | 0.0 | 5.0 | 0.0 | 1 | 5 | 1.0 | 9.0 | yes |
| Medium bow (N) | 460 | 0.0 | 5.0 | 0.0 | 1 | 5 | 1.0 | 9.0 | yes |
| Large bow (N) | 461 | 0.0 | 5.0 | 0.0 | 1 | 5 | 1.0 | 9.0 | yes |
| Hatchet | 7 | 1.0 | 5.0 | 0.0 | 2 | 2 | 0.0 | 3.0 | yes |
| Pick axe | 20 | 1.5 | 5.0 | 0.0 | 3 | 3 | 0.1 | 3.0 | yes |
| Shovel | 25 | 1.0 | 5.0 | 0.0 | 4 | 3 | 1.0 | 3.0 | yes |
| Rake | 27 | 0.5 | 5.0 | 0.0 | 5 | 2 | 1.0 | 3.0 | yes |
| Saw | 24 | 0.5 | 5.0 | 0.01 | 2 | 3 | 0.0 | 3.0 | — |
| Plank | 22 | 0.5 | 4.0 | 0.0 | 2 | 1 | 1.0 | 3.0 | yes |
| Shaft | 23 | 0.5 | 4.0 | 0.0 | 2 | 2 | 1.0 | 3.0 | yes |
| Metal hammer | 62 | 0.5 | 3.0 | 0.0 | 1 | 1 | 0.1 | 3.0 | yes |
| Wooden hammer | 63 | 0.3 | 3.0 | 0.0 | 1 | 1 | 0.1 | 3.0 | yes |

---

### `weapon.damage` · Float

Base damage for the new combat path. Full formula in `getModifiedDamageForWeapon()`:

```java
damReturn = damage × (QL/100) × (1 + strength/strengthModifier) × rand[0.5, 1.0]
```

`strengthModifier` = `300.0` on Freedom, `1000.0` on Epic/Challenge (making strength much less impactful on Epic). Body parts skip QL scaling and use base `damage` directly. No server-enforced cap.

| Damage | Weapons |
|---|---|
| 0.0 | Bows (damage handled by ammunition) |
| 0.3–0.5 | Scissors, rake, leather knife, plank, shaft |
| 1.0–1.5 | Awl, crude weapons, knives, hatchet, pickaxe |
| 4.0–5.5 | Short sword, small axe, small maul, long sword |
| 6.0–9.0 | Sickle, battle axe, scythe, two-handed sword |
| 11.0–12.0 | Large maul, large axe |
| 15.0–18.0 | Magic sword, sceptre, magic hammer |

---

### `weapon.speed` · Float

Attack timer baseline. **Higher value = slower weapon.**

```java
flspeed = speed × materialMod
flspeed -= flspeed × 0.1 × (weaponSkill / 100)   // skill reduces by up to 10% at skill 100
calcspeed = flspeed + timeMod                       // timeMod: 0.5 normal strength, 1.5 low strength
// situational: +1.0 if stamina < 2000; ×2.0 if webbed/slowed
return Math.max(3.0f, calcspeed)
```

**Hard floor: 3.0** — no combination of skill, enchants, or template value can go below this. Two-handed weapons at high strength level (3) get a ×0.9 bonus (10% faster). Default for unregistered weapons: `20.0` (extremely slow — always register custom weapons).

| Speed | Weapons |
|---|---|
| 1.0 | Fist |
| 2.0 | Knives, scissors, whip |
| 3.0 | Short sword, sickle, small maul, hammers |
| 4.0–5.0 | Long sword, staff, magic sword/hammer |
| 5.0–6.0 | Pickaxe, shovel, two-handed axe, large maul |

---

### `weapon.critChance` · Float

Critical hit probability. **The registered value is divided by 5 at construction** — stored as `registeredValue / 5.0`. Runtime check:

```java
rand.nextFloat() < (storedValue) × rarityMod
```

Crits checked against **players only**, not creatures. Rarity modifiers: normal ×1.0, rare ×1.1, supreme ×1.3, fantastic ×1.5. Attacking an opponent's stance soft spot adds `+0.05` to the stored value.

No server-enforced cap. Values ≥ `5.0` as registered (stored ≥ `1.0`) guarantee a crit on every hit against a player.

| Registered | Stored | Base crit % | Examples |
|---|---|---|---|
| 0.0 | 0.0 | 0% | Awl, knives, fist, bows |
| 0.01 | 0.002 | 0.2% | Long sword, saw |
| 0.03 | 0.006 | 0.6% | Saddler's knife, battle axe |
| 0.05 | 0.01 | 1% | Two-handed sword, large axe |
| 0.08 | 0.016 | 1.6% | Scythe, magic sword/hammer, sceptre |
| 0.10 | 0.02 | 2% | Short sword |

> **Stub** — what a critical hit does mechanically (damage multiplier? special status effect?) pending investigation.

---

### `weapon.reach` · Integer

Ideal combat engagement distance. Determines how far the weapon "wants" to be from its target:

```java
idealDist = 10 + reach × 3
```

Compared against `Creature.rangeToInDec()` for positional advantage. Higher reach wants more space. Default for unregistered weapons: `1`.

| Value | Weapons |
|---|---|
| 1 | Fist, knives, awl, crude weapons, hammers |
| 2 | Short sword, small axe, sickle, hatchet |
| 3 | Long sword, pickaxe, shovel |
| 4 | Battle axe, scythe, mauls, magic hammer |
| 5 | Rake, two-handed sword, whip, bows |
| 6 | Halberd |
| 7 | Long spear, steel spear |

---

### `weapon.weightGroup` · Integer

**Dead code.** Stored and registered, but `getWeightGroupForWeapon()` has no callers in the server source. The field does not influence any combat calculation.

Default for unregistered weapons: `10`. Vanilla range: `1` (knives, light tools) to `8` (halberd). Safe to define any value without gameplay effect.

---

### `weapon.parryPercent` · Float

Direct probability used in the parry check:

```java
Weapon.getWeaponParryPercent(defParryWeapon) > Server.rand.nextFloat()
```

`1.0` = always parries when in position; `0.0` = cannot parry at all. Checked only after the attacker lands a **non-crit** hit and the defender has a valid parry weapon selected. Default for unregistered weapons: `0.0`.

| Value | Weapons |
|---|---|
| 0.0 | Awl, knife, hatchet, bows, scissors, saw |
| 0.1 | Metal/wooden hammer, pickaxe, scythe |
| 0.2 | Small axe, sickle, large axe |
| 0.3 | Battle axe |
| 1.0 | Swords, spears, mauls, staffs, magic weapons, shovel, rake, plank, shaft |

---

### `weapon.skillPenalty` · Double

Subtracted directly from the player's combat rating when wielding the weapon:

```java
combatRating -= Weapon.getSkillPenaltyForWeapon(weapon);
```

Higher values make it harder to hit enemies. Represents difficulty of effective use without training. Applied to **players only**, not creatures.

**Default for unregistered weapons: `7.0`** — notably high. Any custom weapon without a registered `weapon` block will be very inaccurate.

| Value | Weapons |
|---|---|
| 0.0 | Long sword, two-handed sword, axes, magic sword/hammer, sceptre, spears, halberd, mauls, whip |
| 1.0 | Butchering knife (crude), staff |
| 2.0 | Carving knife, awl, belaying pin, scissors, leather knife |
| 3.0 | Hatchet, pickaxe, shovel, rake, sickle, scythe, crowbar, plank, shaft, saw, hammers |
| 5.0 | Crude shovel, pickaxe, axe |
| 9.0 | All bows |

---

### `weapon.damagedByMetal` · Boolean · Default `false`

When `true`, the item takes structural damage when involved in metal-weapon parry contact — in two scenarios:

- The defender's parry weapon is `damagedByMetal` and the attacker's weapon `isMetal()` → parrying weapon takes damage
- The attacker's weapon is `damagedByMetal` and the defender's parry weapon `isMetal()` → attacker's weapon takes damage

Represents weapons not designed to withstand metal-on-metal contact. Body parts that have been severed are always treated as `damagedByMetal = true` regardless of this flag.

`true` in vanilla: all bows, hatchet, pickaxe, shovel, rake, scythe, plank, shaft, basic staff, belaying pin, metal hammer, wooden hammer, huge club, crude items.

`false` (default): all dedicated combat swords, axes, mauls, spears, magic weapons, knives.

## Implementation Notes

- **All 19 constructor fields are `private final`** — modifying vanilla templates requires a `setFinalField()` helper that strips the FINAL modifier before setting. `ReflectionUtil.setPrivateField()` does NOT strip FINAL — the custom helper is required.
- `imageNumber` is `public final short` — still requires `setFinalField()` to strip FINAL even though it is public.
- `material` is `byte` — deliver as JSON integer, cast `(byte)(int)` at apply time.
- `bodySpaces` is `byte[]` — JSON delivers `int[]`, loop-cast to `byte[]` before setting.
- `itemTypes` — JSON delivers `int[]`, convert to `short[]`, call `template.assignTypes(short[])` (public method). This sets all 167 derived boolean flags correctly.
- `difficulty` is `float` — POJO field is `Float`, call `.floatValue()` or use `(float)` cast.
- `decayTime` is `long` — POJO field is `Long` (not Integer) to avoid truncation.
- `fragmentAmount` has a public setter (`setFragmentAmount(int)`) — no reflection required.
- `secondaryItemTemplateId` does not exist on `ItemTemplate`. The setter is `setSecondryItem(String name, int dyeSecondaryAmountRequired)` — note the Wurm typo.
- Private methods (`setAlcoholStrength`, `setFoodGroup`, `setCrushsTo`, `setPickSeeds`, `setGrows`, `setHarvestsTo`) use `ReflectionUtil.callPrivateMethod()`. The `> 0` guard mirrors the modloader's own convention — zero means "not set".

## Full JSON Schema

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "WurmItemTemplate",
  "type": "object",
  "oneOf": [
    { "required": ["json-type", "identifier", "name", "plural", "itemTypes", "imageNumber", "behaviourType", "combatDamage", "decayTime", "primarySkill", "modelName", "difficulty", "weight", "material", "value", "isPurchased"] },
    { "required": ["json-type", "templateId"] },
    { "required": ["json-type", "templateName"] }
  ],
  "properties": {
    "json-type":    { "type": "string", "const": "item" },
    "identifier":   { "type": "string", "description": "Creation path key. Stable — never rename after first use." },
    "assignedTemplateId": { "type": "integer", "description": "Output only. Written back by WurmTweaker after creation. Use this to reference the item in other definitions." },
    "templateId":   { "type": "integer", "minimum": 0, "description": "Modification path — numeric template ID." },
    "templateName": { "type": "string", "description": "Modification path — ItemList constant name (e.g. 'longsword')." },
    "size":          { "type": "integer", "enum": [1,2,3,4,5] },
    "name":          { "type": "string" },
    "plural":        { "type": "string" },
    "descriptions": {
      "type": "object",
      "properties": {
        "superb": { "type": "string" },
        "normal": { "type": "string" },
        "bad":    { "type": "string" },
        "rotten": { "type": "string" },
        "long":   { "type": "string" }
      }
    },
    "itemTypes":     { "type": "array", "items": { "type": "integer", "minimum": 0, "maximum": 259 } },
    "imageNumber":   { "type": "integer" },
    "behaviourType": { "type": "integer" },
    "combatDamage":  { "type": "integer" },
    "decayTime":     { "type": "integer" },
    "dimensions": {
      "type": "object",
      "properties": {
        "x": { "type": "integer" },
        "y": { "type": "integer" },
        "z": { "type": "integer" }
      }
    },
    "primarySkill":  { "type": "integer" },
    "bodySpaces":    { "type": "array", "items": { "type": "integer" } },
    "modelName":     { "type": "string" },
    "difficulty":    { "type": "number" },
    "weight":        { "type": "integer" },
    "material":      { "type": "integer", "minimum": 0, "maximum": 96 },
    "value":         { "type": "integer" },
    "isPurchased":   { "type": "boolean" },
    "containerSize": {
      "type": "object",
      "properties": {
        "x": { "type": "integer" },
        "y": { "type": "integer" },
        "z": { "type": "integer" }
      }
    },
    "nutrition": {
      "type": "object",
      "properties": {
        "calories":  { "type": "integer" },
        "carbs":     { "type": "integer" },
        "fats":      { "type": "integer" },
        "proteins":  { "type": "integer" }
      }
    },
    "foodGroup":                  { "type": "integer" },
    "alcoholStrength":            { "type": "integer" },
    "maxItemCount":               { "type": "integer" },
    "maxItemWeight":              { "type": "integer" },
    "fragmentAmount":             { "type": "integer", "maximum": 127 },
    "crushsTo":                   { "type": "integer" },
    "pickSeeds":                  { "type": "integer" },
    "grows":                      { "type": "integer" },
    "harvestsTo":                 { "type": "integer" },
    "dyeAmountGrams":             { "type": "integer" },
    "dyeSecondaryAmountRequired": { "type": "integer" },
    "secondaryItemName":          { "type": "string" }
  }
}
```
