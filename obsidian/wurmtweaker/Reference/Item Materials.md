---
type: reference
tags:
  - reference
  - items
related:
  - "[[TASK-005 Items]]"
  - "[[Item Template Fields]]"
---

# Item Material Reference

Constants for the `material` field in item JSON definitions. Sourced from `com.wurmonline.shared.constants.ItemMaterials`.

Use one integer value in the `material` field.

**Java type:** `byte` — stored as a signed byte on the template. Values only go 0–96, well within byte range.

**Gap:** 24 is unused (no constant defined — jumps from 23=FIRE to 25=OIL).

**`MATERIAL_MAX = 96`**

---

## Metals

| ID | Constant | String Alias |
|----|----------|-------------|
| 7  | MATERIAL_GOLD | `gold` |
| 8  | MATERIAL_SILVER | `silver` |
| 9  | MATERIAL_STEEL | `steel` |
| 10 | MATERIAL_COPPER | `copper` |
| 11 | MATERIAL_IRON | `iron` |
| 12 | MATERIAL_LEAD | `lead` |
| 13 | MATERIAL_ZINC | `zinc` |
| 30 | MATERIAL_BRASS | `brass` |
| 31 | MATERIAL_BRONZE | `bronze` |
| 34 | MATERIAL_TIN | `tin` |
| 56 | MATERIAL_ADAMANTINE | `adamantine` |
| 57 | MATERIAL_GLIMMERSTEEL | `glimmersteel` |
| 67 | MATERIAL_SERYLL | `seryll` |
| 93 | MATERIAL_METALFRAG_BASE | `metal` |
| 94 | MATERIAL_METALFRAG_ALLOY | `alloy` |
| 95 | MATERIAL_METALFRAG_MOON | `moonmetal` |
| 96 | MATERIAL_ELECTRUM | `electrum` |

## Wood

| ID | Constant | String Alias |
|----|----------|-------------|
| 14 | MATERIAL_WOOD_BIRCH | `birchwood` |
| 37 | MATERIAL_WOOD_PINE | `pinewood` |
| 38 | MATERIAL_WOOD_OAK | `oakenwood` |
| 39 | MATERIAL_WOOD_CEDAR | `cedarwood` |
| 40 | MATERIAL_WOOD_WILLOW | `willow` |
| 41 | MATERIAL_WOOD_MAPLE | `maplewood` |
| 42 | MATERIAL_WOOD_APPLE | `applewood` |
| 43 | MATERIAL_WOOD_LEMON | `lemonwood` |
| 44 | MATERIAL_WOOD_OLIVE | `olivewood` |
| 45 | MATERIAL_WOOD_CHERRY | `cherrywood` |
| 46 | MATERIAL_WOOD_LAVENDER | `lavenderwood` |
| 47 | MATERIAL_WOOD_ROSE | `rosewood` |
| 48 | MATERIAL_WOOD_THORN | `thorn` |
| 49 | MATERIAL_WOOD_GRAPE | `grapewood` |
| 50 | MATERIAL_WOOD_CAMELLIA | `camelliawood` |
| 51 | MATERIAL_WOOD_OLEANDER | `oleanderwood` |
| 63 | MATERIAL_WOOD_CHESTNUT | `chestnut` |
| 64 | MATERIAL_WOOD_WALNUT | `walnut` |
| 65 | MATERIAL_WOOD_FIR | `firwood` |
| 66 | MATERIAL_WOOD_LINDEN | `lindenwood` |
| 68 | MATERIAL_WOOD_IVY | `ivy` |
| 71 | MATERIAL_WOOD_HAZELNUT | `hazelnutwood` |
| 88 | MATERIAL_WOOD_ORANGE | `orangewood` |
| 90 | MATERIAL_WOOD_RASPBERRY | `raspberrywood` |
| 91 | MATERIAL_WOOD_BLUEBERRY | `blueberrywood` |
| 92 | MATERIAL_WOOD_LINGONBERRY | `lingonberrywood` |

