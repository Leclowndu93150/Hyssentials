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
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.leclowndu93150.hyssentials.manager.HomeManager;
import com.leclowndu93150.hyssentials.util.Permissions;
import java.util.UUID;
import javax.annotation.Nonnull;

public class SetHomeCommand extends AbstractPlayerCommand {
    private final HomeManager homeManager;
    private final RequiredArg<String> nameArg = this.withRequiredArg("name", "Home name", ArgTypes.STRING);

    public SetHomeCommand(@Nonnull HomeManager homeManager) {
        super("sethome", "Set a home at your current location");
        this.homeManager = homeManager;
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
        TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
        if (transform == null) {
            context.sendMessage(Message.raw("Could not get your position."));
            return;
        }
        HeadRotation headRotation = store.getComponent(ref, HeadRotation.getComponentType());
        Vector3f rotation = headRotation != null ? headRotation.getRotation() : new Vector3f(0, 0, 0);
        Vector3d position = transform.getPosition();
        boolean hasVipHomes = Permissions.hasVipHomes(playerRef);
        int maxHomes = hasVipHomes ? homeManager.getVipMaxHomes() : homeManager.getMaxHomes();
        boolean success = homeManager.setHome(playerUuid, name, world, position, rotation, maxHomes);
        if (success) {
            context.sendMessage(Message.raw(String.format(
                "Home '%s' set at %.1f, %.1f, %.1f in %s",
                name, position.getX(), position.getY(), position.getZ(), world.getName())));
        } else {
            context.sendMessage(Message.raw(String.format(
                "You have reached the maximum number of homes (%d). Delete one first using /delhome <name>",
                maxHomes)));
        }
    }
}
