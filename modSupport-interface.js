var ItemTemplateFactory = Packages.com.wurmonline.server.items.ItemTemplateFactory;
var ReflectionUtil = Packages.org.gotti.wurmunlimited.modloader.ReflectionUtil;
var Class = Packages.java.lang.Class;
var Integer = Packages.java.lang.Integer;
var Double = Packages.java.lang.Double;

var initializeDatabase = function() {
    var ModSupportDb = Packages.org.gotti.wurmunlimited.modsupport.ModSupportDb;

    var Logger = Packages.java.util.logging.Logger;
    var logger = Logger.getLogger("scriptRunnerMods.TheWinterExpanse.mod-support-interface.initializeDatabase");

    var modSupportDbConn = ModSupportDb.getModSupportDb();

    if (modSupportDbConn) {
        var createItemOverrideTableQuery = modSupportDbConn.prepareStatement("create table if not exists itemOverrides ( templateId INTEGER PRIMARY KEY, name TEXT, plural TEXT, itemDescriptionSuperb TEXT, itemDescriptionNormal TEXT, itemDescriptionBad TEXT, itemDescriptionRotten TEXT, itemDescriptionLong TEXT, itemTypes TEXT, imageNumber INTEGER, behaviorType INTEGER, combatDamage INTEGER, decayTime INTEGER, centimetersX INTEGER, centimetersY INTEGER, centimetersZ INTEGER, primarySkill INTEGER, bodySpaces TEXT, modelName TEXT, difficulty DECIMAL, weightGrams INTEGER, material INTEGER, \"size\" INTEGER, \"value\" INTEGER, isPurchased boolean, dyeAmountGramsOverride INTEGER, updateExisting INTEGER );");
        createItemOverrideTableQuery.executeUpdate();
    }
}

var updateExistingItems = function() {
    var ModSupportDb = Packages.org.gotti.wurmunlimited.modsupport.ModSupportDb;

    var Logger = Packages.java.util.logging.Logger;
    var logger = Logger.getLogger("scriptRunnerMods.TheWinterExpanse.mod-support-interface.updateExistingItems");

    var modSupportDbConn = ModSupportDb.getModSupportDb();

    if (modSupportDbConn) {
        var itemOverridesQuery = modSupportDbConn.prepareStatement("select * from itemOverrides");
        var itemOverridesResults = itemOverridesQuery.executeQuery();

        var existingItemOverrides = {};

        while(itemOverridesResults.next()) {
            existingItemOverrides[itemOverridesResults.getString("templateId")] = {
                templateId: itemOverridesResults.getString("templateId"),
                name: itemOverridesResults.getString("name"),
                plural: itemOverridesResults.getString("plural"),
                itemDescriptionSuperb: itemOverridesResults.getString("itemDescriptionSuperb"),
                itemDescriptionNormal: itemOverridesResults.getString("itemDescriptionNormal"),
                itemDescriptionBad: itemOverridesResults.getString("itemDescriptionBad"),
                itemDescriptionRotten: itemOverridesResults.getString("itemDescriptionRotten"),
                itemDescriptionLong: itemOverridesResults.getString("itemDescriptionLong"),
                itemTypes: itemOverridesResults.getString("itemTypes"),
                imageNumber: itemOverridesResults.getString("imageNumber"),
                behaviorType: itemOverridesResults.getString("behaviorType"),
                combatDamage: itemOverridesResults.getString("combatDamage"),
                decayTime: itemOverridesResults.getString("decayTime"),
                centimetersX: itemOverridesResults.getString("centimetersX"),
                centimetersY: itemOverridesResults.getString("centimetersY"),
                centimetersZ: itemOverridesResults.getString("centimetersZ"),
                primarySkill: itemOverridesResults.getString("primarySkill"),
                bodySpaces: itemOverridesResults.getString("bodySpaces"),
                modelName: itemOverridesResults.getString("modelName"),
                difficulty: itemOverridesResults.getString("difficulty"),
                weightGrams: itemOverridesResults.getString("weightGrams"),
                material: itemOverridesResults.getString("material"),
                size: itemOverridesResults.getString("size"),
                value: itemOverridesResults.getString("value"),
                isPurchased: itemOverridesResults.getString("isPurchased"),
                dyeAmountGramsOverride: itemOverridesResults.getString("dyeAmountGramsOverride"),
            };
        }

        var itemTemplates = ItemTemplateFactory.getInstance().getTemplates();

        for (var i = 0; i < itemTemplates.length; i++) {
            var instance = itemTemplates[i];
            var instanceId = ReflectionUtil.getPrivateField(instance, ReflectionUtil.getField(Class.forName("com.wurmonline.server.items.ItemTemplate"), "templateId"));

            if (existingItemOverrides[instanceId]) {
                var item = existingItemOverrides[instanceId];
                // logger.info("Attempting to mod: " + item.templateId);
                // logger.info(JSON.stringify(item));

                var keys = Object.keys(item);

                keys.forEach(function(key) {
                    if (key !== 'templateId' && item[key]) {
                        // Reference field is not template id and there exists an override request in the record for this key
                        overrideItemTemplateField(instance, key, item[key]);
                    }
                });
            }
        }
    } else {
        logger.info("Bork Bork");
    }
}

