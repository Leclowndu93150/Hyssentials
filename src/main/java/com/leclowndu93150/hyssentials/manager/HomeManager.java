package com.leclowndu93150.hyssentials.manager;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.universe.world.World;
import com.leclowndu93150.hyssentials.data.LocationData;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HomeManager {
    private final DataManager dataManager;
    private final Map<UUID, Map<String, LocationData>> playerHomes;
    private final int maxHomes;
    private final int vipMaxHomes;

    public HomeManager(@Nonnull DataManager dataManager, int maxHomes, int vipMaxHomes) {
        this.dataManager = dataManager;
        this.maxHomes = maxHomes;
        this.vipMaxHomes = vipMaxHomes;
        this.playerHomes = dataManager.loadPlayerHomes();
    }

    public boolean setHome(@Nonnull UUID playerUuid, @Nonnull String name, @Nonnull World world,
                          @Nonnull Vector3d position, @Nonnull Vector3f rotation, int effectiveMaxHomes) {
        Map<String, LocationData> homes = playerHomes.computeIfAbsent(playerUuid, k -> new ConcurrentHashMap<>());
        String lowerName = name.toLowerCase();
        if (!homes.containsKey(lowerName) && homes.size() >= effectiveMaxHomes) {
            return false;
        }
        homes.put(lowerName, LocationData.from(world.getName(), position, rotation));
        dataManager.savePlayerHomes(playerHomes);
        return true;
    }

    @Nullable
    public LocationData getHome(@Nonnull UUID playerUuid, @Nonnull String name) {
        Map<String, LocationData> homes = playerHomes.get(playerUuid);
        if (homes == null) {
            return null;
        }
        return homes.get(name.toLowerCase());
    }

    public boolean deleteHome(@Nonnull UUID playerUuid, @Nonnull String name) {
        Map<String, LocationData> homes = playerHomes.get(playerUuid);
        if (homes == null) {
            return false;
        }
        LocationData removed = homes.remove(name.toLowerCase());
        if (removed != null) {
            dataManager.savePlayerHomes(playerHomes);
            return true;
        }
        return false;
    }

    @Nonnull
    public Set<String> getHomeNames(@Nonnull UUID playerUuid) {
        Map<String, LocationData> homes = playerHomes.get(playerUuid);
        if (homes == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(homes.keySet());
    }

    public int getHomeCount(@Nonnull UUID playerUuid) {
        Map<String, LocationData> homes = playerHomes.get(playerUuid);
        return homes == null ? 0 : homes.size();
    }

    public int getMaxHomes() {
        return maxHomes;
    }

    public int getVipMaxHomes() {
        return vipMaxHomes;
    }

    public void save() {
        dataManager.savePlayerHomes(playerHomes);
    }

    public int getTotalHomeCount() {
        return playerHomes.values().stream().mapToInt(Map::size).sum();
    }
}
