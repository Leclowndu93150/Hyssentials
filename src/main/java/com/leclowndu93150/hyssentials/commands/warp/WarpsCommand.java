package com.leclowndu93150.hyssentials.commands.warp;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.leclowndu93150.hyssentials.lang.Messages;
import com.leclowndu93150.hyssentials.manager.WarpManager;
import com.leclowndu93150.hyssentials.util.ChatUtil;
import java.util.Set;
import javax.annotation.Nonnull;

public class WarpsCommand extends AbstractPlayerCommand {
    private final WarpManager warpManager;

    public WarpsCommand(@Nonnull WarpManager warpManager) {
        super("warps", "List all server warps");
        this.warpManager = warpManager;
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store,
                          @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        Set<String> warps = warpManager.getWarpNames();
        if (warps.isEmpty()) {
            context.sendMessage(ChatUtil.parse(Messages.ERROR_NO_WARPS));
            return;
        }
        context.sendMessage(ChatUtil.parse(Messages.INFO_WARPS_LIST, warps.size()));
        for (String warp : warps) {
            context.sendMessage(ChatUtil.parse("<gray>  - " + warp + "</gray>"));
        }
    }
}
