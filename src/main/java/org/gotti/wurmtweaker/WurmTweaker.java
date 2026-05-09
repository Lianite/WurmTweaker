package org.gotti.wurmtweaker;

import org.gotti.wurmtweaker.creatures.CreatureHandler;
import org.gotti.wurmtweaker.json.JsonLoader;
import org.gotti.wurmtweaker.skills.SkillHandler;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.modsupport.creatures.ModCreatures;

import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;

public class WurmTweaker implements WurmServerMod, Configurable, Initable, ServerStartedListener {

    private static final Logger logger = Logger.getLogger(WurmTweaker.class.getName());

    private File dataDir;
    private JsonLoader jsonLoader;
    private CreatureHandler creatureHandler;

    @Override
    public void configure(Properties properties) {
        String dataDirPath = properties.getProperty("dataDir", "mods/wurmtweaker/data");
        dataDir = new File(dataDirPath);
    }

    @Override
    public void init() {
        jsonLoader = new JsonLoader(dataDir);
        creatureHandler = new CreatureHandler();
        jsonLoader.registerHandler(creatureHandler);
        ModCreatures.init();
        jsonLoader.loadType("creature");
    }

    @Override
    public void onServerStarted() {
        logger.info("WurmTweaker started. Loading content from: " + dataDir.getAbsolutePath());
        if (!dataDir.exists()) {
            logger.warning("WurmTweaker data directory not found: " + dataDir.getAbsolutePath()
                    + " — create it and add JSON files to customize content.");
            return;
        }
        creatureHandler.applyPostInit();
        jsonLoader.registerHandler(new SkillHandler());
        jsonLoader.loadType("skill");
    }
}
