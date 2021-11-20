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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ChatColor;
import net.silthus.chat.conversations.Channel;
import org.bukkit.NamespacedKey;

import java.util.Objects;
import java.util.function.Function;

import static net.kyori.adventure.text.Component.text;

public final class Constants {

    public static final String PLUGIN_NAME = "sChat";
    public static final int BSTATS_ID = 13304;
    public static final Key NBT_MESSAGE_ID = Key.key("schat:messageid");
    public static final Key NBT_IS_SCHAT_MESSAGE = Key.key("schat:message");

    public static final String PERMISSION_PLAYER_COMMANDS = "schat.player";
    public static final String PERMISSION_PLAYER_CHANNEL_COMMANDS = "schat.player.channel";
    public static final String PERMISSION_PLAYER_CHANNEL_JOIN = "schat.player.channel.join";
    public static final String PERMISSION_PLAYER_CHANNEL_LEAVE = "schat.player.channel.leave";
    public static final String PERMISSION_PLAYER_CHANNEL_QUICKMESSAGE = "schat.player.channel.quickmessage";
    public static final String PERMISSION_PLAYER_DIRECT_MESSAGE = "schat.player.directmessage";
    public static final String PERMISSION_SELECT_MESSAGE = "schat.message.select";
    public static final String PERMISSION_MESSAGE_DELETE = "schat.message.delete";
    public static final String PERMISSION_ADMIN_RELOAD = "schat.admin.reload";
    public static final String PERMISSION_ADMIN_OTHERS = "schat.admin.others";
    public static final String PERMISSION_NICKNAME_SET = "schat.nickname.set";
    public static final String PERMISSION_NICKNAME_SET_OTHERS = "schat.nickname.set.others";
    public static final String PERMISSION_NICKNAME_SET_BLOCKED = "schat.nickname.set.blocked";

    public static class Targets {

        public static final String EMPTY = "N/A";
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

        public static class Commands {

            public static final String COMMANDS_BASE = "commands";
            public static final String ACCESS_TO_CHANNEL_DENIED = "access-to-channel-denied";
            public static final String SEND_TO_CHANNEL_DENIED = "send-to-channel-denied";
            public static final String CANNOT_SEND_TO_SELF = "cannot-send-to-self";
            public static final String JOINED_CHANNEL = "joined-channel";
            public static final String LEAVE_CHANNEL = "leave-channel";
            public static final String INVALID_CONVERSATION = "invalid-conversation";
            public static final String PLUGIN_RELOADED = "plugin-reloaded";
            public static final String SELECTED_MESSAGE = "message.selected";
            public static final String DELETED_MESSAGE = "message.deleted";
            public static final String DESELECTED_MESSAGE = "message.deselected";
            public static final String DELETE_MESSAGE_BUTTON = "message.delete-button";
            public static final String DESELECT_MESSAGE_BUTTON = "message.deselect-button";

            public static class Nicknames {

                public static final String NICKNAMES_BASE = "nickname";
                public static final String INVALID = "invalid";
                public static final String BLOCKED = "blocked";
                public static final String CHANGED = "changed";
                public static final String RESET = "reset";
            }

        }

        public static class Formats {
            public static final String BASE_KEY = "formats";
            public static final String PLAYER_CLICK = "player.click-to-send";
            public static final String SELECT_MESSAGE = "message.select-to-moderate";
        }
    }

    public static class Errors {

        public static final String NO_ACTIVE_CHANNEL = ChatColor.RED + "You have no active channel to write in. Select one with /join <channel>";
    }

    public static class Formatting {

        public static final String DEFAULT = "default";
        public static final String DEFAULT_FORMAT = "<yellow><sender_display_name><gray>: <message>";

        public static final String CHANNEL = "channel";
        public static final String DEFAULT_CHANNEL_FORMAT = "<channel_formatted><yellow><sender><gray>: <message>";

        public static final String NO_FORMAT = "none";
        public static final String DEFAULT_NO_FORMAT = "<message>";

