package com.leclowndu93150.hyssentials.commands.warp;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.leclowndu93150.hyssentials.data.LocationData;
import com.leclowndu93150.hyssentials.manager.BackManager;
import com.leclowndu93150.hyssentials.manager.CooldownManager;
import com.leclowndu93150.hyssentials.manager.WarpManager;
import java.util.UUID;
import javax.annotation.Nonnull;

public class WarpCommand extends AbstractPlayerCommand {
    private final WarpManager warpManager;
    private final BackManager backManager;
    private final CooldownManager cooldownManager;
    private final RequiredArg<String> nameArg = this.withRequiredArg("name", "Warp name", ArgTypes.STRING);

    public WarpCommand(@Nonnull WarpManager warpManager, @Nonnull BackManager backManager, @Nonnull CooldownManager cooldownManager) {
        super("warp", "Teleport to a server warp");
        this.warpManager = warpManager;
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
        String name = nameArg.get(context);
        UUID playerUuid = playerRef.getUuid();

        if (cooldownManager.isOnCooldown(playerUuid, CooldownManager.WARP)) {
            long remaining = cooldownManager.getCooldownRemaining(playerUuid, CooldownManager.WARP);
            context.sendMessage(Message.raw(String.format("You must wait %d seconds before using /warp again.", remaining)));
            return;
        }

        LocationData warp = warpManager.getWarp(name);
        if (warp == null) {
            context.sendMessage(Message.raw(String.format("Warp '%s' not found. Use /warps to see available warps.", name)));
            return;
        }
        World targetWorld = Universe.get().getWorld(warp.worldName());
        if (targetWorld == null) {
            targetWorld = world;
        }
        TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
        HeadRotation headRotation = store.getComponent(ref, HeadRotation.getComponentType());
        if (transform != null) {
            Vector3d currentPos = transform.getPosition().clone();
            Vector3f currentRot = headRotation != null ? headRotation.getRotation().clone() : new Vector3f(0, 0, 0);
            backManager.saveLocation(playerRef.getUuid(), LocationData.from(world.getName(), currentPos, currentRot));
        }
        World finalWorld = targetWorld;
        world.execute(() -> {
            Teleport teleport = new Teleport(finalWorld, warp.toPosition(), warp.toRotation());
            store.addComponent(ref, Teleport.getComponentType(), teleport);
            cooldownManager.setCooldown(playerUuid, CooldownManager.WARP);
            context.sendMessage(Message.raw(String.format(
                "Teleporting to warp '%s' at %.1f, %.1f, %.1f",
                name, warp.x(), warp.y(), warp.z())));
        });
    }
}
