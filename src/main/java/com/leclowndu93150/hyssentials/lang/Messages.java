package com.leclowndu93150.hyssentials.lang;

/**
 * Enum containing all translation keys for the plugin.
 * Each key maps to a translation in the language files (en.json, fr.json).
 */
public enum Messages {
    // ============ PERMISSION ERRORS (RED) ============
    NO_PERMISSION_VANISH("error.no_permission.vanish"),
    NO_PERMISSION_HOME("error.no_permission.home"),
    NO_PERMISSION_SPAWN("error.no_permission.spawn"),
    NO_PERMISSION_BACK("error.no_permission.back"),
    NO_PERMISSION_RTP("error.no_permission.rtp"),
    NO_PERMISSION_TPA("error.no_permission.tpa"),
    NO_PERMISSION_TPAHERE("error.no_permission.tpahere"),
    NO_PERMISSION_WARP("error.no_permission.warp"),
    NO_PERMISSION_RELOAD("error.no_permission.reload"),
    NO_PERMISSION_RANK("error.no_permission.rank"),
    NO_PERMISSION_ASSIGN("error.no_permission.assign"),
    NO_PERMISSION_SETRANK("error.no_permission.setrank"),
    NO_PERMISSION_REMOVERANK("error.no_permission.removerank"),
    NO_PERMISSION_PLAYERINFO("error.no_permission.playerinfo"),
    NO_PERMISSION_ADMINCHAT("error.no_permission.adminchat"),

    // ============ GENERAL ERRORS (RED) ============
    ERROR_PLAYER_NOT_FOUND("error.player_not_found"),
    ERROR_CANNOT_TELEPORT_SELF("error.cannot_teleport_self"),
    ERROR_TARGET_NOT_AVAILABLE("error.target_not_available"),
    ERROR_CANNOT_GET_POSITION("error.cannot_get_position"),
    ERROR_CANNOT_GET_TARGET_POSITION("error.cannot_get_target_position"),
    ERROR_RANK_NOT_FOUND("error.rank_not_found"),
    ERROR_RELOAD_FAILED("error.reload_failed"),
    ERROR_HOME_NOT_FOUND("error.home_not_found"),
    ERROR_WARP_NOT_FOUND("error.warp_not_found"),
    ERROR_MAX_HOMES_REACHED("error.max_homes_reached"),
    ERROR_NO_HOMES("error.no_homes"),
    ERROR_NO_WARPS("error.no_warps"),
    ERROR_NO_BACK_LOCATION("error.no_back_location"),
    ERROR_RTP_NO_SAFE_LOCATION("error.rtp_no_safe_location"),
    ERROR_RTP_FAILED("error.rtp_failed"),
    ERROR_CANNOT_MESSAGE_SELF("error.cannot_message_self"),
    ERROR_NO_REPLY_TARGET("error.no_reply_target"),
    ERROR_REPLY_TARGET_OFFLINE("error.reply_target_offline"),
    ERROR_NO_PENDING_TPA("error.no_pending_tpa"),
    ERROR_TPA_SENDER_OFFLINE("error.tpa_sender_offline"),
    ERROR_TPA_SENDER_NOT_AVAILABLE("error.tpa_sender_not_available"),
    ERROR_ALREADY_PENDING_TPA("error.already_pending_tpa"),
    ERROR_NO_TPA_TO_CANCEL("error.no_tpa_to_cancel"),
    ERROR_WARMUP_FAILED("error.warmup_failed"),
    ERROR_CHUNK_LOAD_FAILED("error.chunk_load_failed"),

    // ============ COOLDOWN MESSAGES (YELLOW) ============
    COOLDOWN_HOME("cooldown.home"),
    COOLDOWN_SPAWN("cooldown.spawn"),
    COOLDOWN_BACK("cooldown.back"),
    COOLDOWN_RTP("cooldown.rtp"),
    COOLDOWN_WARP("cooldown.warp"),
    COOLDOWN_TPA("cooldown.tpa"),

