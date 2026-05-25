---
id: TASK-004c
title: Creature Spawn Support
status: Complete
phase: 2
tags:
  - task
  - planned
related:
  - "[[TASK-004 Creatures]]"
  - "[[TASK-006 Spawn Groups]]"
  - "[[Spawn Tiles]]"
---

# TASK-004c: Creature Spawn Support

## Goal

Allow new custom creatures (id > 119) to declare where they spawn directly in their JSON definition, using the modlauncher's existing `EncounterBuilder` API.

## Scope

New custom creatures only. Vanilla spawn modification (remove/reweight) is deferred to [[TASK-006 Spawn Groups]].

## JSON Shape

```json
{
  "json-type": "creature",
  "id": 200,
  "name": "Mud Crab",
  "modelName": "model.creature.crab",
  "spawns": [
    { "tile": "sand",  "elevation": "beach",  "count": 2, "chance": 8 },
    { "tile": "clay",  "elevation": "ground", "count": 1, "chance": 3 }
  ]
}
```

See [[Spawn Tiles]] for valid `tile` and `elevation` string values.

## How It Works

`ModCreatures.init()` (already called) hooks `SpawnTable.createEncounters()`. After vanilla spawns are built, it calls `addEncounters()` on each registered `ModCreature`. This task implements that method on our anonymous `ModCreature` to call `EncounterBuilder` for each spawn entry.

## Deliverables

- [x] `spawns` array and `SpawnEntry` POJO added to `CreatureDefinition`
- [x] `addEncounters()` implemented on the `ModCreature` registered in `CreatureHandler`
- [x] Tile and elevation name → byte ID registry (static maps in `CreatureHandler`)
- [x] Unknown name → warning + skip (no hard failures)
- [x] `obsidian/Reference/Spawn Tiles.md` reference doc

## Future

When [[TASK-006 Spawn Groups]] is implemented, the `spawns` array inside creature JSON will be **deprecated** (with a warning) in favour of the dedicated `spawn-group` JSON type. Existing data continues to work through the deprecation period.
