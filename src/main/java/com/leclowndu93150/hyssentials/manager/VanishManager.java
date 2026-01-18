package com.leclowndu93150.hyssentials.manager;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.leclowndu93150.hyssentials.lang.Messages;
import com.leclowndu93150.hyssentials.util.ChatUtil;
import javax.annotation.Nonnull;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class VanishManager {
    private final Set<UUID> vanishedPlayers = ConcurrentHashMap.newKeySet();

    public void setVanished(@Nonnull UUID playerUuid, boolean vanished) {
        if (vanished) {
            vanishedPlayers.add(playerUuid);
        } else {
            vanishedPlayers.remove(playerUuid);
        }
        updateVisibilityForAll(playerUuid, vanished);
    }

    public boolean isVanished(@Nonnull UUID playerUuid) {
        return vanishedPlayers.contains(playerUuid);
    }

    public boolean toggleVanish(@Nonnull UUID playerUuid) {
        boolean nowVanished = !isVanished(playerUuid);
        setVanished(playerUuid, nowVanished);
        return nowVanished;
    }

    public void onPlayerJoin(@Nonnull PlayerRef joiningPlayer) {
        for (UUID vanishedUuid : vanishedPlayers) {
            joiningPlayer.getHiddenPlayersManager().hidePlayer(vanishedUuid);
        }

        if (isVanished(joiningPlayer.getUuid())) {
            joiningPlayer.sendMessage(ChatUtil.parse(Messages.INFO_VANISH_REMINDER));
        }
    }

    public void onPlayerLeave(@Nonnull UUID playerUuid) {
        vanishedPlayers.remove(playerUuid);
    }

    private void updateVisibilityForAll(@Nonnull UUID targetUuid, boolean hide) {
        for (PlayerRef player : Universe.get().getPlayers()) {
            if (player.getUuid().equals(targetUuid)) {
                continue;
            }

            if (hide) {
                player.getHiddenPlayersManager().hidePlayer(targetUuid);
            } else {
                player.getHiddenPlayersManager().showPlayer(targetUuid);
            }
        }
    }

    public Set<UUID> getVanishedPlayers() {
        return Set.copyOf(vanishedPlayers);
    }
}
