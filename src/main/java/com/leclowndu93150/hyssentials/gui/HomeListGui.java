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
import com.leclowndu93150.hyssentials.manager.HomeManager;
import com.leclowndu93150.hyssentials.manager.RankManager;
import com.leclowndu93150.hyssentials.manager.TeleportWarmupManager;
import com.leclowndu93150.hyssentials.util.Permissions;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HomeListGui extends InteractiveCustomUIPage<HomeListGui.HomeListData> {

    private final HomeManager homeManager;
    private final TeleportWarmupManager warmupManager;
    private final CooldownManager cooldownManager;
    private final RankManager rankManager;
    private final UUID playerUuid;
    private int deleteConfirmIndex = -1;

    public HomeListGui(@Nonnull PlayerRef playerRef, @Nonnull HomeManager homeManager,
                       @Nonnull TeleportWarmupManager warmupManager, @Nonnull CooldownManager cooldownManager,
                       @Nonnull RankManager rankManager, @Nonnull CustomPageLifetime lifetime) {
        super(playerRef, lifetime, HomeListData.CODEC);
        this.homeManager = homeManager;
        this.warmupManager = warmupManager;
        this.cooldownManager = cooldownManager;
        this.rankManager = rankManager;
        this.playerUuid = playerRef.getUuid();
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder cmd,
                      @Nonnull UIEventBuilder events, @Nonnull Store<EntityStore> store) {
        cmd.append("Pages/Hyssentials_HomeList.ui");

        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        int count = homeManager.getHomeCount(playerUuid);
        int max = rankManager.getEffectiveMaxHomes(playerRef);
        cmd.set("#TitleLabel.Text", Messages.UI_HOME_LIST_TITLE_COUNT.get(count, max));

        events.addEventBinding(CustomUIEventBindingType.Activating, "#CloseButton",
            EventData.of("Action", "Close"), false);

        buildHomeList(cmd, events);
    }

    private void buildHomeList(UICommandBuilder cmd, UIEventBuilder events) {
        cmd.clear("#HomeList");
        cmd.appendInline("#Content #HomeListContainer", "Group #HomeList { LayoutMode: Top; }");

        List<String> homeNames = new ArrayList<>(homeManager.getHomeNames(playerUuid));
        homeNames.sort(String::compareToIgnoreCase);

        for (int i = 0; i < homeNames.size(); i++) {
            String homeName = homeNames.get(i);
            LocationData home = homeManager.getHome(playerUuid, homeName);
            if (home == null) continue;

            cmd.append("#HomeList", "Pages/Hyssentials_HomeEntry.ui");

            cmd.set("#HomeList[" + i + "] #HomeName.Text", homeName);
            cmd.set("#HomeList[" + i + "] #HomeWorld.Text", home.worldName());
            cmd.set("#HomeList[" + i + "] #HomeCoords.Text",
                String.format("%.0f, %.0f, %.0f", home.x(), home.y(), home.z()));

            events.addEventBinding(CustomUIEventBindingType.Activating, "#HomeList[" + i + "] #TeleportButton",
                EventData.of("Action", "Teleport:" + homeName), false);

            if (deleteConfirmIndex == i) {
                cmd.set("#HomeList[" + i + "] #DeleteButton.Text", Messages.UI_BTN_DELETE_CONFIRM.get());
                events.addEventBinding(CustomUIEventBindingType.Activating, "#HomeList[" + i + "] #DeleteButton",
                    EventData.of("Action", "ConfirmDelete:" + homeName), false);
                events.addEventBinding(CustomUIEventBindingType.MouseExited, "#HomeList[" + i + "] #DeleteButton",
                    EventData.of("Action", "CancelDelete"), false);
            } else {
                events.addEventBinding(CustomUIEventBindingType.Activating, "#HomeList[" + i + "] #DeleteButton",
                    EventData.of("Action", "Delete:" + i), false);
            }
        }
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store,
                                @Nonnull HomeListData data) {
        super.handleDataEvent(ref, store, data);

        if (data.action != null) {
            if (data.action.equals("Close")) {
                this.close();
                return;
            }

            if (data.action.startsWith("Teleport:")) {
                String homeName = data.action.substring(9);
                handleTeleport(ref, store, homeName);
                return;
            }

            if (data.action.startsWith("Delete:")) {
                int index = Integer.parseInt(data.action.substring(7));
                deleteConfirmIndex = index;
                refreshList();
                return;
            }

            if (data.action.equals("CancelDelete")) {
                deleteConfirmIndex = -1;
                refreshList();
                return;
            }

            if (data.action.startsWith("ConfirmDelete:")) {
                String homeName = data.action.substring(14);
                homeManager.deleteHome(playerUuid, homeName);
                deleteConfirmIndex = -1;
                refreshListWithTitle(ref, store);
                return;
            }
        }
    }

    private void refreshList() {
        UICommandBuilder cmd = new UICommandBuilder();
        UIEventBuilder events = new UIEventBuilder();
        buildHomeList(cmd, events);
        this.sendUpdate(cmd, events, false);
    }

    private void refreshListWithTitle(Ref<EntityStore> ref, Store<EntityStore> store) {
        UICommandBuilder cmd = new UICommandBuilder();
        UIEventBuilder events = new UIEventBuilder();

        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        int count = homeManager.getHomeCount(playerUuid);
        int max = rankManager.getEffectiveMaxHomes(playerRef);
        cmd.set("#TitleLabel.Text", Messages.UI_HOME_LIST_TITLE_COUNT.get(count, max));

        buildHomeList(cmd, events);
        this.sendUpdate(cmd, events, false);
    }

    private void handleTeleport(Ref<EntityStore> ref, Store<EntityStore> store, String homeName) {
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        Player player = store.getComponent(ref, Player.getComponentType());
        if (playerRef == null || player == null) return;

        CommandSettings settings = rankManager.getEffectiveSettings(playerRef, CooldownManager.HOME);
        boolean bypassCooldown = Permissions.canBypassCooldown(playerRef);

        if (!settings.isEnabled()) {
            playerRef.sendMessage(Message.raw(Messages.UI_ERROR_NO_HOME_PERMISSION.get()));
            return;
        }

        if (!bypassCooldown && cooldownManager.isOnCooldown(playerUuid, CooldownManager.HOME, settings.getCooldownSeconds())) {
            long remaining = cooldownManager.getCooldownRemaining(playerUuid, CooldownManager.HOME, settings.getCooldownSeconds());
            playerRef.sendMessage(Message.raw(Messages.UI_ERROR_HOME_COOLDOWN.get(remaining)));
            return;
        }

        LocationData home = homeManager.getHome(playerUuid, homeName);
        if (home == null) {
            playerRef.sendMessage(Message.raw(Messages.UI_ERROR_HOME_NOT_FOUND.get(homeName)));
            return;
        }

        World world = store.getExternalData().getWorld();
        int warmupSeconds = bypassCooldown ? 0 : settings.getWarmupSeconds();

        this.close();
        warmupManager.startWarmup(playerRef, store, ref, world, home, warmupSeconds, CooldownManager.HOME, "home '" + homeName + "'", null);
    }

    public static class HomeListData {
        public static final BuilderCodec<HomeListData> CODEC = BuilderCodec.<HomeListData>builder(HomeListData.class, HomeListData::new)
            .addField(new KeyedCodec<>("Action", Codec.STRING), (d, s) -> d.action = s, d -> d.action)
            .build();

        private String action;
    }
}
