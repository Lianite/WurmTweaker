# TASK-004c: Creature Spawn Support

## Goal

Allow new custom creatures (id > 119) to declare their own spawn zones directly in their JSON definition. Spawns are injected into the server's `SpawnTable` via the existing `EncounterBuilder` API after vanilla encounters are built.

## Status

COMPLETE

## Scope

- **In scope:** New custom creatures only (id > 119). Vanilla creature spawn modification is deferred — see TASK-006.
- **Out of scope:** Removing or reweighting vanilla spawns, spawn conditions (time of day, kingdom, etc.), cross-creature spawn groups.

## Deliverables

- [x] `spawns` array added to `CreatureDefinition`
- [x] `SpawnEntry` nested POJO (`tile`, `elevation`, `count`, `chance`)
- [x] `TILE_TYPES` static map in `CreatureHandler` — friendly string names → `Tiles.Tile` byte IDs
- [x] `ELEVATIONS` static map in `CreatureHandler` — friendly string names → `EncounterType` elevation byte constants
- [x] `ModCreature.addEncounters()` implemented on the anonymous inner class to call `EncounterBuilder`
- [x] `obsidian/Reference/Spawn Tiles.md` — user-facing reference for valid tile and elevation names

## JSON Schema

Spawns are an optional array on any new creature definition (id > 119). Silently ignored for vanilla overrides (id ≤ 119) for now.

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

### `spawns[]` fields

| Field | Type | Required | Notes |
|---|---|---|---|
| `tile` | String | Yes | Named tile type — see Spawn Tiles reference |
| `elevation` | String | Yes | Named elevation — see Spawn Tiles reference |
| `count` | Integer | Yes | Number of creatures in the spawned group |
| `chance` | Integer | Yes | Relative weight; higher = more frequent relative to other entries on the same tile |

## Architecture

### Hook Flow

`ModCreatures.init()` (called in `WurmTweaker.init()`) already registers a hook on `SpawnTable.createEncounters()`. After vanilla encounters are built, it calls `addEncounters()` on every registered `ModCreature`. Our anonymous `ModCreature` in `CreatureHandler.apply()` simply needs to override `addEncounters()` to call `EncounterBuilder` for each entry.

```
WurmTweaker.init()
  └─ ModCreatures.init()  ← already done; registers hook on SpawnTable.createEncounters()
  └─ jsonLoader.loadType("creature")
       └─ CreatureHandler.apply(def)
            └─ ModCreatures.addCreature(new ModCreature() {
                 createCreateTemplateBuilder() { ... }   ← already implemented
                 addEncounters() {                        ← NEW
                   for each SpawnEntry in def.spawns:
                     new EncounterBuilder(tileId, elevId)
                       .addCreatures(def.id, entry.count)
                       .build(entry.chance)
                 }
               })

[server boot]
  └─ SpawnTable.createEncounters()  ← vanilla runs first
       └─ [ModCreatures hook fires]
            └─ creature.addEncounters()  ← our code runs here
```

### Name Resolution

Two static maps in a new helper class (or inner class on `CreatureHandler`):

- **TileRegistry**: `"grass"` → `Tiles.Tile.TILE_GRASS.id`, `"tree"` → `Tiles.Tile.TILE_TREE.id`, etc.
- **ElevationRegistry**: `"ground"` → `EncounterType.ELEVATION_GROUND`, `"cave"` → `EncounterType.ELEVATION_CAVES`, etc.

Unknown names log a warning and skip that entry. No silent failures.

### `EncounterBuilder` behaviour (existing, not modified)

- `getType(tiletype, elevation)` checks if an `EncounterType` already exists for that slot.
- If yes — appends the new `Encounter` with the given chance weight to the existing pool.
- If no — creates a new `EncounterType`, registers it via reflection on the private `SpawnTable.addTileType()`, then appends.

This means custom creatures can be added to tiles that already have vanilla encounters (e.g., add a custom wolf variant to `tree/ground`) without affecting existing entries.

## Future Transition (TASK-006)

When the full spawn-group system is implemented:

- The `spawns` array inside creature JSON will be **deprecated** — it will continue to work but emit a deprecation warning.
- The preferred authoring surface becomes a separate `json-type: "spawn-group"` file.
- Internally, both surfaces generate the same runtime structure, so no migration of existing server data is required.

## Verification

1. Build passes: `mvn clean package`
2. Drop a creature JSON with `id: 200` and two `spawns` entries
3. Start server; confirm no errors from `addEncounters()`
4. Unknown tile name logs a warning and is skipped (not a hard failure)
5. Vanilla creatures (id ≤ 119) with a `spawns` block: ignored silently, no warning needed
