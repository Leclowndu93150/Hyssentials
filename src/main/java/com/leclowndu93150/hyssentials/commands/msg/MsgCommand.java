package com.leclowndu93150.hyssentials.commands.msg;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.leclowndu93150.hyssentials.lang.Messages;
import com.leclowndu93150.hyssentials.manager.PrivateMessageManager;
import com.leclowndu93150.hyssentials.util.ChatUtil;
import javax.annotation.Nonnull;

public class MsgCommand extends AbstractPlayerCommand {
    private final PrivateMessageManager msgManager;
    private final RequiredArg<String> targetArg = this.withRequiredArg("player", "Player to message", ArgTypes.STRING);

    public MsgCommand(@Nonnull PrivateMessageManager msgManager) {
        super("msg", "Send a private message to a player");
        this.msgManager = msgManager;
        this.addAliases("tell", "whisper", "w", "pm");
        this.setAllowsExtraArguments(true);
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store,
                          @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        String targetName = targetArg.get(context);

        PlayerRef targetPlayer = Universe.get().getPlayerByUsername(targetName, NameMatching.STARTS_WITH_IGNORE_CASE);
        if (targetPlayer == null) {
            context.sendMessage(ChatUtil.parse(Messages.ERROR_PLAYER_NOT_FOUND, targetName));
            return;
        }

        if (targetPlayer.getUuid().equals(playerRef.getUuid())) {
            context.sendMessage(ChatUtil.parse(Messages.ERROR_CANNOT_MESSAGE_SELF));
            return;
        }

        String inputString = context.getInputString().trim();
        String[] parts = inputString.split("\\s+", 3);
        if (parts.length < 3 || parts[2].isBlank()) {
            context.sendMessage(ChatUtil.parse(Messages.USAGE_MSG));
            return;
        }
        String message = parts[2];

        ChatUtil.sendMessage(playerRef, ChatUtil.privateMessageTo(targetPlayer.getUsername(), message));
        ChatUtil.sendMessage(targetPlayer, ChatUtil.privateMessageFrom(playerRef.getUsername(), message));

        msgManager.setLastMessaged(playerRef.getUuid(), targetPlayer.getUuid());
    }
}
