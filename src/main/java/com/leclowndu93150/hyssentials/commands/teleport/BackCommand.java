package com.leclowndu93150.hyssentials.commands.teleport;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
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
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.leclowndu93150.hyssentials.data.LocationData;
import com.leclowndu93150.hyssentials.manager.BackManager;
import com.leclowndu93150.hyssentials.manager.CooldownManager;
import java.util.UUID;
import javax.annotation.Nonnull;

public class BackCommand extends AbstractPlayerCommand {
    private final BackManager backManager;
    private final CooldownManager cooldownManager;

    public BackCommand(@Nonnull BackManager backManager, @Nonnull CooldownManager cooldownManager) {
        super("back", "Teleport to your previous location");
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

        if (cooldownManager.isOnCooldown(playerUuid, CooldownManager.BACK)) {
            long remaining = cooldownManager.getCooldownRemaining(playerUuid, CooldownManager.BACK);
            context.sendMessage(Message.raw(String.format("You must wait %d seconds before using /back again.", remaining)));
            return;
        }

        LocationData lastLocation = backManager.getLastLocation(playerUuid);
        if (lastLocation == null) {
            context.sendMessage(Message.raw("No previous location to return to."));
            return;
        }
        World targetWorld = Universe.get().getWorld(lastLocation.worldName());
        if (targetWorld == null) {
            targetWorld = world;
        }
        TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
        HeadRotation headRotation = store.getComponent(ref, HeadRotation.getComponentType());
        if (transform != null) {
            Vector3d currentPos = transform.getPosition().clone();
            Vector3f currentRot = headRotation != null ? headRotation.getRotation().clone() : new Vector3f(0, 0, 0);
            backManager.saveLocation(playerUuid, LocationData.from(world.getName(), currentPos, currentRot));
        }
        World finalWorld = targetWorld;
        world.execute(() -> {
            Teleport teleport = new Teleport(finalWorld, lastLocation.toPosition(), lastLocation.toRotation());
            store.addComponent(ref, Teleport.getComponentType(), teleport);
            cooldownManager.setCooldown(playerUuid, CooldownManager.BACK);
            context.sendMessage(Message.raw(String.format(
                "Teleporting back to %.1f, %.1f, %.1f",
                lastLocation.x(), lastLocation.y(), lastLocation.z())));
        });
    }
}
