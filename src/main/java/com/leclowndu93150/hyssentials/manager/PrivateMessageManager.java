package com.leclowndu93150.hyssentials.manager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Manages private message reply tracking.
 * Tracks who each player last messaged for the /r (reply) command.
 * Session-based - data is not persisted.
 */
public class PrivateMessageManager {
    private final Map<UUID, UUID> lastMessaged = new ConcurrentHashMap<>();

    /**
     * Records that sender messaged recipient.
     * Updates both directions so either player can reply.
     */
    public void setLastMessaged(@Nonnull UUID sender, @Nonnull UUID recipient) {
        lastMessaged.put(sender, recipient);
        lastMessaged.put(recipient, sender);
    }

    /**
     * Gets the UUID of the last player this player messaged or was messaged by.
     * @return The last messaged player's UUID, or null if none
     */
    @Nullable
    public UUID getLastMessaged(@Nonnull UUID playerUuid) {
        return lastMessaged.get(playerUuid);
    }

    /**
     * Clears the reply target for a player (e.g., on disconnect).
     */
    public void clearLastMessaged(@Nonnull UUID playerUuid) {
        lastMessaged.remove(playerUuid);
    }

    /**
     * Clears all tracking data.
     */
    public void clear() {
        lastMessaged.clear();
    }
}
