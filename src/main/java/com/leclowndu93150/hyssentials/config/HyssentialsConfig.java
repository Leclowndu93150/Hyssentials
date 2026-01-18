package com.leclowndu93150.hyssentials.config;


import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class HyssentialsConfig {
    public static final int CONFIG_VERSION = 5;

    public static final BuilderCodec<HyssentialsConfig> CODEC = BuilderCodec
        .builder(HyssentialsConfig.class, HyssentialsConfig::new)
        .append(new KeyedCodec<>("ConfigVersion", Codec.INTEGER), HyssentialsConfig::setConfigVersion, HyssentialsConfig::getConfigVersion).add()
        .append(new KeyedCodec<>("BackHistorySize", Codec.INTEGER), HyssentialsConfig::setBackHistorySize, HyssentialsConfig::getBackHistorySize).add()
        .append(new KeyedCodec<>("DefaultRankId", Codec.STRING), HyssentialsConfig::setDefaultRankId, HyssentialsConfig::getDefaultRankId).add()
        .append(new KeyedCodec<>("Language", Codec.STRING), HyssentialsConfig::setLanguage, HyssentialsConfig::getLanguage).add()
        .build();

    private int configVersion = CONFIG_VERSION;
    private int backHistorySize = 5;
    private String defaultRankId = "default";
    private String language = "en";

    public HyssentialsConfig() {
    }

    public int getConfigVersion() {
        return configVersion;
    }

    public void setConfigVersion(int configVersion) {
        this.configVersion = configVersion;
    }

    public int getBackHistorySize() {
        return backHistorySize;
    }

    public void setBackHistorySize(int backHistorySize) {
        this.backHistorySize = backHistorySize;
    }

    public String getDefaultRankId() {
        return defaultRankId;
    }

    public void setDefaultRankId(String defaultRankId) {
        this.defaultRankId = defaultRankId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
