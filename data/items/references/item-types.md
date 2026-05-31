# Item Types

Reference for the `itemTypes` array in item JSON definitions. Use the integer ID values — items can have multiple types.

Each entry in this array flips one or more internal boolean flags on the item template, controlling what actions are available and how the item behaves. The array is order-independent.

IDs 43, 107, and 202–204 are unused gaps — do not use them.

---

## Weapons

| ID | Name | Notes |
|----|------|-------|
| 2  | WEAPON_SLASH | Slashing weapon |
| 13 | WEAPON_PIERCE | Piercing weapon |
| 14 | WEAPON_CRUSH | Crushing weapon |
| 15 | WEAPON_AXE | Axe subtype |
| 16 | WEAPON_SWORD | Sword subtype |
| 17 | WEAPON_KNIFE | Knife subtype |
| 18 | WEAPON_MISC | Miscellaneous weapon |
| 35 | WEAPON_MELEE | Generic melee weapon |
| 37 | WEAPON | Generic weapon flag |
| 84 | TWOHANDED | Two-handed weapon |
| 94 | WEAPON_BOW | Bow (strung) |
| 95 | WEAPON_BOW_UNSTRINGED | Bow (unstrung) |
| 154 | WEAPON_POLEARM | Polearm |

## Armor & Protection

| ID | Name | Notes |
|----|------|-------|
| 3  | SHIELD | Shield |
| 4  | ARMOUR | Armor piece |
| 99 | DRAGONARMOUR | Dragon armor |
| 136 | CREATURE_WEARABLE | Can be worn by creatures |

## Tools

| ID | Name | Notes |
|----|------|-------|
| 7  | TOOL_FIELD | Farming tool |
| 10 | TOOL_MINING | Mining tool |
| 11 | TOOL_CARPENTRY | Carpentry tool |
| 12 | TOOL_SMITHING | Smithing tool |
| 19 | TOOL_DIGGING | Digging tool |
| 38 | TOOL | Generic tool flag |
| 125 | DREDGE | Dredge tool |
| 210 | TOOL_COOKING | Cooking tool |

## Food & Drink

| ID | Name | Notes |
|----|------|-------|
| 5  | FOOD | Food item |
| 20 | SEED | Plantable seed |
| 28 | MEAT | Generic meat |
| 29 | VEGETABLE | Vegetable |
| 36 | FISH | Fish |
| 55 | LOWNUTRITION | Low nutrition tier |
| 74 | MEDIUMNUTRITION | Medium nutrition tier |
| 75 | GOODNUTRITION | Good nutrition tier |
| 76 | HIGHNUTRITION | High nutrition tier — also increases decay speed |
| 77 | FOODMAKER | Can make food |
| 78 | HERB | Herb |
| 79 | POISON | Poisonous |
| 80 | FRUIT | Fruit |
| 82 | DISH | Prepared dish |
| 88 | LIQUID_COOKING | Cooking liquid (e.g. water for boiling) |
| 90 | LIQUID_DRINKABLE | Drinkable liquid |
| 96 | EGG | Egg |
| 137 | NONUTRITION | Eaten but provides no nutrition (salt, spice, etc.) |
| 191 | MILK | Milk |
| 192 | CHEESE | Cheese |
| 205 | SPICE | Spice ingredient |
| 206 | POTABLE | Potable liquid |
| 207 | NO_CREATE | Cannot be created in the creation window — used for food group anchors |
| 208 | FOOD_GROUP | Food group anchor item |
| 209 | COOKER | Cooker / stove |
| 211 | RECIPE_ITEM | Used in recipes |
| 212 | USES_FOOD_STATE | Tracks food state (raw/cooked/etc.) |
| 213 | FERMENTED | Fermented food or drink |
| 214 | DISTILLED | Distilled drink |
| 217 | COOKING_OIL | Cooking oil |
| 219 | FOOD_BONUS_HOT | Bonus when served hot |
| 220 | FOOD_BONUS_COLD | Bonus when served cold |
| 226 | MUSHROOM | Mushroom |

## Liquids

