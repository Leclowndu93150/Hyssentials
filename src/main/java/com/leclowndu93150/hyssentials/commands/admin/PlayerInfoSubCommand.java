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
import com.leclowndu93150.hyssentials.manager.HomeManager;
import com.leclowndu93150.hyssentials.manager.RankManager;
import com.leclowndu93150.hyssentials.util.ChatUtil;
import com.leclowndu93150.hyssentials.util.Permissions;
import javax.annotation.Nonnull;

public class PlayerInfoSubCommand extends AbstractPlayerCommand {
    private final RankManager rankManager;
    private final HomeManager homeManager;
    private final RequiredArg<String> playerArg = this.withRequiredArg("player", "Target player", ArgTypes.STRING);

    public PlayerInfoSubCommand(@Nonnull RankManager rankManager, @Nonnull HomeManager homeManager) {
        super("playerinfo", "View player rank and stats");
        this.rankManager = rankManager;
        this.homeManager = homeManager;
        this.requirePermission(Permissions.ADMIN_PLAYERINFO);
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store,
                          @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef sender, @Nonnull World world) {
        if (!Permissions.canViewPlayerInfo(sender)) {
            context.sendMessage(ChatUtil.parse(Messages.NO_PERMISSION_PLAYERINFO));
            return;
        }

        String targetName = playerArg.get(context);
        PlayerRef targetPlayer = Universe.get().getPlayerByUsername(targetName, NameMatching.STARTS_WITH_IGNORE_CASE);
        if (targetPlayer == null) {
            context.sendMessage(ChatUtil.parse(Messages.ERROR_PLAYER_NOT_FOUND, targetName));
            return;
        }

        Rank rank = rankManager.getPlayerRank(targetPlayer);
        int homeCount = homeManager.getHomeCount(targetPlayer.getUuid());
        int maxHomes = rankManager.getEffectiveMaxHomes(targetPlayer);

        context.sendMessage(ChatUtil.parse(Messages.INFO_PLAYER_INFO_HEADER, targetPlayer.getUsername()));
        context.sendMessage(ChatUtil.parse(Messages.INFO_PLAYER_INFO_UUID, targetPlayer.getUuid()));
        context.sendMessage(ChatUtil.parse(Messages.INFO_PLAYER_INFO_RANK, rank.getDisplayName(), rank.getId()));
        context.sendMessage(ChatUtil.parse(Messages.INFO_PLAYER_INFO_PERMISSION, rank.getPermission()));
        context.sendMessage(ChatUtil.parse(Messages.INFO_PLAYER_INFO_HOMES, homeCount, maxHomes));
        context.sendMessage(ChatUtil.parse(Messages.INFO_PLAYER_INFO_COOLDOWN, rank.getHomeSettings().getCooldownSeconds()));
        context.sendMessage(ChatUtil.parse(Messages.INFO_PLAYER_INFO_WARMUP, rank.getHomeSettings().getWarmupSeconds()));
    }
}
