---
type: reference
tags:
  - reference
  - items
related:
  - "[[TASK-005 Items]]"
  - "[[Item Template Fields]]"
---

# Item Type Reference

Constants for the `itemTypes` array in item JSON definitions. Sourced from `com.wurmonline.server.items.ItemTypes`.

Use the integer values in the `itemTypes` array — an item can have multiple types. There are 167 boolean properties on `ItemTemplate` all derived from checking whether a given constant is present in this array, so getting it right is critical.

**Java type:** `short` — values are stored as shorts on the template. The JSON schema accepts integers and casts at apply time.

**Gaps:** 43, 107, and 202–204 are unused (no constant defined).

---

## Weapons

| ID | Constant | Notes |
|----|----------|-------|
| 2  | ITEM_TYPE_WEAPON_SLASH | Slashing weapon |
| 13 | ITEM_TYPE_WEAPON_PIERCE | Piercing weapon |
| 14 | ITEM_TYPE_WEAPON_CRUSH | Crushing weapon |
| 15 | ITEM_TYPE_WEAPON_AXE | Axe subtype |
| 16 | ITEM_TYPE_WEAPON_SWORD | Sword subtype |
| 17 | ITEM_TYPE_WEAPON_KNIFE | Knife subtype |
| 18 | ITEM_TYPE_WEAPON_MISC | Miscellaneous weapon |
| 35 | ITEM_TYPE_WEAPON_MELEE | Generic melee weapon |
| 37 | ITEM_TYPE_WEAPON | Generic weapon flag |
| 84 | ITEM_TYPE_TWOHANDED | Two-handed weapon |
| 94 | ITEM_TYPE_WEAPON_BOW | Bow (strung) |
| 95 | ITEM_TYPE_WEAPON_BOW_UNSTRINGED | Bow (unstrung) |
| 154 | ITEM_TYPE_WEAPON_POLEARM | Polearm |

## Armor & Protection

| ID | Constant | Notes |
|----|----------|-------|
| 3  | ITEM_TYPE_SHIELD | Shield |
| 4  | ITEM_TYPE_ARMOUR | Armor piece |
| 99 | ITEM_TYPE_DRAGONARMOUR | Dragon armor |
| 136 | ITEM_TYPE_CREATURE_WEARABLE | Can be worn by creatures |

## Tools

| ID | Constant | Notes |
|----|----------|-------|
| 7  | ITEM_TYPE_TOOL_FIELD | Farming tool |
| 10 | ITEM_TYPE_TOOL_MINING | Mining tool |
| 11 | ITEM_TYPE_TOOL_CARPENTRY | Carpentry tool |
| 12 | ITEM_TYPE_TOOL_SMITHING | Smithing tool |
| 19 | ITEM_TYPE_TOOL_DIGGING | Digging tool |
| 38 | ITEM_TYPE_TOOL | Generic tool flag |
| 125 | ITEM_TYPE_DREDGE | Dredge tool |
| 210 | ITEM_TYPE_TOOL_COOKING | Cooking tool |

## Food & Drink

| ID | Constant | Notes |
|----|----------|-------|
| 5  | ITEM_TYPE_FOOD | Food item |
| 20 | ITEM_TYPE_SEED | Plantable seed |
| 28 | ITEM_TYPE_MEAT | Generic meat |
| 29 | ITEM_TYPE_VEGETABLE | Vegetable |
| 36 | ITEM_TYPE_FISH | Fish |
| 55 | ITEM_TYPE_LOWNUTRITION | Low nutrition food |
| 74 | ITEM_TYPE_MEDIUMNUTRITION | Medium nutrition food |
| 75 | ITEM_TYPE_GOODNUTRITION | Good nutrition food |
| 76 | ITEM_TYPE_HIGHNUTRITION | High nutrition food |
| 77 | ITEM_TYPE_FOODMAKER | Can make food |
| 78 | ITEM_TYPE_HERB | Herb |
| 79 | ITEM_TYPE_POISON | Poisonous |
| 80 | ITEM_TYPE_FRUIT | Fruit |
| 82 | ITEM_TYPE_DISH | Prepared dish |
| 88 | ITEM_TYPE_LIQUID_COOKING | Cooking liquid (e.g. water for boiling) |
| 90 | ITEM_TYPE_LIQUID_DRINKABLE | Drinkable liquid |
| 96 | ITEM_TYPE_EGG | Egg |
| 137 | ITEM_TYPE_NONUTRITION | Has no nutritional value |
| 191 | ITEM_TYPE_MILK | Milk |
| 192 | ITEM_TYPE_CHEESE | Cheese |
| 205 | ITEM_TYPE_SPICE | Spice ingredient |
| 206 | ITEM_TYPE_POTABLE | Potable liquid |
| 208 | ITEM_TYPE_FOOD_GROUP | Food group item |
| 209 | ITEM_TYPE_COOKER | Cooker/stove |
| 211 | ITEM_TYPE_RECIPE_ITEM | Used in recipes |
| 212 | ITEM_TYPE_USES_FOOD_STATE | Tracks food state (raw/cooked/etc.) |
| 213 | ITEM_TYPE_FERMENTED | Fermented food/drink |
| 214 | ITEM_TYPE_DISTILLED | Distilled drink |
| 217 | ITEM_TYPE_COOKING_OIL | Cooking oil |
| 219 | ITEM_TYPE_FOOD_BONUS_HOT | Bonus when served hot |
| 220 | ITEM_TYPE_FOOD_BONUS_COLD | Bonus when served cold |
| 226 | ITEM_TYPE_MUSHROOM | Mushroom |

