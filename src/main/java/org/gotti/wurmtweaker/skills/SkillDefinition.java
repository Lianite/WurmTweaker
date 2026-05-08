package org.gotti.wurmtweaker.skills;

import com.google.gson.annotations.SerializedName;

public class SkillDefinition {
    @SerializedName("json-type")
    public String jsonType;
    public int id;
    public String name;
    public Float difficulty;
    public Short type;          // 0=basic, 1=memory, 2=enhancing, 4=normal
    public int[] dependencies;  // IDs of parent skills that receive bonus ticks
    public Long decayTime;      // milliseconds
    public Boolean slowForPriests;
    public Boolean fightSkill;
    public Boolean thieverySkill;
    public Boolean ignoresEnemies;
    public Long tickTime;       // milliseconds; 0 disables tick-based gain
}
