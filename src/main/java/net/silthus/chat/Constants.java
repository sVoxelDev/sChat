/*
 * sChat, a Supercharged Minecraft Chat Plugin
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.silthus.chat;

import lombok.NonNull;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ChatColor;
import net.silthus.chat.conversations.Channel;

import java.util.function.Function;

public final class Constants {

    public static final String PLUGIN_NAME = "sChat";
    public static final Key NBT_MESSAGE_ID = Key.key("schat:messageid");
    public static final String BUNGEECORD_CHANNEL = "BungeeCord";
    public static final String GLOBAL_PLAYERLIST_CHANNEL = "PlayerList";
    public static final String SCHAT_MESSAGES_CHANNEL = "schat:messages";
    public static final String SCHAT_PLAYERLIST_CHANNEL = "schat:playerlist";

    public static final String PERMISSION_PLAYER_COMMANDS = "schat.player";
    public static final String PERMISSION_PLAYER_CHANNEL_COMMANDS = "schat.player.channel";
    public static final String PERMISSION_PLAYER_CHANNEL_JOIN = "schat.player.channel.join";
    public static final String PERMISSION_PLAYER_CHANNEL_QUICKMESSAGE = "schat.player.channel.quickmessage";
    public static final String PERMISSION_PLAYER_DIRECT_MESSAGE = "schat.player.directmessage";

    public static class Targets {

        public static final String EMPTY = "none";
        public static final String SYSTEM = "system";
        public static final String CONSOLE = "console";
    }

    public static class Permissions {

        public static final String BASE_PERMISSION = PLUGIN_NAME.toLowerCase();

        public static final String CHANNEL_PERMISSION = BASE_PERMISSION + ".channel";
        public static final String AUTO_JOIN_CHANNEL_SUFFIX = ".autojoin";

        public static String getChannelPermission(@NonNull Channel channel) {
            return (CHANNEL_PERMISSION + "." + channel.getName()).toLowerCase();
        }

        public static String getAutoJoinPermission(@NonNull Channel channel) {
            return getChannelPermission(channel) + AUTO_JOIN_CHANNEL_SUFFIX;
        }
    }

    public static class Language {
        public static final String ACF_BASE_KEY = "commands";
        public static final String ACCESS_TO_CHANNEL_DENIED = "access-to-channel-denied";
        public static final String SEND_TO_CHANNEL_DENIED = "send-to-channel-denied";
        public static final String JOINED_CHANNEL = "joined-channel";
    }

    public static class Errors {

        public static final String NO_ACTIVE_CHANNEL = ChatColor.RED + "You have no active channel to write in. Select one with /join <channel>";
    }

    public static class Formatting {
        public static final String DEFAULT_CHANNEL_FORMAT = "<gold>[</gold><green><channel_name></green><gold>]</gold><reset><yellow><sender_vault_prefix><sender_name><sender_vault_suffix><gray>: <gray><message>";
        public static final String DEFAULT_FORMAT = "<sender_name>: <message>";
        public static final String NO_FORMAT = "<message>";
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
        public static final Function<Conversation, String> JOIN_CONVERSATION = conversation -> "/schat channel join " + conversation.getName();
    }
}
