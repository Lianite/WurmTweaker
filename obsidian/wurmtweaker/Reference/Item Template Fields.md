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

| JSON Key | POJO Type | ItemTemplate Field | Template Type | Notes |
|---|---|---|---|---|
| `name` | String | `name` | private final String | — |
| `plural` | String | `plural` | private final String | — |
| `size` | Integer | `size` | private final int | See [[Item Sizes]]: 1=TINY … 5=HUGE |
| `imageNumber` | Integer | `imageNumber` | public final short | Cast int → short on apply |
| `behaviourType` | Integer | `behaviourType` | private final short | Cast int → short on apply |
| `combatDamage` | Integer | `combatDamage` | private final int | — |
| `decayTime` | Long | `decayTime` | private final long | Seconds; Long.MAX_VALUE = no decay |
| `primarySkill` | Integer | `primarySkill` | private final int | See [[Skill IDs]]; -10 = none |
| `modelName` | String | `modelName` | private final String | — |
| `difficulty` | Float | `difficulty` | private final float | Crafting difficulty |
| `weight` | Integer | `weight` | private final int | Grams |
| `material` | Integer | `material` | private final byte | Cast int → byte; see [[Item Materials]] (0–96) |
| `value` | Integer | `value` | private final int | Coin value in irons |
| `isPurchased` | Boolean | `isPurchased` | private final boolean | Whether item can be bought from vendors |

### `descriptions` Object (each a private final String)

Optional for creation (builder defaults to "superb" / "good" / "ok" / "poor" / ""). Required if you want meaningful examine text.

```json
"descriptions": {
  "superb": "a superb steel longsword",
  "normal": "a steel longsword",
  "bad":    "a poorly made steel longsword",
  "rotten": "a ruined steel longsword",
  "long":   "A longsword forged from steel."
}
```

| JSON Key | ItemTemplate Field |
|---|---|
| `descriptions.superb` | `itemDescriptionSuperb` |
| `descriptions.normal` | `itemDescriptionNormal` |
| `descriptions.bad` | `itemDescriptionBad` |
| `descriptions.rotten` | `itemDescriptionRotten` |
| `descriptions.long` | `itemDescriptionLong` |

### `dimensions` Object (each a private final int)

```json
"dimensions": { "x": 5, "y": 80, "z": 1 }
```

All values in centimeters. Defaults to `0, 0, 0` on creation if omitted.

### `bodySpaces` and `itemTypes`

| JSON Key | POJO Type | ItemTemplate Type | Notes |
|---|---|---|---|
| `bodySpaces` | int[] | private final byte[] | Body slot IDs where item can be worn. Convert int[] → byte[] on apply. Empty array `[]` = cannot be worn. |
| `itemTypes` | int[] | (set via method) | ITEM_TYPE_* constants — see [[Item Types]]. Convert int[] → short[], then call `assignTypes(short[])`. **Required for creation.** |

## Optional Fields

These fields have public setters on `ItemTemplate` or are set via `ReflectionUtil.callPrivateMethod()`. Include only the ones relevant to your item.

| JSON Key | Type | Apply Method | Notes |
|---|---|---|---|
| `containerSize` | object `{x,y,z}` | `setContainerSize(int, int, int)` | Inner dimensions for containers |
| `maxItemCount` | integer | `setMaxItemCount(int)` | Max items a container holds |
| `maxItemWeight` | integer | `setMaxItemWeight(int)` | Max weight per contained item (grams) |
| `nutrition` | object | `setNutritionValues(int,int,int,int)` | See nested object below |
| `dyeAmountGrams` | integer | `setDyeAmountGrams(int)` | Primary dye amount override |
| `secondaryItemName` | string | `setSecondryItem(String, int)` | Note: Wurm typo — "Secndry" |
| `dyeSecondaryAmountRequired` | integer | `setSecondryItem(String, int)` | Paired with `secondaryItemName` |
| `fragmentAmount` | integer | `setFragmentAmount(int)` | Fragment count; capped at 127 by the engine |
| `alcoholStrength` | integer | `setAlcoholStrength` (private, via ReflectionUtil) | — |
| `foodGroup` | integer | `setFoodGroup` (private, via ReflectionUtil) | — |
| `crushsTo` | integer | `setCrushsTo` (private, via ReflectionUtil) | templateId of result when crushed |
| `pickSeeds` | integer | `setPickSeeds` (private, via ReflectionUtil) | templateId of seed when picked |
| `grows` | integer | `setGrows` (private, via ReflectionUtil) | templateId of grown form |
| `harvestsTo` | integer | `setHarvestsTo` (private, via ReflectionUtil) | templateId of harvest result |

### `nutrition` Object

```json
"nutrition": { "calories": 500, "carbs": 60, "fats": 20, "proteins": 30 }
```

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
