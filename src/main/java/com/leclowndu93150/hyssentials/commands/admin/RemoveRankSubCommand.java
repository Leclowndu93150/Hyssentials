package com.leclowndu93150.hyssentials.commands.admin;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.leclowndu93150.hyssentials.data.Rank;
import com.leclowndu93150.hyssentials.lang.Messages;
import com.leclowndu93150.hyssentials.manager.RankManager;
import com.leclowndu93150.hyssentials.util.ChatUtil;
import com.leclowndu93150.hyssentials.util.Permissions;
import javax.annotation.Nonnull;

public class RemoveRankSubCommand extends AbstractPlayerCommand {
    private final RankManager rankManager;
    private final RequiredArg<String> playerArg = this.withRequiredArg("player", "Target player", ArgTypes.STRING);
    private final RequiredArg<String> rankArg = this.withRequiredArg("rank", "Rank ID", ArgTypes.STRING);

    public RemoveRankSubCommand(@Nonnull RankManager rankManager) {
        super("removerank", "Remove a rank from a player");
        this.rankManager = rankManager;
        this.requirePermission(Permissions.ADMIN_SETRANK);
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store,
                          @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef sender, @Nonnull World world) {
        if (!Permissions.canSetRanks(sender)) {
            context.sendMessage(ChatUtil.parse(Messages.NO_PERMISSION_REMOVERANK));
            return;
        }

        String targetName = playerArg.get(context);
        String rankId = rankArg.get(context);

        PlayerRef targetPlayer = Universe.get().getPlayerByUsername(targetName, NameMatching.STARTS_WITH_IGNORE_CASE);
        if (targetPlayer == null) {
            context.sendMessage(ChatUtil.parse(Messages.ERROR_PLAYER_NOT_FOUND, targetName));
            return;
        }

        Rank rank = rankManager.getRankById(rankId);
        if (rank == null) {
            context.sendMessage(ChatUtil.parse(Messages.ERROR_RANK_NOT_FOUND, rankId));
            return;
        }

        rankManager.revokeRankPermission(targetPlayer.getUuid(), rankId);
        context.sendMessage(ChatUtil.parse(Messages.SUCCESS_RANK_REMOVED,
            rank.getDisplayName(), targetPlayer.getUsername()));
        targetPlayer.sendMessage(ChatUtil.parse(Messages.SUCCESS_YOUR_RANK_REMOVED, rank.getDisplayName()));
    }
}
