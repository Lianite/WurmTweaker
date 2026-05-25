# Spawn Tile Reference

Use these string values in the `tile` and `elevation` fields of a creature's `spawns` entry.

---

## Tile Names

| Name | Description |
|---|---|
| `grass` | Standard open grassland |
| `steppe` | Dry open plains |
| `tree` | Forested ground |
| `sand` | Sandy ground or beach (see elevation) |
| `clay` | Clay terrain |
| `marsh` | Swampy ground |
| `mycelium` | Mycelium-infected ground |
| `rock` | Rocky shoreline or exposed stone |
| `cave` | Underground cave tile |
| `lava` | Lava field (surface or cave) |

---

## Elevation Names

| Name | Description |
|---|---|
| `ground` | Standard surface land |
| `water` | Shallow water / shoreline |
| `deep-water` | Deep water |
| `flying` | Low-altitude airborne |
| `flying-high` | High-altitude airborne |
| `beach` | Sandy beach at the water's edge |
| `cave` | Underground (use with `cave` or `lava` tile) |

---

## Vanilla Spawn Combinations

These tile + elevation pairs already exist in the base game. Adding a `spawns` entry that matches one of these will add your creature to that existing pool alongside the vanilla creatures.

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

Any tile + elevation combination not listed above will create a new spawn pool exclusive to your creature.

---

## Notes

- `sand` at `beach` elevation and `sand` at `ground` elevation are two separate spawn pools.
- The `chance` value is a relative weight within a single tile + elevation pool. A chance of `5` in a pool where all other entries sum to `5` means your creature appears 50% of the time that pool is selected.
- The `count` value is the number of creatures spawned as a group when your creature is selected.

---

## Base Game Spawn Tables

These are the exact spawn pools from the base server (`SpawnTable.createEncounters()`). The **%** column shows each entry's share of that pool before any custom creatures are added — adding your own creatures will reduce all existing percentages proportionally.

Challenge Server entries are included at the bottom of each affected pool but do not apply to standard servers.

> **Note on Sheep + Ram entries:** In the base game code, the "Sheep" encounter spawns *both* a Sheep (id 96) and a Ram (id 102) together when selected. A separate "Ram" encounter object exists in the code but was never assigned any creatures (a base game bug), so it always results in no spawn when rolled.

---

### `grass` / `ground` — Total chance weight: 18

| Creature | Template ID | Count | Chance | % |
|---|---|---|---|---|
| Brown Cow | 3 | 1 | 1 | 5.6% |
| Wildcat | 15 | 2 | 2 | 11.1% |
| Dog | 51 | 2 | 3 | 16.7% |
| Hen | 45 | 3 | 1 | 5.6% |
| Rooster | 52 | 1 | 1 | 5.6% |
| Calf | 50 | 1 | 1 | 5.6% |
| Bull | 49 | 3 | 1 | 5.6% |
| Pheasant | 55 | 2 | 1 | 5.6% |
| Horse | 64 | 2 | 2 | 11.1% |
| Sheep + Ram *(see note above)* | 96 + 102 | 1 each | 3 | 16.7% |
| *(no spawn — base game bug)* | — | — | 1 | 5.6% |
| Unicorn | 21 | 1 | 1 | 5.6% |

---

### `sand` / `beach` — Total chance weight: 12

| Creature | Template ID | Count | Chance | % |
|---|---|---|---|---|
| Crab | 95 | 1 | 8 | 66.7% |
| Seal | 93 | 2 | 3 | 25.0% |
| Tortoise | 94 | 1 | 1 | 8.3% |
| Deathcrawler *(Challenge Server)* | 73 | 1 | 1 | — |
| Uttacha *(Challenge Server)* | 74 | 1 | 1 | — |

---

### `rock` / `water` — Total chance weight: 10

| Creature | Template ID | Count | Chance | % |
|---|---|---|---|---|
| *(no spawn — intentional)* | — | — | 5 | 50.0% |
| Large Rat | 13 | 3 | 2 | 20.0% |
| Seal | 93 | 2 | 2 | 20.0% |
| Lava Creature | 57 | 1 | 1 | 10.0% |
| Uttacha *(Challenge Server)* | 74 | 1 | 1 | — |

---

### `mycelium` / `ground` — Total chance weight: 11

