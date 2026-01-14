package com.leclowndu93150.hyssentials.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hypixel.hytale.logger.HytaleLogger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nonnull;

public class ConfigMigrator {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Path configPath;
    private final HytaleLogger logger;

    public ConfigMigrator(@Nonnull Path dataDirectory, @Nonnull HytaleLogger logger) {
        this.configPath = dataDirectory.resolve("config.json");
        this.logger = logger;
    }

    public void migrate() {
        if (!Files.exists(configPath)) {
            logger.atInfo().log("No existing config found, will create fresh config.");
            return;
        }

        try {
            String content = Files.readString(configPath);
            JsonObject json = JsonParser.parseString(content).getAsJsonObject();

            int currentVersion = json.has("ConfigVersion") ? json.get("ConfigVersion").getAsInt() : 1;

            if (currentVersion >= HyssentialsConfig.CONFIG_VERSION) {
                logger.atInfo().log("Config is up to date (version %d).", currentVersion);
                return;
            }

            logger.atInfo().log("Migrating config from version %d to version %d...",
                currentVersion, HyssentialsConfig.CONFIG_VERSION);

            if (currentVersion < 2) {
                migrateV1ToV2(json);
            }
            if (currentVersion < 3) {
                migrateV2ToV3(json);
            }

            json.addProperty("ConfigVersion", HyssentialsConfig.CONFIG_VERSION);

            Files.writeString(configPath, GSON.toJson(json));
            logger.atInfo().log("Config migration complete!");

        } catch (IOException e) {
            logger.atWarning().log("Failed to migrate config: %s", e.getMessage());
        }
    }

    private void migrateV1ToV2(@Nonnull JsonObject json) {
        // Rename old keys to new format with Seconds suffix
        renameKey(json, "TpaTimeout", "TpaTimeoutSeconds");
        renameKey(json, "TpaCooldown", "TpaCooldownSeconds");
        renameKey(json, "TeleportDelay", "TeleportDelaySeconds");

        // Add new cooldown settings (in seconds)
        if (!json.has("HomeCooldownSeconds")) {
            json.addProperty("HomeCooldownSeconds", 60);
            logger.atInfo().log("Added HomeCooldownSeconds with default value 60");
        }
        if (!json.has("WarpCooldownSeconds")) {
            json.addProperty("WarpCooldownSeconds", 60);
            logger.atInfo().log("Added WarpCooldownSeconds with default value 60");
        }
        if (!json.has("SpawnCooldownSeconds")) {
            json.addProperty("SpawnCooldownSeconds", 60);
            logger.atInfo().log("Added SpawnCooldownSeconds with default value 60");
        }
        if (!json.has("BackCooldownSeconds")) {
            json.addProperty("BackCooldownSeconds", 60);
            logger.atInfo().log("Added BackCooldownSeconds with default value 60");
        }
    }

    private void migrateV2ToV3(@Nonnull JsonObject json) {
        // Add VIP settings
        if (!json.has("VipMaxHomes")) {
            json.addProperty("VipMaxHomes", 10);
            logger.atInfo().log("Added VipMaxHomes with default value 10");
        }
        if (!json.has("VipHomeCooldownSeconds")) {
            json.addProperty("VipHomeCooldownSeconds", 0);
            logger.atInfo().log("Added VipHomeCooldownSeconds with default value 0 (no cooldown)");
        }
        if (!json.has("VipWarpCooldownSeconds")) {
            json.addProperty("VipWarpCooldownSeconds", 0);
            logger.atInfo().log("Added VipWarpCooldownSeconds with default value 0 (no cooldown)");
        }
        if (!json.has("VipSpawnCooldownSeconds")) {
            json.addProperty("VipSpawnCooldownSeconds", 0);
            logger.atInfo().log("Added VipSpawnCooldownSeconds with default value 0 (no cooldown)");
        }
        if (!json.has("VipBackCooldownSeconds")) {
            json.addProperty("VipBackCooldownSeconds", 0);
            logger.atInfo().log("Added VipBackCooldownSeconds with default value 0 (no cooldown)");
        }
    }

    private void renameKey(@Nonnull JsonObject json, @Nonnull String oldKey, @Nonnull String newKey) {
        if (json.has(oldKey) && !json.has(newKey)) {
            json.add(newKey, json.get(oldKey));
            json.remove(oldKey);
            logger.atInfo().log("Renamed config key '%s' to '%s'", oldKey, newKey);
        }
    }
}
