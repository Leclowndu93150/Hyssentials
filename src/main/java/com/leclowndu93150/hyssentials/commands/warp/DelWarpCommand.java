package com.leclowndu93150.hyssentials.commands.warp;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.permissions.HytalePermissions;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.leclowndu93150.hyssentials.lang.Messages;
import com.leclowndu93150.hyssentials.manager.WarpManager;
import com.leclowndu93150.hyssentials.util.ChatUtil;
import javax.annotation.Nonnull;

public class DelWarpCommand extends AbstractPlayerCommand {
    private final WarpManager warpManager;
    private final RequiredArg<String> nameArg = this.withRequiredArg("name", "Warp name to delete", ArgTypes.STRING);

    public DelWarpCommand(@Nonnull WarpManager warpManager) {
        super("delwarp", "Delete a server warp");
        this.warpManager = warpManager;
        this.requirePermission(HytalePermissions.fromCommand("hyssentials.delwarp"));
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store,
                          @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        String name = nameArg.get(context);
        boolean deleted = warpManager.deleteWarp(name);
        if (deleted) {
            context.sendMessage(ChatUtil.parse(Messages.SUCCESS_WARP_DELETED, name));
        } else {
            context.sendMessage(ChatUtil.parse(Messages.ERROR_WARP_NOT_FOUND, name));
        }
    }
}