| Creature | Template ID | Count | Chance | % |
|---|---|---|---|---|
| Spider | 25 | 6 | 4 | 36.4% |
| Large Rat | 13 | 3 | 2 | 18.2% |
| Dog | 51 | 2 | 1 | 9.1% |
| Black Wolf | 10 | 4 | 1 | 9.1% |
| Unicorn | 21 | 1 | 1 | 9.1% |
| Hell Horse | 83 | 1 | 1 | 9.1% |
| Hell Hound | 84 | 1 | 1 | 9.1% |
| Demon Sol *(Challenge Server)* | 72 | 1 | 1 | — |
| Deathcrawler *(Challenge Server)* | 73 | 1 | 1 | — |

---

### `marsh` / `ground` — Total chance weight: 3

| Creature | Template ID | Count | Chance | % |
|---|---|---|---|---|
| Large Rat | 13 | 3 | 2 | 66.7% |
| Anaconda | 38 | 1 | 1 | 33.3% |
| Son of Nogump *(Challenge Server)* | 75 | 1 | 1 | — |
| Demon Sol *(Challenge Server)* | 72 | 1 | 1 | — |

---

### `steppe` / `ground` — Total chance weight: 10

| Creature | Template ID | Count | Chance | % |
|---|---|---|---|---|
| Horse | 64 | 2 | 4 | 40.0% |
| Pheasant | 55 | 2 | 1 | 10.0% |
| Wildcat | 15 | 2 | 1 | 10.0% |
| Hell Horse | 83 | 1 | 1 | 10.0% |
| Bison | 82 | 10 | 1 | 10.0% |
| Sheep + Ram *(see note above)* | 96 + 102 | 1 each | 1 | 10.0% |
| *(no spawn — base game bug)* | — | — | 1 | 10.0% |
| Drake Spirit *(Challenge Server)* | 76 | 1 | 1 | — |
| Eagle Spirit *(Challenge Server)* | 77 | 1 | 1 | — |

---

### `tree` / `ground` — Total chance weight: 10

| Creature | Template ID | Count | Chance | % |
|---|---|---|---|---|
| Spider | 25 | 6 | 2 | 20.0% |
| Pig | 44 | 3 | 1 | 10.0% |
| Black Wolf | 10 | 4 | 1 | 10.0% |
| Brown Bear | 12 | 2 | 1 | 10.0% |
| Hell Hound | 84 | 1 | 1 | 10.0% |
| Pheasant | 55 | 2 | 1 | 10.0% |
| Deer | 54 | 2 | 1 | 10.0% |
| Troll | 11 | 1 | 1 | 10.0% |
| Mountain Lion | 14 | 2 | 1 | 10.0% |
| Demon Sol *(Challenge Server)* | 72 | 1 | 1 | — |
| Deathcrawler *(Challenge Server)* | 73 | 1 | 1 | — |

---

### `sand` / `ground` — Total chance weight: 22

| Creature | Template ID | Count | Chance | % |
|---|---|---|---|---|
| Crocodile | 58 | 1 | 10 | 45.5% |
| Scorpion | 59 | 3 | 10 | 45.5% |
| Hell Scorpion | 85 | 1 | 1 | 4.5% |
| Anaconda | 38 | 1 | 1 | 4.5% |

---

### `clay` / `ground` — Total chance weight: 11

| Creature | Template ID | Count | Chance | % |
|---|---|---|---|---|
| Crocodile | 58 | 1 | 10 | 90.9% |
| Anaconda | 38 | 1 | 1 | 9.1% |

---

### `cave` / `cave` — Total chance weight: 20

| Creature | Template ID | Count | Chance | % |
|---|---|---|---|---|
| Cave Bug | 43 | 5 | 5 | 25.0% |
| Black Bear | 42 | 2 | 4 | 20.0% |
| Lava Creature | 57 | 1 | 4 | 20.0% |
| Large Rat | 13 | 3 | 2 | 10.0% |
| Spider | 25 | 6 | 2 | 10.0% |
| Lava Spider | 56 | 1 | 2 | 10.0% |
| Mountain Lion | 14 | 2 | 1 | 5.0% |

---

### `lava` / `ground` — Total chance weight: 20

| Creature | Template ID | Count | Chance | % |
|---|---|---|---|---|
| Lava Spider | 56 | 10 | 10 | 50.0% |
| Lava Creature | 57 | 1 | 10 | 50.0% |

---

### `lava` / `cave` — Total chance weight: 10

| Creature | Template ID | Count | Chance | % |
|---|---|---|---|---|
| Lava Creature | 57 | 1 | 10 | 100.0% |
