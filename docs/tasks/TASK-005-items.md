# TASK-005: Items

## Goal

JSON-driven item system — **modify existing vanilla templates** or **create brand-new templates** by dropping JSON files into `data/items/`. The handler detects which path to take based on the lookup key in the JSON definition. This is the final content phase.

## Status

PLANNED (depends on TASK-004)

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
| `templateName` | String | Modify existing template by name (resolves via `ItemList`) |
| `identifier` | String | Create new template via `ItemTemplateBuilder` — ID persists across restarts |

`identifier` → creation path (all required fields must be present). `templateId`/`templateName` → modification path (only changed fields needed).

### Core Fields (required for creation; optional for modification)

| JSON Key | POJO Type | ItemTemplate Field | Template Type | Notes |
|---|---|---|---|---|
| `name` | String | `name` | private final String | — |
| `plural` | String | `plural` | private final String | — |
| `size` | Integer | `size` | private final int | 1=TINY…5=HUGE |
| `imageNumber` | Integer | `imageNumber` | public final short | Cast int→short on apply |
| `behaviourType` | Integer | `behaviourType` | private final short | Cast int→short on apply |
| `combatDamage` | Integer | `combatDamage` | private final int | — |
| `decayTime` | Long | `decayTime` | private final long | — |
| `primarySkill` | Integer | `primarySkill` | private final int | -10 = none |
| `modelName` | String | `modelName` | private final String | — |
| `difficulty` | Float | `difficulty` | private final float | — |
| `weight` | Integer | `weight` | private final int | Grams |
| `material` | Integer | `material` | private final byte | Cast int→byte on apply |
| `value` | Integer | `value` | private final int | Irons |
| `isPurchased` | Boolean | `isPurchased` | private final boolean | — |

#### Descriptions (each private final String)

| JSON Key | POJO Field | ItemTemplate Field |
|---|---|---|
| `descriptions.superb` | `descriptionSuperb` | `itemDescriptionSuperb` |
| `descriptions.normal` | `descriptionNormal` | `itemDescriptionNormal` |
| `descriptions.bad` | `descriptionBad` | `itemDescriptionBad` |
| `descriptions.rotten` | `descriptionRotten` | `itemDescriptionRotten` |
| `descriptions.long` | `descriptionLong` | `itemDescriptionLong` |

#### Dimensions (each private final int)

| JSON Key | POJO Field | ItemTemplate Field |
|---|---|---|
| `dimensions.x` | `centimetersX` | `centimetersX` |
| `dimensions.y` | `centimetersY` | `centimetersY` |
| `dimensions.z` | `centimetersZ` | `centimetersZ` |

#### Body Spaces and Item Types

| JSON Key | POJO Type | ItemTemplate Type | Notes |
|---|---|---|---|
| `bodySpaces` | int[] | private final byte[] | Convert int[]→byte[] on apply |
| `itemTypes` | int[] | (set via method) | Convert int[]→short[], call `assignTypes(short[])` |

### Optional Fields — Public Setters

| JSON Key | POJO Type | Apply Method |
|---|---|---|
| `containerSize` | Nested {x,y,z} | `setContainerSize(int, int, int)` |
| `maxItemCount` | Integer | `setMaxItemCount(int)` |
| `maxItemWeight` | Integer | `setMaxItemWeight(int)` |
| `nutrition` | Nested {calories,carbs,fats,proteins} | `setNutritionValues(int,int,int,int)` |
| `dyeAmountGrams` | Integer | `setDyeAmountGrams(int)` |
| `secondaryItemName` | String | `setSecondryItem(String, int)` — Wurm typo |
| `dyeSecondaryAmountRequired` | Integer | `setSecondryItem(String, int)` — paired with above |

### Optional Fields — Private Methods (ReflectionUtil)

| JSON Key | POJO Type | Private Method |
|---|---|---|
| `alcoholStrength` | Integer | `setAlcoholStrength` |
| `foodGroup` | Integer | `setFoodGroup` |
| `crushsTo` | Integer | `setCrushsTo` |
| `pickSeeds` | Integer | `setPickSeeds` |
| `grows` | Integer | `setGrows` |
| `harvestsTo` | Integer | `setHarvestsTo` |

### Optional Fields — Direct Field Reflection

| JSON Key | POJO Type | ItemTemplate Field |
|---|---|---|
| `fragmentAmount` | Integer | `fragmentAmount` (no setter) |

## Research Findings

1. **ItemTemplateBuilder covers all fields** but is for creating new templates — we bypass it. Modification path: `ItemTemplateFactory.getInstance().getTemplateOrNull(int)` → mutate directly.

2. **`assignTypes(short[])`** is public on `ItemTemplate`. Call it directly after converting JSON `int[]` to `short[]`.

