---
type: reference
tags:
  - reference
  - items
related:
  - "[[TASK-005 Items]]"
  - "[[Item Template Fields]]"
  - "[[Item Types]]"
  - "[[Item Materials]]"
---

# Base Game Items

**Total**: 1,425 items  **ID range**: 0–1438  **Gaps**: 14 unused IDs (see below)

Items are defined across six creator files, all called from `ItemTemplateFactory.java`. Source file is **not** a useful category boundary — `Third`, `Cooking`, and `Fishing` all contribute items in the 1000–1400 ID range.

| Source File | Rough ID Range | Item Count |
|---|---|---|
| `ItemTemplateCreator.java` | 0–608 | 639 |
| `ItemTemplateCreatorContinued.java` | 609–1307 | 402 |
| `ItemTemplateCreatorThird.java` | 1049–1438 | 132 |
| `ItemTemplateCreatorCooking.java` | 1130–1331 | 164 |
| `ItemTemplateCreatorFishing.java` | 94–1399 | 87 |
| `ItemTemplateCreatorKingdom.java` | 384–1431 | 5 |

---

## Gap IDs (unused / reserved)

The following 14 IDs have no item defined:

`137, 155, 235, 240, 241, 243, 255, 256, 450, 453, 648, 783, 930, 1314`

---

## Proposed File Structure

```
data/items/base/
├── weapons/
│   ├── axes.json.example
│   ├── swords.json.example
│   ├── blunt.json.example
│   ├── polearms.json.example
│   ├── ranged.json.example
│   ├── ammo.json.example
│   └── parts.json.example          ← unfinished components (heads, blades, shafts, strings)
├── armor/
│   ├── leather.json.example
│   ├── cloth.json.example
│   ├── studded.json.example
│   ├── chain.json.example
│   ├── plate.json.example
│   ├── drake-dragon.json.example
│   ├── shields.json.example
│   ├── hats-helmets.json.example
│   ├── shoulders.json.example
│   └── clothing.json.example       ← cosmetic / royal garments
├── jewelry.json.example
├── gems.json.example
├── runes.json.example
├── tools/
│   ├── mining-digging.json.example
│   ├── farming.json.example        ← includes hatchet, sickle, scythe
│   ├── woodworking-masonry.json.example
│   ├── leatherworking.json.example
│   ├── smithing.json.example
│   ├── cooking-tools.json.example  ← portable cooking gear (pans, cauldrons, molds)
│   ├── textile.json.example
│   ├── navigation.json.example
│   ├── writing.json.example
│   ├── crude.json.example
│   └── misc.json.example
├── furniture/
│   ├── tables.json.example
│   ├── seating.json.example
│   ├── beds.json.example
│   ├── storage-furniture.json.example
│   ├── rugs-carpets.json.example
│   ├── fireplace.json.example
│   └── crafting-stations.json.example  ← forges, ovens, kilns, smelters, stills
├── materials/
│   ├── ores-metals.json.example
│   ├── stone-earth.json.example
│   ├── building-stone.json.example
│   ├── wood.json.example
│   ├── hides-leather.json.example
│   ├── textile-fibres.json.example
│   ├── creature-parts.json.example
│   ├── paper-ink.json.example
│   └── misc.json.example
├── crops/
│   ├── grains.json.example
│   ├── vegetables.json.example
│   ├── seeds.json.example
│   ├── herbs-spices.json.example
│   ├── fruits-nuts.json.example
│   ├── mushrooms.json.example
│   └── plants-trellises.json.example
├── food/
│   ├── basics.json.example
│   ├── meals-stews.json.example
│   ├── baked.json.example
│   ├── cooked-meat.json.example
│   ├── prepared-dishes.json.example
│   └── condiments-misc.json.example
├── beverages.json.example
├── fishing/
│   ├── fish.json.example
│   ├── gear-old.json.example
│   ├── gear-new.json.example
│   ├── bait.json.example
│   └── containers-accessories.json.example
├── containers/
│   ├── bags.json.example
│   ├── jars-flasks.json.example
│   ├── chests-crates.json.example
│   ├── barrels.json.example
│   ├── racks-shelves.json.example
│   └── food-containers.json.example
├── decorations/
│   ├── statues.json.example
│   ├── tapestries.json.example
│   ├── flowerpots-planters.json.example
│   ├── puppets-ornaments.json.example
│   ├── banners-flags.json.example
│   └── signs-misc.json.example
├── lighting.json.example
├── economy/
│   ├── coins.json.example
│   ├── deeds.json.example
│   └── contracts-tokens.json.example
├── magic/
│   ├── wands-staves.json.example
│   ├── tomes-scrolls.json.example
│   ├── artifacts.json.example
│   ├── altars.json.example
│   └── alchemical.json.example
├── potions.json.example
├── rift/
│   ├── materials.json.example
│   ├── jewelry.json.example
│   └── devices.json.example
├── ships/
│   ├── vessels.json.example
│   └── parts.json.example
├── vehicles/
│   ├── wagons-carts.json.example
│   ├── horse-equipment.json.example
│   └── wagoner.json.example
├── structures/
│   ├── towers.json.example
│   ├── portals.json.example
│   ├── religious.json.example
│   ├── military.json.example
│   ├── siege.json.example
│   └── misc.json.example
├── traps.json.example
├── dyes.json.example
└── internal.json.example          ← documented only; do not modify
```

---

## Category Listings

> Items marked ⚠ have borderline assignments noted in the Ambiguous Items table at the bottom.

---

### Weapons (`base/weapons/`)

Combat offense items. Farm tools that double as weapons (hatchet, sickle, scythe, carving/butchering knife) live in Tools. Siege machines live in Structures.

**`axes.json.example`**
3 (axe), 87 (axe), 90 (axe), 1011 (crude axe)

**`swords.json.example`**
21 (longsword), 80 (short sword), 81 (two handed sword)

**`blunt.json.example`**
290 (maul), 291 (maul), 292 (maul), 314 (shod club)

**`polearms.json.example`**
705 (long spear), 706 (halberd), 707 (spear), 710 (staff), 711 (staff)

**`ranged.json.example`**
447 (short bow), 448 (bow), 449 (long bow), 459 (short bow), 460 (bow), 461 (long bow)

