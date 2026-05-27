package org.gotti.wurmtweaker.items;

import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.items.ItemTemplateFactory;
import com.wurmonline.server.skills.NoSuchSkillException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemTemplateDumper {

    private static final Logger logger = Logger.getLogger(ItemTemplateDumper.class.getName());
    private static final Map<String, Field> FIELD_CACHE = new HashMap<String, Field>();

    public void dump(File outputFile) {
        ItemTemplate[] templates = ItemTemplateFactory.getInstance().getTemplates();
        Arrays.sort(templates, new Comparator<ItemTemplate>() {
            public int compare(ItemTemplate a, ItemTemplate b) {
                return Integer.compare(a.getTemplateId(), b.getTemplateId());
            }
        });

        File parent = outputFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        int count = 0;
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter(outputFile));
            for (ItemTemplate t : templates) {
                try {
                    String line = toJsonLine(t);
                    pw.println(line);
                    count++;
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Failed to serialize template id=" + t.getTemplateId(), e);
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to write item dump: " + outputFile, e);
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
        logger.info("ItemTemplateDumper: wrote " + count + " templates to " + outputFile.getAbsolutePath());
    }

    private String toJsonLine(ItemTemplate t) throws NoSuchSkillException {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        appendString(sb, "json-type", "item");
        sb.append(",");
        appendInt(sb, "templateId", t.getTemplateId());
        sb.append(",");
        appendString(sb, "name", t.getName());
        sb.append(",");
        appendString(sb, "plural", t.getPlural());
        sb.append(",");
        appendInt(sb, "size", inferSize(t.sizeString));
        sb.append(",");

        sb.append("\"descriptions\":{");
        appendString(sb, "superb", t.getDescriptionSuperb());
        sb.append(",");
        appendString(sb, "normal", t.getDescriptionNormal());
        sb.append(",");
        appendString(sb, "bad", t.getDescriptionBad());
        sb.append(",");
        appendString(sb, "rotten", t.getDescriptionRotten());
        sb.append(",");
        appendString(sb, "long", t.getDescriptionLong());
        sb.append("},");

        appendShortArray(sb, "itemTypes", reconstructItemTypes(t));
        sb.append(",");
        appendInt(sb, "imageNumber", t.imageNumber);
        sb.append(",");
        appendInt(sb, "behaviourType", t.getBehaviourType());
        sb.append(",");
        appendInt(sb, "combatDamage", t.getDamagePercent());
        sb.append(",");
        appendLong(sb, "decayTime", t.getDecayTime());
        sb.append(",");

        sb.append("\"dimensions\":{");
        appendInt(sb, "x", t.getSizeX());
        sb.append(",");
        appendInt(sb, "y", t.getSizeY());
        sb.append(",");
        appendInt(sb, "z", t.getSizeZ());
        sb.append("},");

        int primarySkill;
        try {
            primarySkill = t.getPrimarySkill();
        } catch (NoSuchSkillException e) {
            primarySkill = -10;
        }
        appendInt(sb, "primarySkill", primarySkill);
        sb.append(",");

        appendByteArray(sb, "bodySpaces", t.getBodySpaces());
        sb.append(",");
        appendString(sb, "modelName", t.getModelName());
        sb.append(",");
        appendDouble(sb, "difficulty", t.getDifficulty());
        sb.append(",");
        appendInt(sb, "weight", t.getWeightGrams());
        sb.append(",");
        appendInt(sb, "material", t.getMaterial() & 0xFF);
        sb.append(",");
        appendInt(sb, "value", t.getValue());
        sb.append(",");
        appendBool(sb, "isPurchased", t.isPurchased());

        sb.append("}");
        return sb.toString();
    }

    private int inferSize(String sizeString) {
        if (sizeString == null || sizeString.isEmpty()) return 3;
        String s = sizeString.trim();
        if (s.equals("tiny")) return 1;
        if (s.equals("small")) return 2;
        if (s.equals("large")) return 4;
        if (s.equals("huge")) return 5;
        return 3;
    }

    private short[] reconstructItemTypes(ItemTemplate t) {
        List<Short> types = new ArrayList<Short>();

        // HOLLOW
        if (boolOf(t, "hollow")) types.add((short) 1);

        // WEAPON subtypes — check specific types first; "weapon" alone = 37
        if (boolOf(t, "weaponslash")) types.add((short) 2);
        if (boolOf(t, "shield")) types.add((short) 3);
        if (boolOf(t, "armour")) types.add((short) 4);
        if (boolOf(t, "weaponpierce")) types.add((short) 13);
        if (boolOf(t, "weaponcrush")) types.add((short) 14);
        if (boolOf(t, "weaponaxe")) types.add((short) 15);
        if (boolOf(t, "weaponsword")) types.add((short) 16);
        if (boolOf(t, "weaponPolearm")) types.add((short) 154);
        if (boolOf(t, "weaponknife")) types.add((short) 17);
        if (boolOf(t, "weaponmisc")) types.add((short) 18);
        if (boolOf(t, "weaponmelee")) types.add((short) 35);

        // weapon (no specific subtype — type 37)
        boolean hasWeaponSubtype = boolOf(t, "weaponslash") || boolOf(t, "weaponpierce")
                || boolOf(t, "weaponcrush") || boolOf(t, "weaponaxe") || boolOf(t, "weaponsword")
                || boolOf(t, "weaponPolearm") || boolOf(t, "weaponknife") || boolOf(t, "weaponmisc")
                || boolOf(t, "weaponmelee");
        if (boolOf(t, "weapon") && !hasWeaponSubtype) types.add((short) 37);

        // FOOD — type 82 = isDish (also sets food+canLarder+namedCreator); type 5 = food (also sets canLarder)
        if (boolOf(t, "isDish")) types.add((short) 82);
        else if (boolOf(t, "food")) types.add((short) 5);

        // canLarder alone (not via food/isDish) = type 234
        if (boolOf(t, "canLarder") && !boolOf(t, "food") && !boolOf(t, "isDish")) types.add((short) 234);

        // MAGIC
        if (boolOf(t, "magic")) types.add((short) 6);

        // TOOLS
        if (boolOf(t, "fieldtool")) types.add((short) 7);

        // BODYPART — type 8 also sets temporary
        if (boolOf(t, "bodypart")) types.add((short) 8);

        if (boolOf(t, "inventory")) types.add((short) 9);
        if (boolOf(t, "miningtool")) types.add((short) 10);
        if (boolOf(t, "carpentrytool")) types.add((short) 11);
        if (boolOf(t, "smithingtool")) types.add((short) 12);
        if (boolOf(t, "diggingtool")) types.add((short) 19);
        if (boolOf(t, "seed")) types.add((short) 20);
        if (boolOf(t, "wood")) types.add((short) 21);
        if (boolOf(t, "metal")) types.add((short) 22);
        if (boolOf(t, "leather")) types.add((short) 23);
        if (boolOf(t, "cloth")) types.add((short) 24);
        if (boolOf(t, "stone")) types.add((short) 25);
        if (boolOf(t, "liquid")) types.add((short) 26);
        if (boolOf(t, "melting")) types.add((short) 27);
        if (boolOf(t, "meat")) types.add((short) 28);
        if (boolOf(t, "vegetable")) types.add((short) 29);
        if (boolOf(t, "pottery")) types.add((short) 30);
        if (boolOf(t, "notake")) types.add((short) 31);

        // LIGHT — type 116 sets brightLight+light; type 32 = light alone
        if (boolOf(t, "brightLight")) types.add((short) 116);
        else if (boolOf(t, "light")) types.add((short) 32);

        if (boolOf(t, "containerliquid")) types.add((short) 33);
        if (boolOf(t, "liquidinflammable")) types.add((short) 34);
        if (boolOf(t, "fish")) types.add((short) 36);
        if (boolOf(t, "tool")) types.add((short) 38);
        if (boolOf(t, "lock")) types.add((short) 39);
        if (boolOf(t, "indestructible")) types.add((short) 40);
        if (boolOf(t, "key")) types.add((short) 41);
        if (boolOf(t, "nodrop")) types.add((short) 42);
        if (boolOf(t, "repairable")) types.add((short) 44);

        // TEMPORARY — type 8 also sets temporary; only emit 45 if not from bodypart
        if (boolOf(t, "temporary") && !boolOf(t, "bodypart")) types.add((short) 45);

        if (boolOf(t, "combine")) types.add((short) 46);
        if (boolOf(t, "lockable")) types.add((short) 47);
        if (boolOf(t, "canHaveInscription")) types.add((short) 159);

        // HASDATA — type 62 sets hasdata+isButcheredItem; type 48 = hasdata alone
        if (boolOf(t, "isButcheredItem")) types.add((short) 62);
        else if (boolOf(t, "hasdata")) types.add((short) 48);

        if (boolOf(t, "outsideonly")) types.add((short) 49);

        // COIN — type 50 sets coin+fullprice; type 53 = fullprice alone
        if (boolOf(t, "coin")) types.add((short) 50);
        else if (boolOf(t, "fullprice")) types.add((short) 53);

        if (boolOf(t, "turnable")) types.add((short) 51);
        if (boolOf(t, "decoration")) types.add((short) 52);
        if (boolOf(t, "norename")) types.add((short) 54);
        if (boolOf(t, "nonutrition")) types.add((short) 137);
        if (boolOf(t, "lownutrition")) types.add((short) 55);
        if (boolOf(t, "mediumnutrition")) types.add((short) 74);
        if (boolOf(t, "goodnutrition")) types.add((short) 75);
        if (boolOf(t, "highnutrition")) types.add((short) 76);
        if (boolOf(t, "isFoodMaker")) types.add((short) 77);
        if (boolOf(t, "draggable")) types.add((short) 56);
        if (boolOf(t, "villagedeed")) types.add((short) 57);
        if (boolOf(t, "homesteaddeed")) types.add((short) 58);

        // ALWAYSPOLL — type 124 sets isWind+alwayspoll; type 59 = alwayspoll alone
        if (boolOf(t, "isWind")) types.add((short) 124);
        else if (boolOf(t, "alwayspoll")) types.add((short) 59);

        if (boolOf(t, "floating")) types.add((short) 60);
        if (boolOf(t, "notrade")) types.add((short) 61);
        if (boolOf(t, "isNoPut")) types.add((short) 63);
        if (boolOf(t, "isLeadCreature")) types.add((short) 64);
        if (boolOf(t, "isLeadMultipleCreatures")) types.add((short) 198);
        if (boolOf(t, "isFire")) types.add((short) 65);
        if (boolOf(t, "domainItem")) types.add((short) 66);
        if (boolOf(t, "useOnGroundOnly")) types.add((short) 67);
        if (boolOf(t, "hugeAltar")) types.add((short) 68);

        // ARTIFACT — type 69 sets artifact+alwaysLoaded+isServerBound
        if (boolOf(t, "artifact")) types.add((short) 69);
        // UNIQUE — type 70 sets unique+alwaysLoaded
        if (boolOf(t, "unique")) types.add((short) 70);
        // ROYAL — type 122 sets isRoyal+isServerBound+alwaysLoaded
        if (boolOf(t, "isRoyal")) types.add((short) 122);
        // ALWAYSLOADED alone — type 114
        if (boolOf(t, "alwaysLoaded") && !boolOf(t, "artifact") && !boolOf(t, "unique") && !boolOf(t, "isRoyal"))
            types.add((short) 114);

        // SERVERBOUND alone — type 83 (69 and 122 also set it)
        if (boolOf(t, "isServerBound") && !boolOf(t, "artifact") && !boolOf(t, "isRoyal"))
            types.add((short) 83);

        if (boolOf(t, "destroysHugeAltar")) types.add((short) 71);
        if (boolOf(t, "passFullData")) types.add((short) 72);
        if (boolOf(t, "isForm")) types.add((short) 73);
        if (boolOf(t, "herb")) types.add((short) 78);
        if (boolOf(t, "spice")) types.add((short) 205);
        if (boolOf(t, "poison")) types.add((short) 79);
        if (boolOf(t, "fruit")) types.add((short) 80);
        if (boolOf(t, "descIsExam")) types.add((short) 81);
        if (boolOf(t, "isServerBound")) { /* handled above */ }

        if (boolOf(t, "isTwohanded")) types.add((short) 84);
        if (boolOf(t, "kingdomMarker")) types.add((short) 85);
        if (boolOf(t, "destroyable")) types.add((short) 86);
        if (boolOf(t, "priceAffectedByMaterial")) types.add((short) 87);
        if (boolOf(t, "liquidCooking")) types.add((short) 88);
        if (boolOf(t, "positiveDecay")) types.add((short) 89);
        if (boolOf(t, "drinkable")) types.add((short) 90);
        if (boolOf(t, "isColor")) types.add((short) 91);
        if (boolOf(t, "colorable")) types.add((short) 92);
        if (boolOf(t, "gem")) types.add((short) 93);
        if (boolOf(t, "bow")) types.add((short) 94);
        if (boolOf(t, "bowUnstringed")) types.add((short) 95);
        if (boolOf(t, "egg")) types.add((short) 96);
        if (boolOf(t, "newbieItem")) types.add((short) 97);
        if (boolOf(t, "challengeNewbieItem")) types.add((short) 189);
        if (boolOf(t, "isTileAligned")) types.add((short) 98);
        if (boolOf(t, "isDragonArmour")) types.add((short) 99);
        if (boolOf(t, "isCompass")) types.add((short) 100);
        if (boolOf(t, "isToolbelt")) types.add((short) 121);
        if (boolOf(t, "oilConsuming")) types.add((short) 101);

        // HEALING — types 102-106 distinguished by alchemyType
        if (boolOf(t, "healing")) {
            int alchType = intOf(t, "alchemyType");
            if (alchType >= 1 && alchType <= 5) {
                types.add((short) (101 + alchType));
            } else {
                types.add((short) 102);
            }
        }

        // NAMEDCREATOR — type 82 also sets this (handled above); type 108 alone
        if (boolOf(t, "namedCreator") && !boolOf(t, "isDish")) types.add((short) 108);

        if (boolOf(t, "onePerTile")) types.add((short) 109);
        if (boolOf(t, "fourPerTile")) types.add((short) 167);
        if (boolOf(t, "tenPerTile")) types.add((short) 166);
        if (boolOf(t, "bed")) types.add((short) 110);
        if (boolOf(t, "insideOnly")) types.add((short) 111);

        // NOBANK — type 113 sets isRecycled+nobank; type 155 = alwaysBankable (nobank=false); type 112 = nobank alone
        if (boolOf(t, "alwaysBankable")) types.add((short) 155);
        if (boolOf(t, "isRecycled")) types.add((short) 113);
        if (boolOf(t, "nobank") && !boolOf(t, "isRecycled") && !boolOf(t, "alwaysBankable")) types.add((short) 112);

        if (boolOf(t, "flickeringLight")) types.add((short) 115);
        if (boolOf(t, "isVehicle")) types.add((short) 117);
        if (boolOf(t, "isChair")) types.add((short) 197);
        if (boolOf(t, "isVehicleDragged")) types.add((short) 134);
        if (boolOf(t, "isCart")) types.add((short) 193);

        // FLOWER — type 118 sets isFlower+isNaturePlantable; type 186 = isNaturePlantable alone
        if (boolOf(t, "isFlower")) types.add((short) 118);
        else if (boolOf(t, "isNaturePlantable")) types.add((short) 186);

        if (boolOf(t, "isImproveItem")) types.add((short) 119);
        if (boolOf(t, "isDeathProtection")) types.add((short) 120);
        if (boolOf(t, "isNoMove")) types.add((short) 123);
        if (boolOf(t, "isDredgingTool")) types.add((short) 125);
        if (boolOf(t, "isMineDoor")) types.add((short) 126);
        if (boolOf(t, "isNoSellBack")) types.add((short) 127);
        if (boolOf(t, "isSpringFilled")) types.add((short) 128);
        if (boolOf(t, "destroyOnDecay")) types.add((short) 129);
        if (boolOf(t, "rechargeable")) types.add((short) 130);
        if (boolOf(t, "isServerPortal")) types.add((short) 131);
        if (boolOf(t, "isTrap")) types.add((short) 132);
        if (boolOf(t, "isDisarmTrap")) types.add((short) 133);
        if (boolOf(t, "ownerDestroyable")) types.add((short) 135);
        if (boolOf(t, "wearableByCreaturesOnly")) types.add((short) 136);
        if (boolOf(t, "puppet")) types.add((short) 138);
        if (boolOf(t, "overrideNonEnchantable")) types.add((short) 139);
        if (boolOf(t, "isMeditation")) types.add((short) 140);
        if (boolOf(t, "isTransmutable")) types.add((short) 141);
        if (boolOf(t, "sign")) types.add((short) 142);
        if (boolOf(t, "streetlamp")) types.add((short) 143);
        if (boolOf(t, "visibleDecay")) types.add((short) 144);
        if (boolOf(t, "bulkContainer")) types.add((short) 145);
        if (boolOf(t, "bulk")) types.add((short) 146);
        if (boolOf(t, "missions")) types.add((short) 147);
        if (boolOf(t, "notMissions")) types.add((short) 157);
        if (boolOf(t, "combineCold")) types.add((short) 148);
        if (boolOf(t, "spawnsTrees")) types.add((short) 149);
        if (boolOf(t, "killsTrees")) types.add((short) 150);
        if (boolOf(t, "isCrude")) types.add((short) 151);
        if (boolOf(t, "minable")) types.add((short) 152);
        if (boolOf(t, "isEnchantableJewelry")) types.add((short) 153);
        if (boolOf(t, "alwaysLit")) types.add((short) 156);
        if (boolOf(t, "isMassProduction")) types.add((short) 158);
        if (boolOf(t, "noWorkParent")) types.add((short) 160);
        if (boolOf(t, "isWarTarget")) types.add((short) 161);
        if (boolOf(t, "isSourceSpring")) types.add((short) 162);
        if (boolOf(t, "isSource")) types.add((short) 163);
        if (boolOf(t, "isColorComponent")) types.add((short) 164);
        if (boolOf(t, "isTutorialItem")) types.add((short) 165);
        if (boolOf(t, "isEquipmentSlot")) types.add((short) 170);
        if (boolOf(t, "inventoryGroup")) types.add((short) 171);
        if (boolOf(t, "isAbility")) types.add((short) 168);
        if (boolOf(t, "plantedFlowerpot")) types.add((short) 169);
        if (boolOf(t, "isMagicStaff")) types.add((short) 172);
        if (boolOf(t, "improveUsesTypeAsMaterial")) types.add((short) 173);
        if (boolOf(t, "noDiscard")) types.add((short) 174);
        if (boolOf(t, "instaDiscard")) types.add((short) 175);
        if (boolOf(t, "isTransportable")) types.add((short) 176);
        if (boolOf(t, "isWarmachine")) types.add((short) 177);
        if (boolOf(t, "hideAddToCreationWindow")) types.add((short) 178);
        if (boolOf(t, "isBrazier")) types.add((short) 179);
        if (boolOf(t, "usesSpecifiedContainerSizes")) types.add((short) 180);
        if (boolOf(t, "isTent")) types.add((short) 181);
        if (boolOf(t, "useMaterialAndKingdom")) types.add((short) 182);
        if (boolOf(t, "isSmearable")) types.add((short) 183);
        if (boolOf(t, "isCarpet")) types.add((short) 184);
        if (boolOf(t, "isMilk")) types.add((short) 191);
        if (boolOf(t, "isCheese")) types.add((short) 192);
        if (boolOf(t, "noImprove")) types.add((short) 187);
        if (boolOf(t, "isTapestry")) types.add((short) 188);
        if (boolOf(t, "isUnfinishedNoTake")) types.add((short) 190);
        if (boolOf(t, "isOwnerTurnable")) types.add((short) 194);
        if (boolOf(t, "isOwnerMoveable")) types.add((short) 195);
        if (boolOf(t, "isUnfired")) types.add((short) 196);

        // PLANTABLE — 200 sets isPlantable+isPlantOneAWeek; 244 sets isPlantable+decorationWhenPlanted; 199 = isPlantable alone
        if (boolOf(t, "isPlantOneAWeek")) types.add((short) 200);
        else if (boolOf(t, "decorationWhenPlanted")) types.add((short) 244);
        else if (boolOf(t, "isPlantable")) types.add((short) 199);

        if (boolOf(t, "isHitchTarget")) types.add((short) 201);
        if (boolOf(t, "isPotable")) types.add((short) 206);
        if (boolOf(t, "canBeGrownInPot")) types.add((short) 221);
        if (boolOf(t, "isCooker")) types.add((short) 209);
        if (boolOf(t, "isFoodGroup")) types.add((short) 208);
        if (boolOf(t, "isCookingTool")) types.add((short) 210);
        if (boolOf(t, "isRecipeItem")) types.add((short) 211);
        if (boolOf(t, "isNoCreate")) types.add((short) 207);

        // USESFOODSTATE — 222 sets canBePapyrusWrapped+usesFoodState; 212 = usesFoodState alone
        if (boolOf(t, "canBePapyrusWrapped")) types.add((short) 222);
        else if (boolOf(t, "usesFoodState")) types.add((short) 212);

        if (boolOf(t, "canBeFermented")) types.add((short) 213);
        if (boolOf(t, "canBeDistilled")) types.add((short) 214);
        if (boolOf(t, "canBeSealed")) types.add((short) 215);
        if (boolOf(t, "canBePegged")) types.add((short) 236);
        if (boolOf(t, "canBeCookingOil")) types.add((short) 217);
        if (boolOf(t, "useRealTemplateIcon")) types.add((short) 216);
        if (boolOf(t, "hovers")) types.add((short) 218);
        if (boolOf(t, "foodBonusHot")) types.add((short) 219);
        if (boolOf(t, "foodBonusCold")) types.add((short) 220);
        if (boolOf(t, "canBeRawWrapped")) types.add((short) 223);
        if (boolOf(t, "canBeClothWrapped")) types.add((short) 224);
        if (boolOf(t, "surfaceonly")) types.add((short) 225);
        if (boolOf(t, "isMushroom")) types.add((short) 226);
        if (boolOf(t, "canShowRaw")) types.add((short) 228);
        if (boolOf(t, "cannotBeSpellTarget")) types.add((short) 229);
        if (boolOf(t, "isTrellis")) types.add((short) 230);
        if (boolOf(t, "containsIngredientsOnly")) types.add((short) 231);
        if (boolOf(t, "isComponentItem")) types.add((short) 232);
        if (boolOf(t, "parentMustBeOnGround")) types.add((short) 240);
        if (boolOf(t, "usesRealTemplate")) types.add((short) 233);
        if (boolOf(t, "isRune")) types.add((short) 235);
        if (boolOf(t, "decayOnDeed")) types.add((short) 237);
        if (boolOf(t, "isInsulated")) types.add((short) 238);
        if (boolOf(t, "isGuardTower")) types.add((short) 239);
        if (boolOf(t, "isRoadMarker")) types.add((short) 241);
        if (boolOf(t, "isPaveable")) types.add((short) 242);
        if (boolOf(t, "isCavePaveable")) types.add((short) 243);
        if (boolOf(t, "descIsName")) types.add((short) 245);

        // NOTRUNEABLE — 40 sets indestructible+isNotRuneable; 146 sets bulk+isNotRuneable; 235 sets isRune+isNotRuneable; 246 alone
        if (boolOf(t, "isNotRuneable") && !boolOf(t, "indestructible") && !boolOf(t, "bulk") && !boolOf(t, "isRune"))
            types.add((short) 246);

        if (boolOf(t, "showsSlopes")) types.add((short) 247);
        if (boolOf(t, "isPluralName")) types.add((short) 248);
        if (boolOf(t, "supportsSecondryColor")) types.add((short) 249);
        if (boolOf(t, "isFishingReel")) types.add((short) 250);
        if (boolOf(t, "isFishingLine")) types.add((short) 251);
        if (boolOf(t, "isFishingFloat")) types.add((short) 252);
        if (boolOf(t, "isFishingHook")) types.add((short) 253);
        if (boolOf(t, "isFishingBait")) types.add((short) 254);
        if (boolOf(t, "hasExtraData")) types.add((short) 255);

        // VIEWABLE SUBITEMS — 259 sets viewableSubItems+isContainerWithSubItems; 256 = viewableSubItems alone
        if (boolOf(t, "isContainerWithSubItems")) types.add((short) 259);
        else if (boolOf(t, "viewableSubItems")) types.add((short) 256);

        if (boolOf(t, "createsWithLock")) types.add((short) 257);
        if (boolOf(t, "isBracelet")) types.add((short) 258);

        short[] result = new short[types.size()];
        for (int i = 0; i < types.size(); i++) {
            result[i] = types.get(i);
        }
        return result;
    }

    private boolean boolOf(ItemTemplate t, String fieldName) {
        try {
            Field f = getField(fieldName);
            return f.getBoolean(t);
        } catch (Exception e) {
            return false;
        }
    }

    private int intOf(ItemTemplate t, String fieldName) {
        try {
            Field f = getField(fieldName);
            return f.getInt(t);
        } catch (Exception e) {
            return 0;
        }
    }

    private Field getField(String name) {
        Field f = FIELD_CACHE.get(name);
        if (f == null) {
            try {
                f = ItemTemplate.class.getDeclaredField(name);
                f.setAccessible(true);
                FIELD_CACHE.put(name, f);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("ItemTemplate field not found: " + name, e);
            }
        }
        return f;
    }

    // --- JSON helpers ---

    private void appendString(StringBuilder sb, String key, String value) {
        sb.append('"').append(escapeJson(key)).append("\":\"").append(escapeJson(value)).append('"');
    }

    private void appendInt(StringBuilder sb, String key, int value) {
        sb.append('"').append(key).append("\":").append(value);
    }

    private void appendLong(StringBuilder sb, String key, long value) {
        sb.append('"').append(key).append("\":").append(value);
    }

    private void appendDouble(StringBuilder sb, String key, double value) {
        // Format with one decimal place if it's a whole number, else enough precision
        if (value == Math.floor(value) && !Double.isInfinite(value)) {
            sb.append('"').append(key).append("\":").append((long) value).append(".0");
        } else {
            sb.append('"').append(key).append("\":").append(value);
        }
    }

    private void appendBool(StringBuilder sb, String key, boolean value) {
        sb.append('"').append(key).append("\":").append(value);
    }

    private void appendShortArray(StringBuilder sb, String key, short[] values) {
        sb.append('"').append(key).append("\":[");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(values[i]);
        }
        sb.append("]");
    }

    private void appendByteArray(StringBuilder sb, String key, byte[] values) {
        sb.append('"').append(key).append("\":[");
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                if (i > 0) sb.append(",");
                sb.append(values[i] & 0xFF);
            }
        }
        sb.append("]");
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\t"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }
}
