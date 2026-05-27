---
id: TASK-005
title: Items
status: Planned
phase: 3
tags:
  - task
  - planned
related:
  - "[[TASK-004 Creatures]]"
  - "[[Architecture]]"
  - "[[Item Template Fields]]"
  - "[[Item Types]]"
  - "[[Item Materials]]"
  - "[[Item Sizes]]"
---

# TASK-005: Items

## Goal

JSON-driven item system — **modify existing vanilla templates** or **create brand-new templates** by dropping JSON files into `data/items/`. The handler detects which path to take based on the lookup key in the JSON definition. This is the final content phase.

Item templates are defined across six `ItemTemplateCreator*.java` files, all registered through `ItemTemplateFactory.java`. See [[Item Template Fields]] for the full JSON schema.

## Deliverables

- [ ] `src/main/java/org/gotti/wurmtweaker/items/ItemDefinition.java` — POJO matching the full JSON schema
- [ ] `src/main/java/org/gotti/wurmtweaker/items/ItemHandler.java` — `ContentHandler<ItemDefinition>`
- [ ] `data/items/example.json` — sample file
- [ ] Hook: `ItemTemplatesCreatedListener.onItemTemplatesCreated()`

## Hook Point

Item modifications MUST happen in `onItemTemplatesCreated()`. This fires after `ItemTemplateFactory` has built all templates but before the server has distributed them. Modifying templates after this point may have no effect or cause inconsistencies.

## Template Creator Files

| File | Contents |
|---|---|
| `ItemTemplateCreator.java` | Primary batch — bulk of all items |
| `ItemTemplateCreatorContinued.java` | Overflow of general items |
| `ItemTemplateCreatorThird.java` | Third batch |
| `ItemTemplateCreatorCooking.java` | Cooking items |
| `ItemTemplateCreatorFishing.java` | Fishing items |
| `ItemTemplateCreatorKingdom.java` | Kingdom items |

## Known Fields

### Lookup / Creation Key (exactly one required)

| JSON Key | Type | Meaning |
|---|---|---|
| `templateId` | int | Modify existing template by numeric ID |
| `templateName` | String | Modify existing template by name (resolves via `ItemList` constants) |
| `identifier` | String | Create new template via `ItemTemplateBuilder` (e.g. `"myplugin:mysword"`) — ID is assigned by `IdFactory` and persists across restarts in `modsupport.db` |
| `assignedTemplateId` | Integer | **Output only.** Written back to the JSON file by WurmTweaker on first boot after a successful creation. Use this numeric value to reference the item in other JSON definitions (creature drops, `crushsTo`, `grows`, etc.). Never set this manually — it is managed automatically. |

**`identifier` triggers the creation path.** `templateId` and `templateName` trigger the modification path. Only one of the three should be present in a given file.

> **`identifier` vs `assignedTemplateId`:** `identifier` is the stable string key the modloader uses to look up or create the numeric ID in `modsupport.db`. It is a persistence key — the game engine never sees it. `assignedTemplateId` is the actual numeric ID the game engine uses everywhere. Never rename `identifier` after a server has run with it: renaming causes `IdFactory` to assign a new ID, the old DB row orphans, and any items in the world with the old ID break. See [[Item Template Fields]] for the full lifecycle explanation.

For **modification**: only fields that should change need to be in the JSON — absent fields leave the vanilla value untouched.

For **creation**: all required fields (see table below) must be present. The builder has defaults for `size` (MEDIUM), `decayTime` (9072000), and `primarySkill` (-10), but everything else is mandatory.

### Core Fields (constructor-final — require reflection to modify)

| JSON Key | POJO Type | ItemTemplate Field | Template Type | Notes |
|---|---|---|---|---|
| `name` | String | `name` | private final String | — |
| `plural` | String | `plural` | private final String | — |
| `size` | Integer | `size` | private final int | See [[Item Sizes]] |
| `imageNumber` | Integer | `imageNumber` | public final short | Cast int → short on apply |
| `behaviourType` | Integer | `behaviourType` | private final short | Cast int → short on apply |
| `combatDamage` | Integer | `combatDamage` | private final int | — |
| `decayTime` | Long | `decayTime` | private final long | JSON delivers long |
| `primarySkill` | Integer | `primarySkill` | private final int | -10 = none; see [[Skill IDs]] |
| `modelName` | String | `modelName` | private final String | — |
| `difficulty` | Float | `difficulty` | private final float | JSON delivers float |
| `weight` | Integer | `weight` | private final int | Grams |
| `material` | Integer | `material` | private final byte | Cast int → byte on apply |
| `value` | Integer | `value` | private final int | Irons |
| `isPurchased` | Boolean | `isPurchased` | private final boolean | — |