## Liquids

| ID | Constant | Notes |
|----|----------|-------|
| 26 | ITEM_TYPE_LIQUID | Liquid item |
| 33 | ITEM_TYPE_CONTAINER_LIQUID | Container for liquids |
| 34 | ITEM_TYPE_LIQUID_INFLAMMABLE | Inflammable liquid |
| 128 | ITEM_TYPE_SPRINGFILLED | Filled from a spring |

## Containers & Storage

| ID | Constant | Notes |
|----|----------|-------|
| 1  | ITEM_TYPE_HOLLOW | Has an interior (container) |
| 9  | ITEM_TYPE_INVENTORY | Inventory container |
| 145 | ITEM_TYPE_BULKCONTAINER | Bulk storage container |
| 146 | ITEM_TYPE_BULK | Bulk item (goes in bulk container) |
| 180 | ITEM_TYPE_USES_SPECIFIED_CONTAINER_VOLUME | Uses explicit container volume |
| 234 | ITEM_TYPE_LARDER | Larder (food storage) |
| 256 | ITEM_TYPE_VIEWABLE_SUBITEMS | Container contents visible |
| 259 | ITEM_TYPE_HOLLOW_VIEWABLE | Hollow with visible contents |

## Lighting

| ID | Constant | Notes |
|----|----------|-------|
| 32 | ITEM_TYPE_LIGHT | Emits light |
| 65 | ITEM_TYPE_FIRE | Is on fire |
| 101 | ITEM_TYPE_OILCONSUMING | Consumes oil for light |
| 115 | ITEM_TYPE_FLICKERING | Flickering light |
| 116 | ITEM_TYPE_LIGHT_BRIGHT | Bright light source |
| 143 | ITEM_TYPE_STREETLAMP | Street lamp |
| 156 | ITEM_TYPE_ALWAYS_LIT | Always lit |
| 179 | ITEM_TYPE_BRAZIER | Brazier |

## Placement & Tile

| ID | Constant | Notes |
|----|----------|-------|
| 49 | ITEM_TYPE_OUTSIDE_ONLY | Can only be placed outside |
| 52 | ITEM_TYPE_DECORATION | Decoration item |
| 67 | ITEM_TYPE_USE_GROUND_ONLY | Can only be used on ground |
| 98 | ITEM_TYPE_TILE_ALIGNED | Must align to tile grid |
| 109 | ITEM_TYPE_ONE_PER_TILE | Only one allowed per tile |
| 111 | ITEM_TYPE_INSIDE_ONLY | Can only be placed inside |
| 123 | ITEM_TYPE_NOMOVE | Cannot be moved |
| 142 | ITEM_TYPE_SIGN | Sign (has text) |
| 166 | ITEM_TYPE_TEN_PER_TILE | Up to 10 per tile |
| 167 | ITEM_TYPE_FOUR_PER_TILE | Up to 4 per tile |
| 185 | ITEM_TYPE_ONE_PER_TILEBORDER | One per tile border |
| 225 | ITEM_TYPE_SURFACE_ONLY | Surface placement only |
| 240 | ITEM_TYPE_PARENT_ON_GROUND | Parent must be on ground |
| 244 | ITEM_TYPE_DECORATION_WHEN_PLANTED | Becomes decoration when planted |

## Nature & Farming

