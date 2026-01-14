package com.leclowndu93150.hyssentials.commands.spawn;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.spawn.ISpawnProvider;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.leclowndu93150.hyssentials.data.LocationData;
import com.leclowndu93150.hyssentials.manager.BackManager;
import com.leclowndu93150.hyssentials.manager.CooldownManager;
import com.leclowndu93150.hyssentials.manager.SpawnManager;
import com.leclowndu93150.hyssentials.util.Permissions;
import java.util.UUID;
import javax.annotation.Nonnull;

public class SpawnCommand extends AbstractPlayerCommand {
    private final SpawnManager spawnManager;
    private final BackManager backManager;
    private final CooldownManager cooldownManager;

    public SpawnCommand(@Nonnull SpawnManager spawnManager, @Nonnull BackManager backManager, @Nonnull CooldownManager cooldownManager) {
        super("spawn", "Teleport to the server spawn");
        this.spawnManager = spawnManager;
        this.backManager = backManager;
        this.cooldownManager = cooldownManager;
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store,
                          @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        UUID playerUuid = playerRef.getUuid();
        boolean isVip = Permissions.hasVipCooldown(playerRef);
        boolean bypassCooldown = Permissions.canBypassCooldown(playerRef);

        if (!bypassCooldown && cooldownManager.isOnCooldown(playerUuid, CooldownManager.SPAWN, isVip)) {
            long remaining = cooldownManager.getCooldownRemaining(playerUuid, CooldownManager.SPAWN, isVip);
            context.sendMessage(Message.raw(String.format("You must wait %d seconds before using /spawn again.", remaining)));
            return;
        }

        TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
        HeadRotation headRotation = store.getComponent(ref, HeadRotation.getComponentType());
        if (transform != null) {
            Vector3d currentPos = transform.getPosition().clone();
            Vector3f currentRot = headRotation != null ? headRotation.getRotation().clone() : new Vector3f(0, 0, 0);
            backManager.saveLocation(playerUuid, LocationData.from(world.getName(), currentPos, currentRot));
        }
        LocationData customSpawn = spawnManager.getSpawn();
        if (customSpawn != null) {
            World targetWorld = Universe.get().getWorld(customSpawn.worldName());
            if (targetWorld == null) {
                targetWorld = world;
            }
            World finalWorld = targetWorld;
            Teleport teleport = new Teleport(finalWorld, customSpawn.toPosition(), customSpawn.toRotation());
            store.addComponent(ref, Teleport.getComponentType(), teleport);
            if (!bypassCooldown) {
                cooldownManager.setCooldown(playerUuid, CooldownManager.SPAWN, isVip);
            }
            context.sendMessage(Message.raw(String.format(
                "Teleporting to spawn at %.1f, %.1f, %.1f",
                customSpawn.x(), customSpawn.y(), customSpawn.z())));
            return;
        }
        ISpawnProvider spawnProvider = world.getWorldConfig().getSpawnProvider();
        Transform spawn = spawnProvider.getSpawnPoint(world, playerUuid);
        Vector3d position = spawn.getPosition();
        Vector3f rotation = spawn.getRotation();
        Teleport teleport = new Teleport(world, position, rotation);
        store.addComponent(ref, Teleport.getComponentType(), teleport);
        if (!bypassCooldown) {
            cooldownManager.setCooldown(playerUuid, CooldownManager.SPAWN, isVip);
        }
        context.sendMessage(Message.raw(String.format(
            "Teleporting to world spawn at %.1f, %.1f, %.1f",
            position.getX(), position.getY(), position.getZ())));
    }
}