| ID | Name | Notes |
|----|------|-------|
| 26 | LIQUID | Liquid item |
| 33 | CONTAINER_LIQUID | Container for liquids |
| 34 | LIQUID_INFLAMMABLE | Inflammable liquid |
| 128 | SPRINGFILLED | Filled from a spring |

## Containers & Storage

| ID | Name | Notes |
|----|------|-------|
| 1  | HOLLOW | Has an interior (container) |
| 9  | INVENTORY | Inventory container |
| 145 | BULKCONTAINER | Bulk storage container |
| 146 | BULK | Bulk item (goes in a bulk container) |
| 180 | USES_SPECIFIED_CONTAINER_VOLUME | Uses explicit `containerSize` dimensions — required alongside the `containerSize` field |
| 234 | LARDER | Larder (food storage) |
| 256 | VIEWABLE_SUBITEMS | Container contents visible |
| 259 | HOLLOW_VIEWABLE | Hollow with visible contents |

## Lighting

| ID | Name | Notes |
|----|------|-------|
| 32 | LIGHT | Emits light |
| 65 | FIRE | Is on fire |
| 101 | OILCONSUMING | Consumes oil for light |
| 115 | FLICKERING | Flickering light |
| 116 | LIGHT_BRIGHT | Bright light source |
| 143 | STREETLAMP | Street lamp |
| 156 | ALWAYS_LIT | Always lit |
| 179 | BRAZIER | Brazier |

## Placement & Tile

| ID | Name | Notes |
|----|------|-------|
| 49 | OUTSIDE_ONLY | Can only be placed outside |
| 52 | DECORATION | Decoration item |
| 67 | USE_GROUND_ONLY | Can only be used on the ground |
| 98 | TILE_ALIGNED | Must align to tile grid |
| 109 | ONE_PER_TILE | Only one allowed per tile |
| 111 | INSIDE_ONLY | Can only be placed inside |
| 123 | NOMOVE | Cannot be moved |
| 142 | SIGN | Sign (has text) |
| 166 | TEN_PER_TILE | Up to 10 per tile |
| 167 | FOUR_PER_TILE | Up to 4 per tile |
| 185 | ONE_PER_TILEBORDER | One per tile border |
| 225 | SURFACE_ONLY | Surface placement only |
| 240 | PARENT_ON_GROUND | Parent must be on ground |
| 244 | DECORATION_WHEN_PLANTED | Becomes decoration when planted |

## Nature & Farming

| ID | Name | Notes |
|----|------|-------|
| 118 | FLOWER | Flower |
| 149 | SPAWNSTREES | Spawns trees when used |
| 150 | KILLSTREES | Kills trees when used |
| 169 | PLANTED_FLOWERPOT | Planted flower pot |
| 186 | NATURE_PLANTABLE | Plantable (nature objects) |
| 199 | PLANTABLE | Can be planted |
| 200 | PLANT_ONE_A_WEEK | Plant limit: one per week |
| 221 | POTTED | Potted plant |
| 227 | HARVESTABLE | Can be harvested |
| 230 | TRELLIS | Trellis |

## Crafting

| ID | Name | Notes |
|----|------|-------|
| 27 | MELTING | Can be melted down |
| 30 | POTTERY | Pottery item |
| 44 | REPAIRABLE | Can be repaired |
| 46 | COMBINE | Can be combined with same type |
| 87 | MATERIAL_PRICEEFFECT | Material affects price |
| 119 | IMPROVEITEM | Can be improved |
| 141 | TRANSMUTABLE | Can be transmuted |
| 148 | COMBINECOLD | Can be combined when cold |
| 151 | CRUDE | Crude quality |
| 152 | MINABLE | Can be mined |
| 158 | MASSPRODUCTION | Mass production allowed |
| 160 | NOWORKPARENT | Parent cannot receive work |
| 173 | IMPROVE_USES_TYPE_AS_MATERIAL | Improvement uses type as material |
| 187 | NO_IMPROVE | Cannot be improved |
| 196 | UNFIRED | Unfired state (pottery) |
| 232 | COMPONENT_ITEM | Component item |
| 233 | USES_REAL_TEMPLATE | Uses real template |
| 257 | CREATES_WITH_LOCK | Created with a lock |

## Materials & Colors