**`ammo.json.example`**
455 (hunting arrow), 456 (war arrow), 462 (quiver), 830 (fragile arrow), 932 (ballista dart)

**`parts.json.example`** — unfinished weapon components
88 (axe head), 89 (axe head), 91 (axe head), 1010 (crude axe head), 147 (short sword blade), 148 (long sword blade), 149 (sword blade), 293 (maul head), 294 (maul head), 295 (maul head), 708 (halberd head), 709 (spear tip), 451 (hunting arrow head), 452 (war arrow head), 454 (arrow shaft), 935 (ballista dart head), 98 (scabbard), 99 (handle), 457 (bow string), 523 (hatchet head ⚠)

---

### Armor & Clothing (`base/armor/`)

All wearable items. Raw armor materials (drake hide unfinished, chain links) stay in Materials.

**`leather.json.example`**
102 (leather belt), 103 (leather glove), 104 (leather jacket), 105 (leather boot), 106 (leather sleeve), 107 (leather cap), 108 (leather pants)

**`cloth.json.example`**
109 (cloth glove), 110 (cloth shirt), 111 (red striped cloth sleeve), 112 (red cloth jacket), 113 (cloth pants), 114 (cloth shoe)

**`studded.json.example`**
115 (studded leather sleeve), 116 (studded leather boot), 117 (studded leather cap), 118 (studded leather pants), 119 (studded leather glove), 120 (studded leather jacket)

**`chain.json.example`**
273 (steel glove), 274 (chain boot), 275 (chain pants), 276 (chain jacket), 277 (chain sleeve), 278 (chain gauntlet), 279 (chain coif), 288 (armour chains)

**`plate.json.example`**
280 (plate sabaton), 281 (plate leggings), 282 (breast plate), 283 (plate vambrace), 284 (plate gauntlet), 285 (basinet helm), 286 (great helm), 287 (open helm)

**`drake-dragon.json.example`**
468 (drake hide sleeve), 469 (drake hide boot), 470 (drake hide cap), 471 (drake hide pants), 472 (drake hide glove), 473 (drake hide jacket), 474 (dragon scale boot), 475 (dragon scale pants), 476 (dragon scale jacket), 477 (dragon scale sleeve), 478 (dragon scale glove)

**`shields.json.example`**
4 (shield), 82 (shield), 83 (metal shield), 84 (shield), 85 (shield), 86 (shield), 898 (tortoise shell ⚠), 899 (tortoise shield), 931 (siege shield ⚠)

