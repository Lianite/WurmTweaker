package org.gotti.wurmtweaker.creatures;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class CreatureDefinition {

    @SerializedName("json-type") public String jsonType;
    public Integer id;

    public String name;
    public String plural;
    public String longDesc;
    public String modelName;

    public SizeInCentimeters sizeInCentimeters;
    public Sounds sounds;

    public Byte    bodyType;
    public Byte    sex;
    public Short   vision;
    public Byte    meatMaterial;
    public Integer maxAge;

    public int[]              types;
    public Movement           movement;
    public Combat             combat;
    public int[]              drops;
    public int[]              combatMoves;
    public Map<String,Double> skills;

    public Float    maxPercentOfCreatures;
    public Boolean  hasHands;
    public Boolean  isHorse;
    public Boolean  keepSex;
    public Boolean  glowing;
    public Boolean  onFire;
    public Integer  fireRadius;
    public Integer  paintMode;
    public Boolean  noSkillGain;
    public Boolean  noServerSounds;
    public Boolean  subterranean;
    public Boolean  tutorial;
    public Boolean  eggLayer;
    public Integer  eggTemplateId;
    public Integer  childTemplateId;
    public Integer  mateTemplateId;
    public Integer  adultFemaleTemplateId;
    public Integer  adultMaleTemplateId;
    public Integer  leaderTemplateId;
    public String   denName;
    public Integer  denMaterial;
    public String   corpseName;
    public float[]  boundsValues;
    public Float    offZ;
    public String[] colourNames;
    public Integer  colorRed;
    public Integer  colorGreen;
    public Integer  colorBlue;
    public ColorDef color;
    public String   creatureAI;
    public String   note;

    public Float physicalResistance;
    public Float acidVulnerability;
    public Float fireVulnerability;
    public Float fireResistance;
    public Float coldResistance;
    public Float coldVulnerability;
    public Float diseaseResistance;
    public Float diseaseVulnerability;
    public Float pierceResistance;
    public Float pierceVulnerability;
    public Float slashResistance;
    public Float slashVulnerability;
    public Float crushResistance;
    public Float crushVulnerability;
    public Float biteResistance;
    public Float biteVulnerability;
    public Float poisonResistance;
    public Float poisonVulnerability;
    public Float waterResistance;
    public Float waterVulnerability;
    public Float acidResistance;
    public Float internalResistance;
    public Float internalVulnerability;
    public Float physicalVulnerability;

    public static class SizeInCentimeters {
        public Short high;
        @SerializedName("long") public Short length;
        public Short wide;
    }

    public static class Sounds {
        public String deathMale;
        public String deathFemale;
        public String hitMale;
        public String hitFemale;
    }

    public static class Movement {
        public Float   speed;
        public Integer moveRate;
    }

    public static class Combat {
        public Float   baseCombatRating;
        public Float   bonusCombatRating;
        public Integer maxGroupAttackSize;
        public Integer maxHuntDistance;
        public Integer aggressivity;
        public Float   alignment;
        public Integer damageType;
        public Armor   armor;
        public Attacks attacks;
        public int[]   combatMoves;
    }

    public static class Armor {
        public Float  naturalArmour;
        public String armourType;
    }

    public static class Attacks {
        public Boolean           useNewSystem;
        public LegacyAttacks     legacy;
        public List<AttackEntry> primary;
        public List<AttackEntry> secondary;
    }

    public static class LegacyAttacks {
        public Float   hand;
        public String  handString;
        public Float   kick;
        public String  kickString;
        public Integer kickPvp;
        public Float   bite;
        public Float   head;
        public String  headString;
        public Float   breath;
        public String  breathString;
    }

    public static class AttackEntry {
        public String       name;
        public String       identifier;
        public AttackValues values;
    }

    public static class AttackValues {
        public Double  baseDamage;
        public Double  criticalChance;
        public Double  baseSpeed;
        public Integer attackReach;
        public Integer weightGroup;
        public Integer damageType;
        public Boolean usesWeapon;
        public Integer rounds;
        public Double  waitUntilNextAttack;
    }

    public static class ColorDef {
        public Integer red;
        public Integer green;
        public Integer blue;
    }
}