        public static final String SENDER = "sender";
        public static final String SENDER_FORMAT = "<sender_vault_prefix><sender_no_vault><sender_vault_suffix>";

        public static final String SENDER_NO_VAULT = "sender_no_vault";
        public static final String SENDER_NO_VAULT_FORMAT = "<click:run_command:/schat message send <sender_name>><hover:show_text:'<sender_hover>'><sender_display_name></hover></click>";

        public static final String SENDER_HOVER = "sender_hover";
        public static final String SENDER_HOVER_FORMAT = """
                <gray>Name: <yellow><sender_name>
                <gray><i>Click to send <yellow><sender_display_name></yellow> a message.""";

        public static final String CHANNEL_FORMATTED = "channel_formatted";
        public static final String CHANNEL_FORMATTED_FORMAT = "<gold>[</gold><green><channel_name></green><gold>]</gold>";

        public static final String PRIVATE_MESSAGE = "private_message";
        public static final String PRIVATE_MESSAGE_FORMAT = "<yellow><sender_no_vault><gray>: <message>";
    }

    public static class View {

        public static final TextColor FRAME_COLOR = NamedTextColor.DARK_GRAY;
        public static final TextColor INFO_COLOR = NamedTextColor.GRAY;
        public static final TextColor COMMAND = NamedTextColor.AQUA;
        public static final TextColor ACTIVE_COLOR = NamedTextColor.GREEN;
        public static final TextColor UNREAD_COLOR = NamedTextColor.WHITE;
        public static final TextColor UNREAD_COUNT_COLOR = NamedTextColor.RED;
        public static final TextColor INACTIVE_COLOR = NamedTextColor.GRAY;
        public static final TextColor CLOSE_CHANNEL_COLOR = NamedTextColor.DARK_RED;
        public static final TextDecoration ACTIVE_DECORATION = TextDecoration.UNDERLINED;

        public static final Component CLOSE_CHANNEL = text("\u2718"); // ✘
        public static final Component CHANNEL_DIVIDER = text("\u2502"); // │
        public static final Component LEFT_FRAME = text("\u250C"); // ┌
        public static final Component FRAME_SPACER = text("\u2500"); // ─
        public static final Component RIGHT_FRAME = text("\u2510"); // ┐
    }

    public static class Commands {

        public static final Function<Conversation, String> JOIN_CHANNEL = conversation -> "/schat channel join " + conversation.getUniqueId();
        public static final Function<Conversation, String> LEAVE_CHANNEL = conversation -> "/schat channel leave " + conversation.getUniqueId();
        public static final Function<Conversation, String> JOIN_CONVERSATION = conversation -> "/schat conversations set-active " + conversation.getUniqueId();
        public static final Function<Conversation, String> LEAVE_CONVERSATION = conversation -> "/schat conversations leave " + conversation.getUniqueId();
        public static final Function<ChatSource, String> PRIVATE_MESSAGE = source -> "/schat message send " + source.getUniqueId();
        public static final Function<Message, String> SELECT_MESSAGE = message -> "/schat message select " + message.getId();
        public static final Function<Message, String> DELETE_MESSAGE = message -> "/schat message delete " + message.getId();
    }

    public static class BungeeCord {

        public static final String BUNGEECORD_CHANNEL = "BungeeCord";
        public static final String SEND_MESSAGE = "schat:send-message";
        public static final String DELETE_MESSAGE = "schat:delete-message";
        public static final String SEND_CHATTER = "schat:send-chatter";
        public static final String SEND_CONVERSATION = "schat:send-conversation";
    }

    public static class Scopes {

        public static final String SERVER = "server";
        public static final String GLOBAL = "global";
        public static final String WORLD = "world";
    }

    public static class Persistence {

        public static final NamespacedKey PLAYER_DATA = Objects.requireNonNull(NamespacedKey.fromString("schat:player_data"));
    }
}
