package com.leclowndu93150.hyssentials.commands.tpa;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.leclowndu93150.hyssentials.lang.Messages;
import com.leclowndu93150.hyssentials.manager.TpaManager;
import com.leclowndu93150.hyssentials.util.ChatUtil;
import javax.annotation.Nonnull;

public class TpcancelCommand extends AbstractPlayerCommand {
    private final TpaManager tpaManager;

    public TpcancelCommand(@Nonnull TpaManager tpaManager) {
        super("tpcancel", "Cancel your pending teleport request");
        this.tpaManager = tpaManager;
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store,
                          @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        boolean cancelled = tpaManager.cancelRequest(playerRef.getUuid());
        if (cancelled) {
            context.sendMessage(ChatUtil.parse(Messages.SUCCESS_TPA_CANCELLED));
        } else {
            context.sendMessage(ChatUtil.parse(Messages.ERROR_NO_TPA_TO_CANCEL));
        }
    }
}
