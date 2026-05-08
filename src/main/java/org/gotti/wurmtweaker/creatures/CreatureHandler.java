package org.gotti.wurmtweaker.creatures;

import com.wurmonline.server.combat.ArmourTemplate;
import com.wurmonline.server.creatures.CreatureTemplate;
import com.wurmonline.server.creatures.CreatureTemplateFactory;
import com.wurmonline.server.creatures.NoSuchCreatureTemplateException;
import org.gotti.wurmtweaker.json.ContentHandler;
import org.gotti.wurmunlimited.modsupport.CreatureTemplateBuilder;
import org.gotti.wurmunlimited.modsupport.creatures.ModCreature;
import org.gotti.wurmunlimited.modsupport.creatures.ModCreatures;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class CreatureHandler implements ContentHandler<CreatureDefinition> {

    private static final Logger logger = Logger.getLogger(CreatureHandler.class.getName());

    // FISH_CID = 119 is the last vanilla creature template ID
    private static final int MAX_VANILLA_ID = 119;

    // Maps JSON behavior tag → private field name on CreatureTemplate
    private static final Map<String, String> BEHAVIOR_FIELDS = new LinkedHashMap<>();

    static {
        BEHAVIOR_FIELDS.put("ANIMAL",        "animal");
        BEHAVIOR_FIELDS.put("MONSTER",       "monster");
        BEHAVIOR_FIELDS.put("HUNTER",        "hunter");
        BEHAVIOR_FIELDS.put("LEADABLE",      "leadable");
        BEHAVIOR_FIELDS.put("GRAZER",        "grazer");
        BEHAVIOR_FIELDS.put("HERBIVORE",     "herbivore");
        BEHAVIOR_FIELDS.put("CARNIVORE",     "carnivore");
        BEHAVIOR_FIELDS.put("OMNIVORE",      "omnivore");
        BEHAVIOR_FIELDS.put("HERD",          "herd");
        BEHAVIOR_FIELDS.put("SWIMMING",      "swimming");
        BEHAVIOR_FIELDS.put("FLEEING",       "fleeing");
        BEHAVIOR_FIELDS.put("DOMESTIC",      "domestic");
        BEHAVIOR_FIELDS.put("MILKABLE",      "milkable");
        BEHAVIOR_FIELDS.put("WOOL_PRODUCER", "woolProducer");
        BEHAVIOR_FIELDS.put("CLIMBER",       "climber");
        BEHAVIOR_FIELDS.put("INVULNERABLE",  "invulnerable");
        BEHAVIOR_FIELDS.put("MOVE_RANDOM",   "moveRandom");
        BEHAVIOR_FIELDS.put("MOVE_LOCAL",    "moveLocal");
        BEHAVIOR_FIELDS.put("MOVE_GLOBAL",   "moveGlobal");
    }

    private final List<CreatureDefinition> pendingDefs = new ArrayList<>();

    @Override
    public String getTypeName() {
        return "creature";
    }

    @Override
    public Class<CreatureDefinition> getDefinitionClass() {
        return CreatureDefinition.class;
    }

    @Override
    public void apply(CreatureDefinition def) {
        if (def.id == null) {
            logger.warning("WurmTweaker: creature definition missing required field 'id' — skipping");
            return;
        }

        if (def.id > MAX_VANILLA_ID) {
            if (def.name == null || def.modelName == null) {
                logger.warning("WurmTweaker: new creature id=" + def.id
                        + " requires 'name' and 'modelName' — skipping");
                return;
            }
            ModCreatures.addCreature(new ModCreature() {
                @Override
                public CreatureTemplateBuilder createCreateTemplateBuilder() {
                    return buildTemplate(def);
                }
            });
        }

        pendingDefs.add(def);
    }

    /** Called from WurmTweaker.onServerStarted() once all templates exist. */
    public void applyPostInit() {
        for (CreatureDefinition def : pendingDefs) {
            try {
                CreatureTemplate template = CreatureTemplateFactory.getInstance().getTemplate(def.id);
                applySetters(template, def);
                if (def.id <= MAX_VANILLA_ID) {
                    applyReflectedFinalFields(template, def);
                }
                logger.info("WurmTweaker: applied creature id=" + def.id
                        + " (" + template.getName() + ")");
            } catch (NoSuchCreatureTemplateException e) {
                logger.warning("WurmTweaker: creature template not found for id=" + def.id);
            }
        }
    }

    private CreatureTemplateBuilder buildTemplate(CreatureDefinition def) {
        short high   = sizeField(def.sizeInCentimeters, true,  (short) 100);
        short length = sizeField(def.sizeInCentimeters, false, (short) 100);
        short wide   = def.sizeInCentimeters != null && def.sizeInCentimeters.wide != null
                       ? def.sizeInCentimeters.wide : (short) 100;

        return new CreatureTemplateBuilder(def.id)
                .name(def.name)
                .plural(def.plural != null ? def.plural : def.name + "s")
                .description(def.longDesc != null ? def.longDesc : "A " + def.name + ".")
                .modelName(def.modelName)
                .vision(def.vision != null ? def.vision : (short) 50)
                .sex(def.sex != null ? def.sex : (byte) 0)
                .bodyType(def.bodyType != null ? def.bodyType : (byte) 0)
                .dimension(high, length, wide)
                .naturalArmour(def.naturalArmour != null ? def.naturalArmour : 1.0f)
                .speed(def.speed != null ? def.speed : 5.0f)
                .aggressive(def.aggressivity != null ? def.aggressivity : 0)
                .damages(
                        def.handDamage   != null ? def.handDamage   : 0f,
                        def.kickDamage   != null ? def.kickDamage   : 0f,
                        def.biteDamage   != null ? def.biteDamage   : 0f,
                        def.headDamage   != null ? def.headDamage   : 0f,
                        def.breathDamage != null ? def.breathDamage : 0f)
                .moveRate(def.moveRate != null ? def.moveRate : 60)
                .maxHuntDist(def.maxHuntDistance != null ? def.maxHuntDistance : 20)
                .meatMaterial(def.meatMaterial != null ? def.meatMaterial : (byte) 0)
                .deathSounds(sound(def.sounds, true,  true),
                             sound(def.sounds, true,  false))
                .hitSounds(  sound(def.sounds, false, true),
                             sound(def.sounds, false, false))
                .baseCombatRating(def.baseCombatRating != null ? def.baseCombatRating : 1.0f)
                .defaultSkills();
    }

    private void applySetters(CreatureTemplate template, CreatureDefinition def) {
        if (def.armourType != null) {
            try {
                // WHY: ArmourType is not a standard Java enum, so valueOf() is unavailable;
                // reflective field lookup works for both enum-style and static-constant classes.
                Field f = ArmourTemplate.ArmourType.class.getDeclaredField(def.armourType.toUpperCase());
                template.setArmourType((ArmourTemplate.ArmourType) f.get(null));
            } catch (Exception e) {
                logger.warning("WurmTweaker: unknown armourType '" + def.armourType
                        + "' for creature id=" + def.id);
            }
        }
        if (def.baseCombatRating != null)  template.setBaseCombatRating(def.baseCombatRating);
        if (def.bonusCombatRating != null) template.setBonusCombatRating(def.bonusCombatRating);
        if (def.maxAge != null)            template.setMaxAge(def.maxAge);
        if (def.alignment != null)         template.setAlignment(def.alignment);

        if (def.behaviors != null) {
            for (String tag : def.behaviors) {
                String fieldName = BEHAVIOR_FIELDS.get(tag.toUpperCase());
                if (fieldName != null) {
                    setBoolean(template, fieldName, true);
                } else {
                    logger.warning("WurmTweaker: unknown behavior '" + tag
                            + "' for creature id=" + def.id);
                }
            }
        }
    }

    private void applyReflectedFinalFields(CreatureTemplate template, CreatureDefinition def) {
        // WHY: these fields are private final in CreatureTemplate with no public setters;
        // reflection is the only way to override values set at construction time for vanilla templates.
        if (def.speed != null)        setField(template, "speed",           def.speed);
        if (def.aggressivity != null) setField(template, "aggressivity",    def.aggressivity);
        if (def.naturalArmour != null)setField(template, "naturalArmour",   def.naturalArmour);
        if (def.handDamage != null)   setField(template, "handDamage",      def.handDamage);
        if (def.kickDamage != null)   setField(template, "kickDamage",      def.kickDamage);
        if (def.biteDamage != null)   setField(template, "biteDamage",      def.biteDamage);
        if (def.headDamage != null)   setField(template, "headButtDamage",  def.headDamage);
        if (def.breathDamage != null) setField(template, "breathDamage",    def.breathDamage);
        if (def.moveRate != null)     setField(template, "moveRate",        def.moveRate);
        if (def.sounds != null) {
            CreatureDefinition.Sounds s = def.sounds;
            if (s.deathMale   != null) setField(template, "deathSoundMale",   s.deathMale);
            if (s.deathFemale != null) setField(template, "deathSoundFemale", s.deathFemale);
            if (s.hitMale     != null) setField(template, "hitSoundMale",     s.hitMale);
            if (s.hitFemale   != null) setField(template, "hitSoundFemale",   s.hitFemale);
        }
    }

    // --- Reflection helpers ---

    private static void setBoolean(Object target, String fieldName, boolean value) {
        try {
            Field f = findField(target.getClass(), fieldName);
            f.setAccessible(true);
            f.setBoolean(target, value);
        } catch (Exception e) {
            logger.warning("WurmTweaker: could not set boolean field '" + fieldName
                    + "': " + e.getMessage());
        }
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field f = findField(target.getClass(), fieldName);
            // WHY: removing final modifier before assignment — same pattern used by SkillHandler
            // for SkillList.skillArray; required to overwrite private final fields at runtime.
            f.setAccessible(true);
            Field modifiers = Field.class.getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(f, f.getModifiers() & ~Modifier.FINAL);
            f.set(target, value);
        } catch (Exception e) {
            logger.warning("WurmTweaker: could not set field '" + fieldName
                    + "': " + e.getMessage());
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

    // --- Size / sound helpers ---

    private static short sizeField(CreatureDefinition.SizeInCentimeters size,
                                   boolean high, short defaultVal) {
        if (size == null) return defaultVal;
        Short v = high ? size.high : size.length;
        return v != null ? v : defaultVal;
    }

    private static String sound(CreatureDefinition.Sounds sounds, boolean isDeath, boolean isMale) {
        if (sounds == null) return "";
        if (isDeath) {
            if (isMale) return sounds.deathMale   != null ? sounds.deathMale   : "";
            String fallback = sounds.deathMale != null ? sounds.deathMale : "";
            return sounds.deathFemale != null ? sounds.deathFemale : fallback;
        } else {
            if (isMale) return sounds.hitMale     != null ? sounds.hitMale     : "";
            String fallback = sounds.hitMale != null ? sounds.hitMale : "";
            return sounds.hitFemale   != null ? sounds.hitFemale   : fallback;
        }
    }
}
