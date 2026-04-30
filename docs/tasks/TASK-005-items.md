# TASK-005: Items

## Goal
JSON-driven item customization using `ItemTemplateBuilder` where possible, and documented reflection for fields not covered by the builder. This is the final content phase. The old JavaScript `modSupport-interface.js` is the reference for which fields exist.

## Status
PLANNED (depends on TASK-004)

## Deliverables

- [ ] `src/main/java/org/gotti/wurmtweaker/items/ItemDefinition.java` — POJO with all 29 known fields
- [ ] `src/main/java/org/gotti/wurmtweaker/items/ItemHandler.java` — `ContentHandler<ItemDefinition>`
- [ ] `data/items/example.json` — sample file
- [ ] Hook: `ItemTemplatesCreatedListener.onItemTemplatesCreated()`

## Hook Point

Item modifications MUST happen in `onItemTemplatesCreated()`. This fires after `ItemTemplateFactory` has built all templates but before the server has distributed them. Modifying templates after this point may have no effect or cause inconsistencies.

## Known Fields (from old modSupport-interface.js)

These 29 fields are confirmed to exist on `ItemTemplate`. Whether `ItemTemplateBuilder` exposes them directly needs to be verified during implementation.

| Field | Java Type | Notes |
|---|---|---|
| `name` | String | Singular item name |
| `plural` | String | Plural item name |
| `itemDescriptionSuperb` | String | Quality-level description |
| `itemDescriptionNormal` | String | Quality-level description |
| `itemDescriptionBad` | String | Quality-level description |
| `itemDescriptionRotten` | String | Quality-level description |
| `itemDescriptionLong` | String | Examine description |
| `modelName` | String | 3D model name |
| `imageNumber` | short | Inventory icon (reflection needed — short type) |
| `behaviorType` | short | Behavior flags (reflection needed — short type) |
| `combatDamage` | int | Weapon damage value |
| `decayTime` | int | How fast item decays |
| `centimetersX` | int | Physical size X |
| `centimetersY` | int | Physical size Y |
| `centimetersZ` | int | Physical size Z |
| `primarySkill` | int | Skill used when crafting/using |
| `weightGrams` | int | Item weight |
| `material` | int | Material type enum |
| `size` | int | Size enum |
| `value` | int | Vendor sale price |
| `difficulty` | double | Crafting difficulty |
| `dyeAmountGramsOverride` | int | Dye amount override |
| `itemTypes` | int[] | Boolean type flags array (200+ flags) |
| `bodySpaces` | int[] | Where item can be worn |
| `isPurchased` | boolean | Whether item can be bought from vendors |
| `updateExisting` | boolean | Whether to update existing item instances |

## Research Required Before Implementing

1. **What does `ItemTemplateBuilder` expose?**
   Check `org.gotti.wurmunlimited.modsupport.ItemTemplateBuilder` for all setter methods. Map each of the 29 fields above to either "use builder" or "use reflection."

2. **How are `itemTypes` flags set?**
   The old JS code calls `assignTypes(types)` on the template. Check if `ItemTemplateBuilder` wraps this.

3. **What are the `imageNumber` and `behaviorType` short conversion issues?**
   The old code explicitly skipped these due to JS-Java short conversion issues. In Java this is not a problem — confirm that `ReflectionUtil.setPrivateField()` handles shorts correctly.

4. **What is the template ID used for lookups?**
   The old code used integer template IDs. Confirm whether we should look up items by ID, by name, or both.

## Example JSON

`data/items/longsword.json`:
```json
{
  "type": "item",
  "templateId": 7,
  "name": "steel longsword",
  "plural": "steel longswords",
  "combatDamage": 22,
  "weightGrams": 1200,
  "difficulty": 35.0
}
```

## Verification
- Drop `longsword.json`, start server → in-game longsword has modified stats
- Missing `templateId` → validation error logged, item skipped, server runs normally
- Unknown field in JSON → Gson ignores it, no crash
