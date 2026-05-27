package org.gotti.wurmtweaker.items;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.items.ItemTemplateFactory;
import org.gotti.wurmtweaker.json.ContentHandler;
import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modsupport.ItemTemplateBuilder;
import org.gotti.wurmunlimited.modsupport.items.ItemIdParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ItemHandler implements ContentHandler<ItemDefinition> {

    private static final Logger logger = Logger.getLogger(ItemHandler.class.getName());

    @Override
    public String getTypeName() {
        return "item";
    }

    @Override
    public Class<ItemDefinition> getDefinitionClass() {
        return ItemDefinition.class;
    }

    @Override
    public void apply(ItemDefinition def) {
        apply(def, null);
    }

    @Override
    public void apply(ItemDefinition def, File sourceFile) {
        if (def.identifier != null) {
            createItem(def, sourceFile);
        } else if (def.templateId != null || def.templateName != null) {
            modifyItem(def);
        } else {
            logger.warning("WurmTweaker: item definition has no lookup key "
                    + "(identifier, templateId, or templateName) — skipping");
        }
    }

    // --- Modification path ---

    private void modifyItem(ItemDefinition def) {
        ItemTemplate template = lookupTemplate(def);
        if (template == null) return;

        // Group A — private/final core fields via reflection
        if (def.name          != null) setFinalField(template, "name",          def.name);
        if (def.plural        != null) setFinalField(template, "plural",        def.plural);
        if (def.size          != null) setFinalField(template, "size",          def.size);
        if (def.imageNumber   != null) setFinalField(template, "imageNumber",   (short)(int) def.imageNumber);
        if (def.behaviourType != null) setFinalField(template, "behaviourType", (short)(int) def.behaviourType);
        if (def.combatDamage  != null) setFinalField(template, "combatDamage",  def.combatDamage);
        if (def.decayTime     != null) setFinalField(template, "decayTime",     def.decayTime);
        if (def.primarySkill  != null) setFinalField(template, "primarySkill",  def.primarySkill);
        if (def.modelName     != null) setFinalField(template, "modelName",     def.modelName);
        if (def.difficulty    != null) setFinalField(template, "difficulty",    def.difficulty);
        if (def.weight        != null) setFinalField(template, "weight",        def.weight);
        if (def.material      != null) setFinalField(template, "material",      (byte)(int) def.material);
        if (def.value         != null) setFinalField(template, "value",         def.value);
        if (def.isPurchased   != null) setFinalField(template, "isPurchased",   def.isPurchased);

        if (def.descriptions != null) {
            ItemDefinition.Descriptions d = def.descriptions;
            if (d.superb   != null) setFinalField(template, "itemDescriptionSuperb",  d.superb);
            if (d.normal   != null) setFinalField(template, "itemDescriptionNormal",  d.normal);
            if (d.bad      != null) setFinalField(template, "itemDescriptionBad",     d.bad);
            if (d.rotten   != null) setFinalField(template, "itemDescriptionRotten",  d.rotten);
            if (d.longDesc != null) setFinalField(template, "itemDescriptionLong",    d.longDesc);
        }

        if (def.dimensions != null) {
            ItemDefinition.Dimensions dim = def.dimensions;
            if (dim.x != null) setFinalField(template, "centimetersX", dim.x);
            if (dim.y != null) setFinalField(template, "centimetersY", dim.y);
            if (dim.z != null) setFinalField(template, "centimetersZ", dim.z);
        }

        if (def.bodySpaces != null) setFinalField(template, "bodySpaces", toByteArray(def.bodySpaces));

        // Group B — item types
        if (def.itemTypes != null) template.assignTypes(toShortArray(def.itemTypes));

        // Groups C + D — optional fields (shared with creation path)
        applyOptionalFields(template, def);

        logger.info("WurmTweaker: modified item templateId="
                + template.getTemplateId() + " (" + template.getName() + ")");
    }

    private ItemTemplate lookupTemplate(ItemDefinition def) {
        if (def.templateId != null) {
            ItemTemplate template = ItemTemplateFactory.getInstance().getTemplateOrNull(def.templateId);
            if (template == null) {
                logger.warning("WurmTweaker: item template not found for templateId="
                        + def.templateId + " — skipping");
            }
            return template;
        }
        int id;
        try {
            id = new ItemIdParser().parse(def.templateName);
        } catch (IllegalArgumentException e) {
            logger.warning("WurmTweaker: unknown templateName '" + def.templateName
                    + "': " + e.getMessage() + " — skipping");
            return null;
        }
        ItemTemplate template = ItemTemplateFactory.getInstance().getTemplateOrNull(id);
        if (template == null) {
            logger.warning("WurmTweaker: item template not found for templateName='"
                    + def.templateName + "' (id=" + id + ") — skipping");
        }
        return template;
    }

    // --- Shared optional field application (Groups C + D) ---

    void applyOptionalFields(ItemTemplate template, ItemDefinition def) {
        // Group C — public setters
        if (def.containerSize != null) {
            ItemDefinition.ContainerSize cs = def.containerSize;
            if (cs.x != null && cs.y != null && cs.z != null) {
                template.setContainerSize(cs.x, cs.y, cs.z);
            }
        }
        if (def.maxItemCount  != null) template.setMaxItemCount(def.maxItemCount);
        if (def.maxItemWeight != null) template.setMaxItemWeight(def.maxItemWeight);
        if (def.nutrition != null) {
            ItemDefinition.Nutrition n = def.nutrition;
            if (n.calories != null && n.carbs != null && n.fats != null && n.proteins != null) {
                template.setNutritionValues(n.calories, n.carbs, n.fats, n.proteins);
            }
        }
        if (def.dyeAmountGrams    != null) template.setDyeAmountGrams(def.dyeAmountGrams);
        if (def.secondaryItemName != null) {
            int dyeSecondary = def.dyeSecondaryAmountRequired != null ? def.dyeSecondaryAmountRequired : 0;
            template.setSecondryItem(def.secondaryItemName, dyeSecondary);
        }
        if (def.fragmentAmount != null) template.setFragmentAmount(def.fragmentAmount);

        // Group D — private methods via ReflectionUtil
        if (def.alcoholStrength != null && def.alcoholStrength > 0) callPrivate(template, "setAlcoholStrength", def.alcoholStrength);
        if (def.foodGroup       != null && def.foodGroup       > 0) callPrivate(template, "setFoodGroup",       def.foodGroup);
        if (def.crushsTo        != null && def.crushsTo        > 0) callPrivate(template, "setCrushsTo",        def.crushsTo);
        if (def.pickSeeds       != null && def.pickSeeds       > 0) callPrivate(template, "setPickSeeds",       def.pickSeeds);
        if (def.grows           != null && def.grows           > 0) callPrivate(template, "setGrows",           def.grows);
        if (def.harvestsTo      != null && def.harvestsTo      > 0) callPrivate(template, "setHarvestsTo",      def.harvestsTo);
    }

    private void callPrivate(ItemTemplate template, String methodName, Object value) {
        try {
            ReflectionUtil.callPrivateMethod(template,
                    ReflectionUtil.getMethod(ItemTemplate.class, methodName), value);
        } catch (Exception e) {
            logger.warning("WurmTweaker: could not call " + methodName
                    + " on item templateId=" + template.getTemplateId()
                    + ": " + e.getMessage());
        }
    }

    // --- Creation path ---

    private void createItem(ItemDefinition def, File sourceFile) {
        List<String> missing = new ArrayList<String>();
        if (def.name          == null) missing.add("name");
        if (def.plural        == null) missing.add("plural");
        if (def.size          == null) missing.add("size");
        if (def.imageNumber   == null) missing.add("imageNumber");
        if (def.behaviourType == null) missing.add("behaviourType");
        if (def.combatDamage  == null) missing.add("combatDamage");
        if (def.decayTime     == null) missing.add("decayTime");
        if (def.primarySkill  == null) missing.add("primarySkill");
        if (def.modelName     == null) missing.add("modelName");
        if (def.difficulty    == null) missing.add("difficulty");
        if (def.weight        == null) missing.add("weight");
        if (def.material      == null) missing.add("material");
        if (def.value         == null) missing.add("value");
        if (def.isPurchased   == null) missing.add("isPurchased");
        if (def.itemTypes     == null) missing.add("itemTypes");
        if (!missing.isEmpty()) {
            logger.warning("WurmTweaker: cannot create item identifier='" + def.identifier
                    + "' — missing required fields: " + missing + " — skipping");
            return;
        }

        ItemDefinition.Descriptions desc = def.descriptions;
        String superb   = desc != null && desc.superb   != null ? desc.superb   : "superb";
        String normal   = desc != null && desc.normal   != null ? desc.normal   : "good";
        String bad      = desc != null && desc.bad      != null ? desc.bad      : "ok";
        String rotten   = desc != null && desc.rotten   != null ? desc.rotten   : "poor";
        String longDesc = desc != null && desc.longDesc != null ? desc.longDesc : "";

        ItemDefinition.Dimensions dim = def.dimensions;
        int dimX = dim != null && dim.x != null ? dim.x : 0;
        int dimY = dim != null && dim.y != null ? dim.y : 0;
        int dimZ = dim != null && dim.z != null ? dim.z : 0;

        byte[] bodySpaces = def.bodySpaces != null ? toByteArray(def.bodySpaces) : new byte[0];

        try {
            ItemTemplate template = new ItemTemplateBuilder(def.identifier)
                    .name(def.name, def.plural, longDesc)
                    .size(def.size)
                    .descriptions(superb, normal, bad, rotten)
                    .itemTypes(toShortArray(def.itemTypes))
                    .imageNumber((short)(int) def.imageNumber)
                    .behaviourType((short)(int) def.behaviourType)
                    .combatDamage(def.combatDamage)
                    .decayTime(def.decayTime)
                    .dimensions(dimX, dimY, dimZ)
                    .primarySkill(def.primarySkill)
                    .bodySpaces(bodySpaces)
                    .modelName(def.modelName)
                    .difficulty(def.difficulty)
                    .weightGrams(def.weight)
                    .material((byte)(int) def.material)
                    .value(def.value)
                    .isTraded(def.isPurchased)
                    .build();

            applyOptionalFields(template, def);

            int assignedId = template.getTemplateId();
            logger.info("WurmTweaker: created item identifier='" + def.identifier
                    + "' templateId=" + assignedId + " (" + template.getName() + ")");

            if (sourceFile != null
                    && (def.assignedTemplateId == null || def.assignedTemplateId != assignedId)) {
                writeBackAssignedId(def.identifier, assignedId, sourceFile);
            }
        } catch (IOException e) {
            logger.warning("WurmTweaker: failed to create item identifier='" + def.identifier
                    + "': " + e.getMessage());
        }
    }

    private void writeBackAssignedId(String identifier, int assignedId, File sourceFile) {
        try {
            JsonElement root;
            try (FileReader reader = new FileReader(sourceFile)) {
                root = JsonParser.parseReader(reader);
            }

            boolean updated = false;
            if (root.isJsonArray()) {
                for (JsonElement element : root.getAsJsonArray()) {
                    if (isMatchingIdentifier(element, identifier)) {
                        element.getAsJsonObject().addProperty("assignedTemplateId", assignedId);
                        updated = true;
                    }
                }
            } else if (isMatchingIdentifier(root, identifier)) {
                root.getAsJsonObject().addProperty("assignedTemplateId", assignedId);
                updated = true;
            }

            if (!updated) {
                logger.warning("WurmTweaker: could not locate identifier='" + identifier
                        + "' in " + sourceFile.getName() + " for ID write-back");
                return;
            }

            try (FileWriter writer = new FileWriter(sourceFile)) {
                writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(root));
            }
            logger.info("WurmTweaker: wrote assignedTemplateId=" + assignedId
                    + " back to " + sourceFile.getName());
        } catch (IOException e) {
            logger.warning("WurmTweaker: could not write assigned ID back to "
                    + sourceFile.getName() + ": " + e.getMessage());
        }
    }

    private static boolean isMatchingIdentifier(JsonElement element, String identifier) {
        if (!element.isJsonObject()) return false;
        JsonElement idEl = element.getAsJsonObject().get("identifier");
        return idEl != null && identifier.equals(idEl.getAsString());
    }

    // --- Reflection helpers ---

    static void setFinalField(Object target, String fieldName, Object value) {
        try {
            Field f = findField(target.getClass(), fieldName);
            f.setAccessible(true);
            Field modifiers = Field.class.getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(f, f.getModifiers() & ~Modifier.FINAL);
            f.set(target, value);
        } catch (Exception e) {
            logger.warning("WurmTweaker: could not set field '" + fieldName + "': " + e.getMessage());
        }
    }

    private static Field findField(Class<?> clazz, String name) throws NoSuchFieldException {
        Class<?> c = clazz;
        while (c != null) {
            try {
                return c.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                c = c.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name + " not found in " + clazz.getName() + " hierarchy");
    }

    // --- Type conversion helpers ---

    static short[] toShortArray(int[] arr) {
        short[] result = new short[arr.length];
        for (int i = 0; i < arr.length; i++) {
            result[i] = (short) arr[i];
        }
        return result;
    }

    static byte[] toByteArray(int[] arr) {
        byte[] result = new byte[arr.length];
        for (int i = 0; i < arr.length; i++) {
            result[i] = (byte) arr[i];
        }
        return result;
    }
}
