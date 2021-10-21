package net.silthus.chat;

import net.md_5.bungee.api.ChatColor;

public final class Constants {
    private Constants() {}

    public static final String BASE_PERMISSION = "schat";
    public static final String CHANNEL_PERMISSION = BASE_PERMISSION + ".channel";

    public static class Formatting {

        public static final String DEFAULT_PREFIX = null;
        public static final String DEFAULT_SUFFIX = ": ";
        public static final ChatColor DEFAULT_CHAT_COLOR = null;
    }
}