    // ============ SUCCESS MESSAGES (GREEN) ============
    SUCCESS_VANISH_ENABLED("success.vanish_enabled"),
    SUCCESS_VANISH_DISABLED("success.vanish_disabled"),
    SUCCESS_CONFIG_RELOADED("success.config_reloaded"),
    SUCCESS_HOME_SET("success.home_set"),
    SUCCESS_HOME_DELETED("success.home_deleted"),
    SUCCESS_WARP_SET("success.warp_set"),
    SUCCESS_WARP_DELETED("success.warp_deleted"),
    SUCCESS_SPAWN_SET("success.spawn_set"),
    SUCCESS_TELEPORTING("success.teleporting"),
    SUCCESS_TELEPORTED("success.teleported"),
    SUCCESS_TELEPORTED_TO_PLAYER("success.teleported_to_player"),
    SUCCESS_TELEPORTED_PLAYER_TO_YOU("success.teleported_player_to_you"),
    SUCCESS_PLAYER_TELEPORTED_TO_YOU("success.player_teleported_to_you"),
    SUCCESS_RANK_GRANTED("success.rank_granted"),
    SUCCESS_RANK_REMOVED("success.rank_removed"),
    SUCCESS_YOU_RECEIVED_RANK("success.you_received_rank"),
    SUCCESS_YOUR_RANK_REMOVED("success.your_rank_removed"),
    SUCCESS_TPA_SENT("success.tpa_sent"),
    SUCCESS_TPA_ACCEPTED("success.tpa_accepted"),
    SUCCESS_TPA_ACCEPTED_NOTIFY("success.tpa_accepted_notify"),
    SUCCESS_TPA_DENIED("success.tpa_denied"),
    SUCCESS_TPA_DENIED_NOTIFY("success.tpa_denied_notify"),
    SUCCESS_TPA_CANCELLED("success.tpa_cancelled"),

    // ============ INFO MESSAGES (GRAY/SECONDARY) ============
    INFO_VANISH_REMINDER("info.vanish_reminder"),
    INFO_RTP_SEARCHING("info.rtp_searching"),
    INFO_WARMUP_STARTED("info.warmup_started"),
    INFO_WARMUP_CANCELLED("info.warmup_cancelled"),
    INFO_TPA_REQUEST_RECEIVED("info.tpa_request_received"),
    INFO_TPAHERE_REQUEST_RECEIVED("info.tpahere_request_received"),
    INFO_HOMES_LIST("info.homes_list"),
    INFO_WARPS_LIST("info.warps_list"),
    INFO_PLAYER_INFO_HEADER("info.player_info_header"),
    INFO_PLAYER_INFO_UUID("info.player_info_uuid"),
    INFO_PLAYER_INFO_RANK("info.player_info_rank"),
    INFO_PLAYER_INFO_PERMISSION("info.player_info_permission"),
    INFO_PLAYER_INFO_HOMES("info.player_info_homes"),
    INFO_PLAYER_INFO_COOLDOWN("info.player_info_cooldown"),
    INFO_PLAYER_INFO_WARMUP("info.player_info_warmup"),

    // ============ USAGE MESSAGES (YELLOW) ============
    USAGE_MSG("usage.msg"),
    USAGE_REPLY("usage.reply"),
    USAGE_ADMINCHAT("usage.adminchat"),
    USAGE_ADMINCHAT_GROUP("usage.adminchat_group"),
    USAGE_ADMINCHAT_GROUPS("usage.adminchat_groups"),

    // ============ PRIVATE MESSAGE FORMAT (GRAY) ============
    PM_TO("pm.to"),
    PM_FROM("pm.from");

    private final String key;

    Messages(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    /**
     * Get the translated message for the current language.
     */
    public String get() {
        return LanguageManager.get(this);
    }

    /**
     * Get the translated message with format arguments.
     */
    public String get(Object... args) {
        return LanguageManager.format(this, args);
    }
}
