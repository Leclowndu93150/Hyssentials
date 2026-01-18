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
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.leclowndu93150.hyssentials.data.TpaRequest;
import com.leclowndu93150.hyssentials.data.TpaSettings;
import com.leclowndu93150.hyssentials.lang.Messages;
import com.leclowndu93150.hyssentials.manager.RankManager;
import com.leclowndu93150.hyssentials.manager.TpaManager;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class TpaPlayerListGui extends InteractiveCustomUIPage<TpaPlayerListGui.TpaListData> {

    private final TpaManager tpaManager;
    private final RankManager rankManager;
    private final UUID senderUuid;
    private final TpaRequest.TpaType tpaType;

    public TpaPlayerListGui(@Nonnull PlayerRef playerRef, @Nonnull TpaManager tpaManager,
                            @Nonnull RankManager rankManager, @Nonnull TpaRequest.TpaType tpaType,
                            @Nonnull CustomPageLifetime lifetime) {
        super(playerRef, lifetime, TpaListData.CODEC);
        this.tpaManager = tpaManager;
        this.rankManager = rankManager;
        this.senderUuid = playerRef.getUuid();
        this.tpaType = tpaType;
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder cmd,
                      @Nonnull UIEventBuilder events, @Nonnull Store<EntityStore> store) {
        cmd.append("Pages/Hyssentials_TpaList.ui");

        String title = tpaType == TpaRequest.TpaType.TPA ? Messages.UI_TPA_LIST_TITLE_TPA.get() : Messages.UI_TPA_LIST_TITLE_TPAHERE.get();
        cmd.set("#TitleLabel.Text", title);

        events.addEventBinding(CustomUIEventBindingType.Activating, "#CloseButton",
            EventData.of("Action", "Close"), false);

        buildPlayerList(cmd, events);
    }

    private void buildPlayerList(UICommandBuilder cmd, UIEventBuilder events) {
        cmd.clear("#PlayerList");
        cmd.appendInline("#Content #PlayerListContainer", "Group #PlayerList { LayoutMode: Top; }");

        List<PlayerRef> onlinePlayers = new ArrayList<>();
        for (PlayerRef player : Universe.get().getPlayers()) {
            if (!player.getUuid().equals(senderUuid)) {
                onlinePlayers.add(player);
            }
        }

        onlinePlayers.sort(Comparator.comparing(PlayerRef::getUsername, String.CASE_INSENSITIVE_ORDER));

        for (int i = 0; i < onlinePlayers.size(); i++) {
            PlayerRef targetPlayer = onlinePlayers.get(i);

            cmd.append("#PlayerList", "Pages/Hyssentials_TpaEntry.ui");

            cmd.set("#PlayerList[" + i + "] #PlayerName.Text", targetPlayer.getUsername());

            events.addEventBinding(CustomUIEventBindingType.Activating, "#PlayerList[" + i + "] #RequestButton",
                EventData.of("Action", "Request:" + targetPlayer.getUuid().toString()), false);
        }

        if (onlinePlayers.isEmpty()) {
            cmd.appendInline("#PlayerList", "Label { Text: \"" + Messages.UI_TPA_NO_PLAYERS.get() + "\"; Style: (FontSize: 14, TextColor: #ffffff(0.5), HorizontalAlignment: Center); Padding: (Top: 20); }");
        }
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store,
                                @Nonnull TpaListData data) {
        super.handleDataEvent(ref, store, data);

        if (data.action != null) {
            if (data.action.equals("Close")) {
                this.close();
                return;
            }

            if (data.action.startsWith("Request:")) {
                String targetUuidStr = data.action.substring(8);
                handleRequest(ref, store, targetUuidStr);
                return;
            }
        }
    }

    private void handleRequest(Ref<EntityStore> ref, Store<EntityStore> store, String targetUuidStr) {
        PlayerRef senderPlayer = store.getComponent(ref, PlayerRef.getComponentType());
        if (senderPlayer == null) return;

        TpaSettings settings = rankManager.getEffectiveTpaSettings(senderPlayer);

        if (!settings.isEnabled()) {
            String cmdName = tpaType == TpaRequest.TpaType.TPA ? "/tpa" : "/tpahere";
            senderPlayer.sendMessage(Message.raw(Messages.UI_ERROR_NO_TPA_PERMISSION.get(cmdName)));
            return;
        }

        if (tpaManager.isOnCooldown(senderUuid, settings.getCooldownSeconds())) {
            long remaining = tpaManager.getCooldownRemaining(senderUuid, settings.getCooldownSeconds());
            senderPlayer.sendMessage(Message.raw(Messages.UI_ERROR_TPA_COOLDOWN.get(remaining)));
            return;
        }

        UUID targetUuid;
        try {
            targetUuid = UUID.fromString(targetUuidStr);
        } catch (IllegalArgumentException e) {
            senderPlayer.sendMessage(Message.raw(Messages.UI_ERROR_INVALID_PLAYER.get()));
            return;
        }

        PlayerRef targetPlayer = Universe.get().getPlayer(targetUuid);
        if (targetPlayer == null) {
            senderPlayer.sendMessage(Message.raw(Messages.UI_ERROR_PLAYER_OFFLINE.get()));
            return;
        }

        boolean sent = tpaManager.sendRequest(senderUuid, targetUuid, tpaType, settings.getTimeoutSeconds());
        if (!sent) {
            senderPlayer.sendMessage(Message.raw(Messages.UI_ERROR_ALREADY_PENDING.get()));
            return;
        }

        tpaManager.setCooldown(senderUuid);

        this.close();

        senderPlayer.sendMessage(Message.raw(Messages.UI_SUCCESS_TPA_SENT.get(targetPlayer.getUsername())));

        String requestMsg = tpaType == TpaRequest.TpaType.TPA
            ? Messages.UI_TPA_REQUEST_RECEIVED.get(senderPlayer.getUsername(), settings.getTimeoutSeconds())
            : Messages.UI_TPAHERE_REQUEST_RECEIVED.get(senderPlayer.getUsername(), settings.getTimeoutSeconds());
        targetPlayer.sendMessage(Message.raw(requestMsg));
    }

    public static class TpaListData {
        public static final BuilderCodec<TpaListData> CODEC = BuilderCodec.<TpaListData>builder(TpaListData.class, TpaListData::new)
            .addField(new KeyedCodec<>("Action", Codec.STRING), (d, s) -> d.action = s, d -> d.action)
            .build();

        private String action;
    }
}
