package com.leclowndu93150.hyssentials.gui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.leclowndu93150.hyssentials.data.CommandSettings;
import com.leclowndu93150.hyssentials.data.LocationData;
import com.leclowndu93150.hyssentials.lang.Messages;
import com.leclowndu93150.hyssentials.manager.CooldownManager;
import com.leclowndu93150.hyssentials.manager.RankManager;
import com.leclowndu93150.hyssentials.manager.TeleportWarmupManager;
import com.leclowndu93150.hyssentials.manager.WarpManager;
import com.leclowndu93150.hyssentials.util.Permissions;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WarpListGui extends InteractiveCustomUIPage<WarpListGui.WarpListData> {

    private final WarpManager warpManager;
    private final TeleportWarmupManager warmupManager;
    private final CooldownManager cooldownManager;
    private final RankManager rankManager;

    public WarpListGui(@Nonnull PlayerRef playerRef, @Nonnull WarpManager warpManager,
                       @Nonnull TeleportWarmupManager warmupManager, @Nonnull CooldownManager cooldownManager,
                       @Nonnull RankManager rankManager, @Nonnull CustomPageLifetime lifetime) {
        super(playerRef, lifetime, WarpListData.CODEC);
        this.warpManager = warpManager;
        this.warmupManager = warmupManager;
        this.cooldownManager = cooldownManager;
        this.rankManager = rankManager;
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder cmd,
                      @Nonnull UIEventBuilder events, @Nonnull Store<EntityStore> store) {
        cmd.append("Pages/Hyssentials_WarpList.ui");

        cmd.set("#TitleLabel.Text", Messages.UI_WARP_LIST_TITLE.get());

        events.addEventBinding(CustomUIEventBindingType.Activating, "#CloseButton",
            EventData.of("Action", "Close"), false);

        buildWarpList(cmd, events);
    }

    private void buildWarpList(UICommandBuilder cmd, UIEventBuilder events) {
        cmd.clear("#WarpList");
        cmd.appendInline("#Content #WarpListContainer", "Group #WarpList { LayoutMode: Top; }");

        List<String> warpNames = new ArrayList<>(warpManager.getWarpNames());
        warpNames.sort(String::compareToIgnoreCase);

        for (int i = 0; i < warpNames.size(); i++) {
            String warpName = warpNames.get(i);
            LocationData warp = warpManager.getWarp(warpName);
            if (warp == null) continue;

            cmd.append("#WarpList", "Pages/Hyssentials_WarpEntry.ui");

            cmd.set("#WarpList[" + i + "] #WarpName.Text", warpName);
            cmd.set("#WarpList[" + i + "] #WarpWorld.Text", warp.worldName());
            cmd.set("#WarpList[" + i + "] #WarpCoords.Text",
                String.format("%.0f, %.0f, %.0f", warp.x(), warp.y(), warp.z()));

            events.addEventBinding(CustomUIEventBindingType.Activating, "#WarpList[" + i + "] #TeleportButton",
                EventData.of("Action", "Teleport:" + warpName), false);
        }
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store,
                                @Nonnull WarpListData data) {
        super.handleDataEvent(ref, store, data);

        if (data.action != null) {
            if (data.action.equals("Close")) {
                this.close();
                return;
            }

            if (data.action.startsWith("Teleport:")) {
                String warpName = data.action.substring(9);
                handleTeleport(ref, store, warpName);
                return;
            }
        }
    }

    private void handleTeleport(Ref<EntityStore> ref, Store<EntityStore> store, String warpName) {
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        Player player = store.getComponent(ref, Player.getComponentType());
        if (playerRef == null || player == null) return;

        UUID playerUuid = playerRef.getUuid();
        CommandSettings settings = rankManager.getEffectiveSettings(playerRef, CooldownManager.WARP);
        boolean bypassCooldown = Permissions.canBypassCooldown(playerRef);

        if (!settings.isEnabled()) {
            playerRef.sendMessage(Message.raw(Messages.UI_ERROR_NO_WARP_PERMISSION.get()));
            return;
        }

        if (!bypassCooldown && cooldownManager.isOnCooldown(playerUuid, CooldownManager.WARP, settings.getCooldownSeconds())) {
            long remaining = cooldownManager.getCooldownRemaining(playerUuid, CooldownManager.WARP, settings.getCooldownSeconds());
            playerRef.sendMessage(Message.raw(Messages.UI_ERROR_WARP_COOLDOWN.get(remaining)));
            return;
        }

        LocationData warp = warpManager.getWarp(warpName);
        if (warp == null) {
            playerRef.sendMessage(Message.raw(Messages.UI_ERROR_WARP_NOT_FOUND.get(warpName)));
            return;
        }

        World world = store.getExternalData().getWorld();
        int warmupSeconds = bypassCooldown ? 0 : settings.getWarmupSeconds();

        this.close();
        warmupManager.startWarmup(playerRef, store, ref, world, warp, warmupSeconds, CooldownManager.WARP, "warp '" + warpName + "'", null);
    }

    public static class WarpListData {
        public static final BuilderCodec<WarpListData> CODEC = BuilderCodec.<WarpListData>builder(WarpListData.class, WarpListData::new)
            .addField(new KeyedCodec<>("Action", Codec.STRING), (d, s) -> d.action = s, d -> d.action)
            .build();

        private String action;
    }
}
