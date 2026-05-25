---
type: reference
tags:
  - reference
  - spawning
related:
  - "[[TASK-004c Creature Spawns]]"
  - "[[TASK-006 Spawn Groups]]"
---

# Spawn Tiles Reference

Use these string values in the `tile` and `elevation` fields of a `spawns` entry (TASK-004c) or a `spawn-group` definition (TASK-006).

---

## Tile Names

| Name | Internal Constant | Notes |
|---|---|---|
| `grass` | `Tiles.Tile.TILE_GRASS` | Standard open grassland |
| `steppe` | `Tiles.Tile.TILE_STEPPE` | Dry open plains |
| `tree` | `Tiles.Tile.TILE_TREE` | Forested ground |
| `sand` | `Tiles.Tile.TILE_SAND` | Sandy ground or beach (see elevation) |
| `clay` | `Tiles.Tile.TILE_CLAY` | Clay-heavy terrain |
| `marsh` | `Tiles.Tile.TILE_MARSH` | Swampy ground |
| `mycelium` | `Tiles.Tile.TILE_MYCELIUM` | Mycelium-infected ground |
| `rock` | `Tiles.Tile.TILE_ROCK` | Rocky shoreline or exposed stone |
| `cave` | `Tiles.Tile.TILE_CAVE` | Underground cave tile |
| `lava` | `Tiles.Tile.TILE_LAVA` | Lava field (surface or cave) |

---

## Elevation Names

| Name | Constant Value | Internal Constant | Notes |
|---|---|---|---|
| `ground` | 0 | `ELEVATION_GROUND` | Standard surface land |
| `water` | 1 | `ELEVATION_WATER` | Shallow water / shoreline |
| `deep-water` | 2 | `ELEVATION_DEEP_WATER` | Deep water |
| `flying` | 3 | `ELEVATION_FLYING` | Low-altitude airborne |
| `flying-high` | 4 | `ELEVATION_FLYING_HIGH` | High-altitude airborne |
| `beach` | 5 | `ELEVATION_BEACH` | Beach / sand at water's edge |
| `cave` | -1 | `ELEVATION_CAVES` | Underground (cave tiles) |

---

## Vanilla Spawn Combinations

These are the tile+elevation pairs used by the base game. Custom creatures can be added to any of these, or to entirely new combinations.

| Tile | Elevation | Vanilla Creatures |
|---|---|---|
| `grass` | `ground` | Cow, wildcat, dog, hen, rooster, calf, bull, pheasant, horse, sheep, ram, unicorn |
| `steppe` | `ground` | Pheasant, horse, wildcat, hell horse, bison, sheep, ram |
| `tree` | `ground` | Pig, wolf, brown bear, hell hound, pheasant, deer, spider, troll, mountain lion |
| `sand` | `ground` | Crocodile, scorpion, hell scorpion, anaconda |
| `sand` | `beach` | Crab, tortoise, seal |
| `clay` | `ground` | Crocodile, anaconda |
| `marsh` | `ground` | Rat, anaconda |
| `mycelium` | `ground` | Spider, rat, dog, wolf, unicorn, hell horse, hell hound |
| `rock` | `water` | Rat, seal, lava creature |
| `cave` | `cave` | Black bear, rat, cave bugs, spider, lava spider, lava creature, mountain lion |
| `lava` | `ground` | Lava spider (group), lava creature |
| `lava` | `cave` | Lava creature |

---

## Notes

- `sand` at `beach` elevation and `sand` at `ground` elevation are two separate spawn pools despite sharing a tile name — the `elevation` field distinguishes them.
- Any tile+elevation combination that doesn't exist in vanilla will be created as a new pool when your creature spawns.
- The `chance` value is a relative weight within a single tile+elevation pool. A chance of 5 on a pool where all other entries sum to 5 means your creature appears 50% of the time that pool is selected.