**`hats-helmets.json.example`**
600 (summer hat), 779 (blue cloth hood), 791 (soft cap), 943 (peasant wool cap), 944 (yellow peasant wool cap), 945 (green peasant wool cap), 946 (red peasant wool cap), 947 (blue peasant wool cap), 948 (common wool hat), 949 (dark common wool hat), 950 (brown common wool hat), 951 (green common wool hat), 952 (red common wool hat), 953 (blue common wool hat), 954 (foresters wool hat), 955 (green foresters wool hat), 956 (dark foresters wool hat), 957 (blue foresters wool hat), 958 (red foresters wool hat), 959 (brown bear helm), 960 (leather adventurer hat), 961 (squire wool cap), 962 (green squire wool cap), 963 (blue squire wool cap), 964 (black squire wool cap), 965 (red squire wool cap), 966 (yellow squire wool cap), 973 (mask of the enlightened), 974 (mask of the ravager), 975 (pale mask), 976 (mask of the shadow), 977 (mask of the challenger), 978 (mask of the isles), 979 (horned helmet of gold), 980 (plumed helm of the hunt), 998 (cavalier helmet), 1014 (goblin war bonnet), 1015 (crown of the troll king), 1099 (mask of the returner), 1321 (troll mask), 1428 (skull mask), 1429 (witch's hat)

**`shoulders.json.example`**
1049 (small shoulder pad), 1050 (double shoulder pad), 1051 (curved shoulder pad), 1052 (triple shoulder pad), 1053 (right elaborate shoulder pad), 1054 (exquisite shoulder pad), 1055 (right basic shoulder pad), 1056 (right shielding shoulder pad), 1057 (right stylish shoulder pad), 1058 (right layered shoulder pad), 1059 (chain shoulder pad), 1060 (crafted shoulder pad), 1061 (boar shoulder pad), 1062 (ribboned shoulder pad), 1063 (skull shoulder pad), 1064 (human skull shoulder pad), 1065 (dragon shoulder pad), 1066 (left elaborate shoulder pad), 1092 (left basic shoulder pad), 1093 (left shielding shoulder pad), 1094 (left stylish shoulder pad), 1095 (left layered shoulder pad)

**`clothing.json.example`** — cosmetic and royal garments
531 (royal robes), 534 (chancellor cape), 537 (thorn robes), 831 (kingdom tabard), 1067 (green cloth tunic), 1068 (black belted vest), 1069 (red cloth tunic), 1070 (brown striped breeches), 1071 (patchwork pants), 1072 (black cloth pants), 1073 (green cloth pants), 1074 (green cloth sleeve), 1075 (red cloth sleeve), 1105 (black cloth sleeve), 1106 (plain white cloth sleeve), 1107 (plain white cloth pants), 1424 (leather drinking boot ⚠), 1425 (white cloth hood), 1426 (white cloth sleeve), 1427 (white cloth jacket)

---

### Jewelry (`base/jewelry.json.example`)

229 (chain), 230 (necklace), 231 (bracelet), 232 (ball), 233 (pendulum), 297 (ring), 740 (medallion), 985 (hota necklace), 1397 (pearl), 1398 (black pearl), 1399 (pearl necklace)

---

### Gems (`base/gems.json.example`)

374 (emerald), 375 (star emerald), 376 (ruby), 377 (star ruby), 378 (opal), 379 (black opal), 380 (diamond), 381 (star diamond), 382 (sapphire), 383 (star sapphire)

---

### Runes (`base/runes.json.example`)

1289 (rune of Magranon), 1290 (rune of Fo), 1291 (rune of Vynora), 1292 (rune of Libila), 1293 (rune of Jackal)

---

### Tools (`base/tools/`)

Non-weapon handheld items used for crafting, gathering, and artisan work. Includes farm tools that can also be used as weapons (hatchet, sickle, scythe). Placed crafting stations (forges, kilns, stills) live in Furniture.

**`mining-digging.json.example`**
20 (pickaxe), 25 (shovel), 121 (shovel blade), 122 (shovel blade), 123 (pickaxe head), 686 (crude pickaxe head), 687 (crude pickaxe), 689 (crude shovel blade), 690 (crude shovel)

**`farming.json.example`**
7 (hatchet), 27 (rake), 124 (rake blade), 267 (sickle), 268 (scythe), 269 (sickle blade), 270 (scythe blade), 413 (fruit press), 523 (hatchet head ⚠), 747 (press)

**`woodworking-masonry.json.example`**
24 (saw), 62 (hammer), 63 (mallet), 64 (anvil), 97 (stone chisel), 127 (hammer head), 154 (chisel blade), 156 (mallet head), 185 (anvil), 202 (grindstone), 388 (file), 389 (file blade), 441 (metal brush), 492 (mortar), 493 (trowel), 494 (trowel blade), 1115 (crowbar)

**`leatherworking.json.example`**
8 (carving knife), 93 (butchering knife), 125 (butchering knife blade), 126 (carving knife blade), 390 (awl), 391 (awl blade), 392 (leather knife), 393 (leather knife blade), 394 (scissors), 395 (scissor blade), 396 (clay shaper), 397 (spatula)

**`smithing.json.example`**
65 (cheese drill), 219 (pliers), 296 (whetstone), 788 (smelting pot), 789 (clay smelting pot)

**`cooking-tools.json.example`** — portable cooking gear; placed stations are in furniture/crafting-stations
75 (frying pan), 257 (spoon), 258 (knife), 259 (fork), 350 (sauce pan), 351 (cauldron), 1164 (clay pie dish), 1165 (pie dish), 1166 (cake tin), 1167 (baking stone), 1168 (clay roasting dish), 1169 (roasting dish), 1171 (clay measuring jug), 1172 (measuring jug), 1173 (plate), 1237 (mortar and pestle), 1243 (bee smoker), 1255 (wax sealing kit)

**`textile.json.example`**
139 (spindle), 215 (needle), 216 (needle)

**`navigation.json.example`**
480 (compass), 489 (spyglass), 781 (hand mirror), 901 (range pole), 902 (protractor), 903 (dioptra), 904 (sight), 1127 (almanac), 1128 (almanac folder)

**`writing.json.example`**
749 (reed pen), 752 (ink sac), 753 (black ink)

**`crude.json.example`**
685 (crude knife), 688 (branch), 691 (crude shaft)

**`misc.json.example`**
143 (steel and flint), 271 (yoyo), 320 (rope tool), 647 (grooming brush), 701 (branding iron), 1024 (conch)

---

### Raw Materials (`base/materials/`)

Unprocessed or semi-processed resources. Includes raw armor inputs (chain links, drake hide unfinished) and building materials.

**`ores-metals.json.example`**
38 (ore), 39 (ore), 40 (ore), 41 (ore), 42 (ore), 43 (ore), 44 (lump), 45 (lump), 46 (lump), 47 (lump), 48 (lump), 49 (lump), 131 (rivets), 205 (lump), 207 (ore), 220 (lump), 221 (lump), 223 (lump), 694 (lump), 698 (lump), 837 (lump), 1091 (metallic liquid), 1411 (lump)

**`stone-earth.json.example`**
26 (dirt), 130 (clay), 132 (stone brick), 146 (rock shards), 298 (heap of sand), 467 (peat), 684 (rock), 692 (boulder), 693 (ore), 696 (boulder), 697 (ore), 770 (shards), 785 (shards), 1116 (shards)

**`building-stone.json.example`**
406 (slab), 769 (clay brick), 771 (slate slab), 776 (pottery brick), 777 (clay shingle), 778 (pottery shingle), 782 (concrete), 784 (slate shingle), 786 (marble brick), 787 (marble slab), 790 (wood shingle), 1121 (sandstone brick), 1122 (rounded stone), 1123 (slate brick), 1124 (sandstone slab)

**`wood.json.example`**
9 (log), 22 (plank), 23 (shaft), 169 (wood scrap), 429 (support beam), 860 (wooden beam), 774 (leggat), 775 (staircase)

**`hides-leather.json.example`**
71 (hide), 72 (leather), 73 (lye), 100 (strip of leather), 101 (leather wound handle), 302 (fur), 313 (pelt), 371 (drake hide), 372 (scale)

**`textile-fibres.json.example`**
144 (cotton), 145 (cotton seed), 188 (ribbon), 213 (square piece of cloth), 214 (string of cloth), 318 (wemp fibre), 319 (rope), 444 (wires), 557 (thick rope), 756 (thatch), 897 (brass ribbon), 921 (wool), 925 (yarn), 926 (square piece of wool cloth)

**`creature-parts.json.example`** — butchering drops and body parts
10 (leg), 11 (arm), 12 (head), 13 (torso), 14 (hand), 15 (foot), 17 (face), 18 (eye), 19 (legs), 92 (meat), 129 (cooked meat ⚠), 140 (fat), 141 (ash), 153 (tar), 173 (pig food), 303 (tooth), 304 (horn), 305 (paw), 306 (hoof), 307 (tail), 308 (eye), 309 (bladder), 310 (gland), 311 (twisted horn), 312 (long horn), 535 (black claw), 636 (heart), 866 (blood), 867 (strange bone), 868 (skull)

**`paper-ink.json.example`**
748 (papyrus sheet), 1270 (wood pulp), 1272 (paper sheet)

**`misc.json.example`**
36 (kindling ⚠), 170 (scrap), 171 (rags), 172 (leather pieces), 195 (scrap), 196 (scrap), 197 (scrap), 198 (scrap), 199 (scrap), 204 (charcoal), 206 (scrap), 222 (scrap), 224 (scrap), 225 (scrap), 266 (sprout ⚠), 695 (scrap), 699 (scrap), 859 (chain link), 1307 (fragment ⚠)

---

### Crops & Plants (`base/crops/`)

Harvestable crops, seeds, herbs, spices, mushrooms, and trellised plants.

**`grains.json.example`**
28 (barley), 29 (wheat), 30 (rye), 31 (oat), 32 (corn), 746 (rice)

**`vegetables.json.example`**
33 (pumpkin), 35 (potato), 355 (onion), 356 (garlic), 1133 (carrot), 1134 (cabbage), 1135 (tomato), 1136 (sugar beet), 1137 (lettuce), 1138 (pea pod), 1150 (pea), 1247 (cucumber)

**`seeds.json.example`**
34 (pumpkin seed), 145 (cotton seed ⚠), 317 (wemp seed ⚠), 744 (reed seed ⚠), 750 (strawberry seeds), 1145 (carrot seed), 1146 (cabbage seed), 1147 (tomato seed), 1148 (sugar beet seed), 1149 (lettuce seed), 1151 (fennel seed), 1153 (paprika seed), 1154 (turmeric seed), 1248 (cucumber seed)

**`herbs-spices.json.example`**
353 (lovage), 354 (sage), 357 (oregano), 358 (parsley), 359 (basil), 360 (thyme), 361 (belladonna), 363 (rosemary), 365 (nettles), 366 (sassafras), 1130 (mint), 1131 (fennel), 1132 (fennel plant), 1139 (sugar), 1140 (cumin), 1141 (ginger), 1142 (nutmeg), 1143 (paprika), 1144 (turmeric)

**`fruits-nuts.json.example`**
6 (green apple), 134 (hazelnuts), 362 (strawberries), 364 (blueberry), 367 (lingonberry), 409 (cherries), 410 (lemon), 411 (blue grapes), 412 (olives), 414 (green grapes), 800 (white cherry), 801 (red cherry), 802 (green cherry), 803 (giant walnut), 832 (walnut), 833 (chestnut), 1152 (cocoa), 1155 (cocoa bean), 1196 (raspberries), 1280 (coconut), 1283 (orange)

**`mushrooms.json.example`**
246 (green mushroom), 247 (black mushroom), 248 (brown mushroom), 249 (yellow mushroom), 250 (blue mushroom), 251 (red mushroom), 1223 (mushroom)

**`plants-trellises.json.example`** — growing plants, seedlings, trellises
266 (sprout ⚠), 316 (wemp plants), 436 (acorn ⚠), 743 (reed plants), 755 (kelp), 917 (ivy seedling), 918 (grape seedling), 919 (ivy trellis), 920 (grape trellis), 1017 (rose seedling), 1018 (rose trellis), 1273 (hops), 1274 (hops trellis), 1275 (hops seedling)

**Cooking catch-all grouping IDs** *(internal; probably internal/system territory)*
1156 (any veg), 1157 (any cereal), 1158 (any herb), 1159 (any spice), 1163 (any fruit), 1179 (any berry), 1197 (any nut), 1198 (any cheese), 1199 (any mushroom), 1200 (any milk), 1201 (any fish), 1261 (any meat), 1263 (any oil), 1267 (any flower)

---

### Food (`base/food/`)

Prepared and cooked food items. Raw ingredients that are also crops live in Crops. Beverages have their own file.

**`basics.json.example`**
66 (cheese), 67 (goat cheese), 68 (feta cheese), 69 (buffalo cheese), 70 (honey), 200 (dough), 201 (flour), 203 (bread), 349 (salt), 368 (meat fillet), 369 (fish fillet), 373 (porridge), 464 (egg), 465 (huge egg), 488 (sandwich), 754 (cooked rice), 856 (rice porridge), 857 (risotto)

**`meals-stews.json.example`**
345 (stew), 346 (casserole), 347 (meal), 348 (gulasch), 352 (soup), 442 (delicious julbord)

**`baked.json.example`**
729 (cake), 730 (cake slice), 1170 (slice of bread), 1174 (batter), 1202 (biscuit), 1208 (pastry), 1221 (cheesecake), 1227 (scone), 1228 (toast), 1229 (pasty), 1242 (cake mix), 1256 (biscuit mix), 1257 (scone mix), 1258 (tart), 1262 (pizza), 1266 (pudding), 1282 (sweet), 1287 (muffin), 1288 (cookie)

**`cooked-meat.json.example`**
900 (crab meat), 1190 (sausage), 1191 (bacon), 1214 (rat-on-a-stick), 1215 (hog roast), 1216 (lamb spit), 1222 (kielbasa), 1236 (sausage skin)

**`prepared-dishes.json.example`**
1185 (chocolate), 1186 (butter), 1187 (omelette), 1188 (curry), 1189 (salad), 1192 (corn dough), 1193 (cooking oil), 1194 (gravy), 1195 (custard), 1203 (fries), 1204 (gelatine), 1207 (pasta), 1209 (pesto), 1210 (stock), 1217 (mushy peas), 1218 (croutons), 1219 (haggis), 1220 (cornflour), 1224 (stuffed mushroom), 1225 (crisps), 1226 (jelly), 1230 (chocolate nut spread), 1240 (spaghetti), 1241 (icing), 1244 (breadcrumbs), 1245 (meatballs), 1259 (stir fry), 1260 (nori), 1264 (sushi), 1265 (pickle), 1268 (broth), 1281 (ice cream), 1331 (chocolate milk)

**`condiments-misc.json.example`**
415 (maple syrup), 416 (maple sap), 417 (fruit juice), 428 (jam), 466 (easter egg), 522 (carved pumpkin ⚠), 1176 (fudge sauce), 1205 (honey water), 1206 (passata), 1211 (tomato ketchup), 1212 (white sauce), 1213 (wort), 1238 (rock salt), 1246 (vinegar), 1250 (goblin skull ⚠), 1276 (snowball ⚠)

---

### Beverages (`base/beverages.json.example`)

128 (water), 142 (milk), 419 (red wine), 420 (white wine), 425 (tea), 427 (lemonade), 858 (rice wine), 1012 (sheep milk), 1013 (bison milk), 1101 (champagne), 1180 (mead), 1181 (cider), 1182 (beer), 1183 (whisky), 1184 (pinenut ⚠), 1231 (vodka), 1232 (brandy), 1233 (moonshine), 1234 (gin), 1249 (cream ⚠), 1286 (rum)

---

### Fish & Fishing (`base/fishing/`)

**`fish.json.example`**
157 (pike), 158 (smallmouth bass), 159 (herring), 160 (catfish), 161 (snook), 162 (roach), 163 (perch), 164 (carp), 165 (brook trout), 569 (marlin), 570 (blue shark), 571 (white shark), 572 (octopus), 573 (sailfish), 574 (dorado), 575 (tuna), 1335 (salmon), 1336 (tarpon), 1337 (sardine), 1338 (minnow), 1339 (loach), 1340 (wurmfish), 1394 (clam)

**`gear-old.json.example`** — pre-rework fishing system
94 (old fine fishing rod), 95 (old fine fishing hook), 96 (old fishing hook), 150 (old fine fishing line), 151 (old fishing line), 152 (old fishing rod), 780 (old unstrung fishing rod)

**`gear-new.json.example`** — current fishing system rods and reels
1344 (fishing pole), 1345 (eyelet), 1346 (fishing rod), 1347 (basic fishing line), 1348 (light fishing line), 1349 (medium fishing line), 1350 (heavy fishing line), 1351 (braided fishing line), 1367 (wood reel), 1368 (metal reel), 1369 (professional reel), 1370 (reinforced handle), 1371 (padded handle), 1372 (light fishing reel), 1373 (medium fishing reel), 1374 (deep water fishing reel), 1375 (professional fishing reel)

**`bait.json.example`**
1352 (feather), 1353 (twig), 1354 (small piece of moss), 1355 (bark), 1356 (wooden fishing hook), 1357 (metal fishing hook), 1358 (bone fishing hook), 1359 (fly), 1360 (small piece of cheese), 1361 (dough ball), 1362 (wurm), 1363 (bit of fish), 1364 (grub), 1365 (grain of wheat), 1366 (corn kernel)

**`containers-accessories.json.example`**
1341 (tackle box), 1342 (fish keep net), 1343 (fishing net), 1376 (compartment), 1377 (compartment), 1378 (compartment), 1379 (compartment), 1380 (compartment), 1381 (compartment), 1382 (compartment), 1383 (compartment), 1384 (compartment), 1385 (compartment), 1386 (compartment), 1387 (compartment), 1388 (compartment), 1389 (compartment), 1390 (compartment), 1391 (compartment), 1393 (fishing rod rack), 1395 (fishing trophy), 1396 (buoy)

---

### Containers (`base/containers/`)

**`bags.json.example`**
1 (backpack), 2 (satchel), 1100 (knapsack), 1332 (small bag), 1333 (saddle bags), 1334 (saddle sacks)

**`jars-flasks.json.example`**
76 (pottery jar), 77 (pottery bowl), 78 (pottery flask), 79 (water skin), 181 (clay jar), 182 (clay bowl), 183 (clay flask), 653 (glass flask), 1019 (small clay amphora), 1020 (small pottery amphora), 1021 (large clay amphora), 1022 (large pottery amphora), 1251 (clay beer stein), 1252 (beer stein), 1253 (skull cup)

**`chests-crates.json.example`**
184 (chest), 192 (chest), 443 (bag of keeping), 651 (gift box), 664 (magical chest), 665 (magical chest), 851 (small crate), 852 (large crate), 995 (treasure chest), 1097 (gift pack), 1098 (returner tool chest)

**`barrels.json.example`**
189 (barrel), 190 (barrel), 576 (tub), 661 (food storage bin), 662 (bulk storage bin), 757 (oil barrel), 768 (wine barrel)

**`racks-shelves.json.example`**
1108 (wine barrel rack), 1109 (small barrel rack), 1110 (planter rack), 1111 (amphora rack), 1119 (storage unit), 1120 (storage shelf), 1315 (rack for empty bsb), 1316 (bulk container unit), 1317 (bulk storage shelf)

**`food-containers.json.example`**
1277 (larder), 1278 (ice box), 1279 (food shelf), 1294 (thermos), 1295 (food compartment), 1296 (lunchbox), 1297 (picnic basket)

---

### Furniture (`base/furniture/`)

**`tables.json.example`**
260 (round table), 262 (square table), 264 (dining table), 896 (tripod table), 928 (round marble table), 929 (rectangular marble table), 1402 (bar table)

**`seating.json.example`**
261 (stool), 263 (chair), 265 (armchair), 404 (bench), 891 (bench), 894 (royal throne), 913 (fine high chair), 914 (high chair), 915 (paupers high chair), 923 (lounge chair), 924 (royal lounge chaise)

**`beds.json.example`**
482 (head board), 483 (bed frame), 484 (bed), 485 (foot board), 890 (canopy bed), 1313 (straw bed)

**`storage-furniture.json.example`**
228 (candelabra ⚠), 885 (bedside table), 892 (wardrobe), 893 (coffer), 911 (high bookshelf), 912 (low bookshelf), 927 (cupboard), 1030 (sword display), 1031 (axe display), 1117 (alchemist's cupboard), 1400 (empty low bookshelf), 1401 (empty high bookshelf), 1412 (empty shelf)

**`rugs-carpets.json.example`**
486 (sheet ⚠), 639 (meditation rug), 644 (fine meditation rug), 645 (beautiful meditation rug), 646 (exquisite meditation rug), 846 (black bear rug), 847 (brown bear rug), 848 (mountain lion rug), 849 (black wolf rug), 908 (colourful carpet), 909 (colourful carpet), 910 (colourful carpet)

**`fireplace.json.example`**
889 (open fireplace)

**`crafting-stations.json.example`** — placed structures used for crafting
178 (oven), 180 (forge), 226 (floor loom), 922 (spinning wheel), 1023 (kiln), 1028 (smelter), 1178 (still), 1284 (boiler), 1285 (condenser)

**Misc furniture items** *(not yet assigned to a subcategory)*
758 (bow rack), 759 (armour stand), 987 (tapestry stand), 1025 (birdcage), 895 (washing bowl)

---

### Decorations (`base/decorations/`)

**`statues.json.example`**
227 (statuette), 398 (statue of nymph), 399 (statue of demon), 400 (statue of dog), 401 (statue of troll), 402 (statue of boy), 403 (statue of girl), 505 (statuette of Fo), 506 (statuette of Libila), 507 (statuette of Magranon), 508 (statuette of Vynora), 518 (colossus), 519 (colossus brick), 811 (statue of horse), 821 (gravestone), 822 (gravestone), 869 (Colossus of Vynora), 870 (Colossus of Magranon), 907 (Colossus of Fo), 916 (Colossus of Libila), 981 (challenge statue), 982 (challenge statue), 983 (challenge statue), 984 (challenge statue), 1323 (statue of eagle), 1324 (statue of worg), 1325 (statue of hell horse), 1326 (statue of Vynora), 1327 (statue of Magranon), 1328 (statue of Fo), 1329 (statue of Libila), 1330 (statue of drake), 1405 (statue of guard), 1406 (statue of kyklops), 1407 (statue of rift beast), 1408 (statue of mountain lion), 1415 (statue of unicorn), 1416 (statue of goblin), 1417 (statue of lava fiend), 1418 (statuette of miner), 1419 (statuette of swordsman), 1420 (statuette of axeman), 1421 (statuette of digger), 1430 (statue of Tich), 1437 (snowman statue)

**`tapestries.json.example`**
988 (green tapestry), 989 (beige tapestry), 990 (orange tapestry), 991 (cavalry motif tapestry), 992 (festivities motif tapestry), 993 (battle of Kyara tapestry), 994 (tapestry of Faeldray), 1318 (tapestry of Evening), 1319 (tapestry of Mclavin), 1320 (tapestry of Ehizellbob)

**`flowerpots-planters.json.example`**
812 (clay flowerpot), 813 (pottery flowerpot), 814 (yellow flowerpot), 815 (blue flowerpot), 816 (purple flowerpot), 817 (white flowerpot), 818 (orange-red flowerpot), 819 (greenish-yellow flowerpot), 820 (white-dotted flowerpot), 1001 (marble planter), 1002 (yellow planter), 1003 (blue planter), 1004 (purple planter), 1005 (white planter), 1006 (orange-red planter), 1007 (greenish-yellow planter), 1008 (white-dotted planter)

**`puppets-ornaments.json.example`**
640 (Fo puppet), 641 (Magranon puppet), 642 (Vynora puppet), 643 (Libila puppet), 652 (christmas tree), 655 (snowman), 700 (fireworks), 731 (tree stump), 738 (garden gnome), 742 (hota statue), 967 (garden gnome), 972 (yule goat), 1032 (yule reindeer), 997 (valentines)

**`banners-flags.json.example`**
487 (flag), 577 (banner), 578 (kingdom banner), 579 (kingdom flag), 831 (kingdom tabard ⚠), 999 (tall kingdom banner)

**`signs-misc.json.example`**
209 (sign), 210 (sign), 344 (marker), 405 (decorative fountain), 407 (coffin), 408 (fountain), 498 (bouquet of yellow flowers), 499 (bouquet of orange-red flowers), 500 (bouquet of purple flowers), 501 (bouquet of white flowers), 502 (bouquet of blue flowers), 503 (bouquet of greenish-yellow flowers), 504 (bouquet of white-dotted flowers), 635 (ornate fountain), 656 (shop sign), 677 (gm sign), 835 (village recruitment board), 844 (snow lantern ⚠), 845 (water marker), 1403 (archaeology report ⚠), 1404 (archaeology journal ⚠)

---

### Lighting (`base/lighting.json.example`)

133 (candle), 135 (lantern), 136 (oil lamp), 138 (torch), 228 (candelabra ⚠), 496 (lamp), 497 (lamp head), 520 (firemarker), 657 (torch lamp), 658 (hanging lamp), 659 (imperial street lamp), 660 (metal torch), 674 (hanging lamp head), 675 (imperial lamp head), 838 (brazier stand), 839 (brazier bowl), 840 (brazier bowl), 841 (small brazier), 842 (marble brazier pillar)

---

### Economy (`base/economy/`)

**`coins.json.example`**
50 (coin), 51 (coin), 52 (coin), 53 (coin), 54 (five coin), 55 (five coin), 56 (five coin), 57 (five coin), 58 (twenty coin), 59 (twenty coin), 60 (twenty coin), 61 (twenty coin)

**`deeds.json.example`**
166 (writ of ownership), 211 (size ten village deed), 234 (size five homestead deed), 236 (settlement token), 237 (size five village deed), 238 (size fifteen village deed), 239 (size twenty village deed), 242 (size fifty village deed), 244 (size hundred village deed), 245 (size twohundred village deed), 253 (size ten homestead deed), 254 (size twenty homestead deed), 663 (settlement form), 862 (deed stake)

**`contracts-tokens.json.example`**
209 (sign ⚠), 299 (trader contract), 300 (personal merchant contract), 649 (light token), 656 (shop sign ⚠), 843 (name change certificate), 1000 (ownership papers), 1129 (wagoner contract ⚠), 1269 (label), 1272 (paper sheet ⚠), 1298 (sheet), 1299 (sheet), 1300 (golden mirror ⚠), 1392 (item slot ⚠), 1409 (book ⚠), 1422 (village cache), 1423 (village token), 1438 (affinity token)

---

### Magic Items (`base/magic/`)

**`wands-staves.json.example`**
174 (wand of teleportation), 176 (ebony wand), 315 (ivory wand), 329 (Rod of Beguiling), 633 (brittle wand), 668 (Rod of Transmutation), 805 (wand of the seas), 825 (sapphire staff), 826 (ruby staff), 827 (diamond staff), 828 (opal staff), 829 (emerald staff), 986 (staff of land), 1009 (rod of eruption), 1027 (steel wand)

**`tomes-scrolls.json.example`**
798 (red tome of magic), 799 (scroll of binding), 804 (tome of incineration), 806 (libram of the night), 807 (green tome of magic), 808 (black tome of magic), 809 (blue tome of magic), 810 (white tome of magic)

**`artifacts.json.example`** — named unique items
301 (cornucopia), 330 (Crown of Might), 331 (Charm of Fo), 332 (Eye of Vynora), 333 (Ear of Vynora), 334 (Mouth of Vynora), 335 (Finger of Fo), 336 (Sword of Magranon), 337 (Hammer of Magranon), 338 (Scale of Libila), 339 (Orb of Doom), 340 (Sceptre of Ascension), 443 (bag of keeping), 509 (resurrection stone), 514 (whip of One), 515 (crown of One), 516 (toolbelt ⚠), 524 (farwalker twig), 525 (farwalker stone), 526 (granite wand), 527 (farwalker amulet), 601 (shaker orb), 602 (sculpting wand), 794 (key of the heavens), 795 (blood of the angels), 796 (smoke from sol), 797 (uttacha slime), 1016 (Stone of Soulfall)

**`altars.json.example`**
322 (altar), 323 (altar), 324 (altar), 325 (altar), 326 (bowl), 327 (Altar of Three), 328 (Huge bone altar), 678 (Fo obelisk), 680 (Libila stone), 712 (shrine ⚠), 792 (sacrificial knife), 793 (sacrificial knife blade)

**`alchemical.json.example`**
5 (potion), 634 (dishwater), 654 (transmutation liquid), 666 (sleep powder), 667 (tuning fork of metal detection), 763 (source), 764 (source salt), 765 (source crystal), 766 (source fountain), 767 (source spring), 1118 (alchemy flask)

---

### Potions & Salves (`base/potions.json.example`)

650 (farmer's salve), 834 (yellow potion), 836 (brown potion), 871 (oil of the weapon smith), 872 (potion of the ropemaker), 873 (potion of water walking), 874 (potion of mining), 875 (ointment of tailoring), 876 (oil of the armour smith), 877 (fletching potion), 878 (oil of the blacksmith), 879 (potion of leatherworking), 880 (potion of shipbuilding), 881 (ointment of stonecutting), 882 (ointment of masonry), 883 (potion of woodcutting), 884 (potion of carpentry), 886 (potion of acid), 887 (salve of fire), 888 (salve of frost), 1413 (potion of butchery)

---

### Rift Items (`base/rift/`)

**`materials.json.example`**
1033 (rift stone), 1034 (rift stone), 1035 (rift stone), 1036 (rift stone), 1037 (rift crystal), 1038 (rift crystal), 1039 (rift crystal), 1040 (rift crystal), 1041 (plant), 1042 (plant), 1043 (plant), 1044 (plant), 1096 (rift heart), 1102 (rift stone shard), 1103 (rift crystal), 1104 (rift wood)

**`jewelry.json.example`**
1076 (socketed ring), 1077 (artisan ring), 1078 (seal ring), 1079 (dark ring), 1080 (ring of the Eye), 1081 (fist bracelet), 1082 (huge sword bracelet), 1083 (short sword bracelet), 1084 (spear bracelet), 1085 (bracelet of inspiration), 1086 (soul stealer necklace), 1087 (artisan necklace), 1088 (necklace of protection), 1089 (necklace of focus), 1090 (necklace of replenishment)

**`devices.json.example`**
1026 (unstable source rift), 1045 (rift altar), 1046 (rift device), 1047 (rift device), 1048 (rift device)

---

### Ships & Watercraft (`base/ships/`)

**`vessels.json.example`**
289 (raft), 490 (rowing boat), 491 (sailing boat), 540 (cog), 541 (corbita), 542 (knarr), 543 (caravel)

**`parts.json.example`**
544 (rudder), 545 (seat), 546 (hull plank), 547 (anchor), 548 (ship helm), 549 (tackle), 550 (tackle), 551 (tenon), 552 (tall mast), 553 (stern), 554 (triangular sail), 555 (square sail), 556 (oar), 557 (thick rope ⚠), 558 (mooring rope ⚠), 559 (cordage rope ⚠), 560 (keel section), 561 (peg), 562 (keel), 563 (triangular rig), 564 (square rig), 565 (mooring anchor), 566 (deck board), 567 (belaying pin), 568 (boat lock), 581 (dredge), 582 (dredge scraping lip), 583 (crows nest), 584 (spinnaker rig), 585 (large square rig), 586 (square yard rig), 587 (tall square rig), 588 (small mast), 589 (medium mast), 590 (large mast), 591 (small square sail), 597 (sheet), 598 (sheet), 599 (sheet)

---

### Vehicles & Mounts (`base/vehicles/`)

**`wagons-carts.json.example`**
186 (cart), 187 (wheel), 191 (wheel axle), 539 (cart), 850 (wagon), 853 (ship transporter ⚠)

**`horse-equipment.json.example`**
621 (saddle), 622 (saddle), 623 (horse shoe), 624 (bridle), 625 (girth), 626 (stirrups), 627 (mouth bit), 628 (reins), 629 (saddle seat), 630 (saddle seat), 631 (headstall), 632 (yoke), 702 (leather barding), 703 (chain barding), 704 (cloth barding), 1029 (halter rope)

**`wagoner.json.example`** — wagoner NPC/contract system items
1301 (wagoner campfire), 1302 (slate keystone), 1303 (clay keystone), 1304 (pottery keystone), 1305 (sandstone keystone), 1308 (wagoner tent), 1309 (wagoner container), 1310 (stored creature), 1311 (creature cage), 1312 (crate rack), 1410 (creature transporter ⚠)

---

### Structures (`base/structures/`)

**`towers.json.example`**
384 (guard tower — Mol Rehan), 430 (guard tower — Jenn-Kellon), 528 (guard tower — Horde of the Summoned), 638 (guard tower — Freedom Isles), 996 (neutral guard tower)

**`portals.json.example`**
603 (monolith portal), 604 (ring portal), 605 (desolate portal), 606 (flame portal), 607 (portal), 732 (epic portal), 733 (huge epic portal), 855 (steel portal)

**`religious.json.example`**
510 (spirit house), 511 (spirit cottage), 512 (spirit mansion), 513 (spirit castle), 521 (lair), 712 (shrine), 713 (pylon), 714 (obelisk), 715 (temple), 716 (spirit gate), 717 (foundation pillar), 718 (huge bell), 719 (small bell), 720 (small resonator), 721 (large bell resonator), 722 (bell tower), 723 (bell cot), 734 (tiny clapper), 735 (large clapper), 736 (pillar), 741 (shrine of the rush)

**`military.json.example`**
760 (outpost), 761 (battle camp), 762 (fortification), 724 (weapons rack), 725 (polearms rack), 726 (ring center), 727 (duelling ring), 728 (ring corner), 861 (tent), 863 (explorer tent), 864 (military tent), 865 (pavilion), 969 (supply depot), 970 (supply depot), 971 (supply depot)

**`siege.json.example`**
445 (catapult), 933 (machine mount), 934 (strange device), 936 (ballista), 937 (trebuchet), 938 (spike barrier), 939 (archery tower), 940 (acid turret), 941 (fire turret), 942 (lightning turret), 968 (frost turret), 1125 (battering ram), 1126 (battering ram head)

**`misc.json.example`**
608 (well), 592 (mine door), 593 (mine door), 594 (mine door), 595 (mine door), 596 (mine door), 580 (market stall ⚠), 679 (Construction marker), 1112 (waystone), 1113 (blind catseye), 1114 (catseye), 1431 (kingdom stone), 1432 (chicken coop), 1433 (egg box), 1434 (feeder), 1435 (drinker), 1436 (nesting box)

---

### Traps (`base/traps.json.example`)

609 (spring), 610 (stick trap), 611 (pole trap), 612 (corrosive trap), 613 (axe trap), 614 (knife trap), 615 (net trap), 616 (scythe trap), 617 (man trap), 618 (bow trap), 619 (rope trap)

---

### Dyes & Pigments (`base/dyes.json.example`)

431 (black dye), 432 (white dye), 433 (red dye), 434 (blue dye), 435 (green dye), 437 (tannin), 438 (dye), 439 (cochineal), 440 (woad)

---

### Internal / System (`base/internal.json.example`)

These items are used internally by the game engine. They are listed here for completeness and documentation only — **modifying them is likely to cause crashes or corrupt world state**.

0 (inventory — player's personal inventory), 16 (body — player body root), 177 (pile of items — internal stack container), 179 (unfinished item — crafting placeholder), 272 (corpse — creature body container), 386 (unfinished item), 387 (illusionary item), 669 (bulk item — bulk storage placeholder), 670 (trash heap — decay container), 672 (decayitem — internal decay tracking), 676 (mission ruler), 751 (mission ruler recharge), 823 (equipmentslot — equipment slot template), 824 (group — item group template), 854 (tutorial object), 671 (Deed border), 673 (Perimeter), 737 (Valrei mission item), 739 (Hota pillar ⚠)

**Cooking system catch-all IDs** *(internal grouping placeholders)*
1156 (any veg), 1157 (any cereal), 1158 (any herb), 1159 (any spice), 1163 (any fruit), 1179 (any berry), 1197 (any nut), 1198 (any cheese), 1199 (any mushroom), 1200 (any milk), 1201 (any fish), 1261 (any meat), 1263 (any oil), 1267 (any flower)

---

## Ambiguous / Disputed Items ⚠

| ID   | Name                  | Currently Assigned To       | Alternative              | Decision                 |
| ---- | --------------------- | --------------------------- | ------------------------ | ------------------------ |
| 36   | kindling              | Materials/misc              | Tools                    | Materials/misc           |
| 37   | campfire              | *(unassigned)*              | Structures or Lighting   | Crafting Station         |
| 100  | strip of leather      | Materials/hides-leather     | Armor/leather            | Materials/hides-leather  |
| 101  | leather wound handle  | Materials/hides-leather     | Armor/leather            | Materials/hides-leather  |
| 129  | cooked meat           | Materials/creature-parts    | Food/cooked-meat         | Food/cooked-meat         |
| 228  | candelabra            | Furniture/storage-furniture | Lighting                 | Lighting                 |
| 266  | sprout                | Crops/plants-trellises      | Materials/misc           | Crops/plants-trellises   |
| 436  | acorn                 | Crops/plants-trellises      | Materials/misc           | Crops/plants-trellises   |
| 458  | archery target        | Weapons/ammo                | Decorations              | Decorations              |
| 516  | toolbelt              | Magic/artifacts             | Tools/misc               | Tools/misc               |
| 522  | carved pumpkin        | Food/condiments-misc        | Decorations              | Decorations              |
| 523  | hatchet head          | Weapons/parts               | Tools/farming            | Weapons/parts            |
| 529  | royal sceptre         | Armor/clothing              | Magic/artifacts          | Magic/artifacts          |
| 530  | royal crown           | Armor/clothing              | Armor/hats-helmets       | Magic/artifacts          |
| 712  | shrine                | Structures/religious        | Magic/altars             | Structures/religious     |
| 831  | kingdom tabard        | Armor/clothing              | Decorations/banners      | Armor/clothing           |
| 844  | snow lantern          | Lighting                    | Decorations              | Lighting                 |
| 898  | tortoise shell        | Armor/shields               | Materials/creature-parts | Armor/shields            |
| 931  | siege shield          | Armor/shields               | Structures/siege         | Structures/siege         |
| 1184 | pinenut               | Beverages                   | Food/basics              | Food/basics              |
| 1249 | cream                 | Beverages                   | Food/condiments-misc     | Food/condiments-misc     |
| 1276 | snowball              | Food/condiments-misc        | Decorations              | Food/condiments-misc     |
| 1307 | fragment              | Materials/misc              | Internal/system          | Internal/system          |
| 1392 | item slot             | Economy/contracts-tokens    | Internal/system          | Internal/system          |
| 1403 | archaeology report    | Decorations/signs-misc      | Economy/contracts-tokens | Decorations/signs-misc   |
| 1404 | archaeology journal   | Decorations/signs-misc      | Economy/contracts-tokens | Decorations/signs-misc   |
| 1409 | book                  | Economy/contracts-tokens    | Internal/system          | Economy/contracts-tokens |
| 1410 | creature transporter  | Vehicles/wagoner            | Structures/misc          | Structures/misc          |
| 1424 | leather drinking boot | Armor/clothing              | Containers/jars-flasks   | Armor/clothing           |

---

## Uncategorized Items

| ID  | Name         | Likely Category            | Decision |
| --- | ------------ | -------------------------- | -------- |
| 580 | market stall | Structures/misc or Economy | Economy  |
| 739 | Hota pillar  | Decorations or Internal    | Internal |
