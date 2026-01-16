package com.leclowndu93150.hyssentials.commands.msg;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.leclowndu93150.hyssentials.data.AdminChatGroup;
import com.leclowndu93150.hyssentials.manager.AdminChatManager;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class AdminChatCommand extends AbstractPlayerCommand {
    private final AdminChatManager adminChatManager;

    public AdminChatCommand(@Nonnull AdminChatManager adminChatManager) {
        super("a", "Send a message to admin chat");
        this.adminChatManager = adminChatManager;
        this.addAliases("adminchat", "ac");
        this.setAllowsExtraArguments(true);
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store,
                          @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {

        List<AdminChatGroup> playerGroups = adminChatManager.getPlayerGroups(playerRef);
        if (playerGroups.isEmpty()) {
            context.sendMessage(Message.raw("You don't have permission to use admin chat."));
            return;
        }


        String inputString = context.getInputString().trim();
        String[] parts = inputString.split("\\s+", 2);
        if (parts.length < 2 || parts[1].isBlank()) {
            showUsage(context, playerGroups);
            return;
        }

        String remainder = parts[1];
        AdminChatGroup targetGroup;
        String message;


        String[] messageParts = remainder.split("\\s+", 2);
        AdminChatGroup specifiedGroup = adminChatManager.getGroup(messageParts[0]);

        if (specifiedGroup != null && playerGroups.contains(specifiedGroup)) {

            if (messageParts.length < 2 || messageParts[1].isBlank()) {
                context.sendMessage(Message.raw("Usage: /a " + specifiedGroup.getId() + " <message>"));
                return;
            }
            targetGroup = specifiedGroup;
            message = messageParts[1];
        } else if (playerGroups.size() == 1) {
            targetGroup = playerGroups.get(0);
            message = remainder;
        } else {
            targetGroup = playerGroups.get(0);
            message = remainder;
        }

        adminChatManager.broadcast(targetGroup, playerRef, message);
    }

    private void showUsage(@Nonnull CommandContext context, @Nonnull List<AdminChatGroup> groups) {
        if (groups.size() == 1) {
            context.sendMessage(Message.raw("Usage: /a <message>"));
        } else {
            String groupNames = groups.stream()
                .map(AdminChatGroup::getId)
                .collect(Collectors.joining(", "));
            context.sendMessage(Message.raw("Usage: /a [group] <message>"));
            context.sendMessage(Message.raw("Available groups: " + groupNames));
        }
    }
}
