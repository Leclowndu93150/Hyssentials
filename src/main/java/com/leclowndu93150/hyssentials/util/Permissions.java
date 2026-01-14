package com.leclowndu93150.hyssentials.util;

import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import javax.annotation.Nonnull;

public final class Permissions {
    // VIP permission nodes
    public static final String VIP = "hyssentials.vip";
    public static final String VIP_HOMES = "hyssentials.vip.homes";
    public static final String VIP_COOLDOWN = "hyssentials.vip.cooldown";
    public static final String COOLDOWN_BYPASS = "hyssentials.cooldown.bypass";

    private Permissions() {
    }

    public static boolean hasPermission(@Nonnull PlayerRef player, @Nonnull String permission) {
        return PermissionsModule.get().hasPermission(player.getUuid(), permission);
    }

    public static boolean isVip(@Nonnull PlayerRef player) {
        return hasPermission(player, VIP);
    }

    public static boolean hasVipHomes(@Nonnull PlayerRef player) {
        return hasPermission(player, VIP_HOMES) || isVip(player);
    }

    public static boolean hasVipCooldown(@Nonnull PlayerRef player) {
        return hasPermission(player, VIP_COOLDOWN) || isVip(player);
    }

    public static boolean canBypassCooldown(@Nonnull PlayerRef player) {
        return hasPermission(player, COOLDOWN_BYPASS);
    }
}
