package org.gotti.wurmtweaker.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class JsonLoader {

    private static final Logger logger = Logger.getLogger(JsonLoader.class.getName());

    private final File baseDir;
    private final Map<String, ContentHandler<?>> handlers = new HashMap<String, ContentHandler<?>>();
    private final Gson gson = new GsonBuilder().create();

    public JsonLoader(File baseDir) {
        this.baseDir = baseDir;
    }

    public void registerHandler(ContentHandler<?> handler) {
        handlers.put(handler.getTypeName(), handler);
    }

    public void loadAll() {
        for (ContentHandler<?> handler : handlers.values()) {
            loadType(handler);
        }
    }

    private <T> void loadType(ContentHandler<T> handler) {
        File typeDir = new File(baseDir, handler.getTypeName());
        if (!typeDir.exists()) {
            logger.info("WurmTweaker: no data directory for type '" + handler.getTypeName() + "' — skipping.");
            return;
        }

        File[] files = typeDir.listFiles(f -> f.isFile() && f.getName().endsWith(".json"));
        if (files == null || files.length == 0) {
            logger.info("WurmTweaker: no JSON files found in " + typeDir.getPath());
            return;
        }

        for (File file : files) {
            loadFile(file, handler);
        }
    }

    private <T> void loadFile(File file, ContentHandler<T> handler) {
        try (FileReader reader = new FileReader(file)) {
            T definition = gson.fromJson(reader, handler.getDefinitionClass());
            handler.apply(definition);
        } catch (JsonSyntaxException e) {
            logger.warning("WurmTweaker: failed to parse " + file.getName() + ": " + e.getMessage());
        } catch (IOException e) {
            logger.warning("WurmTweaker: could not read " + file.getName() + ": " + e.getMessage());
        }
    }
}