| ID | Constant | Notes |
|----|----------|-------|
| 118 | ITEM_TYPE_FLOWER | Flower |
| 149 | ITEM_TYPE_SPAWNSTREES | Spawns trees when used |
| 150 | ITEM_TYPE_KILLSTREES | Kills trees when used |
| 169 | ITEM_TYPE_PLANTED_FLOWERPOT | Planted flower pot |
| 186 | ITEM_TYPE_NATURE_PLANTABLE | Plantable (nature objects) |
| 199 | ITEM_TYPE_PLANTABLE | Can be planted |
| 200 | ITEM_TYPE_PLANT_ONE_A_WEEK | Plant limit: one per week |
| 221 | ITEM_TYPE_POTTED | Potted plant |
| 227 | ITEM_TYPE_HARVESTABLE | Can be harvested |
| 230 | ITEM_TYPE_TRELLIS | Trellis |

## Crafting Flags

| ID | Constant | Notes |
|----|----------|-------|
| 27 | ITEM_TYPE_MELTING | Can be melted down |
| 30 | ITEM_TYPE_POTTERY | Pottery item |
| 44 | ITEM_TYPE_REPAIRABLE | Can be repaired |
| 46 | ITEM_TYPE_COMBINE | Can be combined with same type |
| 87 | ITEM_TYPE_MATERIAL_PRICEEFFECT | Material affects price |
| 119 | ITEM_TYPE_IMPROVEITEM | Can be improved |
| 141 | ITEM_TYPE_TRANSMUTABLE | Can be transmuted |
| 148 | ITEM_TYPE_COMBINECOLD | Can be combined when cold |
| 151 | ITEM_TYPE_CRUDE | Crude quality |
| 152 | ITEM_TYPE_MINABLE | Can be mined |
| 158 | ITEM_TYPE_MASSPRODUCTION | Mass production allowed |
| 160 | ITEM_TYPE_NOWORKPARENT | Parent cannot receive work |
| 173 | ITEM_TYPE_IMPROVE_USES_TYPE_AS_MATERIAL | Improvement uses type as material |
| 187 | ITEM_TYPE_NO_IMPROVE | Cannot be improved |
| 196 | ITEM_TYPE_UNFIRED | Unfired state (pottery) |
| 207 | ITEM_TYPE_NO_CREATE | Cannot be created in creation window |
| 232 | ITEM_TYPE_COMPONENT_ITEM | Component item |
| 233 | ITEM_TYPE_USES_REAL_TEMPLATE | Uses real template |
| 257 | ITEM_TYPE_CREATES_WITH_LOCK | Created with a lock |

## Materials & Colors

| ID | Constant | Notes |
|----|----------|-------|
| 21 | ITEM_TYPE_WOOD | Wood material |
| 22 | ITEM_TYPE_METAL | Metal material |
| 23 | ITEM_TYPE_LEATHER | Leather material |
| 24 | ITEM_TYPE_CLOTH | Cloth material |
| 25 | ITEM_TYPE_STONE | Stone material |
| 91 | ITEM_TYPE_COLOR | Has color |
| 92 | ITEM_TYPE_COLORABLE | Can be dyed |
| 164 | ITEM_TYPE_COLORCOMPONENT | Color component |
| 183 | ITEM_TYPE_SMEARABLE | Can be smeared |
| 184 | ITEM_TYPE_CARPET | Carpet |
| 188 | ITEM_TYPE_TAPESTRY | Tapestry |
| 249 | ITEM_TYPE_SUPPORTS_SECONDARY_COLOR | Has secondary color |

## Vehicles & Transport

| ID | Constant | Notes |
|----|----------|-------|
| 117 | ITEM_TYPE_VEHICLE | Is a vehicle |
| 134 | ITEM_TYPE_VEHICLE_DRAGGED | Dragged vehicle |
| 176 | ITEM_TYPE_TRANSPORTABLE | Can be transported by vehicle |
| 177 | ITEM_TYPE_WARMACHINE | War machine |
| 193 | ITEM_TYPE_CART | Cart |
| 201 | ITEM_TYPE_HITCH_TARGET | Can be hitched to |

## Creatures & Butchery

| ID | Constant | Notes |
|----|----------|-------|
| 62 | ITEM_TYPE_BUTCHERED | Product of butchering |
| 64 | ITEM_TYPE_LEADCREATURE | Used to lead a creature |
| 198 | ITEM_TYPE_LEAD_MULTIPLE_CREATURES | Can lead multiple creatures |

## Locks & Keys

| ID | Constant | Notes |
|----|----------|-------|
| 39 | ITEM_TYPE_LOCK | Is a lock |
| 41 | ITEM_TYPE_KEY | Is a key |
| 47 | ITEM_TYPE_LOCKABLE | Can be locked |

## Trade & Economy

