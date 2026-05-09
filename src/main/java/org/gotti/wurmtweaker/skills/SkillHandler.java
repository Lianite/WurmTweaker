package org.gotti.wurmtweaker.skills;

import com.wurmonline.server.skills.SkillList;
import com.wurmonline.server.skills.SkillSystem;
import com.wurmonline.server.skills.SkillTemplate;
import org.gotti.wurmtweaker.json.ContentHandler;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class SkillHandler implements ContentHandler<SkillDefinition> {

    private static final Logger logger = Logger.getLogger(SkillHandler.class.getName());

    // SkillTemplate exposes no setters for these fields, and no modsupport SkillTemplateBuilder
    // exists. Reflection is the only path for all fields below.
    private static final Field TYPE_FIELD;
    private static final Field DEPENDENCIES_FIELD;
    private static final Field DECAY_TIME_FIELD;
    private static final Field NAME_FIELD;
    private static final Field FIGHT_SKILL_FIELD;
    private static final Field THIEVERY_SKILL_FIELD;
    private static final Field IGNORES_ENEMIES_FIELD;
    private static final Field TICK_TIME_FIELD;

    // New skill creation: SkillTemplate constructors are package-private; addSkillTemplate is private static.
    // SkillList.skillArray is static final — un-finaled at init time so we can append to it.
    private static final Constructor<SkillTemplate> CTOR_BASIC;
    private static final Constructor<SkillTemplate> CTOR_FIGHT;
    private static final Constructor<SkillTemplate> CTOR_THIEVERY;
    private static final Method ADD_SKILL_TEMPLATE_METHOD;
    private static final Field SKILL_ARRAY_FIELD;

    static {
        try {
            TYPE_FIELD = SkillTemplate.class.getDeclaredField("type");
            TYPE_FIELD.setAccessible(true);
            DEPENDENCIES_FIELD = SkillTemplate.class.getDeclaredField("dependencies");
            DEPENDENCIES_FIELD.setAccessible(true);
            DECAY_TIME_FIELD = SkillTemplate.class.getDeclaredField("decayTime");
            DECAY_TIME_FIELD.setAccessible(true);
            NAME_FIELD = SkillTemplate.class.getDeclaredField("name");
            NAME_FIELD.setAccessible(true);
            FIGHT_SKILL_FIELD = SkillTemplate.class.getDeclaredField("fightSkill");
            FIGHT_SKILL_FIELD.setAccessible(true);
            THIEVERY_SKILL_FIELD = SkillTemplate.class.getDeclaredField("thieverySkill");
            THIEVERY_SKILL_FIELD.setAccessible(true);
            IGNORES_ENEMIES_FIELD = SkillTemplate.class.getDeclaredField("ignoresEnemies");
            IGNORES_ENEMIES_FIELD.setAccessible(true);
            TICK_TIME_FIELD = SkillTemplate.class.getDeclaredField("tickTime");
            TICK_TIME_FIELD.setAccessible(true);

            CTOR_BASIC = SkillTemplate.class.getDeclaredConstructor(
                    int.class, String.class, float.class, int[].class, long.class, short.class);
            CTOR_BASIC.setAccessible(true);
            CTOR_FIGHT = SkillTemplate.class.getDeclaredConstructor(
                    int.class, String.class, float.class, int[].class, long.class, short.class,
                    boolean.class, boolean.class);
            CTOR_FIGHT.setAccessible(true);
            CTOR_THIEVERY = SkillTemplate.class.getDeclaredConstructor(
                    int.class, String.class, float.class, int[].class, long.class, short.class,
                    boolean.class, long.class);
            CTOR_THIEVERY.setAccessible(true);

            ADD_SKILL_TEMPLATE_METHOD = SkillSystem.class.getDeclaredMethod("addSkillTemplate", SkillTemplate.class);
            ADD_SKILL_TEMPLATE_METHOD.setAccessible(true);

            SKILL_ARRAY_FIELD = SkillList.class.getDeclaredField("skillArray");
            SKILL_ARRAY_FIELD.setAccessible(true);
            // Remove the final modifier so we can replace the array reference when adding new skills.
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(SKILL_ARRAY_FIELD, SKILL_ARRAY_FIELD.getModifiers() & ~Modifier.FINAL);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    public String getTypeName() {
        return "skill";
    }

    @Override
    public Class<SkillDefinition> getDefinitionClass() {
        return SkillDefinition.class;
    }

    @Override
    public void apply(SkillDefinition def) {
        if (def.jsonType != null && !def.jsonType.equals("skill")) {
            logger.warning("WurmTweaker: expected json-type 'skill' but got '" + def.jsonType + "' — skipping.");
            return;
        }

        SkillTemplate template = SkillSystem.templates.get(def.id);
        if (template == null) {
            if (def.name == null) {
                logger.warning("WurmTweaker: no skill found with id " + def.id + " and no name provided — skipping.");
                return;
            }
            createNewSkill(def);
            return;
        }

        List<String> applied = new ArrayList<String>();

        if (def.difficulty != null) {
            template.setDifficulty(def.difficulty);
            applied.add("difficulty=" + def.difficulty);
        }

        if (def.slowForPriests != null) {
            template.setIsSlowForPriests(def.slowForPriests);
            applied.add("slowForPriests=" + def.slowForPriests);
        }

        try {
            if (def.type != null) {
                TYPE_FIELD.setShort(template, def.type);
                applied.add("type=" + def.type);
            }
            if (def.dependencies != null) {
                DEPENDENCIES_FIELD.set(template, def.dependencies);
                applied.add("dependencies=" + java.util.Arrays.toString(def.dependencies));
            }
            if (def.decayTime != null) {
                DECAY_TIME_FIELD.setLong(template, def.decayTime);
                applied.add("decayTime=" + def.decayTime);
            }
            if (def.name != null) {
                String oldName = template.getName();
                NAME_FIELD.set(template, def.name);
                SkillSystem.skillNames.put(def.id, def.name);
                SkillSystem.namesToSkill.remove(oldName.toLowerCase());
                SkillSystem.namesToSkill.put(def.name.toLowerCase(), def.id);
                applied.add("name='" + def.name + "'");
            }
            if (def.fightSkill != null) {
                FIGHT_SKILL_FIELD.setBoolean(template, def.fightSkill);
                applied.add("fightSkill=" + def.fightSkill);
            }
            if (def.thieverySkill != null) {
                THIEVERY_SKILL_FIELD.setBoolean(template, def.thieverySkill);
                // mirror constructor behaviour: thievery skills ignore enemies
                if (def.thieverySkill && def.ignoresEnemies == null) {
                    IGNORES_ENEMIES_FIELD.setBoolean(template, true);
                    applied.add("ignoresEnemies=true (auto from thieverySkill)");
                }
                applied.add("thieverySkill=" + def.thieverySkill);
            }
            if (def.ignoresEnemies != null) {
                IGNORES_ENEMIES_FIELD.setBoolean(template, def.ignoresEnemies);
                applied.add("ignoresEnemies=" + def.ignoresEnemies);
            }
            if (def.tickTime != null) {
                TICK_TIME_FIELD.setLong(template, def.tickTime);
                applied.add("tickTime=" + def.tickTime);
            }
        } catch (IllegalAccessException e) {
            logger.warning("WurmTweaker: reflection error applying skill '" + def.id + "': " + e.getMessage());
        }

        if (!applied.isEmpty()) {
            logger.info("WurmTweaker: applied skill override to '" + def.id + "': " + applied);
        }
    }

    private void createNewSkill(SkillDefinition def) {
        float difficulty = def.difficulty != null ? def.difficulty : 1.0f;
        short type = def.type != null ? def.type : SkillList.TYPE_NORMAL;
        int[] dependencies = def.dependencies != null ? def.dependencies : new int[0];
        long decayTime = def.decayTime != null ? def.decayTime : 1209600000L;
        boolean fightSkill = def.fightSkill != null && def.fightSkill;
        boolean thieverySkill = def.thieverySkill != null && def.thieverySkill;
        boolean ignoresEnemies = def.ignoresEnemies != null ? def.ignoresEnemies : thieverySkill;
        long tickTime = def.tickTime != null ? def.tickTime : 0L;

        try {
            SkillTemplate template;
            if (fightSkill) {
                template = CTOR_FIGHT.newInstance(def.id, def.name, difficulty, dependencies, decayTime, type, true, ignoresEnemies);
            } else if (thieverySkill) {
                template = CTOR_THIEVERY.newInstance(def.id, def.name, difficulty, dependencies, decayTime, type, true, tickTime);
            } else {
                template = CTOR_BASIC.newInstance(def.id, def.name, difficulty, dependencies, decayTime, type);
            }

            if (def.slowForPriests != null) {
                template.setIsSlowForPriests(def.slowForPriests);
            }

            ADD_SKILL_TEMPLATE_METHOD.invoke(null, template);

            int[] oldArray = (int[]) SKILL_ARRAY_FIELD.get(null);
            int[] newArray = Arrays.copyOf(oldArray, oldArray.length + 1);
            newArray[oldArray.length] = def.id;
            SKILL_ARRAY_FIELD.set(null, newArray);

            logger.info("WurmTweaker: created new skill '" + def.name + "' (id=" + def.id + ")");
        } catch (ReflectiveOperationException e) {
            logger.warning("WurmTweaker: failed to create skill '" + def.name + "' (id=" + def.id + "): " + e.getMessage());
        }
    }
}