#### Descriptions (each a private final String field)

| JSON Key | POJO Field | ItemTemplate Field |
|---|---|---|
| `descriptions.superb` | `descriptionSuperb` | `itemDescriptionSuperb` |
| `descriptions.normal` | `descriptionNormal` | `itemDescriptionNormal` |
| `descriptions.bad` | `descriptionBad` | `itemDescriptionBad` |
| `descriptions.rotten` | `descriptionRotten` | `itemDescriptionRotten` |
| `descriptions.long` | `descriptionLong` | `itemDescriptionLong` |

#### Dimensions (each a private final int field)

| JSON Key | POJO Field | ItemTemplate Field |
|---|---|---|
| `dimensions.x` | `centimetersX` | `centimetersX` |
| `dimensions.y` | `centimetersY` | `centimetersY` |
| `dimensions.z` | `centimetersZ` | `centimetersZ` |

#### Body Spaces and Item Types

| JSON Key | POJO Type | ItemTemplate Type | Notes |
|---|---|---|---|
| `bodySpaces` | int[] | private final byte[] | Convert int[] → byte[] on apply |
| `itemTypes` | int[] | (set via method) | Convert int[] → short[], then call `assignTypes(short[])` |

### Optional Fields — Public Setters Available

These fields are mutable on `ItemTemplate` and have public setter methods.

| JSON Key | POJO Type | Apply Method | Notes |
|---|---|---|---|
| `containerSize` | Nested {x,y,z} | `setContainerSize(int, int, int)` | — |
| `maxItemCount` | Integer | `setMaxItemCount(int)` | — |
| `maxItemWeight` | Integer | `setMaxItemWeight(int)` | Grams |
| `nutrition` | Nested {calories,carbs,fats,proteins} | `setNutritionValues(int,int,int,int)` | — |
| `dyeAmountGrams` | Integer | `setDyeAmountGrams(int)` | Primary dye amount |
| `secondaryItemName` | String | `setSecondryItem(String, int)` | Note: Wurm typo — "Secndry" |
| `dyeSecondaryAmountRequired` | Integer | `setSecondryItem(String, int)` | Paired with secondaryItemName |

### Optional Fields — Private Methods (use ReflectionUtil)

| JSON Key | POJO Type | Private Method Name | Notes |
|---|---|---|---|
| `alcoholStrength` | Integer | `setAlcoholStrength` | — |
| `foodGroup` | Integer | `setFoodGroup` | — |
| `crushsTo` | Integer | `setCrushsTo` | templateId of crush result |
| `pickSeeds` | Integer | `setPickSeeds` | templateId of seed |
| `grows` | Integer | `setGrows` | templateId of grown form |
| `harvestsTo` | Integer | `setHarvestsTo` | templateId of harvest result |

### Optional Fields — Public Setter (previously misclassified)

| JSON Key | POJO Type | Apply Method | Notes |
|---|---|---|---|
| `fragmentAmount` | Integer | `setFragmentAmount(int)` | Public setter exists; capped at 127 by the engine |

## Research Findings

### 1. Two paths — ItemTemplateBuilder for creation, reflection for modification

`ItemTemplateBuilder` (refs/WurmServerModLauncher-develop) exposes setters for **every field in the schema** and calls `ItemTemplateFactory.createItemTemplate()` internally. It is the correct tool for creating new templates.

**Creation path** (`identifier` present): Construct `new ItemTemplateBuilder(identifier)`, chain all field setters from `ItemDefinition`, call `build()`. The builder handles all type coercions internally. No reflection needed.

**Modification path** (`templateId` or `templateName` present): `ItemTemplateFactory.getInstance().getTemplateOrNull(int)` returns the live template; mutate it directly via `setFinalField()` and the public/private setters.

**Type coercions differ between paths:**
- Builder: `itemTypes(short[])`, `imageNumber(short)`, `behaviourType(short)`, `material(byte)`, `bodySpaces(byte[])`, `difficulty(float)`, `decayTime(long)` — all explicit casts needed before calling setters.
- Modification: same casts, applied via `setFinalField()` or `callPrivateMethod()`.

### 2. `itemTypes` — `assignTypes(short[])` is public

`ItemTemplate.assignTypes(short[] types)` is a public method. Call it directly — no reflection needed. The method iterates the array and sets all 167 boolean flags accordingly.

**Type conversion required:** JSON delivers `int[]`; method requires `short[]`. Convert in the handler before calling.

### 3. `imageNumber` and `behaviourType` shorts — direct field reflection handles it

Both are `private final short` fields on `ItemTemplate`. Our own reflection helper (same pattern as `CreatureHandler.setField()`) strips the `FINAL` modifier and sets the value. Wrap the JSON int in `(short)` at call site.

