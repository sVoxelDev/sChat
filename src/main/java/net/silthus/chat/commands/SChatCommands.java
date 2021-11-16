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
import net.silthus.chat.Conversation;
import net.silthus.chat.Message;
import net.silthus.chat.SChat;
import net.silthus.chat.conversations.Channel;
import net.silthus.chat.identities.Chatter;

import static net.silthus.chat.Constants.*;

@CommandAlias("schat")
@CommandPermission(PERMISSION_PLAYER_COMMANDS)
public class SChatCommands extends BaseCommand {

    static MessageKey key(String key) {
        return MessageKey.of(Language.Commands.BASE_KEY + "." + key);
    }

    private final SChat plugin;

    public SChatCommands(SChat plugin) {
        this.plugin = plugin;
    }

    @Subcommand("reload")
    @CommandPermission(PERMISSION_ADMIN_RELOAD)
    public void reload() {
        plugin.reload();
        success(Language.Commands.PLUGIN_RELOADED);
    }

    @Subcommand("conversations")
    public class ConversationCommands extends BaseCommand {

        @Subcommand("set-active")
        public void setActive(@Flags("self") Chatter chatter, Conversation conversation) {
            if (!chatter.getConversations().contains(conversation)) {
                throw new ConditionFailedException(key(Language.Commands.INVALID_CONVERSATION));
            }
            chatter.setActiveConversation(conversation);
            chatter.updateView();
        }

        @Subcommand("leave")
        private void leave(@Flags("self") Chatter chatter, Conversation conversation) {
            if (!chatter.getConversations().contains(conversation)) {
                throw new ConditionFailedException(key(Language.Commands.INVALID_CONVERSATION));
            }
            chatter.unsubscribe(conversation);
            chatter.updateView();
        }
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
                success(Language.Commands.JOINED_CHANNEL, "{channel}", getChannelName(channel));
                chatter.updateView();
            } catch (AccessDeniedException e) {
                error(Language.Commands.ACCESS_TO_CHANNEL_DENIED, "{channel}", getChannelName(channel));
                throw new ConditionFailedException(key(Language.Commands.ACCESS_TO_CHANNEL_DENIED), "{channel}", getChannelName(channel));
            }
        }

        @Subcommand("leave")
        @CommandAlias("leave|quit")
        @CommandCompletion("@channels")
        @CommandPermission(PERMISSION_PLAYER_CHANNEL_LEAVE)
        public void leave(@Flags("self") Chatter chatter, Channel channel) {
            chatter.unsubscribe(channel);
            success(Language.Commands.LEAVE_CHANNEL, "{channel}", getChannelName(channel));
            chatter.updateView();
        }

        @Subcommand("message|msg|qm")
        @CommandAlias("ch|qmc")
        @CommandCompletion("@channels *")
        @CommandPermission(PERMISSION_PLAYER_CHANNEL_QUICKMESSAGE)
        public void quickMessage(@Flags("self") Chatter chatter, Channel channel, String message) {
            if (chatter.canSendMessage(channel)) {
                Message.message(chatter, message).to(channel).send();
            } else {
                error(Language.Commands.SEND_TO_CHANNEL_DENIED, "{channel}", getChannelName(channel));
                throw new ConditionFailedException(key(Language.Commands.SEND_TO_CHANNEL_DENIED), "{channel}", getChannelName(channel));
            }
        }
    }

    @Subcommand("directmessage")
    @CommandPermission(PERMISSION_PLAYER_DIRECT_MESSAGE)
    public class DirectMessageCommands extends BaseCommand {

        @Subcommand("send")
        @CommandAlias("m|tell|msg|message|w|dm|pm|qm")
        @CommandCompletion("@chatters *")
        public void directMessage(@Flags("self") Chatter source, Chatter target, @Optional String message) {
            if (source.equals(target))
                throw new ConditionFailedException(key(Language.Commands.CANNOT_SEND_TO_SELF));

            if (message != null) {
                source.message(message).to(target).send();
            } else {
                source.setActiveConversation(Conversation.direct(source, target));
                source.updateView();
            }
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
