package com.leclowndu93150.hyssentials.lang;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hypixel.hytale.logger.HytaleLogger;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages translations for the plugin.
 * Supports loading from external files or bundled resources.
 */
public final class LanguageManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type NESTED_MAP_TYPE = new TypeToken<Map<String, Object>>() {}.getType();

    private static String currentLanguage = "en";
    private static Map<String, String> translations = new HashMap<>();
    private static Map<String, String> fallbackTranslations = new HashMap<>();
    private static HytaleLogger logger;
    private static Path dataDirectory;

    private LanguageManager() {}

    /**
     * Initialize the language manager.
     * @param dataDir The plugin's data directory
     * @param log The logger to use
     */
    public static void init(@Nonnull Path dataDir, @Nonnull HytaleLogger log) {
        dataDirectory = dataDir;
        logger = log;

        // Always load English as fallback
        fallbackTranslations = loadLanguageFile("en");

        // Load configured language
        loadLanguage(currentLanguage);
    }

    /**
     * Set the current language.
     * @param language The language code (e.g., "en", "fr")
     */
    public static void setLanguage(@Nonnull String language) {
        currentLanguage = language.toLowerCase();
        loadLanguage(currentLanguage);
    }

    /**
     * Get the current language code.
     */
    @Nonnull
    public static String getCurrentLanguage() {
        return currentLanguage;
    }

    /**
     * Reload translations.
     */
    public static void reload() {
        fallbackTranslations = loadLanguageFile("en");
        loadLanguage(currentLanguage);
    }

    /**
     * Get a translated message.
     */
    @Nonnull
    public static String get(@Nonnull Messages message) {
        String key = message.getKey();
        String translation = translations.get(key);
        if (translation != null) {
            return translation;
        }
        translation = fallbackTranslations.get(key);
        if (translation != null) {
            return translation;
        }
        return key;
    }

    /**
     * Get a translated message with format arguments.
     */
    @Nonnull
    public static String format(@Nonnull Messages message, Object... args) {
        String template = get(message);
        if (args == null || args.length == 0) {
            return template;
        }
        try {
            return String.format(template, args);
        } catch (Exception e) {
            if (logger != null) {
                logger.at(Level.WARNING).log("Failed to format message %s: %s", message.getKey(), e.getMessage());
            }
            return template;
        }
    }

    private static void loadLanguage(@Nonnull String language) {
        translations = loadLanguageFile(language);
        if (translations.isEmpty() && !language.equals("en")) {
            if (logger != null) {
                logger.at(Level.WARNING).log("Language file for '%s' not found or empty, using English.", language);
            }
            translations = fallbackTranslations;
        }
    }

    @Nonnull
    private static Map<String, String> loadLanguageFile(@Nonnull String language) {
        Map<String, String> result = new HashMap<>();

        // First try to load from data directory (allows server admins to customize)
        Path externalFile = dataDirectory != null ? dataDirectory.resolve("lang").resolve(language + ".json") : null;
        if (externalFile != null && Files.exists(externalFile)) {
            try (Reader reader = Files.newBufferedReader(externalFile, StandardCharsets.UTF_8)) {
                Map<String, Object> nested = GSON.fromJson(reader, NESTED_MAP_TYPE);
                if (nested != null) {
                    flattenMap("", nested, result);
                }
                if (logger != null) {
                    logger.at(Level.INFO).log("Loaded language file: %s", externalFile);
                }
                return result;
            } catch (IOException e) {
                if (logger != null) {
                    logger.at(Level.WARNING).log("Failed to load external language file %s: %s", externalFile, e.getMessage());
                }
            }
        }

        // Fall back to bundled resource
        String resourcePath = "/lang/" + language + ".json";
        try (InputStream is = LanguageManager.class.getResourceAsStream(resourcePath)) {
            if (is != null) {
                try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                    Map<String, Object> nested = GSON.fromJson(reader, NESTED_MAP_TYPE);
                    if (nested != null) {
                        flattenMap("", nested, result);
                    }
                }
            }
        } catch (IOException e) {
            if (logger != null) {
                logger.at(Level.WARNING).log("Failed to load bundled language file %s: %s", resourcePath, e.getMessage());
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private static void flattenMap(String prefix, Map<String, Object> nested, Map<String, String> result) {
        for (Map.Entry<String, Object> entry : nested.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                result.put(key, (String) value);
            } else if (value instanceof Map) {
                flattenMap(key, (Map<String, Object>) value, result);
            }
        }
    }

    /**
     * Export default language files to the data directory.
     */
    public static void exportDefaultLanguages() {
        if (dataDirectory == null) return;

        Path langDir = dataDirectory.resolve("lang");
        try {
            Files.createDirectories(langDir);
        } catch (IOException e) {
            if (logger != null) {
                logger.at(Level.WARNING).log("Failed to create lang directory: %s", e.getMessage());
            }
            return;
        }

        exportResource("/lang/en.json", langDir.resolve("en.json"));
        exportResource("/lang/fr.json", langDir.resolve("fr.json"));
    }

    private static void exportResource(String resourcePath, Path destination) {
        if (Files.exists(destination)) return;

        try (InputStream is = LanguageManager.class.getResourceAsStream(resourcePath)) {
            if (is != null) {
                Files.copy(is, destination);
                if (logger != null) {
                    logger.at(Level.INFO).log("Exported language file: %s", destination);
                }
            }
        } catch (IOException e) {
            if (logger != null) {
                logger.at(Level.WARNING).log("Failed to export language file %s: %s", resourcePath, e.getMessage());
            }
        }
    }
}