3. **`imageNumber` and `behaviourType`** are `private final short`. Use our own `setFinalField()` helper (same pattern as CreatureHandler) with an explicit `(short)` cast. `ReflectionUtil.setPrivateField()` does NOT strip FINAL — we need our own helper.

4. **Template lookup** supports both `templateId` (int) and `templateName` (String). For name lookup, use `ItemIdParser` from the modloader (`org.gotti.wurmunlimited.modsupport.items.ItemIdParser`) to resolve via `ItemList` constants.

5. **`isPurchased` vs `isTraded`** — builder calls it `isTraded`; the actual `ItemTemplate` field is `isPurchased`. Use `isPurchased` everywhere.

6. **`ReflectionUtil`** is on the classpath (modlauncher JAR, `provided` scope). Use `callPrivateMethod()` for the six private setter methods. Do NOT use `setPrivateField()` for final fields — implement `setFinalField()` instead.

7. **`ModItems`** (`org.gotti.wurmunlimited.modsupport.items.ModItems`) is a runtime model-name hook, not a template registry. Not needed for TASK-005. Relevant only if per-item model overrides are needed in the future.

8. **Schema corrections:**
   - `secondaryItemTemplateId` removed — does not exist on `ItemTemplate`
   - `dyeAmountGrams` replaces `dyePrimaryAmountRequired` (matches the public setter name)
   - `decayTime` POJO type is `Long` (not Integer)
   - `difficulty` POJO type is `Float` (not double)

## Implementation Plan

### Template Lookup

Support both `templateId` (int) and `templateName` (String) in `ItemDefinition`. Priority: `templateId` if set, else resolve `templateName` via `new ItemIdParser().parse(templateName)`. If neither set, log and skip.

### Reflection Helpers

**`setFinalField(Object target, String fieldName, Object value)`** — strips FINAL modifier, then sets. Same pattern as `CreatureHandler.setField()`.

**`ReflectionUtil.callPrivateMethod()`** — use for the six private non-final setter methods (alcoholStrength, foodGroup, crushsTo, pickSeeds, grows, harvestsTo). Already on classpath.

### Apply Strategy

- **Group A — core final fields:** `setField()` helper with type cast at call site
- **Group B — itemTypes:** convert `int[]` → `short[]`, call `template.assignTypes(short[])`
- **Group C — public setters:** call directly on template
- **Group D — private methods:** `ReflectionUtil.callPrivateMethod(template, ReflectionUtil.getMethod(ItemTemplate.class, "methodName"), value)`
- **Group E — no setter (fragmentAmount):** `setField()` helper

### Type Conversions at Apply Time

| From (JSON/POJO) | To (template) | How |
|---|---|---|
| Integer | short | `(short)(int)` value |
| Integer | byte | `(byte)(int)` value |
| int[] | short[] | loop cast |
| int[] | byte[] | loop cast |
| Float | float | `.floatValue()` |

### ItemDefinition POJO Design

All fields except `templateId` are boxed types (Integer, Long, Float, Boolean) so Gson leaves them null when absent. Use nested static inner classes for `Descriptions` and `Dimensions`.

### Handler Flow

```
onItemTemplatesCreated() → jsonLoader.loadType("item")

ItemHandler.handle(ItemDefinition def):
  if def.identifier != null:
    // Creation path
    validate all required fields non-null → log error and return if not
    builder = new ItemTemplateBuilder(def.identifier)
    builder.name(...).size(...).descriptions(...).itemTypes(toShortArray(...))
           .imageNumber((short)...).behaviourType((short)...)...
    template = builder.build()
    apply optional post-build setters (containerSize, nutrition, etc.)
  else:
    // Modification path
    template = lookupTemplate(def)  // by templateId or templateName
    if null → log error, return
    apply Group A (setFinalField: core private final fields)
    apply Group B (assignTypes(short[]))
    apply Group C (public setters)
    apply Group D (ReflectionUtil.callPrivateMethod)
    apply Group E (setFinalField: fragmentAmount)
```

## Example JSON

**Modification:**
```json
{
  "type": "item",
  "templateId": 7,
  "combatDamage": 22,
  "weight": 1200,
  "difficulty": 35.0
}
```

**Creation:**
```json
{
  "type": "item",
  "identifier": "wurmtweaker:runedblade",
  "name": "runed blade",
  "plural": "runed blades",
  "descriptions": { "superb": "...", "normal": "...", "bad": "...", "rotten": "...", "long": "..." },
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

- **Modification:** Drop `longsword.json`, start server → existing item has modified stats
- **Creation:** Drop `runed-blade.json`, start server → new item exists in game
- Missing lookup key → validation error logged, item skipped, server continues
- Creation with missing required field → validation error logged, item skipped
- Unknown field in JSON → Gson ignores it, no crash
