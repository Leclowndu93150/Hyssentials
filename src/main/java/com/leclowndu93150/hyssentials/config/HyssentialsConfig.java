package com.leclowndu93150.hyssentials.config;


import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class HyssentialsConfig {
    public static final int CONFIG_VERSION = 2;

    public static final BuilderCodec<HyssentialsConfig> CODEC = BuilderCodec
        .builder(HyssentialsConfig.class, HyssentialsConfig::new)
        .append(new KeyedCodec<>("ConfigVersion", Codec.INTEGER), HyssentialsConfig::setConfigVersion, HyssentialsConfig::getConfigVersion).add()
        .append(new KeyedCodec<>("MaxHomes", Codec.INTEGER), HyssentialsConfig::setMaxHomes, HyssentialsConfig::getMaxHomes).add()
        .append(new KeyedCodec<>("TpaTimeoutSeconds", Codec.INTEGER), HyssentialsConfig::setTpaTimeout, HyssentialsConfig::getTpaTimeout).add()
        .append(new KeyedCodec<>("TpaCooldownSeconds", Codec.INTEGER), HyssentialsConfig::setTpaCooldown, HyssentialsConfig::getTpaCooldown).add()
        .append(new KeyedCodec<>("TeleportDelaySeconds", Codec.INTEGER), HyssentialsConfig::setTeleportDelay, HyssentialsConfig::getTeleportDelay).add()
        .append(new KeyedCodec<>("BackHistorySize", Codec.INTEGER), HyssentialsConfig::setBackHistorySize, HyssentialsConfig::getBackHistorySize).add()
        .append(new KeyedCodec<>("HomeCooldownMinutes", Codec.INTEGER), HyssentialsConfig::setHomeCooldownMinutes, HyssentialsConfig::getHomeCooldownMinutes).add()
        .append(new KeyedCodec<>("WarpCooldownMinutes", Codec.INTEGER), HyssentialsConfig::setWarpCooldownMinutes, HyssentialsConfig::getWarpCooldownMinutes).add()
        .append(new KeyedCodec<>("SpawnCooldownMinutes", Codec.INTEGER), HyssentialsConfig::setSpawnCooldownMinutes, HyssentialsConfig::getSpawnCooldownMinutes).add()
        .append(new KeyedCodec<>("BackCooldownMinutes", Codec.INTEGER), HyssentialsConfig::setBackCooldownMinutes, HyssentialsConfig::getBackCooldownMinutes).add()
        .build();

    private int configVersion = CONFIG_VERSION;
    private int maxHomes = 5;
    private int tpaTimeout = 60;
    private int tpaCooldown = 30;
    private int teleportDelay = 3;
    private int backHistorySize = 5;
    private int homeCooldownMinutes = 1;
    private int warpCooldownMinutes = 1;
    private int spawnCooldownMinutes = 1;
    private int backCooldownMinutes = 1;

    public HyssentialsConfig() {
    }

    public int getMaxHomes() {
        return maxHomes;
    }

    public void setMaxHomes(int maxHomes) {
        this.maxHomes = maxHomes;
    }

    public int getTpaTimeout() {
        return tpaTimeout;
    }

    public void setTpaTimeout(int tpaTimeout) {
        this.tpaTimeout = tpaTimeout;
    }

    public int getTpaCooldown() {
        return tpaCooldown;
    }

    public void setTpaCooldown(int tpaCooldown) {
        this.tpaCooldown = tpaCooldown;
    }

    public int getTeleportDelay() {
        return teleportDelay;
    }

    public void setTeleportDelay(int teleportDelay) {
        this.teleportDelay = teleportDelay;
    }

    public int getBackHistorySize() {
        return backHistorySize;
    }

    public void setBackHistorySize(int backHistorySize) {
        this.backHistorySize = backHistorySize;
    }

    public int getConfigVersion() {
        return configVersion;
    }

    public void setConfigVersion(int configVersion) {
        this.configVersion = configVersion;
    }

    public int getHomeCooldownMinutes() {
        return homeCooldownMinutes;
    }

    public void setHomeCooldownMinutes(int homeCooldownMinutes) {
        this.homeCooldownMinutes = homeCooldownMinutes;
    }

    public int getWarpCooldownMinutes() {
        return warpCooldownMinutes;
    }

    public void setWarpCooldownMinutes(int warpCooldownMinutes) {
        this.warpCooldownMinutes = warpCooldownMinutes;
    }

    public int getSpawnCooldownMinutes() {
        return spawnCooldownMinutes;
    }

    public void setSpawnCooldownMinutes(int spawnCooldownMinutes) {
        this.spawnCooldownMinutes = spawnCooldownMinutes;
    }

    public int getBackCooldownMinutes() {
        return backCooldownMinutes;
    }

    public void setBackCooldownMinutes(int backCooldownMinutes) {
        this.backCooldownMinutes = backCooldownMinutes;
    }
}
