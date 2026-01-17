package com.leclowndu93150.hyssentials.commands.spawn;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.spawn.ISpawnProvider;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.leclowndu93150.hyssentials.data.CommandSettings;
import com.leclowndu93150.hyssentials.data.LocationData;
import com.leclowndu93150.hyssentials.lang.Messages;
import com.leclowndu93150.hyssentials.manager.CooldownManager;
import com.leclowndu93150.hyssentials.manager.RankManager;
import com.leclowndu93150.hyssentials.manager.TeleportWarmupManager;
import com.leclowndu93150.hyssentials.util.ChatUtil;
import com.leclowndu93150.hyssentials.util.Permissions;
import java.util.UUID;
import javax.annotation.Nonnull;

public class SpawnCommand extends AbstractPlayerCommand {
    private final TeleportWarmupManager warmupManager;
    private final CooldownManager cooldownManager;
    private final RankManager rankManager;

    public SpawnCommand(@Nonnull TeleportWarmupManager warmupManager,
                        @Nonnull CooldownManager cooldownManager, @Nonnull RankManager rankManager) {
        super("spawn", "Teleport to the world spawn");
        this.warmupManager = warmupManager;
        this.cooldownManager = cooldownManager;
        this.rankManager = rankManager;
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store,
                          @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        UUID playerUuid = playerRef.getUuid();
        CommandSettings settings = rankManager.getEffectiveSettings(playerRef, CooldownManager.SPAWN);
        boolean bypassCooldown = Permissions.canBypassCooldown(playerRef);

        if (!settings.isEnabled()) {
            context.sendMessage(ChatUtil.parse(Messages.NO_PERMISSION_SPAWN));
            return;
        }

        if (!bypassCooldown && cooldownManager.isOnCooldown(playerUuid, CooldownManager.SPAWN, settings.getCooldownSeconds())) {
            long remaining = cooldownManager.getCooldownRemaining(playerUuid, CooldownManager.SPAWN, settings.getCooldownSeconds());
            context.sendMessage(ChatUtil.parse(Messages.COOLDOWN_SPAWN, remaining));
            return;
        }

        ISpawnProvider spawnProvider = world.getWorldConfig().getSpawnProvider();
        Transform spawn = spawnProvider.getSpawnPoint(world, playerUuid);
        Vector3d position = spawn.getPosition();
        Vector3f rotation = spawn.getRotation();
        LocationData spawnLocation = new LocationData(world.getName(), position.getX(), position.getY(), position.getZ(), rotation.getPitch(), rotation.getYaw());

        int warmupSeconds = bypassCooldown ? 0 : settings.getWarmupSeconds();
        warmupManager.startWarmup(playerRef, store, ref, world, spawnLocation, warmupSeconds, CooldownManager.SPAWN, "spawn", null);
    }
}
