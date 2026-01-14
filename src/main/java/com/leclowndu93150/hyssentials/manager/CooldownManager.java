package com.leclowndu93150.hyssentials.manager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;

public class CooldownManager {
    private final Map<String, Map<UUID, Long>> cooldowns = new ConcurrentHashMap<>();
    private final int homeCooldownSeconds;
    private final int warpCooldownSeconds;
    private final int spawnCooldownSeconds;
    private final int backCooldownSeconds;

    public CooldownManager(int homeCooldownSeconds, int warpCooldownSeconds, int spawnCooldownSeconds, int backCooldownSeconds) {
        this.homeCooldownSeconds = homeCooldownSeconds;
        this.warpCooldownSeconds = warpCooldownSeconds;
        this.spawnCooldownSeconds = spawnCooldownSeconds;
        this.backCooldownSeconds = backCooldownSeconds;
    }

    public boolean isOnCooldown(@Nonnull UUID player, @Nonnull String commandType) {
        int cooldownSeconds = getCooldownSeconds(commandType);
        if (cooldownSeconds <= 0) {
            return false;
        }
        Map<UUID, Long> commandCooldowns = cooldowns.get(commandType);
        if (commandCooldowns == null) {
            return false;
        }
        Long lastUse = commandCooldowns.get(player);
        if (lastUse == null) {
            return false;
        }
        return System.currentTimeMillis() - lastUse < cooldownSeconds * 1000L;
    }

    public long getCooldownRemaining(@Nonnull UUID player, @Nonnull String commandType) {
        int cooldownSeconds = getCooldownSeconds(commandType);
        if (cooldownSeconds <= 0) {
            return 0;
        }
        Map<UUID, Long> commandCooldowns = cooldowns.get(commandType);
        if (commandCooldowns == null) {
            return 0;
        }
        Long lastUse = commandCooldowns.get(player);
        if (lastUse == null) {
            return 0;
        }
        long remaining = (cooldownSeconds * 1000L) - (System.currentTimeMillis() - lastUse);
        return Math.max(0, remaining / 1000);
    }

    public void setCooldown(@Nonnull UUID player, @Nonnull String commandType) {
        int cooldownSeconds = getCooldownSeconds(commandType);
        if (cooldownSeconds <= 0) {
            return;
        }
        cooldowns.computeIfAbsent(commandType, k -> new ConcurrentHashMap<>())
                .put(player, System.currentTimeMillis());
        cleanupExpired(commandType);
    }

    private int getCooldownSeconds(@Nonnull String commandType) {
        return switch (commandType) {
            case "home" -> homeCooldownSeconds;
            case "warp" -> warpCooldownSeconds;
            case "spawn" -> spawnCooldownSeconds;
            case "back" -> backCooldownSeconds;
            default -> 0;
        };
    }

    private void cleanupExpired(@Nonnull String commandType) {
        int cooldownSeconds = getCooldownSeconds(commandType);
        if (cooldownSeconds <= 0) {
            return;
        }
        Map<UUID, Long> commandCooldowns = cooldowns.get(commandType);
        if (commandCooldowns != null) {
            long cutoff = System.currentTimeMillis() - (cooldownSeconds * 1000L);
            commandCooldowns.entrySet().removeIf(entry -> entry.getValue() < cutoff);
        }
    }

    public static final String HOME = "home";
    public static final String WARP = "warp";
    public static final String SPAWN = "spawn";
    public static final String BACK = "back";
}