| ID | Constant | Notes |
|----|----------|-------|
| 50 | ITEM_TYPE_COIN | Currency coin |
| 53 | ITEM_TYPE_FULLPRICE | Always sells at full price |
| 61 | ITEM_TYPE_NOTRADE | Cannot be traded |
| 85 | ITEM_TYPE_KINGDOM_MARKER | Kingdom marker |
| 122 | ITEM_TYPE_ROYAL | Royal item |
| 127 | ITEM_TYPE_NOSELLBACK | Cannot be sold back |
| 155 | ITEM_TYPE_ALWAYS_BANKABLE | Always bankable |
| 161 | ITEM_TYPE_WARTARGET | War target |

## Player Restrictions

| ID | Constant | Notes |
|----|----------|-------|
| 40 | ITEM_TYPE_INDESTRUCTIBLE | Cannot be destroyed |
| 42 | ITEM_TYPE_NODROP | Cannot be dropped |
| 54 | ITEM_TYPE_NORENAME | Cannot be renamed |
| 56 | ITEM_TYPE_DRAGGABLE | Can be dragged |
| 63 | ITEM_TYPE_NOPUT | Cannot be put in containers |
| 97 | ITEM_TYPE_NEWBIEITEM | New player item |
| 112 | ITEM_TYPE_NOBANK | Cannot be banked |
| 113 | ITEM_TYPE_RECYCLED | Recycled item |
| 120 | ITEM_TYPE_DEATHPROT | Protected on death |
| 159 | ITEM_TYPE_CAN_HAVE_INSCRIPTION | Accepts inscription |
| 174 | ITEM_TYPE_NODISCARD | Cannot be discarded |
| 175 | ITEM_TYPE_INSTADISCARD | Instantly discarded |
| 190 | ITEM_TYPE_UNFINISHED_NOTAKE | Unfinished — cannot take |

## Special & Magical

| ID | Constant | Notes |
|----|----------|-------|
| 6  | ITEM_TYPE_MAGIC | Magic item |
| 66 | ITEM_TYPE_DOMAIN | Domain item |
| 68 | ITEM_TYPE_HUGEALTAR | Huge altar |
| 69 | ITEM_TYPE_ARTIFACT | Artifact |
| 70 | ITEM_TYPE_UNIQUE | Unique item |
| 71 | ITEM_TYPE_DESTROY_HUGEALTAR | Destroys huge altar |
| 93 | ITEM_TYPE_GEM | Gem |
| 100 | ITEM_TYPE_COMPASS | Compass |
| 130 | ITEM_TYPE_RECHARGEABLE | Rechargeable |
| 131 | ITEM_TYPE_SERVERPORTAL | Server portal |
| 132 | ITEM_TYPE_TRAP | Trap |
| 133 | ITEM_TYPE_DISARM_TRAP | Can disarm traps |
| 138 | ITEM_TYPE_PUPPET | Puppet |
| 139 | ITEM_TYPE_OVERRIDEENCHANT | Overrides enchantment |
| 140 | ITEM_TYPE_MEDITATION | Meditation item |
| 153 | ITEM_TYPE_ENCHANT_JEWELRY | Enchantable jewelry |
| 162 | ITEM_TYPE_SOURCESPRING | Source spring |
| 163 | ITEM_TYPE_SOURCE | Source item |
| 168 | ITEM_TYPE_ABILITY | Ability item |
| 172 | ITEM_TYPE_MAGICAL_STAFF | Magical staff |
| 235 | ITEM_TYPE_RUNE | Rune |
| 236 | ITEM_TYPE_PEGABLE | Can be pegged |

## Decay & Lifecycle

| ID | Constant | Notes |
|----|----------|-------|
| 45 | ITEM_TYPE_TEMPORARY | Temporary (decays quickly) |
| 48 | ITEM_TYPE_HASDATA | Has extra persistent data |
| 59 | ITEM_TYPE_ALWAYSPOLL | Always polled each tick |
| 60 | ITEM_TYPE_FLOATING | Floats on water |
| 72 | ITEM_TYPE_PASS_FULLDATA | Passes full data |
| 89 | ITEM_TYPE_POSITIVE_DECAY | Improves with age |
| 114 | ITEM_TYPE_LOADED | Loaded state |
| 124 | ITEM_TYPE_WIND | Wind-affected |
| 129 | ITEM_TYPE_DECAYDESTROYS | Destroyed when fully decayed |
| 135 | ITEM_TYPE_OWNER_DESTROYABLE | Owner can destroy |
| 144 | ITEM_TYPE_VISIBLEDECAY | Decay is visually visible |
| 237 | ITEM_TYPE_DECAY_ON_DEED | Decays on deed |
| 238 | ITEM_TYPE_INSULATED | Thermally insulated |

