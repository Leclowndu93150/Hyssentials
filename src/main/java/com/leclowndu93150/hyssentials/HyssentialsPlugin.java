package com.leclowndu93150.hyssentials;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import com.leclowndu93150.hyssentials.commands.admin.HysCommand;
import com.leclowndu93150.hyssentials.commands.home.DelHomeCommand;
import com.leclowndu93150.hyssentials.commands.home.HomeCommand;
import com.leclowndu93150.hyssentials.commands.home.HomesCommand;
import com.leclowndu93150.hyssentials.commands.home.SetHomeCommand;
import com.leclowndu93150.hyssentials.commands.spawn.SetSpawnCommand;
import com.leclowndu93150.hyssentials.commands.spawn.SpawnCommand;
import com.leclowndu93150.hyssentials.commands.teleport.BackCommand;
import com.leclowndu93150.hyssentials.commands.teleport.RtpCommand;
import com.leclowndu93150.hyssentials.commands.teleport.TpCommand;
import com.leclowndu93150.hyssentials.commands.teleport.TphereCommand;
import com.leclowndu93150.hyssentials.commands.tpa.TpacceptCommand;
import com.leclowndu93150.hyssentials.commands.tpa.TpaCommand;
import com.leclowndu93150.hyssentials.commands.tpa.TpahereCommand;
import com.leclowndu93150.hyssentials.commands.tpa.TpcancelCommand;
import com.leclowndu93150.hyssentials.commands.tpa.TpdenyCommand;
import com.leclowndu93150.hyssentials.commands.msg.AdminChatCommand;
import com.leclowndu93150.hyssentials.commands.msg.MsgCommand;
import com.leclowndu93150.hyssentials.commands.msg.ReplyCommand;
import com.leclowndu93150.hyssentials.commands.warp.DelWarpCommand;
import com.leclowndu93150.hyssentials.commands.warp.SetWarpCommand;
import com.leclowndu93150.hyssentials.commands.warp.WarpCommand;
import com.leclowndu93150.hyssentials.commands.warp.WarpsCommand;
import com.leclowndu93150.hyssentials.config.ConfigMigrator;
import com.leclowndu93150.hyssentials.config.HyssentialsConfig;
import com.leclowndu93150.hyssentials.manager.BackManager;
import com.leclowndu93150.hyssentials.manager.CooldownManager;
import com.leclowndu93150.hyssentials.manager.DataManager;
import com.leclowndu93150.hyssentials.manager.HomeManager;
import com.leclowndu93150.hyssentials.manager.RankManager;
import com.leclowndu93150.hyssentials.manager.TeleportWarmupManager;
import com.leclowndu93150.hyssentials.manager.TpaManager;
import com.leclowndu93150.hyssentials.manager.WarpManager;
import com.leclowndu93150.hyssentials.manager.PrivateMessageManager;
import com.leclowndu93150.hyssentials.manager.AdminChatManager;
import com.leclowndu93150.hyssentials.manager.VanishManager;
import com.leclowndu93150.hyssentials.manager.JoinMessageManager;
import com.leclowndu93150.hyssentials.commands.admin.VanishCommand;
import com.leclowndu93150.hyssentials.system.PlayerDeathBackSystem;
import com.leclowndu93150.hyssentials.lang.LanguageManager;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.event.events.player.DrainPlayerFromWorldEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class HyssentialsPlugin extends JavaPlugin {
    private final Config<HyssentialsConfig> config = this.withConfig("config", HyssentialsConfig.CODEC);
    private DataManager dataManager;
    private RankManager rankManager;
    private TpaManager tpaManager;
    private HomeManager homeManager;
    private WarpManager warpManager;
    private BackManager backManager;
    private CooldownManager cooldownManager;
    private TeleportWarmupManager warmupManager;
    private PrivateMessageManager msgManager;
    private AdminChatManager adminChatManager;
    private VanishManager vanishManager;
    private JoinMessageManager joinMessageManager;

    public HyssentialsPlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        ConfigMigrator migrator = new ConfigMigrator(this.getDataDirectory(), this.getLogger());
        migrator.migrate();

        HyssentialsConfig cfg = this.config.get();
        this.config.save();

        // Initialize language system (auto-syncs translation files)
        LanguageManager.init(this.getDataDirectory(), this.getLogger());
        LanguageManager.setLanguage(cfg.getLanguage());

        this.dataManager = new DataManager(this.getDataDirectory(), this.getLogger());
        this.rankManager = new RankManager(this.getDataDirectory(), this.getLogger(), cfg.getDefaultRankId());
        this.backManager = new BackManager(cfg.getBackHistorySize());
        this.cooldownManager = new CooldownManager();
        this.warmupManager = new TeleportWarmupManager(this.backManager, this.cooldownManager);
        this.tpaManager = new TpaManager(this.rankManager);
        this.homeManager = new HomeManager(this.dataManager, this.rankManager);
        this.warpManager = new WarpManager(this.dataManager);
        this.msgManager = new PrivateMessageManager();
        this.adminChatManager = new AdminChatManager(this.getDataDirectory(), this.getLogger());
        this.vanishManager = new VanishManager();
        this.joinMessageManager = new JoinMessageManager(this.getDataDirectory(), this.getLogger());

        this.getEntityStoreRegistry().registerSystem(new PlayerDeathBackSystem(this.backManager));

        this.getEventRegistry().register(PlayerConnectEvent.class, this::onPlayerConnect);
        this.getEventRegistry().register(PlayerDisconnectEvent.class, this::onPlayerDisconnect);
        this.getEventRegistry().registerGlobal(AddPlayerToWorldEvent.class, this.joinMessageManager::onPlayerEnterWorld);
        this.getEventRegistry().registerGlobal(DrainPlayerFromWorldEvent.class, this.joinMessageManager::onPlayerLeaveWorld);
    }

    @Override
    protected void start() {
        this.getCommandRegistry().registerCommand(new TpaCommand(this.tpaManager, this.rankManager));
        this.getCommandRegistry().registerCommand(new TpahereCommand(this.tpaManager, this.rankManager));
        this.getCommandRegistry().registerCommand(new TpacceptCommand(this.tpaManager, this.warmupManager, this.rankManager));
        this.getCommandRegistry().registerCommand(new TpdenyCommand(this.tpaManager));
        this.getCommandRegistry().registerCommand(new TpcancelCommand(this.tpaManager));
        this.getCommandRegistry().registerCommand(new SetHomeCommand(this.homeManager, this.rankManager));
        this.getCommandRegistry().registerCommand(new HomeCommand(this.homeManager, this.warmupManager, this.cooldownManager, this.rankManager));
        this.getCommandRegistry().registerCommand(new DelHomeCommand(this.homeManager));
        this.getCommandRegistry().registerCommand(new HomesCommand(this.homeManager, this.rankManager));
        this.getCommandRegistry().registerCommand(new SetWarpCommand(this.warpManager));
        this.getCommandRegistry().registerCommand(new WarpCommand(this.warpManager, this.warmupManager, this.cooldownManager, this.rankManager));
        this.getCommandRegistry().registerCommand(new DelWarpCommand(this.warpManager));
        this.getCommandRegistry().registerCommand(new WarpsCommand(this.warpManager));
        this.getCommandRegistry().registerCommand(new SetSpawnCommand());
        this.getCommandRegistry().registerCommand(new SpawnCommand(this.warmupManager, this.cooldownManager, this.rankManager));
        this.getCommandRegistry().registerCommand(new BackCommand(this.backManager, this.warmupManager, this.cooldownManager, this.rankManager));
        this.getCommandRegistry().registerCommand(new RtpCommand(this.warmupManager, this.cooldownManager, this.rankManager));
        this.getCommandRegistry().registerCommand(new TpCommand(this.backManager));
        this.getCommandRegistry().registerCommand(new TphereCommand(this.backManager));
        this.getCommandRegistry().registerCommand(new HysCommand(this.rankManager, this.homeManager, this.config));
        this.getCommandRegistry().registerCommand(new MsgCommand(this.msgManager));
        this.getCommandRegistry().registerCommand(new ReplyCommand(this.msgManager));
        this.getCommandRegistry().registerCommand(new AdminChatCommand(this.adminChatManager));
        this.getCommandRegistry().registerCommand(new VanishCommand(this.vanishManager));
        this.getLogger().at(Level.INFO).log("Hyssentials loaded with rank system!");
    }

    private void onPlayerConnect(@Nonnull PlayerConnectEvent event) {
        vanishManager.onPlayerJoin(event.getPlayerRef());
        joinMessageManager.onPlayerConnect(event);

        PlayerRef player = event.getPlayerRef();
        if (!playerHasAnyRank(player)) {
            rankManager.grantRankPermission(player.getUuid(), rankManager.getDefaultRankId());
        }
    }

    private boolean playerHasAnyRank(@Nonnull PlayerRef player) {
        for (var rank : rankManager.getAllRanks()) {
            if (com.hypixel.hytale.server.core.permissions.PermissionsModule.get()
                    .hasPermission(player.getUuid(), rank.getPermission())) {
                return true;
            }
        }
        return false;
    }

    private void onPlayerDisconnect(@Nonnull PlayerDisconnectEvent event) {
        joinMessageManager.onPlayerDisconnect(event);
        vanishManager.onPlayerLeave(event.getPlayerRef().getUuid());
    }

    @Override
    protected void shutdown() {
        if (this.homeManager != null) {
            this.homeManager.save();
        }
        if (this.warpManager != null) {
            this.warpManager.save();
        }
        if (this.warmupManager != null) {
            this.warmupManager.shutdown();
        }
    }

    public RankManager getRankManager() {
        return rankManager;
    }

    public void reloadConfig() {
        this.config.load();
        this.rankManager.reload();
        if (this.adminChatManager != null) {
            this.adminChatManager.reload();
        }
        if (this.joinMessageManager != null) {
            this.joinMessageManager.reload();
        }
        // Reload language
        LanguageManager.setLanguage(this.config.get().getLanguage());
        LanguageManager.reload();
    }
}
