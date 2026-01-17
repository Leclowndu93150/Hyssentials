package com.leclowndu93150.hyssentials.util;

import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.plugin.PluginManager;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.leclowndu93150.chatcustomization.util.ColorUtil;
import com.leclowndu93150.hyssentials.lang.Messages;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Utility for sending formatted messages that integrates with ChatCustomization if available.
 * Falls back to vanilla Message formatting if ChatCustomization is not installed.
 *
 * Supports MiniMessage-style tags:
 * - &lt;red&gt;text&lt;/red&gt; - red (#FF5555)
 * - &lt;green&gt;text&lt;/green&gt; - green (#55FF55)
 * - &lt;yellow&gt;text&lt;/yellow&gt; - yellow (#FFFF55)
 * - &lt;gray&gt;text&lt;/gray&gt; - gray (#AAAAAA)
 * - &lt;orange&gt;text&lt;/orange&gt; - orange (#FFAA00)
 * - &lt;#RRGGBB&gt;text&lt;/#RRGGBB&gt; - custom hex color
 * - &lt;bold&gt;text&lt;/bold&gt; or &lt;b&gt;text&lt;/b&gt;
 * - &lt;italic&gt;text&lt;/italic&gt; or &lt;i&gt;text&lt;/i&gt;
 * - &lt;underline&gt;text&lt;/underline&gt; or &lt;u&gt;text&lt;/u&gt;
 */
public final class ChatUtil {
    private static final PluginIdentifier CHAT_CUSTOMIZATION_ID = new PluginIdentifier("com.leclowndu93150", "ChatCustomization");
    private static final Pattern FORMAT_CODE_PATTERN = Pattern.compile("&(#[0-9A-Fa-f]{6}|[bilmru])");

    // MiniMessage-style tag pattern: matches <tag>, </tag>, <#RRGGBB>, </#RRGGBB>
    private static final Pattern TAG_PATTERN = Pattern.compile("<(/?)(red|green|yellow|gray|orange|white|aqua|blue|purple|pink|bold|b|italic|i|underline|u|#[0-9A-Fa-f]{6})>");

    // ============ COLOR CONSTANTS ============
    public static final String COLOR_RED = "#FF5555";
    public static final String COLOR_GREEN = "#55FF55";
    public static final String COLOR_YELLOW = "#FFFF55";
    public static final String COLOR_GRAY = "#AAAAAA";
    public static final String COLOR_ORANGE = "#FFAA00";
    public static final String COLOR_WHITE = "#FFFFFF";
    public static final String COLOR_AQUA = "#55FFFF";
    public static final String COLOR_BLUE = "#5555FF";
    public static final String COLOR_PURPLE = "#AA00AA";
    public static final String COLOR_PINK = "#FF55FF";

    private ChatUtil() {}

    /**
     * Checks if ChatCustomization mod is available.
     */
    public static boolean isChatCustomizationAvailable() {
        PluginManager pm = PluginManager.get();
        return pm != null && pm.getPlugin(CHAT_CUSTOMIZATION_ID) != null;
    }

    /**
     * Converts a color string to hex format.
     * If ChatCustomization is available, supports named colors (RED, GOLD, etc.).
     * Otherwise, returns the color as-is (should already be hex).
     */
    @Nonnull
    public static String toHex(@Nonnull String color) {
        if (isChatCustomizationAvailable()) {
            return ColorUtil.toHex(color);
        }

        if (color.startsWith("#") && color.length() == 7) {
            return color.toUpperCase();
        }
        return "#FFFFFF";
    }

    /**
     * Sends a formatted message to a single player.
     */
    public static void sendMessage(@Nonnull PlayerRef player, @Nonnull Message message) {
        player.sendMessage(message);
    }

    /**
     * Sends a formatted message to multiple players.
     */
    public static void broadcastMessage(@Nonnull Collection<PlayerRef> players, @Nonnull Message message) {
        for (PlayerRef player : players) {
            if (player != null) {
                player.sendMessage(message);
            }
        }
    }

    /**
     * Creates a colored message.
     */
    @Nonnull
    public static Message colored(@Nonnull String text, @Nonnull String color) {
        return Message.raw(text).color(toHex(color));
    }

    // ============ MINIMESSAGE-STYLE PARSER ============

    /**
     * Parses a string with MiniMessage-style tags and returns a formatted Message.
     * Supports:
     * - &lt;red&gt;text&lt;/red&gt; - red (#FF5555)
     * - &lt;green&gt;text&lt;/green&gt; - green (#55FF55)
     * - &lt;yellow&gt;text&lt;/yellow&gt; - yellow (#FFFF55)
     * - &lt;gray&gt;text&lt;/gray&gt; - gray (#AAAAAA)
     * - &lt;orange&gt;text&lt;/orange&gt; - orange (#FFAA00)
     * - &lt;#RRGGBB&gt;text&lt;/#RRGGBB&gt; - custom hex color
     * - &lt;bold&gt;text&lt;/bold&gt; or &lt;b&gt;text&lt;/b&gt;
     * - &lt;italic&gt;text&lt;/italic&gt; or &lt;i&gt;text&lt;/i&gt;
     * - &lt;underline&gt;text&lt;/underline&gt; or &lt;u&gt;text&lt;/u&gt;
     */
    @Nonnull
    public static Message parse(@Nonnull String text) {
        if (!text.contains("<")) {
            return Message.raw(text);
        }

        List<Message> parts = new ArrayList<>();
        Matcher matcher = TAG_PATTERN.matcher(text);

        Deque<String> colorStack = new ArrayDeque<>();
        boolean bold = false;
        boolean italic = false;
        boolean underline = false;

        int lastEnd = 0;

        while (matcher.find()) {
            // Add text before this tag
            if (matcher.start() > lastEnd) {
                String segment = text.substring(lastEnd, matcher.start());
                String currentColor = colorStack.isEmpty() ? null : colorStack.peek();
                parts.add(applyMiniFormat(segment, bold, italic, underline, currentColor));
            }

            boolean isClosing = matcher.group(1).equals("/");
            String tag = matcher.group(2).toLowerCase();

            if (isClosing) {
                // Handle closing tags
                switch (tag) {
                    case "red", "green", "yellow", "gray", "orange", "white", "aqua", "blue", "purple", "pink" -> {
                        if (!colorStack.isEmpty()) colorStack.pop();
                    }
                    case "bold", "b" -> bold = false;
                    case "italic", "i" -> italic = false;
                    case "underline", "u" -> underline = false;
                    default -> {
                        // Hex color closing tag
                        if (tag.startsWith("#") && !colorStack.isEmpty()) {
                            colorStack.pop();
                        }
                    }
                }
            } else {
                // Handle opening tags
                switch (tag) {
                    case "red" -> colorStack.push(COLOR_RED);
                    case "green" -> colorStack.push(COLOR_GREEN);
                    case "yellow" -> colorStack.push(COLOR_YELLOW);
                    case "gray" -> colorStack.push(COLOR_GRAY);
                    case "orange" -> colorStack.push(COLOR_ORANGE);
                    case "white" -> colorStack.push(COLOR_WHITE);
                    case "aqua" -> colorStack.push(COLOR_AQUA);
                    case "blue" -> colorStack.push(COLOR_BLUE);
                    case "purple" -> colorStack.push(COLOR_PURPLE);
                    case "pink" -> colorStack.push(COLOR_PINK);
                    case "bold", "b" -> bold = true;
                    case "italic", "i" -> italic = true;
                    case "underline", "u" -> underline = true;
                    default -> {
                        // Hex color tag
                        if (tag.startsWith("#")) {
                            colorStack.push(tag.toUpperCase());
                        }
                    }
                }
            }
            lastEnd = matcher.end();
        }

        // Add remaining text after last tag
        if (lastEnd < text.length()) {
            String segment = text.substring(lastEnd);
            String currentColor = colorStack.isEmpty() ? null : colorStack.peek();
            parts.add(applyMiniFormat(segment, bold, italic, underline, currentColor));
        }

        if (parts.isEmpty()) {
            return Message.empty();
        }
        if (parts.size() == 1) {
            return parts.get(0);
        }

        Message result = Message.empty();
        for (Message part : parts) {
            result.insert(part);
        }
        return result;
    }

    /**
     * Parses a translated message with MiniMessage-style tags.
     */
    @Nonnull
    public static Message parse(@Nonnull Messages message, Object... args) {
        return parse(message.get(args));
    }

    private static Message applyMiniFormat(String text, boolean bold, boolean italic,
                                           boolean underline, @Nullable String color) {
        Message msg = Message.raw(text);
        if (bold) msg.bold(true);
        if (italic) msg.italic(true);
        // Note: underline is tracked but Message API doesn't support it yet
        if (color != null) msg.color(color);
        return msg;
    }

    /**
     * Creates a message with a colored prefix.
     */
    @Nonnull
    public static Message prefixed(@Nonnull String prefix, @Nonnull String prefixColor,
                                   @Nonnull String text, @Nullable String textColor) {
        Message msg = Message.empty()
            .insert(Message.raw("[" + prefix + "] ").color(toHex(prefixColor)));

        if (textColor != null) {
            msg.insert(Message.raw(text).color(toHex(textColor)));
        } else {
            msg.insert(Message.raw(text));
        }

        return msg;
    }

    /**
     * Creates an admin chat style message.
     */
    @Nonnull
    public static Message adminChat(@Nonnull String prefix, @Nonnull String prefixColor,
                                    @Nonnull String senderName, @Nonnull String message,
                                    @Nonnull String messageColor) {
        return Message.empty()
            .insert(Message.raw("[" + prefix + "] ").color(toHex(prefixColor)))
            .insert(Message.raw(senderName + ": "))
            .insert(Message.raw(message).color(toHex(messageColor)));
    }

    /**
     * Creates a private message format for the sender view.
     */
    @Nonnull
    public static Message privateMessageTo(@Nonnull String recipientName, @Nonnull String message) {
        return Message.empty()
            .insert(parse(Messages.PM_TO.get(recipientName)))
            .insert(Message.raw(message).color(COLOR_WHITE));
    }

    /**
     * Creates a private message format for the recipient view.
     */
    @Nonnull
    public static Message privateMessageFrom(@Nonnull String senderName, @Nonnull String message) {
        return Message.empty()
            .insert(parse(Messages.PM_FROM.get(senderName)))
            .insert(Message.raw(message).color(COLOR_WHITE));
    }

    /**
     * Parses a string with format codes and returns a formatted Message.
     * Supports:
     * - &b = bold
     * - &i = italic
     * - &u = underline
     * - &m = monospace
     * - &r = reset (clears all formatting)
     * - &#RRGGBB = hex color (e.g., &#FF5555 for red)
     * - &l = bold (alias, for familiarity)
     */
    @Nonnull
    public static Message parseFormatted(@Nonnull String text) {
        if (!text.contains("&")) {
            return Message.raw(text);
        }

        List<Message> parts = new ArrayList<>();
        Matcher matcher = FORMAT_CODE_PATTERN.matcher(text);

        boolean bold = false;
        boolean italic = false;
        boolean underline = false;
        boolean monospace = false;
        String color = null;

        int lastEnd = 0;

        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                String segment = text.substring(lastEnd, matcher.start());
                parts.add(applyFormat(segment, bold, italic, underline, monospace, color));
            }

            String code = matcher.group(1);
            if (code.startsWith("#")) {
                color = code.toUpperCase();
            } else {
                switch (code.toLowerCase()) {
                    case "b", "l" -> bold = true;
                    case "i" -> italic = true;
                    case "u" -> underline = true;
                    case "m" -> monospace = true;
                    case "r" -> {
                        bold = false;
                        italic = false;
                        underline = false;
                        monospace = false;
                        color = null;
                    }
                }
            }
            lastEnd = matcher.end();
        }

        if (lastEnd < text.length()) {
            String segment = text.substring(lastEnd);
            parts.add(applyFormat(segment, bold, italic, underline, monospace, color));
        }

        if (parts.isEmpty()) {
            return Message.empty();
        }
        if (parts.size() == 1) {
            return parts.get(0);
        }

        Message result = Message.empty();
        for (Message part : parts) {
            result.insert(part);
        }
        return result;
    }

    private static Message applyFormat(String text, boolean bold, boolean italic,
                                       boolean underline, boolean monospace, @Nullable String color) {
        Message msg = Message.raw(text);
        if (bold) msg.bold(true);
        if (italic) msg.italic(true);
        if (monospace) msg.monospace(true);
        if (color != null) msg.color(color);
        return msg;
    }
}
