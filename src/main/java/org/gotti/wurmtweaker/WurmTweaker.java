package org.gotti.wurmtweaker;

import org.gotti.wurmtweaker.json.JsonLoader;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;

import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;

public class WurmTweaker implements WurmServerMod, Configurable, Initable, ServerStartedListener {

    private static final Logger logger = Logger.getLogger(WurmTweaker.class.getName());

    private File dataDir;
    private JsonLoader jsonLoader;

    @Override
    public void configure(Properties properties) {
        String dataDirPath = properties.getProperty("dataDir", "mods/wurmtweaker/data");
        dataDir = new File(dataDirPath);
    }

    @Override
    public void init() {
        jsonLoader = new JsonLoader(dataDir);
        // Content handlers are registered here as phases are implemented.
        // Example (uncomment when TASK-003 is complete):
        // jsonLoader.registerHandler(new SkillHandler());
    }

    @Override
    public void onServerStarted() {
        logger.info("WurmTweaker started. Loading content from: " + dataDir.getAbsolutePath());
        if (!dataDir.exists()) {
            logger.warning("WurmTweaker data directory not found: " + dataDir.getAbsolutePath()
                    + " — create it and add JSON files to customize content.");
            return;
        }
        jsonLoader.loadAll();
    }
}
