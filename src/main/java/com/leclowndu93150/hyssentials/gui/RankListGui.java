package com.leclowndu93150.hyssentials.gui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.leclowndu93150.hyssentials.data.Rank;
import com.leclowndu93150.hyssentials.lang.Messages;
import com.leclowndu93150.hyssentials.manager.RankManager;
import javax.annotation.Nonnull;
import java.util.List;

public class RankListGui extends InteractiveCustomUIPage<RankListGui.RankListData> {

    private final RankManager rankManager;
    private int deleteConfirmIndex = -1;

    public RankListGui(@Nonnull PlayerRef playerRef, @Nonnull RankManager rankManager, @Nonnull CustomPageLifetime lifetime) {
        super(playerRef, lifetime, RankListData.CODEC);
        this.rankManager = rankManager;
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder cmd,
                      @Nonnull UIEventBuilder events, @Nonnull Store<EntityStore> store) {
        cmd.append("Pages/Hyssentials_RankList.ui");

        cmd.set("#TitleLabel.Text", Messages.UI_RANK_LIST_TITLE.get());
        cmd.set("#CreateButton.Text", Messages.UI_BTN_CREATE_NEW.get());
        cmd.set("#CloseButton.Text", Messages.UI_BTN_CLOSE.get());

        events.addEventBinding(CustomUIEventBindingType.Activating, "#CreateButton",
            EventData.of("Action", "Create"), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, "#CloseButton",
            EventData.of("Action", "Close"), false);

        buildRankList(cmd, events);
    }

    private void buildRankList(UICommandBuilder cmd, UIEventBuilder events) {
        cmd.clear("#RankList");
        cmd.appendInline("#Content #RankListContainer", "Group #RankList { LayoutMode: Top; }");

        List<Rank> ranks = rankManager.getAllRanks();
        for (int i = 0; i < ranks.size(); i++) {
            Rank rank = ranks.get(i);

            cmd.append("#RankList", "Pages/Hyssentials_RankEntry.ui");

            cmd.set("#RankList[" + i + "] #RankName.Text", rank.getDisplayName());
            cmd.set("#RankList[" + i + "] #RankId.Text", Messages.UI_LABEL_ID_PREFIX.get() + rank.getId());
            cmd.set("#RankList[" + i + "] #RankPriority.Text", Messages.UI_LABEL_PRIORITY_PREFIX.get() + rank.getPriority());
            cmd.set("#RankList[" + i + "] #RankPermission.Text", rank.getPermission());
            cmd.set("#RankList[" + i + "] #EditButton.Text", Messages.UI_BTN_EDIT.get());

            events.addEventBinding(CustomUIEventBindingType.Activating, "#RankList[" + i + "] #EditButton",
                EventData.of("Action", "Edit:" + rank.getId()), false);

            if (rank.getId().equals("default")) {
                cmd.set("#RankList[" + i + "] #DeleteButton.Visible", false);
            } else if (deleteConfirmIndex == i) {
                cmd.set("#RankList[" + i + "] #DeleteButton.Text", Messages.UI_BTN_DELETE_CONFIRM.get());
                events.addEventBinding(CustomUIEventBindingType.Activating, "#RankList[" + i + "] #DeleteButton",
                    EventData.of("Action", "ConfirmDelete:" + rank.getId()), false);
                events.addEventBinding(CustomUIEventBindingType.MouseExited, "#RankList[" + i + "] #DeleteButton",
                    EventData.of("Action", "CancelDelete"), false);
            } else {
                events.addEventBinding(CustomUIEventBindingType.Activating, "#RankList[" + i + "] #DeleteButton",
                    EventData.of("Action", "Delete:" + i), false);
            }
        }
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store,
                                @Nonnull RankListData data) {
        super.handleDataEvent(ref, store, data);

        if (data.action != null) {
            if (data.action.equals("Close")) {
                this.close();
                return;
            }

            if (data.action.equals("Create")) {
                PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
                Player player = store.getComponent(ref, Player.getComponentType());
                player.getPageManager().openCustomPage(ref, store, new RankEditorGui(playerRef, rankManager, null));
                return;
            }

            if (data.action.startsWith("Edit:")) {
                String rankId = data.action.substring(5);
                Rank rank = rankManager.getRankById(rankId);
                if (rank != null) {
                    PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
                    Player player = store.getComponent(ref, Player.getComponentType());
                    player.getPageManager().openCustomPage(ref, store, new RankEditorGui(playerRef, rankManager, rank));
                }
                return;
            }

            if (data.action.startsWith("Delete:")) {
                int index = Integer.parseInt(data.action.substring(7));
                deleteConfirmIndex = index;
                UICommandBuilder cmd = new UICommandBuilder();
                UIEventBuilder events = new UIEventBuilder();
                buildRankList(cmd, events);
                this.sendUpdate(cmd, events, false);
                return;
            }

            if (data.action.equals("CancelDelete")) {
                deleteConfirmIndex = -1;
                UICommandBuilder cmd = new UICommandBuilder();
                UIEventBuilder events = new UIEventBuilder();
                buildRankList(cmd, events);
                this.sendUpdate(cmd, events, false);
                return;
            }

            if (data.action.startsWith("ConfirmDelete:")) {
                String rankId = data.action.substring(14);
                rankManager.deleteRank(rankId);
                deleteConfirmIndex = -1;
                UICommandBuilder cmd = new UICommandBuilder();
                UIEventBuilder events = new UIEventBuilder();
                buildRankList(cmd, events);
                this.sendUpdate(cmd, events, false);
                return;
            }
        }
    }

    public static class RankListData {
        public static final BuilderCodec<RankListData> CODEC = BuilderCodec.<RankListData>builder(RankListData.class, RankListData::new)
            .addField(new KeyedCodec<>("Action", Codec.STRING), (d, s) -> d.action = s, d -> d.action)
            .build();

        private String action;
    }
}