// Currently supported fields: name
var overrideItemTemplateField = function(instance, field, value) {
    switch(field) {
        case 'name':
        case 'plural':
        case 'itemDescriptionSuperb':
        case 'itemDescriptionNormal':
        case 'itemDescriptionBad':
        case 'itemDescriptionRotten':
        case 'itemDescriptionLong':
        case 'modelName':
            // This covers string based changes, i.e. the easy modifications
            ReflectionUtil.setPrivateField(instance, ReflectionUtil.getField(Class.forName("com.wurmonline.server.items.ItemTemplate"), field), value);
            break;
        case 'itemTypes':
        case 'bodySpaces':
            // This covers arrays of numbers
            // We expect value to be a string containing comma seperated numbers, and that's it...anything else is an error
            // First, we sanitize the string by stripping all non-digits and commas (i.e. the only allowed characters) as well as all spaces
            var preSplit = value.replace(/\s/g, '').replace(/[^\d,]/g, '');

            // logger.info(preSplit);
            // We only want to do this if there are actual types left to adjust
            if (preSplit.length > 0) {
                var types = preSplit.split(',');

                // Clear out all flags and set things to default values
                clearItemTemplateItemTypes(instance);

                // Set the new flags based on the supplied array
                instance.assignTypes(types);
            }
            break;
        case 'difficulty':
        case 'value':
        case 'centimetersX':
        case 'centimetersY':
        case 'centimetersZ':
        case 'dyeAmountGramsOverride':
        case 'size':
        case 'material':
        case 'weightGrams':
        case 'combatDamage':
        case 'decayTime':
        case 'primarySkill':
            // This covers number based changes
            var toSet = Integer.parseInt(Double.toString(value).replace(".0", ""), 10);
            ReflectionUtil.setPrivateField(instance, ReflectionUtil.getField(Class.forName("com.wurmonline.server.items.ItemTemplate"), field), toSet);
            break;
        // Unable to determine how to set Java shorts from javascript...will investigate further in the future
        // case 'imageNumber':
        // case 'behaviorType':
        //     // These are short fields, and as such, bit shifting is required?
        //     var toSet = (Integer.parseInt(Double.toString(value).replace(".0", ""), 10) << 16) >> 16;
        //     ReflectionUtil.setPrivateField(instance, ReflectionUtil.getField(Class.forName("com.wurmonline.server.items.ItemTemplate"), field), toSet);
        //     break;
    }
}

