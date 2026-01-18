package com.leclowndu93150.hyssentials.lang;

/**
 * Enum containing all translation keys for the plugin.
 * Each key maps to a translation in the language files (en.json, fr.json).
 */
public enum Messages {

    // ============ GENERAL ERRORS ============
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

    // ============ COOLDOWN MESSAGES ============
    COOLDOWN_HOME("cooldown.home"),
    COOLDOWN_SPAWN("cooldown.spawn"),
    COOLDOWN_BACK("cooldown.back"),
    COOLDOWN_RTP("cooldown.rtp"),
    COOLDOWN_WARP("cooldown.warp"),
    COOLDOWN_TPA("cooldown.tpa"),

    // ============ SUCCESS MESSAGES ============
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

    // ============ INFO MESSAGES ============
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

    // ============ USAGE MESSAGES ============
    USAGE_MSG("usage.msg"),
    USAGE_REPLY("usage.reply"),
    USAGE_ADMINCHAT("usage.adminchat"),
    USAGE_ADMINCHAT_GROUP("usage.adminchat_group"),
    USAGE_ADMINCHAT_GROUPS("usage.adminchat_groups"),

    // ============ PRIVATE MESSAGE FORMAT ============
    PM_TO("pm.to"),
    PM_FROM("pm.from"),

    // ============ UI ELEMENTS ============
    // Home List
    UI_HOME_LIST_TITLE("ui.home_list.title"),
    UI_HOME_LIST_TITLE_COUNT("ui.home_list.title_count"),
    UI_BTN_CLOSE("ui.btn.close"),
    UI_BTN_TELEPORT("ui.btn.teleport"),
    UI_BTN_DELETE_CONFIRM("ui.btn.delete_confirm"),

    // Warp List
    UI_WARP_LIST_TITLE("ui.warp_list.title"),

    // Rank List
    UI_RANK_LIST_TITLE("ui.rank_list.title"),
    UI_BTN_CREATE_NEW("ui.btn.create_new"),
    UI_BTN_EDIT("ui.btn.edit"),
    UI_LABEL_PERMISSION("ui.label.permission"),
    UI_LABEL_ID_PREFIX("ui.label.id_prefix"),
    UI_LABEL_PRIORITY_PREFIX("ui.label.priority_prefix"),

    // Rank Editor
    UI_RANK_EDITOR_TITLE_NEW("ui.rank_editor.title_new"),
    UI_RANK_EDITOR_TITLE_EDIT("ui.rank_editor.title_edit"),
    UI_BTN_SAVE("ui.btn.save"),
    UI_BTN_CANCEL("ui.btn.cancel"),
    UI_LABEL_ID("ui.label.id"),
    UI_LABEL_ID_DESC("ui.label.id_desc"),
    UI_LABEL_DISPLAY_NAME("ui.label.display_name"),
    UI_LABEL_PRIORITY("ui.label.priority"),
    UI_LABEL_PRIORITY_DESC("ui.label.priority_desc"),
    UI_LABEL_MAX_HOMES("ui.label.max_homes"),
    UI_SECTION_HOME_CMD("ui.section.home_command"),
    UI_SECTION_WARP_CMD("ui.section.warp_command"),
    UI_SECTION_SPAWN_CMD("ui.section.spawn_command"),
    UI_SECTION_BACK_CMD("ui.section.back_command"),
    UI_SECTION_TPA_CMD("ui.section.tpa_command"),
    UI_SECTION_RTP_CMD("ui.section.rtp_command"),
    UI_LABEL_ENABLED("ui.label.enabled"),
    UI_LABEL_COOLDOWN("ui.label.cooldown"),
    UI_LABEL_WARMUP("ui.label.warmup"),
    UI_LABEL_TIMEOUT("ui.label.timeout"),
    UI_SECTION_PERMISSIONS("ui.section.permissions"),
    UI_SECTION_PERMISSIONS_DESC("ui.section.permissions_desc"),

    // TPA List
    UI_TPA_LIST_TITLE("ui.tpa_list.title"),
    UI_TPA_LIST_TITLE_TPA("ui.tpa_list.title_tpa"),
    UI_TPA_LIST_TITLE_TPAHERE("ui.tpa_list.title_tpahere"),
    UI_TPA_NO_PLAYERS("ui.tpa_list.no_players"),
    UI_LABEL_CURRENT_RANK("ui.label.current_rank"),

    // Player Assign
    UI_PLAYER_ASSIGN_TITLE("ui.player_assign.title"),

    // GUI Error messages
    UI_ERROR_NO_HOME_PERMISSION("ui.error.no_home_permission"),
    UI_ERROR_HOME_COOLDOWN("ui.error.home_cooldown"),
    UI_ERROR_HOME_NOT_FOUND("ui.error.home_not_found"),
    UI_ERROR_NO_WARP_PERMISSION("ui.error.no_warp_permission"),
    UI_ERROR_WARP_COOLDOWN("ui.error.warp_cooldown"),
    UI_ERROR_WARP_NOT_FOUND("ui.error.warp_not_found"),
    UI_ERROR_NO_TPA_PERMISSION("ui.error.no_tpa_permission"),
    UI_ERROR_TPA_COOLDOWN("ui.error.tpa_cooldown"),
    UI_ERROR_INVALID_PLAYER("ui.error.invalid_player"),
    UI_ERROR_PLAYER_OFFLINE("ui.error.player_offline"),
    UI_ERROR_ALREADY_PENDING("ui.error.already_pending"),
    UI_SUCCESS_TPA_SENT("ui.success.tpa_sent"),
    UI_TPA_REQUEST_RECEIVED("ui.tpa_list.request_received"),
    UI_TPAHERE_REQUEST_RECEIVED("ui.tpa_list.request_here_received");

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
