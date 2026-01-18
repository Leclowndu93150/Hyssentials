package com.leclowndu93150.hyssentials.commands.tpa;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.leclowndu93150.hyssentials.data.TpaRequest;
import com.leclowndu93150.hyssentials.lang.Messages;
import com.leclowndu93150.hyssentials.manager.TpaManager;
import com.leclowndu93150.hyssentials.util.ChatUtil;
import java.util.UUID;
import javax.annotation.Nonnull;

public class TpdenyCommand extends AbstractPlayerCommand {
    private final TpaManager tpaManager;

    public TpdenyCommand(@Nonnull TpaManager tpaManager) {
        super("tpdeny", "Deny a pending teleport request");
        this.tpaManager = tpaManager;
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store,
                          @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        UUID targetUuid = playerRef.getUuid();
        int timeoutSeconds = tpaManager.getSettingsForPlayer(playerRef).getTimeoutSeconds();
        TpaRequest request = tpaManager.getRequest(targetUuid, timeoutSeconds);
        if (request == null) {
            context.sendMessage(ChatUtil.parse(Messages.ERROR_NO_PENDING_TPA));
            return;
        }
        tpaManager.denyRequest(targetUuid);
        PlayerRef senderPlayer = Universe.get().getPlayer(request.sender());
        if (senderPlayer != null) {
            senderPlayer.sendMessage(ChatUtil.parse(Messages.SUCCESS_TPA_DENIED_NOTIFY, playerRef.getUsername()));
        }
        context.sendMessage(ChatUtil.parse(Messages.SUCCESS_TPA_DENIED));
    }
}