// We make this a separate function because there are a lot of things to set here and I didn't want to
//  pollute the logic function with all of this if it isn't necessary
var clearItemTemplateItemTypes = function(instance) {
    var types = [
        'isBracelet',
        'createsWithLock',
        'isContainerWithSubItems',
        'viewableSubItems',
        'hasExtraData',
        'isFishingBait',
        'isFishingHook',
        'isFishingFloat',
        'isFishingLine',
        'isFishingReel',
        'supportsSecondryColor',
        'isPluralName',
        'showsSlopes',
        'isNotRuneable',
        'descIsName',
        'decorationWhenPlanted',
        'isPlantable',
        'isCavePaveable',
        'isPaveable',
        'isRoadMarker',
        'isGuardTower',
        'isInsulated',
        'decayOnDeed',
        'isRune',
        'canLarder',
        'usesRealTemplate',
        'parentMustBeOnGround',
        'isComponentItem',
        'containsIngredientsOnly',
        'isTrellis',
        'cannotBeSpellTarget',
        'canShowRaw',
        'isMushroom',
        'surfaceonly',
        'canBeClothWrapped',
        'usesFoodState',
        'canBePapyrusWrapped',
        'canBeRawWrapped',
        'foodBonusCold',
        'foodBonusHot',
        'hovers',
        'useRealTemplateIcon',
        'canBeCookingOil',
        'canBePegged',
        'canBeSealed',
        'canBeDistilled',
        'canBeFermented',
        'usesFoodState',
        'isNoCreate',
        'isRecipeItem',
        'isCookingTool',
        'isFoodGroup',
        'isCooker',
        'canBeGrownInPot',
        'isPotable',
        'isHitchTarget',
        'isPlantOneAWeek',
        'isUnfired',
        'isOwnerMoveable',
        'isOwnerTurnable',
        'isUnfinishedNoTake',
        'isTapestry',
        'noImprove',
        'isCheese',
        'isMilk',
        'isCarpet',
        'isSmearable',
        'useMaterialAndKingdom',
        'isTent',
        'usesSpecifiedContainerSizes',
        'isBrazier',
        'hideAddToCreationWindow',
        'isWarmachine',
        'isTransportable',
        'instaDiscard',
        'noDiscard',
        'improveUsesTypeAsMaterial',
        'isMagicStaff',
        'plantedFlowerpot',
        'isAbility',
        'isEquipmentSlot',
        'isTutorialItem',
        'isColorComponent',
        'isSource',
        'isSourceSpring',
        'isWarTarget',
        'noWorkParent',
        'isMassProduction',
        'alwaysLit',
        'isEnchantableJewelry',
        'minable',
        'isCrude',
        'killsTrees',
        'spawnsTrees',
        'combineCold',
        'notMissions',
        'missions',
        'bulk',
        'bulkContainer',
        'visibleDecay',
        'isTransmutable',
        'isMeditation',
        'overrideNonEnchantable',
        'puppet',
        'wearableByCreaturesOnly',
        'ownerDestroyable',
        'isDisarmTrap',
        'isTrap',
        'isServerPortal',
        'rechargeable',
        'destroyOnDecay',
        'isSpringFilled',
        'isNoSellBack',
        'isMineDoor',
        'isDredgingTool',
        'alwayspoll',
        'isWind',
        'isNoMove',
        'isServerBound',
        'isRoyal',
        'isDeathProtection',
        'isImproveItem',
        'isNaturePlantable',
        'isFlower',
        'isCart',
        'isVehicleDragged',
        'isChair',
        'isVehicle',
        'light',
        'brightLight',
        'flickeringLight',
        'nobank',
        'isRecycled',
        'alwaysBankable',
        'insideOnly',
        'bed',
        'tenPerTile',
        'fourPerTile',
        'onePerTile',
        'healing',
        'oilConsuming',
        'isToolbelt',
        'isCompass',
        'isDragonArmour',
        'isTileAligned',
        'challengeNewbieItem',
        'newbieItem',
        'egg',
        'bowUnstringed',
        'bow',
        'gem',
        'colorable',
        'isColor',
        'drinkable',
        'positiveDecay',
        'liquidCooking',
        'priceAffectedByMaterial',
        'destroyable',
        'kingdomMarker',
        'isTwohanded',
        'namedCreator',
        'isDish',
        'descIsExam',
        'fruit',
        'poison',
        'spice',
        'herb',
        'isForm',
        'passFullData',
        'destroysHugeAltar',
        'alwaysLoaded',
        'unique',
        'artifact',
        'nonDeedable',
        'hugeAltar',
        'useOnGroundOnly',
        'domainItem',
        'isFire',
        'isLeadMultipleCreatures',
        'isLeadCreature',
        'isNoPut',
        'isButcheredItem',
        'notrade',
        'floating',
        'homesteaddeed',
        'villagedeed',
        'draggable',
        'isFoodMaker',
        'highnutrition',
        'goodnutrition',
        'mediumnutrition',
        'lownutrition',
        'nonutrition',
        'norename',
        'fullprice',
        'decoration',
        'turnable',
        'coin',
        'outsideonly',
        'hasdata',
        'canHaveInscription',
        'lockable',
        'combine',
        'temporary',
        'repairable',
        'nodrop',
        'key',
        'indestructible',
        'lock',
        'tool',
        'weapon',
        'fish',
        'weaponmelee',
        'liquidinflammable',
        'containerliquid',
        'notake',
        'pottery',
        'vegetable',
        'streetlamp',
        'sign',
        'meat',
        'melting',
        'liquid',
        'stone',
        'cloth',
        'leather',
        'metal',
        'wood',
        'seed',
        'diggingtool',
        'weaponmisc',
        'weaponknife',
        'weaponPolearm',
        'weaponsword',
        'weaponaxe',
        'weaponcrush',
        'weaponpierce',
        'smithingtool',
        'carpentrytool',
        'miningtool',
        'inventory',
        'temporary',
        'bodypart',
        'fieldtool',
        'magic',
        'food',
        'armour',
        'shield',
        'weaponslash',
        'hollow',
        'alchemyType',
        'improveItem'
    ];

    types.forEach(function(type) {
        ReflectionUtil.setPrivateField(instance, ReflectionUtil.getField(Class.forName("com.wurmonline.server.items.ItemTemplate"), type), (type === 'alchemyType' ? 0 : (type === 'improveItem' ? -1 : false)));
    });
}

module.exports = {
  initializeDatabase: initializeDatabase,
  UpdateExistingItems: updateExistingItems
};