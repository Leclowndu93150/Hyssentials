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
import com.leclowndu93150.hyssentials.data.CommandSettings;
import com.leclowndu93150.hyssentials.data.Rank;
import com.leclowndu93150.hyssentials.data.TpaSettings;
import com.leclowndu93150.hyssentials.lang.Messages;
import com.leclowndu93150.hyssentials.manager.RankManager;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class RankEditorGui extends InteractiveCustomUIPage<RankEditorGui.EditorData> {

    private final RankManager rankManager;
    private final boolean isNewRank;

    private String id;
    private String displayName;
    private int priority;
    private int maxHomes;

    private boolean homeEnabled;
    private int homeCooldown;
    private int homeWarmup;

    private boolean warpEnabled;
    private int warpCooldown;
    private int warpWarmup;

    private boolean spawnEnabled;
    private int spawnCooldown;
    private int spawnWarmup;

    private boolean backEnabled;
    private int backCooldown;
    private int backWarmup;

    private boolean tpaEnabled;
    private int tpaCooldown;
    private int tpaWarmup;
    private int tpaTimeout;

    private boolean rtpEnabled;
    private int rtpCooldown;
    private int rtpWarmup;

    private List<String> grantedPermissions;
    private String newPermissionInput = "";

    public RankEditorGui(@Nonnull PlayerRef playerRef, @Nonnull RankManager rankManager, @Nullable Rank existingRank) {
        super(playerRef, CustomPageLifetime.CanDismiss, EditorData.CODEC);
        this.rankManager = rankManager;
        this.isNewRank = existingRank == null;

        if (existingRank != null) {
            this.id = existingRank.getId();
            this.displayName = existingRank.getDisplayName();
            this.priority = existingRank.getPriority();
            this.maxHomes = existingRank.getMaxHomes();

            this.homeEnabled = existingRank.getHomeSettings().isEnabled();
            this.homeCooldown = existingRank.getHomeSettings().getCooldownSeconds();
            this.homeWarmup = existingRank.getHomeSettings().getWarmupSeconds();

            this.warpEnabled = existingRank.getWarpSettings().isEnabled();
            this.warpCooldown = existingRank.getWarpSettings().getCooldownSeconds();
            this.warpWarmup = existingRank.getWarpSettings().getWarmupSeconds();

            this.spawnEnabled = existingRank.getSpawnSettings().isEnabled();
            this.spawnCooldown = existingRank.getSpawnSettings().getCooldownSeconds();
            this.spawnWarmup = existingRank.getSpawnSettings().getWarmupSeconds();

            this.backEnabled = existingRank.getBackSettings().isEnabled();
            this.backCooldown = existingRank.getBackSettings().getCooldownSeconds();
            this.backWarmup = existingRank.getBackSettings().getWarmupSeconds();

            this.tpaEnabled = existingRank.getTpaSettings().isEnabled();
            this.tpaCooldown = existingRank.getTpaSettings().getCooldownSeconds();
            this.tpaWarmup = existingRank.getTpaSettings().getWarmupSeconds();
            this.tpaTimeout = existingRank.getTpaSettings().getTimeoutSeconds();

            this.rtpEnabled = existingRank.getRtpSettings().isEnabled();
            this.rtpCooldown = existingRank.getRtpSettings().getCooldownSeconds();
            this.rtpWarmup = existingRank.getRtpSettings().getWarmupSeconds();

            this.grantedPermissions = new ArrayList<>(existingRank.getGrantedPermissions());
        } else {
            this.id = "";
            this.displayName = "";
            this.priority = 0;
            this.maxHomes = 5;

            CommandSettings defaults = CommandSettings.defaultSettings();
            this.homeEnabled = defaults.isEnabled();
            this.homeCooldown = defaults.getCooldownSeconds();
            this.homeWarmup = defaults.getWarmupSeconds();

            this.warpEnabled = defaults.isEnabled();
            this.warpCooldown = defaults.getCooldownSeconds();
            this.warpWarmup = defaults.getWarmupSeconds();

            this.spawnEnabled = defaults.isEnabled();
            this.spawnCooldown = defaults.getCooldownSeconds();
            this.spawnWarmup = defaults.getWarmupSeconds();

            this.backEnabled = defaults.isEnabled();
            this.backCooldown = defaults.getCooldownSeconds();
            this.backWarmup = defaults.getWarmupSeconds();

            TpaSettings tpaDefaults = TpaSettings.defaultSettings();
            this.tpaEnabled = tpaDefaults.isEnabled();
            this.tpaCooldown = tpaDefaults.getCooldownSeconds();
            this.tpaWarmup = tpaDefaults.getWarmupSeconds();
            this.tpaTimeout = tpaDefaults.getTimeoutSeconds();

            CommandSettings rtpDefaults = CommandSettings.rtpDefaultSettings();
            this.rtpEnabled = rtpDefaults.isEnabled();
            this.rtpCooldown = rtpDefaults.getCooldownSeconds();
            this.rtpWarmup = rtpDefaults.getWarmupSeconds();

            this.grantedPermissions = new ArrayList<>();
        }
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder cmd,
                      @Nonnull UIEventBuilder events, @Nonnull Store<EntityStore> store) {
        cmd.append("Pages/Hyssentials_RankEditor.ui");

        cmd.set("#TitleLabel.Text", isNewRank ? Messages.UI_RANK_EDITOR_TITLE_NEW.get() : Messages.UI_RANK_EDITOR_TITLE_EDIT.get(displayName));

        cmd.set("#IdField.Value", id);
        cmd.set("#DisplayNameField.Value", displayName);
        cmd.set("#PriorityField.Value", String.valueOf(priority));
        cmd.set("#MaxHomesField.Value", String.valueOf(maxHomes));

        cmd.set("#HomeEnabled #CheckBox.Value", homeEnabled);
        cmd.set("#HomeCooldownField.Value", String.valueOf(homeCooldown));
        cmd.set("#HomeWarmupField.Value", String.valueOf(homeWarmup));

        cmd.set("#WarpEnabled #CheckBox.Value", warpEnabled);
        cmd.set("#WarpCooldownField.Value", String.valueOf(warpCooldown));
        cmd.set("#WarpWarmupField.Value", String.valueOf(warpWarmup));

        cmd.set("#SpawnEnabled #CheckBox.Value", spawnEnabled);
        cmd.set("#SpawnCooldownField.Value", String.valueOf(spawnCooldown));
        cmd.set("#SpawnWarmupField.Value", String.valueOf(spawnWarmup));

        cmd.set("#BackEnabled #CheckBox.Value", backEnabled);
        cmd.set("#BackCooldownField.Value", String.valueOf(backCooldown));
        cmd.set("#BackWarmupField.Value", String.valueOf(backWarmup));

        cmd.set("#TpaEnabled #CheckBox.Value", tpaEnabled);
        cmd.set("#TpaCooldownField.Value", String.valueOf(tpaCooldown));
        cmd.set("#TpaWarmupField.Value", String.valueOf(tpaWarmup));
        cmd.set("#TpaTimeoutField.Value", String.valueOf(tpaTimeout));

        cmd.set("#RtpEnabled #CheckBox.Value", rtpEnabled);
        cmd.set("#RtpCooldownField.Value", String.valueOf(rtpCooldown));
        cmd.set("#RtpWarmupField.Value", String.valueOf(rtpWarmup));

        events.addEventBinding(CustomUIEventBindingType.ValueChanged, "#IdField",
            EventData.of("@Id", "#IdField.Value"), false);
        events.addEventBinding(CustomUIEventBindingType.ValueChanged, "#DisplayNameField",
            EventData.of("@DisplayName", "#DisplayNameField.Value"), false);
        events.addEventBinding(CustomUIEventBindingType.ValueChanged, "#PriorityField",
            EventData.of("@Priority", "#PriorityField.Value"), false);
        events.addEventBinding(CustomUIEventBindingType.ValueChanged, "#MaxHomesField",
            EventData.of("@MaxHomes", "#MaxHomesField.Value"), false);

        events.addEventBinding(CustomUIEventBindingType.ValueChanged, "#HomeEnabled #CheckBox",
            EventData.of("@HomeEnabled", "#HomeEnabled #CheckBox.Value"), false);
        events.addEventBinding(CustomUIEventBindingType.ValueChanged, "#HomeCooldownField",
            EventData.of("@HomeCooldown", "#HomeCooldownField.Value"), false);
        events.addEventBinding(CustomUIEventBindingType.ValueChanged, "#HomeWarmupField",
            EventData.of("@HomeWarmup", "#HomeWarmupField.Value"), false);

        events.addEventBinding(CustomUIEventBindingType.ValueChanged, "#WarpEnabled #CheckBox",
            EventData.of("@WarpEnabled", "#WarpEnabled #CheckBox.Value"), false);
        events.addEventBinding(CustomUIEventBindingType.ValueChanged, "#WarpCooldownField",
            EventData.of("@WarpCooldown", "#WarpCooldownField.Value"), false);
        events.addEventBinding(CustomUIEventBindingType.ValueChanged, "#WarpWarmupField",
            EventData.of("@WarpWarmup", "#WarpWarmupField.Value"), false);

        events.addEventBinding(CustomUIEventBindingType.ValueChanged, "#SpawnEnabled #CheckBox",
            EventData.of("@SpawnEnabled", "#SpawnEnabled #CheckBox.Value"), false);
        events.addEventBinding(CustomUIEventBindingType.ValueChanged, "#SpawnCooldownField",
            EventData.of("@SpawnCooldown", "#SpawnCooldownField.Value"), false);
        events.addEventBinding(CustomUIEventBindingType.ValueChanged, "#SpawnWarmupField",
            EventData.of("@SpawnWarmup", "#SpawnWarmupField.Value"), false);

        events.addEventBinding(CustomUIEventBindingType.ValueChanged, "#BackEnabled #CheckBox",
            EventData.of("@BackEnabled", "#BackEnabled #CheckBox.Value"), false);
        events.addEventBinding(CustomUIEventBindingType.ValueChanged, "#BackCooldownField",
            EventData.of("@BackCooldown", "#BackCooldownField.Value"), false);
        events.addEventBinding(CustomUIEventBindingType.ValueChanged, "#BackWarmupField",
            EventData.of("@BackWarmup", "#BackWarmupField.Value"), false);

        events.addEventBinding(CustomUIEventBindingType.ValueChanged, "#TpaEnabled #CheckBox",
            EventData.of("@TpaEnabled", "#TpaEnabled #CheckBox.Value"), false);
        events.addEventBinding(CustomUIEventBindingType.ValueChanged, "#TpaCooldownField",
            EventData.of("@TpaCooldown", "#TpaCooldownField.Value"), false);
        events.addEventBinding(CustomUIEventBindingType.ValueChanged, "#TpaWarmupField",
            EventData.of("@TpaWarmup", "#TpaWarmupField.Value"), false);
        events.addEventBinding(CustomUIEventBindingType.ValueChanged, "#TpaTimeoutField",
            EventData.of("@TpaTimeout", "#TpaTimeoutField.Value"), false);

        events.addEventBinding(CustomUIEventBindingType.ValueChanged, "#RtpEnabled #CheckBox",
            EventData.of("@RtpEnabled", "#RtpEnabled #CheckBox.Value"), false);
        events.addEventBinding(CustomUIEventBindingType.ValueChanged, "#RtpCooldownField",
            EventData.of("@RtpCooldown", "#RtpCooldownField.Value"), false);
        events.addEventBinding(CustomUIEventBindingType.ValueChanged, "#RtpWarmupField",
            EventData.of("@RtpWarmup", "#RtpWarmupField.Value"), false);

        events.addEventBinding(CustomUIEventBindingType.ValueChanged, "#NewPermissionField",
            EventData.of("@NewPermission", "#NewPermissionField.Value"), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, "#AddPermissionButton",
            EventData.of("Action", "AddPermission"), false);

        buildPermissionsList(cmd, events);

        events.addEventBinding(CustomUIEventBindingType.Activating, "#SaveButton",
            EventData.of("Action", "Save"), false);
        events.addEventBinding(CustomUIEventBindingType.Activating, "#CancelButton",
            EventData.of("Action", "Cancel"), false);
    }

    private void buildPermissionsList(UICommandBuilder cmd, UIEventBuilder events) {
        cmd.clear("#PermissionsList");

        for (int i = 0; i < grantedPermissions.size(); i++) {
            String permission = grantedPermissions.get(i);
            cmd.append("#PermissionsList", "Pages/Hyssentials_PermissionEntry.ui");
            cmd.set("#PermissionsList[" + i + "] #PermissionText.Text", permission);

            events.addEventBinding(CustomUIEventBindingType.Activating,
                "#PermissionsList[" + i + "] #RemovePermButton",
                EventData.of("Action", "RemovePermission:" + permission), false);
        }
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store,
                                @Nonnull EditorData data) {
        super.handleDataEvent(ref, store, data);

        if (data.id != null && isNewRank) this.id = data.id.toLowerCase().replaceAll("[^a-z0-9_]", "");
        if (data.displayName != null) this.displayName = data.displayName;
        if (data.priority != null) this.priority = parseIntSafe(data.priority, 0);
        if (data.maxHomes != null) this.maxHomes = parseIntSafe(data.maxHomes, 5);

        if (data.homeEnabled != null) this.homeEnabled = data.homeEnabled;
        if (data.homeCooldown != null) this.homeCooldown = parseIntSafe(data.homeCooldown, 60);
        if (data.homeWarmup != null) this.homeWarmup = parseIntSafe(data.homeWarmup, 3);

        if (data.warpEnabled != null) this.warpEnabled = data.warpEnabled;
        if (data.warpCooldown != null) this.warpCooldown = parseIntSafe(data.warpCooldown, 60);
        if (data.warpWarmup != null) this.warpWarmup = parseIntSafe(data.warpWarmup, 3);

        if (data.spawnEnabled != null) this.spawnEnabled = data.spawnEnabled;
        if (data.spawnCooldown != null) this.spawnCooldown = parseIntSafe(data.spawnCooldown, 60);
        if (data.spawnWarmup != null) this.spawnWarmup = parseIntSafe(data.spawnWarmup, 3);

        if (data.backEnabled != null) this.backEnabled = data.backEnabled;
        if (data.backCooldown != null) this.backCooldown = parseIntSafe(data.backCooldown, 60);
        if (data.backWarmup != null) this.backWarmup = parseIntSafe(data.backWarmup, 3);

        if (data.tpaEnabled != null) this.tpaEnabled = data.tpaEnabled;
        if (data.tpaCooldown != null) this.tpaCooldown = parseIntSafe(data.tpaCooldown, 30);
        if (data.tpaWarmup != null) this.tpaWarmup = parseIntSafe(data.tpaWarmup, 0);
        if (data.tpaTimeout != null) this.tpaTimeout = parseIntSafe(data.tpaTimeout, 60);

        if (data.rtpEnabled != null) this.rtpEnabled = data.rtpEnabled;
        if (data.rtpCooldown != null) this.rtpCooldown = parseIntSafe(data.rtpCooldown, 300);
        if (data.rtpWarmup != null) this.rtpWarmup = parseIntSafe(data.rtpWarmup, 5);

        if (data.newPermission != null) this.newPermissionInput = data.newPermission.trim();

        if (data.action != null) {
            if (data.action.equals("AddPermission")) {
                if (!newPermissionInput.isEmpty() && !grantedPermissions.contains(newPermissionInput)) {
                    grantedPermissions.add(newPermissionInput);
                    newPermissionInput = "";
                    UICommandBuilder cmd = new UICommandBuilder();
                    UIEventBuilder events = new UIEventBuilder();
                    cmd.set("#NewPermissionField.Value", "");
                    buildPermissionsList(cmd, events);
                    this.sendUpdate(cmd, events, false);
                }
                return;
            }

            if (data.action.startsWith("RemovePermission:")) {
                String permToRemove = data.action.substring(17);
                grantedPermissions.remove(permToRemove);
                UICommandBuilder cmd = new UICommandBuilder();
                UIEventBuilder events = new UIEventBuilder();
                buildPermissionsList(cmd, events);
                this.sendUpdate(cmd, events, false);
                return;
            }
            if (data.action.equals("Save")) {
                if (id.isEmpty() || displayName.isEmpty()) {
                    return;
                }
                saveRank();
                goBackToList(ref, store);
                return;
            }

            if (data.action.equals("Cancel")) {
                goBackToList(ref, store);
                return;
            }
        }
    }

    private void saveRank() {
        Rank rank = new Rank();
        rank.setId(id);
        rank.setDisplayName(displayName);
        rank.setPriority(priority);
        rank.setMaxHomes(maxHomes);

        CommandSettings homeSettings = new CommandSettings();
        homeSettings.setEnabled(homeEnabled);
        homeSettings.setCooldownSeconds(homeCooldown);
        homeSettings.setWarmupSeconds(homeWarmup);
        rank.setHomeSettings(homeSettings);

        CommandSettings warpSettings = new CommandSettings();
        warpSettings.setEnabled(warpEnabled);
        warpSettings.setCooldownSeconds(warpCooldown);
        warpSettings.setWarmupSeconds(warpWarmup);
        rank.setWarpSettings(warpSettings);

        CommandSettings spawnSettings = new CommandSettings();
        spawnSettings.setEnabled(spawnEnabled);
        spawnSettings.setCooldownSeconds(spawnCooldown);
        spawnSettings.setWarmupSeconds(spawnWarmup);
        rank.setSpawnSettings(spawnSettings);

        CommandSettings backSettings = new CommandSettings();
        backSettings.setEnabled(backEnabled);
        backSettings.setCooldownSeconds(backCooldown);
        backSettings.setWarmupSeconds(backWarmup);
        rank.setBackSettings(backSettings);

        TpaSettings tpaSettings = new TpaSettings();
        tpaSettings.setEnabled(tpaEnabled);
        tpaSettings.setCooldownSeconds(tpaCooldown);
        tpaSettings.setWarmupSeconds(tpaWarmup);
        tpaSettings.setTimeoutSeconds(tpaTimeout);
        rank.setTpaSettings(tpaSettings);

        CommandSettings rtpSettings = new CommandSettings();
        rtpSettings.setEnabled(rtpEnabled);
        rtpSettings.setCooldownSeconds(rtpCooldown);
        rtpSettings.setWarmupSeconds(rtpWarmup);
        rank.setRtpSettings(rtpSettings);

        rank.setGrantedPermissions(grantedPermissions);

        if (isNewRank) {
            rankManager.addRank(rank);
        } else {
            rankManager.updateRank(rank);
        }
    }

    private void goBackToList(Ref<EntityStore> ref, Store<EntityStore> store) {
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        Player player = store.getComponent(ref, Player.getComponentType());
        player.getPageManager().openCustomPage(ref, store, new RankListGui(playerRef, rankManager, CustomPageLifetime.CanDismiss));
    }

    private int parseIntSafe(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static class EditorData {
        public static final BuilderCodec<EditorData> CODEC = BuilderCodec.<EditorData>builder(EditorData.class, EditorData::new)
            .addField(new KeyedCodec<>("@Id", Codec.STRING), (d, s) -> d.id = s, d -> d.id)
            .addField(new KeyedCodec<>("@DisplayName", Codec.STRING), (d, s) -> d.displayName = s, d -> d.displayName)
            .addField(new KeyedCodec<>("@Priority", Codec.STRING), (d, s) -> d.priority = s, d -> d.priority)
            .addField(new KeyedCodec<>("@MaxHomes", Codec.STRING), (d, s) -> d.maxHomes = s, d -> d.maxHomes)
            .addField(new KeyedCodec<>("@HomeEnabled", Codec.BOOLEAN), (d, b) -> d.homeEnabled = b, d -> d.homeEnabled)
            .addField(new KeyedCodec<>("@HomeCooldown", Codec.STRING), (d, s) -> d.homeCooldown = s, d -> d.homeCooldown)
            .addField(new KeyedCodec<>("@HomeWarmup", Codec.STRING), (d, s) -> d.homeWarmup = s, d -> d.homeWarmup)
            .addField(new KeyedCodec<>("@WarpEnabled", Codec.BOOLEAN), (d, b) -> d.warpEnabled = b, d -> d.warpEnabled)
            .addField(new KeyedCodec<>("@WarpCooldown", Codec.STRING), (d, s) -> d.warpCooldown = s, d -> d.warpCooldown)
            .addField(new KeyedCodec<>("@WarpWarmup", Codec.STRING), (d, s) -> d.warpWarmup = s, d -> d.warpWarmup)
            .addField(new KeyedCodec<>("@SpawnEnabled", Codec.BOOLEAN), (d, b) -> d.spawnEnabled = b, d -> d.spawnEnabled)
            .addField(new KeyedCodec<>("@SpawnCooldown", Codec.STRING), (d, s) -> d.spawnCooldown = s, d -> d.spawnCooldown)
            .addField(new KeyedCodec<>("@SpawnWarmup", Codec.STRING), (d, s) -> d.spawnWarmup = s, d -> d.spawnWarmup)
            .addField(new KeyedCodec<>("@BackEnabled", Codec.BOOLEAN), (d, b) -> d.backEnabled = b, d -> d.backEnabled)
            .addField(new KeyedCodec<>("@BackCooldown", Codec.STRING), (d, s) -> d.backCooldown = s, d -> d.backCooldown)
            .addField(new KeyedCodec<>("@BackWarmup", Codec.STRING), (d, s) -> d.backWarmup = s, d -> d.backWarmup)
            .addField(new KeyedCodec<>("@TpaEnabled", Codec.BOOLEAN), (d, b) -> d.tpaEnabled = b, d -> d.tpaEnabled)
            .addField(new KeyedCodec<>("@TpaCooldown", Codec.STRING), (d, s) -> d.tpaCooldown = s, d -> d.tpaCooldown)
            .addField(new KeyedCodec<>("@TpaWarmup", Codec.STRING), (d, s) -> d.tpaWarmup = s, d -> d.tpaWarmup)
            .addField(new KeyedCodec<>("@TpaTimeout", Codec.STRING), (d, s) -> d.tpaTimeout = s, d -> d.tpaTimeout)
            .addField(new KeyedCodec<>("@RtpEnabled", Codec.BOOLEAN), (d, b) -> d.rtpEnabled = b, d -> d.rtpEnabled)
            .addField(new KeyedCodec<>("@RtpCooldown", Codec.STRING), (d, s) -> d.rtpCooldown = s, d -> d.rtpCooldown)
            .addField(new KeyedCodec<>("@RtpWarmup", Codec.STRING), (d, s) -> d.rtpWarmup = s, d -> d.rtpWarmup)
            .addField(new KeyedCodec<>("@NewPermission", Codec.STRING), (d, s) -> d.newPermission = s, d -> d.newPermission)
            .addField(new KeyedCodec<>("Action", Codec.STRING), (d, s) -> d.action = s, d -> d.action)
            .build();

        private String id;
        private String displayName;
        private String priority;
        private String maxHomes;
        private Boolean homeEnabled;
        private String homeCooldown;
        private String homeWarmup;
        private Boolean warpEnabled;
        private String warpCooldown;
        private String warpWarmup;
        private Boolean spawnEnabled;
        private String spawnCooldown;
        private String spawnWarmup;
        private Boolean backEnabled;
        private String backCooldown;
        private String backWarmup;
        private Boolean tpaEnabled;
        private String tpaCooldown;
        private String tpaWarmup;
        private String tpaTimeout;
        private Boolean rtpEnabled;
        private String rtpCooldown;
        private String rtpWarmup;
        private String newPermission;
        private String action;
    }
}
