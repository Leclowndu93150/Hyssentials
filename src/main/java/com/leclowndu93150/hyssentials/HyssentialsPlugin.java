package com.leclowndu93150.hyssentials;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import com.leclowndu93150.hyssentials.commands.home.DelHomeCommand;
import com.leclowndu93150.hyssentials.commands.home.HomeCommand;
import com.leclowndu93150.hyssentials.commands.home.HomesCommand;
import com.leclowndu93150.hyssentials.commands.home.SetHomeCommand;
import com.leclowndu93150.hyssentials.commands.spawn.SetSpawnCommand;
import com.leclowndu93150.hyssentials.commands.spawn.SpawnCommand;
import com.leclowndu93150.hyssentials.commands.teleport.BackCommand;
import com.leclowndu93150.hyssentials.commands.teleport.TpCommand;
import com.leclowndu93150.hyssentials.commands.teleport.TphereCommand;
import com.leclowndu93150.hyssentials.commands.tpa.TpacceptCommand;
import com.leclowndu93150.hyssentials.commands.tpa.TpaCommand;
import com.leclowndu93150.hyssentials.commands.tpa.TpahereCommand;
import com.leclowndu93150.hyssentials.commands.tpa.TpcancelCommand;
import com.leclowndu93150.hyssentials.commands.tpa.TpdenyCommand;
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
import com.leclowndu93150.hyssentials.manager.SpawnManager;
import com.leclowndu93150.hyssentials.manager.TpaManager;
import com.leclowndu93150.hyssentials.manager.WarpManager;
import com.leclowndu93150.hyssentials.system.PlayerDeathBackSystem;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class HyssentialsPlugin extends JavaPlugin {
    private final Config<HyssentialsConfig> config = this.withConfig("config", HyssentialsConfig.CODEC);
    private DataManager dataManager;
    private TpaManager tpaManager;
    private HomeManager homeManager;
    private WarpManager warpManager;
    private SpawnManager spawnManager;
    private BackManager backManager;
    private CooldownManager cooldownManager;

    public HyssentialsPlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        // Run config migration before loading config
        ConfigMigrator migrator = new ConfigMigrator(this.getDataDirectory(), this.getLogger());
        migrator.migrate();

        HyssentialsConfig cfg = this.config.get();
        this.config.save();
        this.dataManager = new DataManager(this.getDataDirectory(), this.getLogger());
        this.tpaManager = new TpaManager(cfg.getTpaTimeout(), cfg.getTpaCooldown());
        this.homeManager = new HomeManager(this.dataManager, cfg.getMaxHomes());
        this.warpManager = new WarpManager(this.dataManager);
        this.spawnManager = new SpawnManager(this.dataManager);
        this.backManager = new BackManager(cfg.getBackHistorySize());
        this.cooldownManager = new CooldownManager(
            cfg.getHomeCooldownMinutes(),
            cfg.getWarpCooldownMinutes(),
            cfg.getSpawnCooldownMinutes(),
            cfg.getBackCooldownMinutes()
        );
        this.getEntityStoreRegistry().registerSystem(new PlayerDeathBackSystem(this.backManager));
    }

    @Override
    protected void start() {
        this.getCommandRegistry().registerCommand(new TpaCommand(this.tpaManager));
        this.getCommandRegistry().registerCommand(new TpahereCommand(this.tpaManager));
        this.getCommandRegistry().registerCommand(new TpacceptCommand(this.tpaManager, this.backManager));
        this.getCommandRegistry().registerCommand(new TpdenyCommand(this.tpaManager));
        this.getCommandRegistry().registerCommand(new TpcancelCommand(this.tpaManager));
        this.getCommandRegistry().registerCommand(new SetHomeCommand(this.homeManager));
        this.getCommandRegistry().registerCommand(new HomeCommand(this.homeManager, this.backManager, this.cooldownManager));
        this.getCommandRegistry().registerCommand(new DelHomeCommand(this.homeManager));
        this.getCommandRegistry().registerCommand(new HomesCommand(this.homeManager));
        this.getCommandRegistry().registerCommand(new SetWarpCommand(this.warpManager));
        this.getCommandRegistry().registerCommand(new WarpCommand(this.warpManager, this.backManager, this.cooldownManager));
        this.getCommandRegistry().registerCommand(new DelWarpCommand(this.warpManager));
        this.getCommandRegistry().registerCommand(new WarpsCommand(this.warpManager));
        this.getCommandRegistry().registerCommand(new SetSpawnCommand(this.spawnManager));
        this.getCommandRegistry().registerCommand(new SpawnCommand(this.spawnManager, this.backManager, this.cooldownManager));
        this.getCommandRegistry().registerCommand(new BackCommand(this.backManager, this.cooldownManager));
        this.getCommandRegistry().registerCommand(new TpCommand(this.backManager));
        this.getCommandRegistry().registerCommand(new TphereCommand(this.backManager));
        this.getLogger().at(Level.INFO).log("Hyssentials loaded! Commands: /tpa, /tpahere, /tpaccept, /tpdeny, /tpcancel, /home, /sethome, /delhome, /homes, /warp, /setwarp, /delwarp, /warps, /spawn, /setspawn, /back, /tp, /tphere");
    }

    @Override
    protected void shutdown() {
        if (this.homeManager != null) {
            this.homeManager.save();
        }
        if (this.warpManager != null) {
            this.warpManager.save();
        }
        if (this.spawnManager != null) {
            this.spawnManager.save();
        }
    }
}
