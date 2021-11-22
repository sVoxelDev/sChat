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
import co.aikar.commands.annotation.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.silthus.chat.*;
import net.silthus.chat.config.BroadcastConfig;
import net.silthus.chat.conversations.Channel;
import net.silthus.chat.conversations.PrivateConversation;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.silthus.chat.Constants.*;
import static net.silthus.chat.Constants.Language.Commands.*;

@CommandAlias("schat")
public class SChatCommands extends SChatBaseCommand {

    public SChatCommands(SChat plugin) {
        super(plugin);
    }

    @Subcommand("reload")
    @CommandPermission(PERMISSION_ADMIN_RELOAD)
    public void reload() {
        plugin.reload();
        send(RELOAD_SUCCESS, "{channels}", plugin.getChannelRegistry().size() + "");
    }

    @CommandAlias("broadcast")
    @Subcommand("broadcast")
    @CommandPermission(PERMISSION_ADMIN_BROADCAST)
    public void broadcast(@Flags("self") Chatter chatter, String message) {
        final BroadcastConfig broadcast = plugin.getPluginConfig().broadcast();
        final ChatTarget[] targets = plugin.getConversationManager().getConversations()
                .stream().filter(conversation -> {
                    if (!broadcast.includePrivateChats())
                        return !(conversation instanceof PrivateConversation);
                    return true;
                }).toArray(ChatTarget[]::new);
        chatter.message(message)
                .to(targets)
                .conversation(null)
                .format(broadcast.format())
                .send();
        chatter.updateView();
    }

    @Subcommand("conversations")
    public class ConversationCommands extends BaseCommand {

        @Subcommand("set-active")
        @CommandPermission(PERMISSION_PLAYER_CHANNEL_JOIN)
        public void setActive(@Flags("self") Chatter chatter, Conversation conversation) {
            if (!chatter.getConversations().contains(conversation)) {
                throw new ConditionFailedException(messageKey(INVALID_CONVERSATION));
            }
            chatter.setActiveConversation(conversation);
            chatter.updateView();
        }

        @Subcommand("leave")
        @CommandPermission(PERMISSION_PLAYER_CHANNEL_LEAVE)
        private void leave(@Flags("self") Chatter chatter, Conversation conversation) {
            if (!chatter.getConversations().contains(conversation)) {
                throw new ConditionFailedException(messageKey(INVALID_CONVERSATION));
            }
            chatter.unsubscribe(conversation);
            chatter.updateView();
        }
    }

    @Subcommand("channel|ch")
    @CommandAlias("channel")
    public class ChannelCommands extends BaseCommand {

        @Subcommand("join")
        @CommandAlias("join|ch")
        @CommandCompletion("@channels")
        @CommandPermission(PERMISSION_PLAYER_CHANNEL_JOIN)
        public void join(@Flags("self") Chatter chatter, Channel channel) {
            try {
                chatter.join(channel);
                success(JOINED_CHANNEL, "{channel}", getChannelName(channel));
                chatter.updateView();
            } catch (AccessDeniedException e) {
                error(ACCESS_TO_CHANNEL_DENIED, "{channel}", getChannelName(channel));
                throw new ConditionFailedException(messageKey(ACCESS_TO_CHANNEL_DENIED), "{channel}", getChannelName(channel));
            }
        }

        @Subcommand("leave")
        @CommandAlias("leave|quit")
        @CommandCompletion("@channels")
        @CommandPermission(PERMISSION_PLAYER_CHANNEL_LEAVE)
        public void leave(@Flags("self") Chatter chatter, Channel channel) {
            chatter.unsubscribe(channel);
            success(LEAVE_CHANNEL, "{channel}", getChannelName(channel));
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
                error(SEND_TO_CHANNEL_DENIED, "{channel}", getChannelName(channel));
                throw new ConditionFailedException(messageKey(SEND_TO_CHANNEL_DENIED), "{channel}", getChannelName(channel));
            }
        }
    }

    @Subcommand("message")
    public class DirectMessageCommands extends BaseCommand {

        @Subcommand("send")
        @CommandAlias("tell|m|msg|message|w|dm|pm|qm")
        @CommandCompletion("@chatters *")
        @CommandPermission(PERMISSION_PLAYER_DIRECT_MESSAGE)
        public void directMessage(@Flags("self") Chatter source, Chatter target, @Optional String message) {
            if (source.equals(target))
                throw new ConditionFailedException(messageKey(CANNOT_SEND_TO_SELF));

            if (message != null) {
                source.message(message).to(target).send();
            } else {
                source.setActiveConversation(Conversation.privateConversation(source, target));
                source.updateView();
            }
        }

        @Subcommand("select")
        @CommandPermission(PERMISSION_SELECT_MESSAGE)
        public void select(@Flags("self") Chatter chatter, Message message) {
            final Boolean messageIsSelected = chatter.getView().selectedMessage()
                    .map(msg -> msg.equals(message)).orElse(false);
            if (messageIsSelected) {
                deselectMessage(chatter);
                sender().sendActionBar(lang(DESELECTED_MESSAGE));
            } else {
                chatter.getView().selectedMessage(message);
                chatter.getView().footer(deleteButton(message).append(abortButton(message)));
                sender().sendActionBar(lang(SELECTED_MESSAGE));
            }
            chatter.updateView();
        }

        @Subcommand("delete")
        @CommandPermission(PERMISSION_MESSAGE_DELETE)
        public void delete(@Flags("self") Chatter chatter, Message message) {
            deselectMessage(chatter);
            message.delete();
            sender().sendActionBar(lang(DELETED_MESSAGE));
        }

        private void deselectMessage(@Flags("self") Chatter chatter) {
            chatter.getView().selectedMessage(null);
            chatter.getView().footer(null);
        }

        @NotNull
        private Component deleteButton(Message message) {
            if (getCurrentCommandIssuer().hasPermission(PERMISSION_MESSAGE_DELETE))
                return lang(DELETE_MESSAGE_BUTTON).clickEvent(runCommand(Commands.DELETE_MESSAGE.apply(message)));
            return Component.empty();
        }

        @NotNull
        private Component abortButton(Message message) {
            return lang(DESELECT_MESSAGE_BUTTON).clickEvent(runCommand(Commands.SELECT_MESSAGE.apply(message)));
        }
    }

    private String getChannelName(Channel channel) {
        return LegacyComponentSerializer.legacySection().serialize(channel.getDisplayName());
    }

    private void success(String key, String... replacements) {
        send(key, replacements);
    }

    private void error(String key, String... replacements) {
        send(key, replacements);
    }
}
