# TASK-004: Creatures

## Goal
JSON-driven creature customization using the `CreatureTemplateBuilder` API provided by Ago's modloader modsupport package. This task is the second content phase and should only begin after TASK-003 (Skills) is complete end-to-end.

## Status
PLANNED (depends on TASK-003)

## Deliverables

- [ ] `src/main/java/org/gotti/wurmtweaker/creatures/CreatureDefinition.java` — POJO
- [ ] `src/main/java/org/gotti/wurmtweaker/creatures/CreatureHandler.java` — `ContentHandler<CreatureDefinition>`
- [ ] `data/creatures/example.json` — sample file
- [ ] Creatures registered via `ModCreatures.addCreature()` during `Initable.init()`

## Hook Point

Creature registration MUST happen during `Initable.init()` — before the server fully starts. Unlike items, creatures cannot be modified after initialization. The `CreatureHandler` must be triggered from `WurmTweaker.init()`, not from a startup listener.

## Research Required Before Implementing

1. **What does `CreatureTemplateBuilder` expose?**
   Check `org.gotti.wurmunlimited.modsupport.CreatureTemplateBuilder` for the full list of settable properties.

2. **What does `ModCreatures.addCreature()` expect?**
   The parameter type and whether it takes a builder or a finished template.

3. **What is the full list of customizable creature properties?**
   Focus on: name, model, combat stats (attack, defence, speed), size, natural armor, vision, behavior flags (aggressive, passive, tameable, etc.).

4. **Are we creating new creatures or overriding existing ones?**
   The CDDA model would suggest both are valid. Confirm whether `CreatureTemplateBuilder` supports overriding existing template IDs.

## Proposed CreatureDefinition Fields (Unconfirmed — research first)

| Field | Type | Description |
|---|---|---|
| `type` | String | Always `"creature"` |
| `id` | String | Creature name/identifier |
| `modelName` | String | 3D model name |
| `maxHp` | Integer | Maximum hit points |
| `attack` | Integer | Attack value |
| `armourType` | Integer | Armor type |
| `speed` | Double | Movement speed |
| `aggressive` | Boolean | Whether creature attacks on sight |
| `tameable` | Boolean | Whether creature can be tamed |

All fields except `type` and `id` are optional.

## Example JSON

`data/creatures/example.json`:
```json
{
  "type": "creature",
  "id": "panda",
  "modelName": "model.creature.panda",
  "aggressive": false,
  "tameable": true
}
```

## Verification
- Drop `example.json`, start server → creature appears in the game's creature list
- Remove the file, restart → creature is gone (additive behavior)
- Invalid JSON → warning logged, server starts normally
