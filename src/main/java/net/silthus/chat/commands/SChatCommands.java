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

package net.silthus.chat.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.MessageType;
import co.aikar.commands.annotation.*;
import co.aikar.locales.MessageKey;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.silthus.chat.AccessDeniedException;
import net.silthus.chat.Message;
import net.silthus.chat.SChat;
import net.silthus.chat.conversations.Channel;
import net.silthus.chat.identities.Chatter;

import static net.silthus.chat.Constants.Language.*;
import static net.silthus.chat.Constants.*;

@CommandAlias("schat")
@CommandPermission(PERMISSION_PLAYER_COMMANDS)
public class SChatCommands extends BaseCommand {

    static MessageKey key(String key) {
        return MessageKey.of(ACF_BASE_KEY + "." + key);
    }

    private final SChat plugin;

    public SChatCommands(SChat plugin) {
        this.plugin = plugin;
    }

    @Subcommand("channel|ch")
    @CommandAlias("channel")
    @CommandPermission(PERMISSION_PLAYER_CHANNEL_COMMANDS)
    public class ChannelCommands extends BaseCommand {

        @Subcommand("join")
        @CommandAlias("join|ch")
        @CommandCompletion("@channels")
        @CommandPermission(PERMISSION_PLAYER_CHANNEL_JOIN)
        public void join(@Flags("self") Chatter chatter, Channel channel) {
            try {
                chatter.join(channel);
                success(JOINED_CHANNEL, "{channel}", getChannelName(channel));
            } catch (AccessDeniedException e) {
                error(ACCESS_TO_CHANNEL_DENIED, "{channel}", getChannelName(channel));
                throw new ConditionFailedException(key(ACCESS_TO_CHANNEL_DENIED), "{channel}", getChannelName(channel));
            }
        }

        @Subcommand("message|msg|qm")
        @CommandAlias("ch|qmc")
        @CommandCompletion("@channels *")
        @CommandPermission(PERMISSION_PLAYER_CHANNEL_QUICKMESSAGE)
        public void quickMessage(@Flags("self") Chatter chatter, Channel channel, String message) {
            if (channel.canSendMessage(chatter)) {
                Message.message(chatter, message).to(channel).send();
            } else {
                error(SEND_TO_CHANNEL_DENIED, "{channel}", getChannelName(channel));
                throw new ConditionFailedException(key(SEND_TO_CHANNEL_DENIED), "{channel}", getChannelName(channel));
            }
        }
    }

    @Subcommand("directmessage")
    @CommandPermission(PERMISSION_PLAYER_DIRECT_MESSAGE)
    public class DirectMessageCommands extends BaseCommand {

        @Subcommand("send")
        @CommandAlias("m|tell|msg|message|w|dm")
        @CommandCompletion("@chatters *")
        public void directMessage(@Flags("self") Chatter source, Chatter target, String message) {
            source.message(message).to(target).send();
        }
    }

    private String getChannelName(Channel channel) {
        return LegacyComponentSerializer.legacySection().serialize(channel.getDisplayName());
    }

    private void success(String key, String... replacements) {
        getCurrentCommandIssuer().sendMessage(MessageType.INFO, key(key), replacements);
    }

    private void error(String key, String... replacements) {
        getCurrentCommandIssuer().sendMessage(MessageType.ERROR, key(key), replacements);
    }
}
