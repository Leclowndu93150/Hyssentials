package com.leclowndu93150.hyssentials.manager;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.leclowndu93150.hyssentials.data.LocationData;
import com.leclowndu93150.hyssentials.lang.Messages;
import com.leclowndu93150.hyssentials.util.ChatUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TeleportWarmupManager {
    private static final double MOVEMENT_THRESHOLD = 0.5;

    private final Map<UUID, PendingTeleport> pendingTeleports = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final BackManager backManager;
    private final CooldownManager cooldownManager;

    public TeleportWarmupManager(@Nonnull BackManager backManager, @Nonnull CooldownManager cooldownManager) {
        this.backManager = backManager;
        this.cooldownManager = cooldownManager;
    }

    public void shutdown() {
        scheduler.shutdown();
        pendingTeleports.clear();
    }

    public void startWarmup(
            @Nonnull PlayerRef playerRef,
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull World currentWorld,
            @Nonnull LocationData destination,
            int warmupSeconds,
            @Nonnull String commandType,
            @Nullable String displayName,
            @Nullable Runnable onComplete
    ) {
        UUID playerUuid = playerRef.getUuid();

        // Cancel any existing warmup
        cancelWarmup(playerUuid, false);

        // If warmup is 0 or negative, teleport immediately
        if (warmupSeconds <= 0) {
            executeTeleport(playerRef, store, ref, currentWorld, destination, commandType, displayName, onComplete);
            return;
        }

        // Get starting position for movement check
        TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
        if (transform == null) {
            playerRef.sendMessage(ChatUtil.parse(Messages.ERROR_WARMUP_FAILED));
            return;
        }

        Vector3d startPos = transform.getPosition().clone();
        String destName = displayName != null ? displayName : String.format("%.1f, %.1f, %.1f", destination.x(), destination.y(), destination.z());

        playerRef.sendMessage(ChatUtil.parse(Messages.INFO_WARMUP_STARTED, destName, warmupSeconds));

        // Schedule the teleport
        ScheduledFuture<?> future = scheduler.schedule(() -> {
            PendingTeleport pending = pendingTeleports.remove(playerUuid);
            if (pending == null) {
                return;
            }

            // Cancel movement check
            pending.movementCheckFuture().cancel(false);

            // Check if player has moved
            World playerWorld = Universe.get().getWorld(currentWorld.getName());
            if (playerWorld == null) {
                playerWorld = currentWorld;
            }

            World finalWorld = playerWorld;
            finalWorld.execute(() -> {
                TransformComponent currentTransform = store.getComponent(ref, TransformComponent.getComponentType());
                if (currentTransform != null) {
                    Vector3d currentPos = currentTransform.getPosition();
                    double distance = startPos.distanceTo(currentPos);

                    if (distance > MOVEMENT_THRESHOLD) {
                        playerRef.sendMessage(ChatUtil.parse(Messages.INFO_WARMUP_CANCELLED));
                        return;
                    }
                }

                executeTeleport(playerRef, store, ref, finalWorld, destination, commandType, destName, onComplete);
            });
        }, warmupSeconds, TimeUnit.SECONDS);

        // Schedule periodic movement checks
        ScheduledFuture<?> movementCheckFuture = scheduleMovementCheck(playerUuid, startPos, store, ref, currentWorld);

        pendingTeleports.put(playerUuid, new PendingTeleport(
            playerRef, store, ref, currentWorld, destination, startPos,
            warmupSeconds, commandType, displayName, onComplete, future, movementCheckFuture
        ));
    }

    private ScheduledFuture<?> scheduleMovementCheck(UUID playerUuid, Vector3d startPos, Store<EntityStore> store, Ref<EntityStore> ref, World world) {
        return scheduler.scheduleAtFixedRate(() -> {
            PendingTeleport pending = pendingTeleports.get(playerUuid);
            if (pending == null) {
                throw new RuntimeException("cancel");
            }

            world.execute(() -> {
                TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
                if (transform != null) {
                    Vector3d currentPos = transform.getPosition();
                    double distance = startPos.distanceTo(currentPos);

                    if (distance > MOVEMENT_THRESHOLD) {
                        cancelWarmup(playerUuid, true);
                    }
                }
            });
        }, 200, 200, TimeUnit.MILLISECONDS);
    }

    private void executeTeleport(
            @Nonnull PlayerRef playerRef,
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull World currentWorld,
            @Nonnull LocationData destination,
            @Nonnull String commandType,
            @Nullable String displayName,
            @Nullable Runnable onComplete
    ) {
        UUID playerUuid = playerRef.getUuid();

        TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
        HeadRotation headRotation = store.getComponent(ref, HeadRotation.getComponentType());
        if (transform != null) {
            Vector3d currentPos = transform.getPosition().clone();
            Vector3f currentRot = headRotation != null ? headRotation.getRotation().clone() : new Vector3f(0, 0, 0);
            backManager.saveLocation(playerUuid, LocationData.from(currentWorld.getName(), currentPos, currentRot));
        }

        World targetWorld = Universe.get().getWorld(destination.worldName());
        if (targetWorld == null) {
            targetWorld = currentWorld;
        }

        World finalWorld = targetWorld;
        long chunkIndex = ChunkUtil.indexChunkFromBlock((int) destination.x(), (int) destination.z());

        finalWorld.getChunkStore().getChunkReferenceAsync(chunkIndex, 0).thenAccept(chunkRef -> {
            currentWorld.execute(() -> {
                Teleport teleport = new Teleport(finalWorld, destination.toPosition(), destination.toBodyRotation())
                    .setHeadRotation(destination.toHeadRotation());
                store.addComponent(ref, Teleport.getComponentType(), teleport);

                cooldownManager.setCooldown(playerUuid, commandType);

                String destName = displayName != null ? displayName : String.format("%.1f, %.1f, %.1f", destination.x(), destination.y(), destination.z());
                playerRef.sendMessage(ChatUtil.parse(Messages.SUCCESS_TELEPORTED, destName));

                if (onComplete != null) {
                    onComplete.run();
                }
            });
        }).exceptionally(ex -> {
            playerRef.sendMessage(ChatUtil.parse(Messages.ERROR_CHUNK_LOAD_FAILED));
            return null;
        });
    }

    public void cancelWarmup(@Nonnull UUID playerUuid, boolean sendMessage) {
        PendingTeleport pending = pendingTeleports.remove(playerUuid);
        if (pending != null) {
            pending.future().cancel(false);
            pending.movementCheckFuture().cancel(false);
            if (sendMessage) {
                pending.playerRef().sendMessage(ChatUtil.parse(Messages.INFO_WARMUP_CANCELLED));
            }
        }
    }

    public boolean hasPendingTeleport(@Nonnull UUID playerUuid) {
        return pendingTeleports.containsKey(playerUuid);
    }

    private record PendingTeleport(
        PlayerRef playerRef,
        Store<EntityStore> store,
        Ref<EntityStore> ref,
        World currentWorld,
        LocationData destination,
        Vector3d startPosition,
        int warmupSeconds,
        String commandType,
        String displayName,
        Runnable onComplete,
        ScheduledFuture<?> future,
        ScheduledFuture<?> movementCheckFuture
    ) {}
}
