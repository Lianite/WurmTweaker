# TASK-005: Items

## Goal

JSON-driven item customization using `ItemTemplateBuilder` where possible, and documented reflection for fields not covered by the builder. This is the final content phase.

Item templates are defined across six `ItemTemplateCreator*.java` files, all registered through `ItemTemplateFactory.java`.

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

### Required Fields

| JSON Field | Java Type | Notes |
|---|---|---|
| `templateId` | int | Integer template ID for lookup |
| `size` | int | 1=TINY 2=SMALL 3=MEDIUM 4=LARGE 5=HUGE |
| `name` | String | Singular item name |
| `plural` | String | Plural item name |
| `descriptions` | object | Nested: `superb`, `normal`, `bad`, `rotten`, `long` |
| `itemTypes` | int[] | ITEM_TYPE_* constants (0–259) |
| `imageNumber` | short | Inventory icon (JSON int, cast to short on apply) |
| `behaviourType` | short | Behavior flags (JSON int, cast to short on apply) |
| `combatDamage` | int | Weapon damage value |
| `decayTime` | int | Seconds; `Long.MAX_VALUE` = no decay |
| `dimensions` | object | Nested: `x`, `y`, `z` in centimeters |
| `primarySkill` | int | Skill ID; -10 = none |
| `bodySpaces` | int[] | Where item can be worn |
| `modelName` | String | 3D model path |
| `difficulty` | double | Crafting difficulty |
| `weight` | int | Weight in grams |
| `material` | int | MATERIAL_* constant (0–96) |
| `value` | int | Coin value in irons |
| `isPurchased` | boolean | Whether item can be bought from vendors |

### Optional Fields

| JSON Field | Java Type | Notes |
|---|---|---|
| `containerSize` | object | Nested: `x`, `y`, `z` — for containers |
| `nutrition` | object | Nested: `calories`, `carbs`, `fats`, `proteins` |
| `foodGroup` | int | Food group enum |
| `alcoholStrength` | int | Alcohol potency |
| `maxItemCount` | int | Max items in container |
| `maxItemWeight` | int | Max weight per contained item (grams) |
| `fragmentAmount` | int | Fragment count |
| `crushsTo` | int | templateId of result when crushed |
| `pickSeeds` | int | templateId of seed when picked |
| `grows` | int | templateId of grown form |
| `harvestsTo` | int | templateId of harvest result |
| `dyeAmountGrams` | int | Dye amount override |
| `dyePrimaryAmountRequired` | int | Primary dye amount required |
| `dyeSecondaryAmountRequired` | int | Secondary dye amount required |
| `secondaryItemName` | String | Secondary item name |
| `secondaryItemTemplateId` | int | Secondary item template ID |
| `updateExisting` | boolean | Whether to update existing item instances |

## Research Required Before Implementing

1. **What does `ItemTemplateBuilder` expose?**
   Check `org.gotti.wurmunlimited.modsupport.ItemTemplateBuilder` for all setter methods. Map each required field above to either "use builder" or "use reflection."

2. **How are `itemTypes` flags set?**
   Items can have many type flags (e.g. wheat has 6). The old JS code calls `assignTypes(types)` on the template. Check if `ItemTemplateBuilder` wraps this, or whether we call it via reflection. There are 167 boolean properties on `ItemTemplate` all derived from whether a given `ITEM_TYPE_*` constant is present — setting this array correctly is critical.

3. **`imageNumber` and `behaviourType` are shorts — JSON delivers ints.**
   Confirm whether `ItemTemplateBuilder` accepts int/short, or whether `ReflectionUtil.setPrivateField()` handles the int-to-short cast.

4. **Template lookup is by integer `templateId`.**
   Confirmed from schema and old JS baseline. Consider name-based lookup as a fallback.

## Example JSON

`data/items/longsword.json`:
```json
{
  "type": "item",
  "templateId": 7,
  "name": "steel longsword",
  "plural": "steel longswords",
  "descriptions": {
    "superb": "a superb steel longsword",
    "normal": "a steel longsword",
    "bad": "a poorly made steel longsword",
    "rotten": "a ruined steel longsword",
    "long": "A longsword forged from steel."
  },
  "dimensions": { "x": 5, "y": 80, "z": 1 },
  "combatDamage": 22,
  "weight": 1200,
  "difficulty": 35.0
}
```

## Verification

- Drop `longsword.json`, start server → in-game longsword has modified stats
- Missing `templateId` → validation error logged, item skipped, server runs normally
- Unknown field in JSON → Gson ignores it, no crash
