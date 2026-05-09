package org.gotti.wurmtweaker.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public void loadType(String typeName) {
        ContentHandler<?> handler = handlers.get(typeName);
        if (handler == null) {
            logger.warning("WurmTweaker: no handler registered for type '" + typeName + "'");
            return;
        }
        loadType(handler);
    }

    private <T> void loadType(ContentHandler<T> handler) {
        if (!baseDir.exists()) {
            logger.warning("WurmTweaker: data directory not found: " + baseDir.getAbsolutePath());
            return;
        }
        List<File> files = collectJsonFiles(baseDir);
        if (files.isEmpty()) {
            logger.info("WurmTweaker: no JSON files found under " + baseDir.getPath());
            return;
        }
        for (File file : files) {
            loadFile(file, handler);
        }
    }

    private List<File> collectJsonFiles(File dir) {
        List<File> result = new ArrayList<File>();
        File[] entries = dir.listFiles();
        if (entries == null) return result;
        for (File entry : entries) {
            if (entry.isDirectory()) {
                result.addAll(collectJsonFiles(entry));
            } else if (entry.isFile() && entry.getName().endsWith(".json")) {
                result.add(entry);
            }
        }
        return result;
    }

    private <T> void loadFile(File file, ContentHandler<T> handler) {
        try (FileReader reader = new FileReader(file)) {
            JsonElement root = JsonParser.parseReader(reader);
            if (root.isJsonArray()) {
                for (JsonElement element : root.getAsJsonArray()) {
                    dispatchIfMatch(element, file, handler);
                }
            } else {
                dispatchIfMatch(root, file, handler);
            }
        } catch (JsonSyntaxException e) {
            logger.warning("WurmTweaker: failed to parse " + file.getName() + ": " + e.getMessage());
        } catch (IOException e) {
            logger.warning("WurmTweaker: could not read " + file.getName() + ": " + e.getMessage());
        }
    }

    private <T> void dispatchIfMatch(JsonElement element, File file, ContentHandler<T> handler) {
        if (!element.isJsonObject()) return;
        JsonElement typeEl = element.getAsJsonObject().get("json-type");
        if (typeEl == null) return;
        if (handler.getTypeName().equals(typeEl.getAsString())) {
            handler.apply(gson.fromJson(element, handler.getDefinitionClass()));
        }
    }
}
