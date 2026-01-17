package com.leclowndu93150.hyssentials.commands.warp;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.leclowndu93150.hyssentials.data.CommandSettings;
import com.leclowndu93150.hyssentials.data.LocationData;
import com.leclowndu93150.hyssentials.gui.WarpListGui;
import com.leclowndu93150.hyssentials.lang.Messages;
import com.leclowndu93150.hyssentials.manager.CooldownManager;
import com.leclowndu93150.hyssentials.manager.RankManager;
import com.leclowndu93150.hyssentials.manager.TeleportWarmupManager;
import com.leclowndu93150.hyssentials.manager.WarpManager;
import com.leclowndu93150.hyssentials.util.ChatUtil;
import com.leclowndu93150.hyssentials.util.Permissions;
import java.util.UUID;
import javax.annotation.Nonnull;

public class WarpCommand extends AbstractPlayerCommand {
    private final WarpManager warpManager;
    private final TeleportWarmupManager warmupManager;
    private final CooldownManager cooldownManager;
    private final RankManager rankManager;

    public WarpCommand(@Nonnull WarpManager warpManager, @Nonnull TeleportWarmupManager warmupManager,
                       @Nonnull CooldownManager cooldownManager, @Nonnull RankManager rankManager) {
        super("warp", "Teleport to a server warp");
        this.warpManager = warpManager;
        this.warmupManager = warmupManager;
        this.cooldownManager = cooldownManager;
        this.rankManager = rankManager;
        this.setAllowsExtraArguments(true);
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store,
                          @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        String input = context.getInputString().trim();
        String[] args = input.split("\\s+");

        if (args.length <= 1) {
            openWarpGui(store, ref, playerRef);
            return;
        }

        String name = args[1];
        teleportToWarp(context, store, ref, playerRef, world, name);
    }

    private void openWarpGui(Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef) {
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) return;

        player.getPageManager().openCustomPage(ref, store,
            new WarpListGui(playerRef, warpManager, warmupManager, cooldownManager, rankManager, CustomPageLifetime.CanDismiss));
    }

    private void teleportToWarp(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref,
                                PlayerRef playerRef, World world, String name) {
        UUID playerUuid = playerRef.getUuid();
        CommandSettings settings = rankManager.getEffectiveSettings(playerRef, CooldownManager.WARP);
        boolean bypassCooldown = Permissions.canBypassCooldown(playerRef);

        if (!settings.isEnabled()) {
            context.sendMessage(ChatUtil.parse(Messages.NO_PERMISSION_WARP));
            return;
        }

        if (!bypassCooldown && cooldownManager.isOnCooldown(playerUuid, CooldownManager.WARP, settings.getCooldownSeconds())) {
            long remaining = cooldownManager.getCooldownRemaining(playerUuid, CooldownManager.WARP, settings.getCooldownSeconds());
            context.sendMessage(ChatUtil.parse(Messages.COOLDOWN_WARP, remaining));
            return;
        }

        LocationData warp = warpManager.getWarp(name);
        if (warp == null) {
            context.sendMessage(ChatUtil.parse(Messages.ERROR_WARP_NOT_FOUND, name));
            return;
        }

        int warmupSeconds = bypassCooldown ? 0 : settings.getWarmupSeconds();
        warmupManager.startWarmup(playerRef, store, ref, world, warp, warmupSeconds, CooldownManager.WARP, "warp '" + name + "'", null);
    }
}
