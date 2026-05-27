package org.gotti.wurmtweaker.items;

import com.google.gson.annotations.SerializedName;

public class ItemDefinition {

    @SerializedName("json-type") public String jsonType;

    // Lookup / creation key — exactly one required
    public Integer templateId;
    public String  templateName;
    public String  identifier;

    // Core fields — required for creation, optional for modification
    public String  name;
    public String  plural;
    public Integer size;
    public Integer imageNumber;
    public Integer behaviourType;
    public Integer combatDamage;
    public Long    decayTime;
    public Integer primarySkill;
    public String  modelName;
    public Float   difficulty;
    public Integer weight;
    public Integer material;
    public Integer value;
    public Boolean isPurchased;

    public int[] bodySpaces;
    public int[] itemTypes;

    public Descriptions descriptions;
    public Dimensions   dimensions;

    // Optional — public setters
    public ContainerSize containerSize;
    public Integer       maxItemCount;
    public Integer       maxItemWeight;
    public Nutrition     nutrition;
    public Integer       dyeAmountGrams;
    public String        secondaryItemName;
    public Integer       dyeSecondaryAmountRequired;

    // Optional — private methods (ReflectionUtil)
    public Integer alcoholStrength;
    public Integer foodGroup;
    public Integer crushsTo;
    public Integer pickSeeds;
    public Integer grows;
    public Integer harvestsTo;

    // Optional — direct field reflection (no setter)
    public Integer fragmentAmount;

    // Output only — written back by ItemHandler after creation; never used for routing or applying
    public Integer assignedTemplateId;

    public static class Descriptions {
        public String superb;
        public String normal;
        public String bad;
        public String rotten;
        @SerializedName("long") public String longDesc;
    }

    public static class Dimensions {
        public Integer x;
        public Integer y;
        public Integer z;
    }

    public static class ContainerSize {
        public Integer x;
        public Integer y;
        public Integer z;
    }

    public static class Nutrition {
        public Integer calories;
        public Integer carbs;
        public Integer fats;
        public Integer proteins;
    }
}
