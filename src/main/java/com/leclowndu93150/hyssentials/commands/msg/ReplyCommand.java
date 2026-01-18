package com.leclowndu93150.hyssentials.commands.msg;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.leclowndu93150.hyssentials.lang.Messages;
import com.leclowndu93150.hyssentials.manager.PrivateMessageManager;
import com.leclowndu93150.hyssentials.util.ChatUtil;
import java.util.UUID;
import javax.annotation.Nonnull;

public class ReplyCommand extends AbstractPlayerCommand {
    private final PrivateMessageManager msgManager;

    public ReplyCommand(@Nonnull PrivateMessageManager msgManager) {
        super("r", "Reply to the last player who messaged you");
        this.msgManager = msgManager;
        this.addAliases("reply");
        this.setAllowsExtraArguments(true);
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store,
                          @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {

        String inputString = context.getInputString().trim();
        String[] parts = inputString.split("\\s+", 2);
        if (parts.length < 2 || parts[1].isBlank()) {
            context.sendMessage(ChatUtil.parse(Messages.USAGE_REPLY));
            return;
        }
        String message = parts[1];

        UUID lastUuid = msgManager.getLastMessaged(playerRef.getUuid());
        if (lastUuid == null) {
            context.sendMessage(ChatUtil.parse(Messages.ERROR_NO_REPLY_TARGET));
            return;
        }

        PlayerRef targetPlayer = Universe.get().getPlayer(lastUuid);
        if (targetPlayer == null) {
            context.sendMessage(ChatUtil.parse(Messages.ERROR_REPLY_TARGET_OFFLINE));
            return;
        }

        ChatUtil.sendMessage(playerRef, ChatUtil.privateMessageTo(targetPlayer.getUsername(), message));
        ChatUtil.sendMessage(targetPlayer, ChatUtil.privateMessageFrom(playerRef.getUsername(), message));

        msgManager.setLastMessaged(playerRef.getUuid(), targetPlayer.getUuid());
    }
}