## Furniture & Housing

| ID | Constant | Notes |
|----|----------|-------|
| 8  | ITEM_TYPE_BODYPART | Body part |
| 58 | ITEM_TYPE_HOMESTEADDEED | Homestead deed |
| 57 | ITEM_TYPE_VILLAGEDEED | Village deed |
| 110 | ITEM_TYPE_BED | Bed |
| 121 | ITEM_TYPE_TOOLBELT | Toolbelt |
| 126 | ITEM_TYPE_MINEDOOR | Mine door |
| 165 | ITEM_TYPE_TUTORIAL | Tutorial item |
| 170 | ITEM_TYPE_EQUIPMENTSLOT | Equipment slot item |
| 171 | ITEM_TYPE_INVENTORY_GROUP | Inventory group |
| 194 | ITEM_TYPE_OWNER_TURNABLE | Owner can turn |
| 195 | ITEM_TYPE_OWNER_MOVEABLE | Owner can move |
| 197 | ITEM_TYPE_CHAIR | Chair |

## Fishing

| ID | Constant | Notes |
|----|----------|-------|
| 250 | ITEM_TYPE_FISHING_REEL | Fishing reel |
| 251 | ITEM_TYPE_FISHING_LINE | Fishing line |
| 252 | ITEM_TYPE_FISHING_FLOAT | Fishing float |
| 253 | ITEM_TYPE_FISHING_HOOK | Fishing hook |
| 254 | ITEM_TYPE_FISHING_BAIT | Fishing bait |

## Display & UI

| ID | Constant | Notes |
|----|----------|-------|
| 51 | ITEM_TYPE_TURNABLE | Can be turned by all |
| 73 | ITEM_TYPE_FORM | Has form data |
| 81 | ITEM_TYPE_DESC_IS_EXAM | Description is examine text |
| 83 | ITEM_TYPE_SERVERBOUND | Server-bound |
| 108 | ITEM_TYPE_NAMED | Has a name |
| 147 | ITEM_TYPE_MISSION | Mission item |
| 157 | ITEM_TYPE_NOT_MISSION | Not a mission item |
| 216 | ITEM_TYPE_USE_REAL_TEMPLATE_ICON | Uses real template icon |
| 218 | ITEM_TYPE_HOVER | Hover effect |
| 222 | ITEM_TYPE_CAN_BE_PAPYRUS_WRAPPED | Can be papyrus-wrapped |
| 223 | ITEM_TYPE_CAN_BE_RAW_WRAPPED | Can be raw-wrapped |
| 224 | ITEM_TYPE_CAN_BE_CLOTH_WRAPPED | Can be cloth-wrapped |
| 228 | ITEM_TYPE_SHOW_RAW | Shows raw state |
| 229 | ITEM_TYPE_NOT_SPELL_TARGET | Cannot be targeted by spells |
| 231 | ITEM_TYPE_INGREDIENTS_ONLY | Ingredients only |
| 241 | ITEM_TYPE_ROAD_MARKER | Road marker |
| 242 | ITEM_TYPE_PAVEABLE | Can be used to pave |
| 243 | ITEM_TYPE_CAVE_PAVEABLE | Can pave caves |
| 245 | ITEM_TYPE_DESC_IS_NAME | Description is item name |
| 246 | ITEM_TYPE_NOT_RUNEABLE | Cannot have runes applied |
| 247 | ITEM_TYPE_SHOWS_SLOPES | Shows slope information |
| 248 | ITEM_TYPE_PLURAL_NAME | Has a plural name |
| 255 | ITEM_TYPE_HAS_EXTRA_DATA | Has extra data field |
| 258 | ITEM_TYPE_BRACELET | Bracelet |

## Healing

| ID | Constant | Notes |
|----|----------|-------|
| 102 | ITEM_TYPE_HEALING_POWER_1 | Healing power level 1 |
| 103 | ITEM_TYPE_HEALING_POWER_2 | Healing power level 2 |
| 104 | ITEM_TYPE_HEALING_POWER_3 | Healing power level 3 |
| 105 | ITEM_TYPE_HEALING_POWER_4 | Healing power level 4 |
| 106 | ITEM_TYPE_HEALING_POWER_5 | Healing power level 5 |

## Infrastructure

| ID | Constant | Notes |
|----|----------|-------|
| 239 | ITEM_TYPE_GUARD_TOWER | Guard tower |

## Unused / Reserved

| ID | Notes |
|----|-------|
| 43 | No constant defined |
| 107 | No constant defined |
| 202–204 | No constants defined |
