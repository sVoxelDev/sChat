package net.silthus.chat;

import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;

public final class Constants {
    private Constants() {
    }

    public static final String PLUGIN_NAME = "sChat";

    public static class Permissions {

        public static final String BASE_PERMISSION = PLUGIN_NAME.toLowerCase();
        public static final String CHANNEL_PERMISSION = BASE_PERMISSION + ".channel";
        public static final String AUTO_JOIN_CHANNE_SUFFIX = ".autojoin";

        public static String getChannelPermission(@NonNull Channel channel) {
            return (CHANNEL_PERMISSION + "." + channel.getIdentifier()).toLowerCase();
        }

        public static String getAutoJoinPermission(@NonNull Channel channel) {
            return getChannelPermission(channel) + AUTO_JOIN_CHANNE_SUFFIX;
        }
    }

    public static class Errors {

        public static final String NO_ACTIVE_CHANNEL = ChatColor.RED + "You have no active channel to write in. Select one with /join <channel>";
    }

    public static class Formatting {

        public static final String DEFAULT_PREFIX = null;
        public static final String DEFAULT_SUFFIX = ": ";
        public static final ChatColor DEFAULT_CHAT_COLOR = null;
    }
}