| ID | Name | Notes |
|----|------|-------|
| 21 | WOOD | Wood material |
| 22 | METAL | Metal material |
| 23 | LEATHER | Leather material |
| 24 | CLOTH | Cloth material |
| 25 | STONE | Stone material |
| 91 | COLOR | Has a color |
| 92 | COLORABLE | Can be dyed |
| 164 | COLORCOMPONENT | Color component |
| 183 | SMEARABLE | Can be smeared |
| 184 | CARPET | Carpet |
| 188 | TAPESTRY | Tapestry |
| 249 | SUPPORTS_SECONDARY_COLOR | Has a secondary dye zone — required alongside `secondaryItemName` |

## Vehicles & Transport

| ID | Name | Notes |
|----|------|-------|
| 117 | VEHICLE | Is a vehicle |
| 134 | VEHICLE_DRAGGED | Dragged vehicle |
| 176 | TRANSPORTABLE | Can be transported by vehicle |
| 177 | WARMACHINE | War machine |
| 193 | CART | Cart |
| 201 | HITCH_TARGET | Can be hitched to |

## Creatures & Butchery

| ID | Name | Notes |
|----|------|-------|
| 62 | BUTCHERED | Product of butchering |
| 64 | LEADCREATURE | Used to lead a creature |
| 198 | LEAD_MULTIPLE_CREATURES | Can lead multiple creatures |

## Locks & Keys

| ID | Name | Notes |
|----|------|-------|
| 39 | LOCK | Is a lock |
| 41 | KEY | Is a key |
| 47 | LOCKABLE | Can be locked |

## Trade & Economy

| ID | Name | Notes |
|----|------|-------|
| 50 | COIN | Currency coin |
| 53 | FULLPRICE | Always sells at full price |
| 61 | NOTRADE | Cannot be traded |
| 85 | KINGDOM_MARKER | Kingdom marker |
| 122 | ROYAL | Royal item |
| 127 | NOSELLBACK | Cannot be sold back |
| 155 | ALWAYS_BANKABLE | Always bankable |
| 161 | WARTARGET | War target |

## Player Restrictions

| ID | Name | Notes |
|----|------|-------|
| 40 | INDESTRUCTIBLE | Cannot be destroyed |
| 42 | NODROP | Cannot be dropped |
| 54 | NORENAME | Cannot be renamed |
| 56 | DRAGGABLE | Can be dragged |
| 63 | NOPUT | Cannot be put in containers |
| 97 | NEWBIEITEM | New player item |
| 112 | NOBANK | Cannot be banked |
| 113 | RECYCLED | Recycled item |
| 120 | DEATHPROT | Protected on death |
| 159 | CAN_HAVE_INSCRIPTION | Accepts inscription |
| 174 | NODISCARD | Cannot be discarded |
| 175 | INSTADISCARD | Instantly discarded |
| 190 | UNFINISHED_NOTAKE | Unfinished — cannot be taken |

## Special & Magical

| ID | Name | Notes |
|----|------|-------|
| 6  | MAGIC | Magic item |
| 66 | DOMAIN | Domain item |
| 68 | HUGEALTAR | Huge altar |
| 69 | ARTIFACT | Artifact |
| 70 | UNIQUE | Unique item |
| 71 | DESTROY_HUGEALTAR | Destroys huge altar |
| 93 | GEM | Gem |
| 100 | COMPASS | Compass |
| 130 | RECHARGEABLE | Rechargeable |
| 131 | SERVERPORTAL | Server portal |
| 132 | TRAP | Trap |
| 133 | DISARM_TRAP | Can disarm traps |
| 138 | PUPPET | Puppet |
| 139 | OVERRIDEENCHANT | Overrides enchantment |
| 140 | MEDITATION | Meditation item |
| 153 | ENCHANT_JEWELRY | Enchantable jewelry |
| 162 | SOURCESPRING | Source spring |
| 163 | SOURCE | Source item |
| 168 | ABILITY | Ability item |
| 172 | MAGICAL_STAFF | Magical staff |
| 235 | RUNE | Rune |
| 236 | PEGABLE | Can be pegged |

## Decay & Lifecycle

