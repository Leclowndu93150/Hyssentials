package com.leclowndu93150.hyssentials.commands.home;

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
import com.leclowndu93150.hyssentials.manager.HomeManager;
import java.util.UUID;
import javax.annotation.Nonnull;

public class HomeCommand extends AbstractPlayerCommand {
    private final HomeManager homeManager;
    private final BackManager backManager;
    private final CooldownManager cooldownManager;
    private final RequiredArg<String> nameArg = this.withRequiredArg("name", "Home name", ArgTypes.STRING);

    public HomeCommand(@Nonnull HomeManager homeManager, @Nonnull BackManager backManager, @Nonnull CooldownManager cooldownManager) {
        super("home", "Teleport to your home");
        this.homeManager = homeManager;
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

        if (cooldownManager.isOnCooldown(playerUuid, CooldownManager.HOME)) {
            long remaining = cooldownManager.getCooldownRemaining(playerUuid, CooldownManager.HOME);
            context.sendMessage(Message.raw(String.format("You must wait %d seconds before using /home again.", remaining)));
            return;
        }

        LocationData home = homeManager.getHome(playerUuid, name);
        if (home == null) {
            context.sendMessage(Message.raw(String.format("Home '%s' not found. Use /sethome %s to set it.", name, name)));
            return;
        }
        World targetWorld = Universe.get().getWorld(home.worldName());
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
            Teleport teleport = new Teleport(finalWorld, home.toPosition(), home.toRotation());
            store.addComponent(ref, Teleport.getComponentType(), teleport);
            cooldownManager.setCooldown(playerUuid, CooldownManager.HOME);
            context.sendMessage(Message.raw(String.format(
                "Teleporting to home '%s' at %.1f, %.1f, %.1f",
                name, home.x(), home.y(), home.z())));
        });
    }
}
