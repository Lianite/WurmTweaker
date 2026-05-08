package org.gotti.wurmtweaker.creatures;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CreatureDefinition {

    @SerializedName("json-type") public String jsonType;
    public Integer id;

    public String name;
    public String plural;
    public String longDesc;
    public String modelName;

    public SizeInCentimeters sizeInCentimeters;
    public Sounds sounds;
    public List<String> behaviors;

    // Constructor-time fields — create mode via builder; modify mode via reflection
    public Byte    bodyType;
    public Byte    sex;
    public Float   naturalArmour;
    public Float   speed;
    public Integer aggressivity;
    public Float   handDamage;
    public Float   kickDamage;
    public Float   biteDamage;
    public Float   headDamage;
    public Float   breathDamage;
    public Integer moveRate;
    public Integer maxHuntDistance;
    public Byte    meatMaterial;
    public Short   vision;

    // Setter-accessible fields — both modes
    public String  armourType;
    public Float   baseCombatRating;
    public Float   bonusCombatRating;
    public Integer maxAge;
    public Float   alignment;

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
}
