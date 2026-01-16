package com.leclowndu93150.hyssentials.util;

import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import javax.annotation.Nonnull;
import java.util.UUID;

public final class Permissions {
    public static final String COOLDOWN_BYPASS = "hyssentials.cooldown.bypass";

    public static final String ADMIN_RANKS = "hyssentials.admin.ranks";
    public static final String ADMIN_SETRANK = "hyssentials.admin.setrank";
    public static final String ADMIN_PLAYERINFO = "hyssentials.admin.playerinfo";
    public static final String ADMIN_RELOAD = "hyssentials.admin.reload";

    // Admin chat permissions (groups defined in adminchat.json)
    public static final String ADMIN_CHAT_STAFF = "hyssentials.adminchat.staff";
    public static final String ADMIN_CHAT_ADMIN = "hyssentials.adminchat.admin";

    @Deprecated
    public static final String VIP = "hyssentials.vip";
    @Deprecated
    public static final String VIP_HOMES = "hyssentials.vip.homes";
    @Deprecated
    public static final String VIP_COOLDOWN = "hyssentials.vip.cooldown";

    private Permissions() {
    }

    public static boolean hasPermission(@Nonnull PlayerRef player, @Nonnull String permission) {
        return PermissionsModule.get().hasPermission(player.getUuid(), permission);
    }

    public static boolean hasPermission(@Nonnull UUID playerUuid, @Nonnull String permission) {
        return PermissionsModule.get().hasPermission(playerUuid, permission);
    }

    public static boolean canBypassCooldown(@Nonnull PlayerRef player) {
        return hasPermission(player, COOLDOWN_BYPASS);
    }

    public static boolean canManageRanks(@Nonnull PlayerRef player) {
        return hasPermission(player, ADMIN_RANKS);
    }

    public static boolean canSetRanks(@Nonnull PlayerRef player) {
        return hasPermission(player, ADMIN_SETRANK);
    }

    public static boolean canViewPlayerInfo(@Nonnull PlayerRef player) {
        return hasPermission(player, ADMIN_PLAYERINFO);
    }

    public static boolean canReload(@Nonnull PlayerRef player) {
        return hasPermission(player, ADMIN_RELOAD);
    }

    @Deprecated
    public static boolean isVip(@Nonnull PlayerRef player) {
        return hasPermission(player, VIP);
    }

    @Deprecated
    public static boolean hasVipHomes(@Nonnull PlayerRef player) {
        return hasPermission(player, VIP_HOMES) || isVip(player);
    }

    @Deprecated
    public static boolean hasVipCooldown(@Nonnull PlayerRef player) {
        return hasPermission(player, VIP_COOLDOWN) || isVip(player);
    }
}
