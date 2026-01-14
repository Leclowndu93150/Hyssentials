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
        .append(new KeyedCodec<>("HomeCooldownSeconds", Codec.INTEGER), HyssentialsConfig::setHomeCooldownSeconds, HyssentialsConfig::getHomeCooldownSeconds).add()
        .append(new KeyedCodec<>("WarpCooldownSeconds", Codec.INTEGER), HyssentialsConfig::setWarpCooldownSeconds, HyssentialsConfig::getWarpCooldownSeconds).add()
        .append(new KeyedCodec<>("SpawnCooldownSeconds", Codec.INTEGER), HyssentialsConfig::setSpawnCooldownSeconds, HyssentialsConfig::getSpawnCooldownSeconds).add()
        .append(new KeyedCodec<>("BackCooldownSeconds", Codec.INTEGER), HyssentialsConfig::setBackCooldownSeconds, HyssentialsConfig::getBackCooldownSeconds).add()
        .build();

    private int configVersion = CONFIG_VERSION;
    private int maxHomes = 5;
    private int tpaTimeout = 60;
    private int tpaCooldown = 30;
    private int teleportDelay = 3;
    private int backHistorySize = 5;
    private int homeCooldownSeconds = 60;
    private int warpCooldownSeconds = 60;
    private int spawnCooldownSeconds = 60;
    private int backCooldownSeconds = 60;

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

    public int getHomeCooldownSeconds() {
        return homeCooldownSeconds;
    }

    public void setHomeCooldownSeconds(int homeCooldownSeconds) {
        this.homeCooldownSeconds = homeCooldownSeconds;
    }

    public int getWarpCooldownSeconds() {
        return warpCooldownSeconds;
    }

    public void setWarpCooldownSeconds(int warpCooldownSeconds) {
        this.warpCooldownSeconds = warpCooldownSeconds;
    }

    public int getSpawnCooldownSeconds() {
        return spawnCooldownSeconds;
    }

    public void setSpawnCooldownSeconds(int spawnCooldownSeconds) {
        this.spawnCooldownSeconds = spawnCooldownSeconds;
    }

    public int getBackCooldownSeconds() {
        return backCooldownSeconds;
    }

    public void setBackCooldownSeconds(int backCooldownSeconds) {
        this.backCooldownSeconds = backCooldownSeconds;
    }
}