### 4. Template lookup — ID and name both available

- `ItemTemplateFactory.getInstance().getTemplateOrNull(int id)` — returns null if not found; log and skip.
- `ItemTemplateFactory.getInstance().getTemplate(String name)` — returns null if not found; available as a name-based fallback.
- **`ItemIdParser`** (modloader: `org.gotti.wurmunlimited.modsupport.items.ItemIdParser`) resolves item names to IDs using the `ItemList` constants class. Use this to support a `"templateName"` JSON field as an alternative to `"templateId"`.

### 5. `isPurchased` vs `isTraded` naming

The JSON key is `isPurchased` (matching the `ItemTemplate` field name). The builder calls this field `isTraded` internally — irrelevant to us since we bypass the builder.

### 6. Schema corrections identified during research

- `secondaryItemTemplateId` — **does not exist**. Removed from schema. The method is `setSecondryItem(String name, int dyeSecondaryAmountRequired)` — it takes a name string and the secondary dye amount, not a template ID.
- `dyePrimaryAmountRequired` — renamed to `dyeAmountGrams` in the JSON to match `setDyeAmountGrams()`, which is the public setter we use.
- `decayTime` must be `Long` in the POJO, not `Integer` — the field is `private final long`.
- `difficulty` must be `Float` in the POJO — the field is `private final float`.

### 7. Modloader tooling inventory

#### `ModItems` — model name hook (not needed for TASK-005)

`org.gotti.wurmunlimited.modsupport.items.ModItems` intercepts `Item.getModelName()` at runtime via Javassist and lets mods return a custom model name per item instance. It is **not** a template registry — it cannot create or modify templates. It must be initialized in `preInit()` if used.

This is out of scope for TASK-005. If we later want per-item model overrides (e.g. different models based on item quality or enchantment), `ModItems.addModelNameProvider(int templateId, ModelNameProvider provider)` is the hook.

#### `ItemTemplateBuilder` — creates new custom items (used in TASK-005)

`org.gotti.wurmunlimited.modsupport.ItemTemplateBuilder` creates brand-new item templates by calling `ItemTemplateFactory.createItemTemplate(...)`. It uses `IdFactory.getIdFor(String identifier, IdType.ITEMTEMPLATE)` to assign a **persistent integer ID** stored in the modloader's SQLite DB (`ModSupportDb`). New template IDs count **down from 22767** (22766, 22765, …).

This is the creation path for JSON definitions with an `identifier` key. The builder handles all type coercions and optional field calls internally — no reflection required.

#### `ReflectionUtil` — confirmed API, confirmed FINAL limitation

`org.gotti.wurmunlimited.modloader.ReflectionUtil` is in the `modlauncher` JAR (`provided` scope in pom.xml — already on the classpath). Confirmed methods from source usage:

| Method | Use |
|---|---|
| `getField(Class<?>, String)` → Field | Locate a field by name |
| `setPrivateField(Object target, Field, Object value)` | Set a private field — does NOT strip FINAL |
| `getMethod(Class<?>, String)` → Method | Locate a method by name |
| `callPrivateMethod(Object target, Method, Object... args)` | Call a private instance method |
| `callPrivateMethod(Class<?>, Method, Object... args)` | Call a private static method |

**Critical:** `setPrivateField()` makes the field accessible but does **not** strip the `FINAL` modifier. For the 19 `private final` constructor fields on `ItemTemplate`, we must implement our own `setFinalField()` helper that strips FINAL via `Field.class.getDeclaredField("modifiers")` before setting — exactly as `CreatureHandler` does.

Use `ReflectionUtil.callPrivateMethod()` for the six private setter methods (alcoholStrength, foodGroup, etc.) — those are not final.

## Implementation Plan

### Template Lookup

Support both `templateId` (int) and `templateName` (String) in `ItemDefinition`. Lookup priority:
1. If `templateId` is set → `ItemTemplateFactory.getInstance().getTemplateOrNull(templateId)`
2. Else if `templateName` is set → resolve via `new ItemIdParser().parse(templateName)`, then look up by ID
3. If neither is set → log error, skip

### Reflection Helpers

`ItemHandler` needs two helpers:

**`setFinalField(Object target, String fieldName, Object value)`** — for the 19 `private final` constructor fields:
1. Walk class hierarchy to find the field
2. `field.setAccessible(true)`
3. Strip FINAL: `Field modifiers = Field.class.getDeclaredField("modifiers"); modifiers.setAccessible(true); modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL)`
4. `field.set(target, value)`

