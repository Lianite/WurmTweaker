---
type: reference
tags:
  - reference
  - items
related:
  - "[[TASK-005 Items]]"
  - "[[Item Types]]"
  - "[[Item Materials]]"
  - "[[Skill IDs]]"
---

# Item Template Fields

Full JSON schema for WurmTweaker item definitions. Derived from `ItemTemplate.java`, `ItemTypes.java`, and `ItemMaterials.java`.

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

## Required Fields

| JSON Field | Type | Description |
|---|---|---|
| `templateId` | integer | Integer template ID (lookup key) |
| `size` | integer | See [[Item Sizes]]: 1=TINY, 2=SMALL, 3=MEDIUM, 4=LARGE, 5=HUGE |
| `name` | string | Singular item name |
| `plural` | string | Plural item name |
| `descriptions` | object | See nested object below |
| `itemTypes` | integer[] | ITEM_TYPE_* constant array — see [[Item Types]] |
| `imageNumber` | integer | Inventory icon ID (stored as short on template) |
| `behaviourType` | integer | Behavior flags (stored as short on template) |
| `combatDamage` | integer | Weapon damage value |
| `decayTime` | integer | Seconds; Long.MAX_VALUE = no decay |
| `dimensions` | object | See nested object below |
| `primarySkill` | integer | Skill ID — see [[Skill IDs]]; -10 = none |
| `bodySpaces` | integer[] | Body slot IDs where item can be worn |
| `modelName` | string | 3D model path |
| `difficulty` | number | Crafting difficulty |
| `weight` | integer | Weight in grams |
| `material` | integer | MATERIAL_* constant — see [[Item Materials]] (0–96) |
| `value` | integer | Coin value in irons |
| `isPurchased` | boolean | Whether item can be bought from vendors |

### `descriptions` Object

```json
"descriptions": {
  "superb": "a superb steel longsword",
  "normal": "a steel longsword",
  "bad":    "a poorly made steel longsword",
  "rotten": "a ruined steel longsword",
  "long":   "A longsword forged from steel."
}
```

### `dimensions` Object

```json
"dimensions": { "x": 5, "y": 80, "z": 1 }
```

All values in centimeters.

## Optional Fields

| JSON Field | Type | Description |
|---|---|---|
| `containerSize` | object | `{ x, y, z }` — inner dimensions for containers |
| `nutrition` | object | `{ calories, carbs, fats, proteins }` |
| `foodGroup` | integer | Food group enum |
| `alcoholStrength` | integer | Alcohol potency |
| `maxItemCount` | integer | Max items a container holds |
| `maxItemWeight` | integer | Max weight per contained item (grams) |
| `fragmentAmount` | integer | Fragment count |
| `crushsTo` | integer | templateId of result when crushed |
| `pickSeeds` | integer | templateId of seed when picked |
| `grows` | integer | templateId of grown form |
| `harvestsTo` | integer | templateId of harvest result |
| `dyeAmountGrams` | integer | Dye amount override |
| `dyePrimaryAmountRequired` | integer | Primary dye required |
| `dyeSecondaryAmountRequired` | integer | Secondary dye required |
| `secondaryItemName` | string | Secondary item name |
| `secondaryItemTemplateId` | integer | Secondary item template ID |
| `updateExisting` | boolean | Update existing item instances in the world |

## Implementation Notes

- `imageNumber` and `behaviourType` are shorts on `ItemTemplate` but delivered as integers from JSON. Confirm that `ItemTemplateBuilder` accepts these or that reflection handles the int-to-short cast.
- `itemTypes` is the most nuanced field — `ItemTemplate` exposes 167 boolean properties all derived from whether a given `ITEM_TYPE_*` constant appears in this array. An item like wheat has 6 type flags. Getting this array right is critical for correct item behavior.
- The old `modSupport-interface.js` called `assignTypes(types)` directly on the template. Check if `ItemTemplateBuilder` wraps this call.

## Full JSON Schema

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "WurmItemTemplate",
  "type": "object",
  "required": [
    "templateId", "size", "name", "plural",
    "descriptions", "itemTypes", "imageNumber",
    "behaviourType", "combatDamage", "decayTime",
    "dimensions", "primarySkill", "bodySpaces",
    "modelName", "difficulty", "weight", "material",
    "value", "isPurchased"
  ],
  "properties": {
    "templateId":    { "type": "integer", "minimum": 0 },
    "size":          { "type": "integer", "enum": [1,2,3,4,5] },
    "name":          { "type": "string" },
    "plural":        { "type": "string" },
    "descriptions": {
      "type": "object",
      "required": ["superb","normal","bad","rotten","long"],
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
      "required": ["x","y","z"],
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
    "foodGroup":                   { "type": "integer" },
    "alcoholStrength":             { "type": "integer" },
    "maxItemCount":                { "type": "integer" },
    "maxItemWeight":               { "type": "integer" },
    "fragmentAmount":              { "type": "integer" },
    "crushsTo":                    { "type": "integer" },
    "pickSeeds":                   { "type": "integer" },
    "grows":                       { "type": "integer" },
    "harvestsTo":                  { "type": "integer" },
    "dyeAmountGrams":              { "type": "integer" },
    "dyePrimaryAmountRequired":    { "type": "integer" },
    "dyeSecondaryAmountRequired":  { "type": "integer" },
    "secondaryItemName":           { "type": "string" },
    "secondaryItemTemplateId":     { "type": "integer" },
    "updateExisting":              { "type": "boolean" }
  }
}
```