| ID | Name | Notes |
|----|------|-------|
| 45 | TEMPORARY | Temporary (decays quickly) |
| 48 | HASDATA | Has extra persistent data |
| 59 | ALWAYSPOLL | Always polled each tick |
| 60 | FLOATING | Floats on water |
| 72 | PASS_FULLDATA | Passes full data |
| 89 | POSITIVE_DECAY | Improves with age (wine aging) |
| 114 | LOADED | Loaded state |
| 124 | WIND | Wind-affected |
| 129 | DECAYDESTROYS | Destroyed when fully decayed |
| 135 | OWNER_DESTROYABLE | Owner can destroy |
| 144 | VISIBLEDECAY | Decay is visually visible |
| 237 | DECAY_ON_DEED | Decays on deed |
| 238 | INSULATED | Thermally insulated |

## Furniture & Housing

| ID | Name | Notes |
|----|------|-------|
| 8  | BODYPART | Body part |
| 57 | VILLAGEDEED | Village deed |
| 58 | HOMESTEADDEED | Homestead deed |
| 110 | BED | Bed |
| 121 | TOOLBELT | Toolbelt |
| 126 | MINEDOOR | Mine door |
| 165 | TUTORIAL | Tutorial item |
| 170 | EQUIPMENTSLOT | Equipment slot item |
| 171 | INVENTORY_GROUP | Inventory group |
| 194 | OWNER_TURNABLE | Owner can turn |
| 195 | OWNER_MOVEABLE | Owner can move |
| 197 | CHAIR | Chair |

## Fishing

| ID | Name | Notes |
|----|------|-------|
| 250 | FISHING_REEL | Fishing reel |
| 251 | FISHING_LINE | Fishing line |
| 252 | FISHING_FLOAT | Fishing float |
| 253 | FISHING_HOOK | Fishing hook |
| 254 | FISHING_BAIT | Fishing bait |

## Display & UI

| ID | Name | Notes |
|----|------|-------|
| 51 | TURNABLE | Can be turned by all |
| 73 | FORM | Has form data |
| 81 | DESC_IS_EXAM | Description is examine text |
| 83 | SERVERBOUND | Server-bound |
| 108 | NAMED | Has a name |
| 147 | MISSION | Mission item |
| 157 | NOT_MISSION | Not a mission item |
| 216 | USE_REAL_TEMPLATE_ICON | Uses real template icon |
| 218 | HOVER | Hover effect |
| 222 | CAN_BE_PAPYRUS_WRAPPED | Can be papyrus-wrapped |
| 223 | CAN_BE_RAW_WRAPPED | Can be raw-wrapped |
| 224 | CAN_BE_CLOTH_WRAPPED | Can be cloth-wrapped |
| 228 | SHOW_RAW | Shows raw state |
| 229 | NOT_SPELL_TARGET | Cannot be targeted by spells |
| 231 | INGREDIENTS_ONLY | Ingredients only |
| 241 | ROAD_MARKER | Road marker |
| 242 | PAVEABLE | Can be used to pave |
| 243 | CAVE_PAVEABLE | Can pave caves |
| 245 | DESC_IS_NAME | Description is item name |
| 246 | NOT_RUNEABLE | Cannot have runes applied |
| 247 | SHOWS_SLOPES | Shows slope information |
| 248 | PLURAL_NAME | Has a plural name |
| 255 | HAS_EXTRA_DATA | Has extra data field |
| 258 | BRACELET | Bracelet |

## Healing

| ID | Name | Notes |
|----|------|-------|
| 102 | HEALING_POWER_1 | Healing power level 1 |
| 103 | HEALING_POWER_2 | Healing power level 2 |
| 104 | HEALING_POWER_3 | Healing power level 3 |
| 105 | HEALING_POWER_4 | Healing power level 4 |
| 106 | HEALING_POWER_5 | Healing power level 5 |

## Infrastructure

| ID | Name | Notes |
|----|------|-------|
| 239 | GUARD_TOWER | Guard tower |

## Unused / Reserved

| ID | Notes |
|----|-------|
| 43 | No constant defined — do not use |
| 107 | No constant defined — do not use |
| 202–204 | No constants defined — do not use |