**Use `ReflectionUtil.callPrivateMethod()`** (already on classpath) for the six private non-final setter methods. No custom helper needed for those.

### Apply Strategy Per Field Group

**Group A — Core final fields** (name, plural, size, descriptions, dimensions, imageNumber, behaviourType, combatDamage, decayTime, primarySkill, modelName, difficulty, weight, material, value, isPurchased, bodySpaces):
→ Use `setField()` reflection helper. Cast to target type at call site.

**Group B — itemTypes**:
→ Convert JSON `int[]` to `short[]`, then call `template.assignTypes(short[])` directly.

**Group C — public setters** (containerSize, maxItemCount, maxItemWeight, nutrition, dyeAmountGrams, secondaryItemName+dyeSecondaryAmountRequired, fragmentAmount):
→ Call the public method directly on the template. `fragmentAmount` has `setFragmentAmount(int)` — no reflection needed.

**Group D — private methods** (alcoholStrength, foodGroup, crushsTo, pickSeeds, grows, harvestsTo):
→ Use `ReflectionUtil.callPrivateMethod(template, ReflectionUtil.getMethod(ItemTemplate.class, "methodName"), value)`.

### ItemDefinition POJO Design

All fields except `templateId` should be boxed types (Integer, Long, Float, Boolean, etc.) so Gson leaves them null when absent from JSON. Apply only non-null fields. Use `Dimensions` and `Descriptions` as nested static inner classes.

### Handler Flow

```
onItemTemplatesCreated() {
    jsonLoader.loadType("item")
}

ItemHandler.handle(ItemDefinition def) {
    if (def.identifier != null) {
        // Creation path
        builder = new ItemTemplateBuilder(def.identifier)
        // validate all required fields are non-null → log error and return if not
        builder.name(...).size(...).descriptions(...).itemTypes(toShortArray(...))
               .imageNumber((short)...).behaviourType((short)...)...
        template = builder.build()
        // apply optional fields that have public setters post-build
    } else {
        // Modification path
        template = lookupTemplate(def)  // by templateId or templateName
        if (template == null) → log error, return
        applyGroup_A(template, def)   // setFinalField: core private final fields
        applyGroup_B(template, def)   // assignTypes(short[])
        applyGroup_C(template, def)   // public setters
        applyGroup_D(template, def)   // ReflectionUtil.callPrivateMethod
        applyGroup_E(template, def)   // setFinalField: fragmentAmount
    }
}
```

### Type Conversion Summary

| POJO Type | Target Java Type | Conversion |
|---|---|---|
| Integer → short | `(short)(int)` value | imageNumber, behaviourType |
| Integer → byte | `(byte)(int)` value | material |
| int[] → short[] | loop cast | itemTypes |
| int[] → byte[] | loop cast | bodySpaces |
| Float/Double → float | `value.floatValue()` | difficulty |
| Integer → long | auto-widened | decayTime (POJO is Long) |

## Example JSON

**Modification** — change stats on an existing vanilla item:

`data/items/longsword.json`:
```json
{
  "json-type": "item",
  "templateId": 7,
  "combatDamage": 22,
  "weight": 1200,
  "difficulty": 35.0
}
```

**Creation** — register a brand-new item template:

`data/items/runed-blade.json`:
```json
{
  "json-type": "item",
  "identifier": "wurmtweaker:runedblade",
  "name": "runed blade",
  "plural": "runed blades",
  "descriptions": {
    "superb": "a superb runed blade",
    "normal": "a runed blade",
    "bad": "a poorly made runed blade",
    "rotten": "a ruined runed blade",
    "long": "A blade etched with ancient runes."
  },
  "itemTypes": [2, 16, 35, 37],
  "imageNumber": 128,
  "behaviourType": 0,
  "combatDamage": 28,
  "decayTime": 9072000,
  "dimensions": { "x": 4, "y": 90, "z": 1 },
  "primarySkill": 102,
  "bodySpaces": [],
  "modelName": "model.weapon.sword.longsword",
  "difficulty": 50.0,
  "weight": 1100,
  "material": 9,
  "value": 40000,
  "isPurchased": false
}
```

## Verification

- **Modification:** Drop `longsword.json`, start server → in-game longsword has modified stats
- **Creation (first boot):** Drop `runed-blade.json`, start server → new item exists in game; `runed-blade.json` now contains `"assignedTemplateId": <n>` written back by the handler
- **Creation (subsequent boots):** `assignedTemplateId` is already present and matches → write-back skipped, file untouched
- Missing lookup key (`templateId`/`templateName`/`identifier`) → warning logged, item skipped, server runs normally
- Creation with missing required field → all missing fields named in one warning, item skipped
- Unknown field in JSON → Gson ignores it, no crash
