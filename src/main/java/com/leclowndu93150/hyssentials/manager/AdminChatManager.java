package com.leclowndu93150.hyssentials.manager;

import com.hypixel.hytale.logger.HytaleLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.leclowndu93150.hyssentials.data.AdminChatGroup;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Manages admin chat groups and message broadcasting.
 * Groups are loaded from adminchat.json and define permission-based chat channels.
 */
public class AdminChatManager {
    private static final String ADMIN_CHAT_FILE = "adminchat.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path dataDirectory;
    private final HytaleLogger logger;
    private final List<AdminChatGroup> groups = new ArrayList<>();
    private String defaultGroupId = "staff";

    public AdminChatManager(@Nonnull Path dataDirectory, @Nonnull HytaleLogger logger) {
        this.dataDirectory = dataDirectory;
        this.logger = logger;
        load();
    }

    /**
     * Loads admin chat configuration from file.
     */
    public void load() {
        Path file = dataDirectory.resolve(ADMIN_CHAT_FILE);
        if (Files.exists(file)) {
            try {
                String json = Files.readString(file);
                Type type = new TypeToken<AdminChatConfig>() {}.getType();
                AdminChatConfig config = GSON.fromJson(json, type);
                if (config != null) {
                    groups.clear();
                    if (config.groups != null) {
                        groups.addAll(config.groups);
                    }
                    if (config.defaultGroup != null) {
                        defaultGroupId = config.defaultGroup;
                    }
                }
            } catch (IOException e) {
                logger.atSevere().log("Failed to load admin chat config: %s", e.getMessage());
            }
        } else {
            // Create default configuration
            createDefaultConfig();
            save();
        }
    }

    /**
     * Saves admin chat configuration to file.
     */
    public void save() {
        try {
            if (!Files.exists(dataDirectory)) {
                Files.createDirectories(dataDirectory);
            }
            Path file = dataDirectory.resolve(ADMIN_CHAT_FILE);
            AdminChatConfig config = new AdminChatConfig();
            config.groups = new ArrayList<>(groups);
            config.defaultGroup = defaultGroupId;
            String json = GSON.toJson(config);
            Files.writeString(file, json);
        } catch (IOException e) {
            logger.atSevere().log("Failed to save admin chat config: %s", e.getMessage());
        }
    }

    private void createDefaultConfig() {
        groups.clear();
        groups.add(AdminChatGroup.staffGroup());
        groups.add(AdminChatGroup.adminGroup());
        defaultGroupId = "staff";
    }

    /**
     * Gets all configured admin chat groups.
     */
    @Nonnull
    public List<AdminChatGroup> getGroups() {
        return new ArrayList<>(groups);
    }

    /**
     * Gets a group by ID.
     */
    @Nullable
    public AdminChatGroup getGroup(@Nonnull String id) {
        return groups.stream()
            .filter(g -> g.getId().equalsIgnoreCase(id))
            .findFirst()
            .orElse(null);
    }

    /**
     * Gets the default group.
     */
    @Nullable
    public AdminChatGroup getDefaultGroup() {
        return getGroup(defaultGroupId);
    }

    /**
     * Gets the first group that a player has permission for.
     */
    @Nullable
    public AdminChatGroup getPlayerGroup(@Nonnull PlayerRef player) {
        for (AdminChatGroup group : groups) {
            if (PermissionsModule.get().hasPermission(player.getUuid(), group.getPermission())) {
                return group;
            }
        }
        return null;
    }

    /**
     * Gets all groups that a player has permission for.
     */
    @Nonnull
    public List<AdminChatGroup> getPlayerGroups(@Nonnull PlayerRef player) {
        List<AdminChatGroup> result = new ArrayList<>();
        for (AdminChatGroup group : groups) {
            if (PermissionsModule.get().hasPermission(player.getUuid(), group.getPermission())) {
                result.add(group);
            }
        }
        return result;
    }

    /**
     * Checks if a player has access to any admin chat group.
     */
    public boolean hasAdminChatAccess(@Nonnull PlayerRef player) {
        return getPlayerGroup(player) != null;
    }

    /**
     * Broadcasts a message to all players with the given group's permission.
     */
    public void broadcast(@Nonnull AdminChatGroup group, @Nonnull PlayerRef sender, @Nonnull String message) {
        // Build colored message: [PREFIX] Username: message
        Message msg = Message.empty()
            .insert(Message.raw(group.getPrefix()).color(group.getColor()))
            .insert(Message.raw(" " + sender.getUsername() + ": "))
            .insert(Message.raw(message).color(group.getColor()));

        // Send to all online players with permission
        Universe universe = Universe.get();
        if (universe != null) {
            for (Map.Entry<String, World> entry : universe.getWorlds().entrySet()) {
                World world = entry.getValue();
                Collection<PlayerRef> players = world.getPlayerRefs();
                if (players != null) {
                    for (PlayerRef player : players) {
                        if (player != null && PermissionsModule.get().hasPermission(player.getUuid(), group.getPermission())) {
                            player.sendMessage(msg);
                        }
                    }
                }
            }
        }
    }

    /**
     * Broadcasts a message using the player's first available group.
     * @return true if message was sent, false if player has no access
     */
    public boolean broadcast(@Nonnull PlayerRef sender, @Nonnull String message) {
        AdminChatGroup group = getPlayerGroup(sender);
        if (group == null) {
            return false;
        }
        broadcast(group, sender, message);
        return true;
    }

    /**
     * Reloads configuration from file.
     */
    public void reload() {
        load();
    }

    // Internal config structure for JSON serialization
    private static class AdminChatConfig {
        List<AdminChatGroup> groups;
        String defaultGroup;
    }
}
