package com.leclowndu93150.hyssentials.data;

import javax.annotation.Nonnull;

/**
 * Represents an admin chat group configuration.
 * Players with the group's permission can send and receive messages in that chat.
 */
public class AdminChatGroup {
    private String id = "staff";
    private String displayName = "Staff Chat";
    private String permission = "hyssentials.adminchat.staff";
    private String prefix = "[STAFF]";
    private String color = "#58a6ff";

    public AdminChatGroup() {
    }

    public AdminChatGroup(@Nonnull String id, @Nonnull String displayName,
                          @Nonnull String permission, @Nonnull String prefix, @Nonnull String color) {
        this.id = id;
        this.displayName = displayName;
        this.permission = permission;
        this.prefix = prefix;
        this.color = color;
    }

    @Nonnull
    public String getId() {
        return id;
    }

    public void setId(@Nonnull String id) {
        this.id = id;
    }

    @Nonnull
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(@Nonnull String displayName) {
        this.displayName = displayName;
    }

    @Nonnull
    public String getPermission() {
        return permission;
    }

    public void setPermission(@Nonnull String permission) {
        this.permission = permission;
    }

    @Nonnull
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(@Nonnull String prefix) {
        this.prefix = prefix;
    }

    @Nonnull
    public String getColor() {
        return color;
    }

    public void setColor(@Nonnull String color) {
        this.color = color;
    }

    /**
     * Creates a default staff chat group.
     */
    public static AdminChatGroup staffGroup() {
        return new AdminChatGroup("staff", "Staff Chat", "hyssentials.adminchat.staff", "[STAFF]", "#58a6ff");
    }

    /**
     * Creates a default admin chat group.
     */
    public static AdminChatGroup adminGroup() {
        return new AdminChatGroup("admin", "Admin Chat", "hyssentials.adminchat.admin", "[ADMIN]", "#f85149");
    }
}
