package com.leclowndu93150.hyssentials.gui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.ui.DropdownEntryInfo;
import com.hypixel.hytale.server.core.ui.LocalizableString;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.logger.HytaleLogger;
import com.leclowndu93150.hyssentials.data.Rank;
import com.leclowndu93150.hyssentials.lang.Messages;
import com.leclowndu93150.hyssentials.manager.RankManager;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class PlayerRankAssignGui extends InteractiveCustomUIPage<PlayerRankAssignGui.AssignData> {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private final RankManager rankManager;
    private String searchQuery = "";
    private List<String> visiblePlayers = new ArrayList<>();
    private final Map<String, String> selectedRanks = new HashMap<>();

    public PlayerRankAssignGui(@Nonnull PlayerRef playerRef, @Nonnull RankManager rankManager, @Nonnull CustomPageLifetime lifetime) {
        super(playerRef, lifetime, AssignData.CODEC);
        this.rankManager = rankManager;
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder cmd,
                      @Nonnull UIEventBuilder events, @Nonnull Store<EntityStore> store) {
        cmd.append("Pages/Hyssentials_PlayerAssign.ui");

        cmd.set("#TitleLabel.Text", Messages.UI_PLAYER_ASSIGN_TITLE.get());
        cmd.set("#SearchInput.Value", this.searchQuery);
        events.addEventBinding(CustomUIEventBindingType.ValueChanged, "#SearchInput",
            EventData.of("@SearchQuery", "#SearchInput.Value"), false);

        buildPlayerList(cmd, events, store);
    }

    private void buildPlayerList(UICommandBuilder cmd, UIEventBuilder events, Store<EntityStore> store) {
        cmd.clear("#PlayerList");

        List<PlayerRef> allPlayers = Universe.get().getPlayers();
        visiblePlayers.clear();

        for (PlayerRef player : allPlayers) {
            String name = player.getUsername();
            if (searchQuery.isEmpty() || name.toLowerCase().contains(searchQuery.toLowerCase())) {
                visiblePlayers.add(name);
            }
        }

        List<DropdownEntryInfo> rankEntries = rankManager.getAllRanks().stream()
            .map(r -> new DropdownEntryInfo(LocalizableString.fromString(r.getDisplayName()), r.getId()))
            .collect(Collectors.toList());

        PermissionsModule perms = PermissionsModule.get();

        for (int i = 0; i < visiblePlayers.size(); i++) {
            String playerName = visiblePlayers.get(i);
            PlayerRef playerRef = Universe.get().getPlayer(playerName, NameMatching.EXACT);
            if (playerRef == null) continue;

            UUID playerUuid = playerRef.getUuid();
            Rank currentRank = rankManager.getPlayerRank(playerUuid);
            List<String> playerRanks = getPlayerRanks(playerUuid, perms);

            cmd.append("#PlayerList", "Pages/Hyssentials_PlayerAssignEntry.ui");

            cmd.set("#PlayerList[" + i + "] #PlayerName.Text", playerName);
            cmd.set("#PlayerList[" + i + "] #CurrentRank.Text", Messages.UI_LABEL_CURRENT_RANK.get() + currentRank.getDisplayName());

            String selectedRank = selectedRanks.getOrDefault(playerName, currentRank.getId());
            cmd.set("#PlayerList[" + i + "] #RankDropdown.Entries", rankEntries);
            cmd.set("#PlayerList[" + i + "] #RankDropdown.Value", selectedRank);

            events.addEventBinding(CustomUIEventBindingType.ValueChanged, "#PlayerList[" + i + "] #RankDropdown",
                EventData.of("Action", "SelectRank:" + playerName)
                    .append("@SelectedRank", "#PlayerList[" + i + "] #RankDropdown.Value"), false);

            events.addEventBinding(CustomUIEventBindingType.Activating, "#PlayerList[" + i + "] #AddButton",
                EventData.of("Action", "AddRank:" + playerName), false);

            events.addEventBinding(CustomUIEventBindingType.Activating, "#PlayerList[" + i + "] #RemoveButton",
                EventData.of("Action", "RemoveRank:" + playerName), false);

            if (playerRanks.size() > 1 || (playerRanks.size() == 1 && !playerRanks.get(0).equals("default"))) {
                cmd.set("#PlayerList[" + i + "] #RanksList.Visible", true);
                cmd.set("#PlayerList[" + i + "] #RanksText.Text", String.join(", ", playerRanks));
            }
        }
    }

    private List<String> getPlayerRanks(UUID playerUuid, PermissionsModule perms) {
        List<String> ranks = new ArrayList<>();
        for (Rank rank : rankManager.getAllRanks()) {
            if (perms.hasPermission(playerUuid, rank.getPermission())) {
                ranks.add(rank.getDisplayName());
            }
        }
        if (ranks.isEmpty()) {
            Rank defaultRank = rankManager.getRankById(rankManager.getDefaultRankId());
            if (defaultRank != null) {
                ranks.add(defaultRank.getDisplayName());
            }
        }
        return ranks;
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store,
                                @Nonnull AssignData data) {
        super.handleDataEvent(ref, store, data);

        LOGGER.atInfo().log("[PlayerRankAssignGui] handleDataEvent called - action: %s, selectedRank: %s, searchQuery: %s",
            data.action, data.selectedRank, data.searchQuery);

        if (data.searchQuery != null) {
            this.searchQuery = data.searchQuery.trim();
            UICommandBuilder cmd = new UICommandBuilder();
            UIEventBuilder events = new UIEventBuilder();
            buildPlayerList(cmd, events, store);
            this.sendUpdate(cmd, events, false);
            return;
        }

        if (data.action != null) {
            if (data.action.startsWith("SelectRank:") && data.selectedRank != null) {
                String playerName = data.action.substring(11);
                selectedRanks.put(playerName, data.selectedRank);
                return;
            }

            if (data.action.startsWith("AddRank:")) {
                String playerName = data.action.substring(8);
                LOGGER.atInfo().log("[PlayerRankAssignGui] AddRank for player: %s", playerName);
                PlayerRef targetPlayer = Universe.get().getPlayer(playerName, NameMatching.EXACT);
                if (targetPlayer == null) {
                    LOGGER.atWarning().log("[PlayerRankAssignGui] Player not found: %s", playerName);
                    return;
                }

                UUID targetUuid = targetPlayer.getUuid();
                String rankToUse = selectedRanks.get(playerName);
                if (rankToUse == null) {
                    rankToUse = rankManager.getPlayerRank(targetUuid).getId();
                }
                LOGGER.atInfo().log("[PlayerRankAssignGui] Granting rank %s to %s", rankToUse, playerName);
                rankManager.grantRankPermission(targetUuid, rankToUse);

                UICommandBuilder cmd = new UICommandBuilder();
                UIEventBuilder events = new UIEventBuilder();
                buildPlayerList(cmd, events, store);
                this.sendUpdate(cmd, events, false);
                return;
            }

            if (data.action.startsWith("RemoveRank:")) {
                String playerName = data.action.substring(11);
                LOGGER.atInfo().log("[PlayerRankAssignGui] RemoveRank for player: %s", playerName);
                PlayerRef targetPlayer = Universe.get().getPlayer(playerName, NameMatching.EXACT);
                if (targetPlayer == null) {
                    LOGGER.atWarning().log("[PlayerRankAssignGui] Player not found: %s", playerName);
                    return;
                }

                UUID targetUuid = targetPlayer.getUuid();
                String rankToUse = selectedRanks.get(playerName);
                if (rankToUse == null) {
                    rankToUse = rankManager.getPlayerRank(targetUuid).getId();
                }
                LOGGER.atInfo().log("[PlayerRankAssignGui] Revoking rank %s from %s", rankToUse, playerName);
                if (!rankToUse.equals("default")) {
                    rankManager.revokeRankPermission(targetUuid, rankToUse);
                    LOGGER.atInfo().log("[PlayerRankAssignGui] After revoke, player ranks: %s",
                        getPlayerRanks(targetUuid, PermissionsModule.get()));
                } else {
                    LOGGER.atInfo().log("[PlayerRankAssignGui] Cannot remove default rank");
                }

                UICommandBuilder cmd = new UICommandBuilder();
                UIEventBuilder events = new UIEventBuilder();
                buildPlayerList(cmd, events, store);
                LOGGER.atInfo().log("[PlayerRankAssignGui] Sending UI update");
                this.sendUpdate(cmd, events, false);
                return;
            }
        }
    }

    public static class AssignData {
        public static final BuilderCodec<AssignData> CODEC = BuilderCodec.<AssignData>builder(AssignData.class, AssignData::new)
            .addField(new KeyedCodec<>("Action", Codec.STRING), (d, s) -> d.action = s, d -> d.action)
            .addField(new KeyedCodec<>("@SelectedRank", Codec.STRING), (d, s) -> d.selectedRank = s, d -> d.selectedRank)
            .addField(new KeyedCodec<>("@SearchQuery", Codec.STRING), (d, s) -> d.searchQuery = s, d -> d.searchQuery)
            .build();

        private String action;
        private String selectedRank;
        private String searchQuery;
    }
}
