package net.silthus.chat;

import lombok.NonNull;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ChatColor;

import java.util.function.Function;

public final class Constants {

    public static final String PLUGIN_NAME = "sChat";
    public static final Key NBT_CHAT_TARGET_KEY = Key.key("schat:target");

    public static class Targets {

        public static final String EMPTY = "none";
        public static final String SYSTEM = "system";
        public static final String CONSOLE = "console";
    }

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

    public static class Language {
        public static final String ACF_BASE_KEY = "commands";
        public static final String ACCESS_TO_CHANNEL_DENIED = "access-to-channel-denied";
        public static final String JOINED_CHANNEL = "joined-channel";
    }

    public static class Errors {

        public static final String NO_ACTIVE_CHANNEL = ChatColor.RED + "You have no active channel to write in. Select one with /join <channel>";
    }

    public static class Formatting {
        public static final String DEFAULT_FORMAT = "<sender_name>: <message>";
        public static final String DIRECT_MESSAGE = "<message>";
    }

    public static class View {

        public static final TextColor FRAME_COLOR = NamedTextColor.DARK_GRAY;
        public static final TextColor INFO_COLOR = NamedTextColor.GRAY;
        public static final TextColor COMMAND = NamedTextColor.AQUA;
        public static final TextColor ACTIVE_COLOR = NamedTextColor.GREEN;
        public static final TextColor INACTIVE_COLOR = NamedTextColor.GRAY;
        public static final TextDecoration ACTIVE_DECORATION = TextDecoration.UNDERLINED;

        public static final String CHANNEL_DIVIDER = "│";
    }

    public static class Commands {
        public static final Function<Channel, String> JOIN_CHANNEL = channel -> "/schat channel join " + channel.getIdentifier();
    }
}
