package com.leclowndu93150.hyssentials.commands.warp;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.permissions.HytalePermissions;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.leclowndu93150.hyssentials.lang.Messages;
import com.leclowndu93150.hyssentials.manager.WarpManager;
import com.leclowndu93150.hyssentials.util.ChatUtil;
import javax.annotation.Nonnull;

public class SetWarpCommand extends AbstractPlayerCommand {
    private final WarpManager warpManager;
    private final RequiredArg<String> nameArg = this.withRequiredArg("name", "Warp name", ArgTypes.STRING);

    public SetWarpCommand(@Nonnull WarpManager warpManager) {
        super("setwarp", "Set a server warp at your current location");
        this.warpManager = warpManager;
        this.requirePermission(HytalePermissions.fromCommand("hyssentials.setwarp"));
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store,
                          @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        String name = nameArg.get(context);
        TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
        if (transform == null) {
            context.sendMessage(ChatUtil.parse(Messages.ERROR_CANNOT_GET_POSITION));
            return;
        }
        HeadRotation headRotation = store.getComponent(ref, HeadRotation.getComponentType());
        Vector3f rotation = headRotation != null ? headRotation.getRotation() : new Vector3f(0, 0, 0);
        Vector3d position = transform.getPosition();
        warpManager.setWarp(name, world, position, rotation);
        context.sendMessage(ChatUtil.parse(Messages.SUCCESS_WARP_SET,
            name, position.getX(), position.getY(), position.getZ(), world.getName()));
    }
}
