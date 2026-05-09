package org.gotti.wurmtweaker.creatures;

import com.wurmonline.server.combat.ArmourTemplate;
import com.wurmonline.server.creatures.AttackAction;
import com.wurmonline.server.creatures.AttackValues;
import com.wurmonline.server.creatures.CreatureTemplate;
import com.wurmonline.server.creatures.CreatureTemplateFactory;
import com.wurmonline.server.creatures.NoSuchCreatureTemplateException;
import com.wurmonline.server.skills.Skills;
import org.gotti.wurmtweaker.json.ContentHandler;
import org.gotti.wurmunlimited.modsupport.CreatureTemplateBuilder;
import org.gotti.wurmunlimited.modsupport.creatures.ModCreature;
import org.gotti.wurmunlimited.modsupport.creatures.ModCreatures;

import java.lang.reflect.Constructor;
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

    private static final Map<String, ArmourTemplate.ArmourType> ARMOUR_TYPES;
    static {
        ARMOUR_TYPES = new LinkedHashMap<String, ArmourTemplate.ArmourType>();
        ARMOUR_TYPES.put("ARMOUR_TYPE_NONE",           ArmourTemplate.ARMOUR_TYPE_NONE);
        ARMOUR_TYPES.put("ARMOUR_TYPE_LEATHER",        ArmourTemplate.ARMOUR_TYPE_LEATHER);
        ARMOUR_TYPES.put("ARMOUR_TYPE_STUDDED",        ArmourTemplate.ARMOUR_TYPE_STUDDED);
        ARMOUR_TYPES.put("ARMOUR_TYPE_CHAIN",          ArmourTemplate.ARMOUR_TYPE_CHAIN);
        ARMOUR_TYPES.put("ARMOUR_TYPE_PLATE",          ArmourTemplate.ARMOUR_TYPE_PLATE);
        ARMOUR_TYPES.put("ARMOUR_TYPE_RING",           ArmourTemplate.ARMOUR_TYPE_RING);
        ARMOUR_TYPES.put("ARMOUR_TYPE_CLOTH",          ArmourTemplate.ARMOUR_TYPE_CLOTH);
        ARMOUR_TYPES.put("ARMOUR_TYPE_SCALE",          ArmourTemplate.ARMOUR_TYPE_SCALE);
        ARMOUR_TYPES.put("ARMOUR_TYPE_SPLINT",         ArmourTemplate.ARMOUR_TYPE_SPLINT);
        ARMOUR_TYPES.put("ARMOUR_TYPE_LEATHER_DRAGON", ArmourTemplate.ARMOUR_TYPE_LEATHER_DRAGON);
        ARMOUR_TYPES.put("ARMOUR_TYPE_SCALE_DRAGON",   ArmourTemplate.ARMOUR_TYPE_SCALE_DRAGON);
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

        CreatureDefinition.Movement     mov = def.movement;
        CreatureDefinition.Combat       cbt = def.combat;
        CreatureDefinition.Armor        arm = cbt != null ? cbt.armor : null;
        CreatureDefinition.LegacyAttacks leg = cbt != null && cbt.attacks != null ? cbt.attacks.legacy : null;

        CreatureTemplateBuilder builder = new CreatureTemplateBuilder(def.id)
                .name(def.name)
                .plural(def.plural != null ? def.plural : def.name + "s")
                .description(def.longDesc != null ? def.longDesc : "A " + def.name + ".")
                .modelName(def.modelName)
                .types(def.types != null ? def.types : new int[0])
                .vision(def.vision != null ? def.vision : (short) 50)
                .sex(def.sex != null ? def.sex : (byte) 0)
                .bodyType(def.bodyType != null ? def.bodyType : (byte) 0)
                .dimension(high, length, wide)
                .naturalArmour(arm != null && arm.naturalArmour != null ? arm.naturalArmour : 1.0f)
                .speed(mov != null && mov.speed != null ? mov.speed : 5.0f)
                .aggressive(cbt != null && cbt.aggressivity != null ? cbt.aggressivity : 0)
                .damages(
                        leg != null && leg.hand   != null ? leg.hand   : 0f,
                        leg != null && leg.kick   != null ? leg.kick   : 0f,
                        leg != null && leg.bite   != null ? leg.bite   : 0f,
                        leg != null && leg.head   != null ? leg.head   : 0f,
                        leg != null && leg.breath != null ? leg.breath : 0f)
                .moveRate(mov != null && mov.moveRate != null ? mov.moveRate : 60)
                .maxHuntDist(cbt != null && cbt.maxHuntDistance != null ? cbt.maxHuntDistance : 20)
                .meatMaterial(def.meatMaterial != null ? def.meatMaterial : (byte) 0)
                .deathSounds(sound(def.sounds, true,  true),
                             sound(def.sounds, true,  false))
                .hitSounds(  sound(def.sounds, false, true),
                             sound(def.sounds, false, false))
                .baseCombatRating(cbt != null && cbt.baseCombatRating != null ? cbt.baseCombatRating : 1.0f)
                .defaultSkills();

        if (def.drops != null) {
            builder.itemsButchered(def.drops);
        }

        if (def.skills != null) {
            for (Map.Entry<String, Double> entry : def.skills.entrySet()) {
                builder.skill(Integer.parseInt(entry.getKey()), entry.getValue().floatValue());
            }
        }

        return builder;
    }

    private void applySetters(CreatureTemplate template, CreatureDefinition def) {
        CreatureDefinition.Combat        cbt = def.combat;
        CreatureDefinition.Armor         arm = cbt != null ? cbt.armor : null;
        CreatureDefinition.Attacks       atk = cbt != null ? cbt.attacks : null;
        CreatureDefinition.LegacyAttacks leg = atk != null ? atk.legacy : null;

        // Armour type — strip optional "ArmourTemplate." prefix, then look up in static map
        if (arm != null && arm.armourType != null) {
            String key = arm.armourType.replaceFirst("^ArmourTemplate\\.", "");
            ArmourTemplate.ArmourType armourType = ARMOUR_TYPES.get(key);
            if (armourType != null) {
                template.setArmourType(armourType);
            } else {
                logger.warning("WurmTweaker: unknown armourType '" + arm.armourType
                        + "' for creature id=" + def.id + ". Valid values: " + ARMOUR_TYPES.keySet());
            }
        }

        // Combat rating and alignment
        if (cbt != null && cbt.baseCombatRating  != null) template.setBaseCombatRating(cbt.baseCombatRating);
        if (cbt != null && cbt.bonusCombatRating != null) template.setBonusCombatRating(cbt.bonusCombatRating);
        if (cbt != null && cbt.alignment         != null) template.setAlignment(cbt.alignment);
        if (cbt != null && cbt.maxGroupAttackSize != null) template.setMaxGroupAttackSize(cbt.maxGroupAttackSize);
        if (cbt != null && cbt.damageType        != null) template.setCombatDamageType(cbt.damageType.byteValue());

        // Age and spawn control
        if (def.maxAge               != null) template.setMaxAge(def.maxAge);
        if (def.maxPercentOfCreatures != null) template.setMaxPercentOfCreatures(def.maxPercentOfCreatures);

        // Den
        if (def.denName     != null) template.setDenName(def.denName);
        if (def.denMaterial != null) template.setDenMaterial(def.denMaterial.byteValue());

        // Behaviour flags
        if (def.subterranean  != null) template.setSubterranean(def.subterranean);
        if (def.tutorial      != null) template.setTutorial(def.tutorial);
        if (def.noSkillGain   != null) template.setNoSkillgain(def.noSkillGain);
        if (def.noServerSounds != null) template.setNoServerSounds(def.noServerSounds);
        if (def.keepSex       != null) template.setKeepSex(def.keepSex);

        // Corpse name
        if (def.corpseName != null) template.setCorpseName(def.corpseName);

        // Breeding
        if (def.eggLayer           != null) template.setEggLayer(def.eggLayer);
        if (def.eggTemplateId      != null) template.setEggTemplateId(def.eggTemplateId);
        if (def.childTemplateId    != null) template.setChildTemplateId(def.childTemplateId);
        if (def.mateTemplateId     != null) template.setMateTemplateId(def.mateTemplateId);
        if (def.adultFemaleTemplateId != null) template.setAdultFemaleTemplateId(def.adultFemaleTemplateId);
        if (def.adultMaleTemplateId   != null) template.setAdultMaleTemplateId(def.adultMaleTemplateId);
        if (def.leaderTemplateId   != null) template.setLeaderTemplateId(def.leaderTemplateId);

        // Bounds
        if (def.boundsValues != null && def.boundsValues.length >= 4) {
            template.setBoundsValues(def.boundsValues[0], def.boundsValues[1],
                                     def.boundsValues[2], def.boundsValues[3]);
        }

        // Cosmetics
        if (def.colourNames != null) template.setColourNames(def.colourNames);
        if (def.glowing     != null) template.setGlowing(def.glowing);
        if (def.paintMode   != null) template.setPaintMode(def.paintMode);
        if (def.onFire      != null) template.setOnFire(def.onFire);
        if (def.fireRadius  != null) template.setFireRadius(def.fireRadius.byteValue());

        // Color
        if (def.color != null) {
            if (def.color.red   != null) template.setColorRed(def.color.red);
            if (def.color.green != null) template.setColorGreen(def.color.green);
            if (def.color.blue  != null) template.setColorBlue(def.color.blue);
        }

        // Direct field assignment — offZ is public; hasHands/isHorse are package-private
        if (def.hasHands != null) setField(template, "hasHands", def.hasHands);
        if (def.isHorse  != null) setField(template, "isHorse",  def.isHorse);
        if (def.offZ     != null) template.offZ = def.offZ;

        // Combat moves — prefer combat.combatMoves over top-level
        int[] moves = cbt != null && cbt.combatMoves != null ? cbt.combatMoves : def.combatMoves;
        if (moves != null) template.setCombatMoves(moves);

        // Attack strings
        if (leg != null) {
            if (leg.handString   != null) template.setHandDamString(leg.handString);
            if (leg.kickString   != null) template.setKickDamString(leg.kickString);
            if (leg.headString   != null) template.setHeadbuttDamString(leg.headString);
            if (leg.breathString != null) template.setBreathDamString(leg.breathString);
        }

        // New attack system
        if (atk != null && Boolean.TRUE.equals(atk.useNewSystem)) {
            template.setUsesNewAttacks(true);
            if (atk.primary != null) {
                for (CreatureDefinition.AttackEntry e : atk.primary) {
                    AttackAction action = buildAttackAction(e, def.id);
                    if (action != null) template.addPrimaryAttack(action);
                }
            }
            if (atk.secondary != null) {
                for (CreatureDefinition.AttackEntry e : atk.secondary) {
                    AttackAction action = buildAttackAction(e, def.id);
                    if (action != null) template.addSecondaryAttack(action);
                }
            }
        }

        // Resistance / vulnerability — public fields, direct assignment
        if (def.physicalResistance    != null) template.physicalResistance    = def.physicalResistance;
        if (def.physicalVulnerability != null) template.physicalVulnerability = def.physicalVulnerability;
        if (def.acidResistance        != null) template.acidResistance        = def.acidResistance;
        if (def.acidVulnerability     != null) template.acidVulnerability     = def.acidVulnerability;
        if (def.fireResistance        != null) template.fireResistance        = def.fireResistance;
        if (def.fireVulnerability     != null) template.fireVulnerability     = def.fireVulnerability;
        if (def.coldResistance        != null) template.coldResistance        = def.coldResistance;
        if (def.coldVulnerability     != null) template.coldVulnerability     = def.coldVulnerability;
        if (def.diseaseResistance     != null) template.diseaseResistance     = def.diseaseResistance;
        if (def.diseaseVulnerability  != null) template.diseaseVulnerability  = def.diseaseVulnerability;
        if (def.pierceResistance      != null) template.pierceResistance      = def.pierceResistance;
        if (def.pierceVulnerability   != null) template.pierceVulnerability   = def.pierceVulnerability;
        if (def.slashResistance       != null) template.slashResistance       = def.slashResistance;
        if (def.slashVulnerability    != null) template.slashVulnerability    = def.slashVulnerability;
        if (def.crushResistance       != null) template.crushResistance       = def.crushResistance;
        if (def.crushVulnerability    != null) template.crushVulnerability    = def.crushVulnerability;
        if (def.biteResistance        != null) template.biteResistance        = def.biteResistance;
        if (def.biteVulnerability     != null) template.biteVulnerability     = def.biteVulnerability;
        if (def.poisonResistance      != null) template.poisonResistance      = def.poisonResistance;
        if (def.poisonVulnerability   != null) template.poisonVulnerability   = def.poisonVulnerability;
        if (def.waterResistance       != null) template.waterResistance       = def.waterResistance;
        if (def.waterVulnerability    != null) template.waterVulnerability    = def.waterVulnerability;
        if (def.internalResistance    != null) template.internalResistance    = def.internalResistance;
        if (def.internalVulnerability != null) template.internalVulnerability = def.internalVulnerability;

        // Drops — butcheredItems is private final, override via reflection
        if (def.drops != null) setField(template, "butcheredItems", def.drops);

        // Skills
        if (def.skills != null) {
            try {
                Skills skills = template.getSkills();
                for (Map.Entry<String, Double> entry : def.skills.entrySet()) {
                    skills.learn(Integer.parseInt(entry.getKey()), entry.getValue().floatValue());
                }
            } catch (Exception e) {
                logger.warning("WurmTweaker: could not apply skills for creature id=" + def.id
                        + ": " + e.getMessage());
            }
        }
    }

    private void applyReflectedFinalFields(CreatureTemplate template, CreatureDefinition def) {
        // WHY: these fields are private final in CreatureTemplate with no public setters;
        // reflection is the only way to override values set at construction time for vanilla templates.
        CreatureDefinition.Movement     mov = def.movement;
        CreatureDefinition.Combat       cbt = def.combat;
        CreatureDefinition.Armor        arm = cbt != null ? cbt.armor : null;
        CreatureDefinition.LegacyAttacks leg = cbt != null && cbt.attacks != null ? cbt.attacks.legacy : null;

        if (mov != null && mov.speed    != null) setField(template, "speed",          mov.speed);
        if (mov != null && mov.moveRate != null) setField(template, "moveRate",        mov.moveRate);
        if (cbt != null && cbt.aggressivity   != null) setField(template, "aggressivity",  cbt.aggressivity);
        if (arm != null && arm.naturalArmour  != null) setField(template, "naturalArmour", arm.naturalArmour);
        if (leg != null && leg.hand   != null) setField(template, "handDamage",      leg.hand);
        if (leg != null && leg.kick   != null) setField(template, "kickDamage",      leg.kick);
        if (leg != null && leg.bite   != null) setField(template, "biteDamage",      leg.bite);
        if (leg != null && leg.head   != null) setField(template, "headButtDamage",  leg.head);
        if (leg != null && leg.breath != null) setField(template, "breathDamage",    leg.breath);

        if (def.sounds != null) {
            CreatureDefinition.Sounds s = def.sounds;
            if (s.deathMale   != null) setField(template, "deathSoundMale",   s.deathMale);
            if (s.deathFemale != null) setField(template, "deathSoundFemale", s.deathFemale);
            if (s.hitMale     != null) setField(template, "hitSoundMale",     s.hitMale);
            if (s.hitFemale   != null) setField(template, "hitSoundFemale",   s.hitFemale);
        }
    }

    @SuppressWarnings("unchecked")
    private AttackAction buildAttackAction(CreatureDefinition.AttackEntry e, int creatureId) {
        if (e.identifier == null || e.values == null) {
            logger.warning("WurmTweaker: attack entry for creature id=" + creatureId
                    + " missing identifier or values — skipping");
            return null;
        }
        try {
            String key = e.identifier.replaceFirst("^AttackIdentifier\\.", "");
            Class<?> identifierCls = Class.forName("com.wurmonline.server.creatures.AttackIdentifier");
            Object identifier = Enum.valueOf((Class<Enum>) identifierCls, key);

            CreatureDefinition.AttackValues v = e.values;
            AttackValues attackValues = new AttackValues(
                    v.baseDamage          != null ? v.baseDamage.floatValue()          : 0f,
                    v.criticalChance      != null ? v.criticalChance.floatValue()      : 0f,
                    v.baseSpeed           != null ? v.baseSpeed.floatValue()           : 1f,
                    v.attackReach         != null ? v.attackReach                      : 1,
                    v.weightGroup         != null ? v.weightGroup                      : 0,
                    v.damageType          != null ? v.damageType.byteValue()           : (byte) 0,
                    v.usesWeapon          != null ? v.usesWeapon                       : false,
                    v.rounds              != null ? v.rounds                           : 1,
                    v.waitUntilNextAttack != null ? v.waitUntilNextAttack.floatValue() : 0f
            );

            Constructor<?> ctor = AttackAction.class.getDeclaredConstructor(
                    String.class, identifierCls, AttackValues.class);
            return (AttackAction) ctor.newInstance(e.name, identifier, attackValues);
        } catch (Exception ex) {
            logger.warning("WurmTweaker: could not build AttackAction for creature id=" + creatureId
                    + ": " + ex.getMessage());
            return null;
        }
    }

    // --- Reflection helpers ---

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field f = findField(target.getClass(), fieldName);
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
            if (isMale) return sounds.deathMale != null ? sounds.deathMale : "";
            String fallback = sounds.deathMale != null ? sounds.deathMale : "";
            return sounds.deathFemale != null ? sounds.deathFemale : fallback;
        } else {
            if (isMale) return sounds.hitMale != null ? sounds.hitMale : "";
            String fallback = sounds.hitMale != null ? sounds.hitMale : "";
            return sounds.hitFemale != null ? sounds.hitFemale : fallback;
        }
    }
}
