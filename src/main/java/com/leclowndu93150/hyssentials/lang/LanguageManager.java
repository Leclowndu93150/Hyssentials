package com.leclowndu93150.hyssentials.lang;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hypixel.hytale.logger.HytaleLogger;

import javax.annotation.Nonnull;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;

/**
 * Manages translations for the plugin.
 * Loads from JAR defaults, overlays user customizations, syncs automatically.
 */
public final class LanguageManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Type MAP_TYPE = new TypeToken<Map<String, Object>>() {}.getType();

    private static final Map<String, String> messages = new HashMap<>();
    private static Path langFolder;
    private static HytaleLogger logger;
    private static String currentLocale = "en";

    private LanguageManager() {}

    public static void init(@Nonnull Path dataDir, @Nonnull HytaleLogger log) {
        langFolder = dataDir.resolve("lang");
        logger = log;

        try {
            Files.createDirectories(langFolder);
        } catch (IOException e) {
            logger.at(Level.WARNING).log("Failed to create lang directory: %s", e.getMessage());
        }
    }

    public static void setLanguage(@Nonnull String locale) {
        currentLocale = locale.toLowerCase();
        load(currentLocale);
    }

    @Nonnull
    public static String getCurrentLanguage() {
        return currentLocale;
    }

    public static void reload() {
        load(currentLocale);
    }

    /**
     * Loads messages for the given locale.
     * First loads defaults from JAR, then overlays user customizations.
     * Syncs user file if keys differ from expected.
     */
    private static void load(@Nonnull String locale) {
        messages.clear();

        String fileName = locale + ".json";
        Path userFile = langFolder.resolve(fileName);

        // Get expected keys from Messages enum
        Set<String> expectedKeys = getExpectedKeys();

        // Load defaults from JAR
        Map<String, String> defaults = loadFromResource("/lang/" + fileName);

        // If no bundled resource exists, create defaults from expected keys
        if (defaults.isEmpty()) {
            for (String key : expectedKeys) {
                defaults.put(key, "TODO: " + key);
            }
        }

        // If user file doesn't exist, create it
        if (!Files.exists(userFile)) {
            writeFile(userFile, defaults);
            messages.putAll(defaults);
            logger.at(Level.INFO).log("[%s] Created language file with %d keys", locale.toUpperCase(), defaults.size());
            return;
        }

        // Load user file
        Map<String, String> userMessages = loadFromFile(userFile);

        // Start with defaults, overlay user values (only for keys that still exist)
        messages.putAll(defaults);
        for (Map.Entry<String, String> entry : userMessages.entrySet()) {
            if (expectedKeys.contains(entry.getKey())) {
                messages.put(entry.getKey(), entry.getValue());
            }
        }

        // Check if sync needed
        boolean hasMissing = !userMessages.keySet().containsAll(expectedKeys);
        boolean hasObsolete = !expectedKeys.containsAll(userMessages.keySet());

        if (hasMissing || hasObsolete) {
            // Rebuild file: use user values where they exist, defaults for new keys
            Map<String, String> synced = new LinkedHashMap<>();
            for (String key : expectedKeys) {
                synced.put(key, userMessages.containsKey(key) ? userMessages.get(key) : defaults.getOrDefault(key, "TODO: " + key));
            }
            writeFile(userFile, synced);

            int added = 0, removed = 0;
            for (String key : expectedKeys) {
                if (!userMessages.containsKey(key)) added++;
            }
            for (String key : userMessages.keySet()) {
                if (!expectedKeys.contains(key)) removed++;
            }
            logger.at(Level.INFO).log("[%s] Synced: +%d, -%d", locale.toUpperCase(), added, removed);
        }

        logger.at(Level.INFO).log("Loaded %d translations for locale '%s'", messages.size(), locale);
    }

    @Nonnull
    private static Set<String> getExpectedKeys() {
        Set<String> keys = new LinkedHashSet<>();
        for (Messages msg : Messages.values()) {
            keys.add(msg.getKey());
        }
        return keys;
    }

    @Nonnull
    public static String get(@Nonnull Messages message) {
        return messages.getOrDefault(message.getKey(), message.getKey());
    }

    @Nonnull
    public static String format(@Nonnull Messages message, Object... args) {
        String template = get(message);
        if (args == null || args.length == 0) {
            return template;
        }
        try {
            return String.format(template, args);
        } catch (Exception e) {
            return template;
        }
    }

    @Nonnull
    public static Set<String> getSupportedLanguages() {
        Set<String> langs = new LinkedHashSet<>();
        langs.add("en");

        // Check bundled resources
        for (String lang : Arrays.asList("fr", "es", "de", "it", "pt", "ru", "zh", "ja", "ko")) {
            if (LanguageManager.class.getResource("/lang/" + lang + ".json") != null) {
                langs.add(lang);
            }
        }

        // Check user directory
        if (langFolder != null && Files.exists(langFolder)) {
            try (var stream = Files.list(langFolder)) {
                stream.filter(p -> p.toString().endsWith(".json"))
                      .map(p -> p.getFileName().toString().replace(".json", ""))
                      .forEach(langs::add);
            } catch (IOException ignored) {}
        }

        return langs;
    }

    // ============ File I/O ============

    @Nonnull
    @SuppressWarnings("unchecked")
    private static Map<String, String> loadFromResource(@Nonnull String resourcePath) {
        Map<String, String> result = new LinkedHashMap<>();
        try (InputStream is = LanguageManager.class.getResourceAsStream(resourcePath)) {
            if (is != null) {
                try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                    Map<String, Object> nested = GSON.fromJson(reader, MAP_TYPE);
                    if (nested != null) {
                        flatten("", nested, result);
                    }
                }
            }
        } catch (IOException e) {
            if (logger != null) {
                logger.at(Level.WARNING).log("Failed to load resource %s: %s", resourcePath, e.getMessage());
            }
        }
        return result;
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    private static Map<String, String> loadFromFile(@Nonnull Path file) {
        Map<String, String> result = new LinkedHashMap<>();
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            Map<String, Object> nested = GSON.fromJson(reader, MAP_TYPE);
            if (nested != null) {
                flatten("", nested, result);
            }
        } catch (IOException e) {
            if (logger != null) {
                logger.at(Level.WARNING).log("Failed to load file %s: %s", file.getFileName(), e.getMessage());
            }
        }
        return result;
    }

    private static void writeFile(@Nonnull Path file, @Nonnull Map<String, String> flat) {
        Map<String, Object> nested = unflatten(flat);
        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            GSON.toJson(nested, writer);
        } catch (IOException e) {
            if (logger != null) {
                logger.at(Level.SEVERE).log("Failed to write %s: %s", file.getFileName(), e.getMessage());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void flatten(@Nonnull String prefix, @Nonnull Map<String, Object> nested, @Nonnull Map<String, String> result) {
        for (Map.Entry<String, Object> entry : nested.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                result.put(key, (String) value);
            } else if (value instanceof Map) {
                flatten(key, (Map<String, Object>) value, result);
            }
        }
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    private static Map<String, Object> unflatten(@Nonnull Map<String, String> flat) {
        Map<String, Object> result = new LinkedHashMap<>();

        List<String> sortedKeys = new ArrayList<>(flat.keySet());
        Collections.sort(sortedKeys);

        for (String flatKey : sortedKeys) {
            String[] parts = flatKey.split("\\.");
            Map<String, Object> current = result;

            for (int i = 0; i < parts.length - 1; i++) {
                current.computeIfAbsent(parts[i], k -> new LinkedHashMap<String, Object>());
                current = (Map<String, Object>) current.get(parts[i]);
            }

            current.put(parts[parts.length - 1], flat.get(flatKey));
        }

        return result;
    }
}