## Stone & Minerals

| ID | Constant | String Alias |
|----|----------|-------------|
| 15 | MATERIAL_STONE | `stone` |
| 52 | MATERIAL_CRYSTAL | `crystal` |
| 54 | MATERIAL_DIAMOND | `diamond` |
| 61 | MATERIAL_SLATE | `slate` |
| 62 | MATERIAL_MARBLE | `marble` |
| 89 | MATERIAL_SANDSTONE | `sandstone` |

## Organic & Textile

| ID | Constant | String Alias |
|----|----------|-------------|
| 16 | MATERIAL_LEATHER | `leather` |
| 17 | MATERIAL_COTTON | `cotton` |
| 33 | MATERIAL_PAPER | `paper` |
| 35 | MATERIAL_BONE | `bone` |
| 53 | MATERIAL_WEMP | `wemp` |
| 55 | MATERIAL_ANIMAL | `animal` |
| 60 | MATERIAL_REED | `reed` |
| 69 | MATERIAL_WOOL | `wool` |
| 70 | MATERIAL_STRAW | `straw` |

## Food & Grain

| ID | Constant | String Alias |
|----|----------|-------------|
| 1  | MATERIAL_FLESH | `flesh` |
| 2  | MATERIAL_MEAT | `meat` |
| 3  | MATERIAL_RYE | `rye` |
| 4  | MATERIAL_OAT | `oat` |
| 5  | MATERIAL_BARLEY | `barley` |
| 6  | MATERIAL_WHEAT | `wheat` |
| 22 | MATERIAL_VEGETARIAN | `vegetarian` |
| 28 | MATERIAL_DAIRY | `dairy` |
| 29 | MATERIAL_HONEY | `honey` |
| 32 | MATERIAL_FAT | `fat` |
| 36 | MATERIAL_SALT | `salt` |

## Meat Subtypes

| ID | Constant | String Alias |
|----|----------|-------------|
| 72 | MATERIAL_MEAT_BEAR | `bear` |
| 73 | MATERIAL_MEAT_BEEF | `beef` |
| 74 | MATERIAL_MEAT_CANINE | `canine` |
| 75 | MATERIAL_MEAT_CAT | `feline` |
| 76 | MATERIAL_MEAT_DRAGON | `dragon` |
| 77 | MATERIAL_MEAT_FOWL | `fowl` |
| 78 | MATERIAL_MEAT_GAME | `game` |
| 79 | MATERIAL_MEAT_HORSE | `horse` |
| 80 | MATERIAL_MEAT_HUMAN | `human` |
| 81 | MATERIAL_MEAT_HUMANOID | `humanoid` |
| 82 | MATERIAL_MEAT_INSECT | `insect` |
| 83 | MATERIAL_MEAT_LAMB | `lamb` |
| 84 | MATERIAL_MEAT_PORK | `pork` |
| 85 | MATERIAL_MEAT_SEAFOOD | `seafood` |
| 86 | MATERIAL_MEAT_SNAKE | `snake` |
| 87 | MATERIAL_MEAT_TOUGH | `tough` |

## Liquids & Elements

| ID | Constant | String Alias |
|----|----------|-------------|
| 18 | MATERIAL_CLAY | `clay` |
| 19 | MATERIAL_POTTERY | `pottery` |
| 20 | MATERIAL_GLASS | `glass` |
| 21 | MATERIAL_MAGIC | `magic` |
| 23 | MATERIAL_FIRE | `fire` |
| 25 | MATERIAL_OIL | `oil` |
| 26 | MATERIAL_WATER | `water` |
| 27 | MATERIAL_COAL | `charcoal` |
| 58 | MATERIAL_TAR | `tar` |
| 59 | MATERIAL_PEAT | `peat` |

## Unused / Reserved

| ID | Notes |
|----|-------|
| 0  | MATERIAL_UNDEFINED |
| 24 | No constant defined |
