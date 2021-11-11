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

package net.silthus.chat.renderer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.silthus.chat.ChatTarget;
import net.silthus.chat.Conversation;
import net.silthus.chat.Message;
import net.silthus.chat.MessageRenderer;
import net.silthus.chat.identities.Chatter;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.event.ClickEvent.clickEvent;
import static net.kyori.adventure.text.event.ClickEvent.suggestCommand;
import static net.silthus.chat.Constants.Commands.JOIN_CONVERSATION;
import static net.silthus.chat.Constants.View.*;

public class TabbedMessageRenderer implements MessageRenderer {

    @Override
    public Component render(Chatter chatter, Message... messages) {
        return text().append(clearChat())
                .append(renderMessages(List.of(messages)))
                .append(newline())
                .append(footer(chatter))
                .build();
    }

    Component footer(Chatter chatter) {
        return conversationTabs(chatter);
    }

    Component conversationTabs(Chatter chatter) {
        if (chatter.getConversations().isEmpty()) {
            return noConversations();
        }

        TextComponent.Builder builder = text().append(text(CHANNEL_DIVIDER + " ").color(FRAME_COLOR));
        for (Conversation conversation : chatter.getConversations()) {
            builder.append(conversation(chatter, conversation));
        }
        return builder.build();
    }

    Component renderMessages(Collection<Message> messages) {
        return Component.join(
                JoinConfiguration.builder()
                        .separator(newline())
                        .build(),
                messages.stream()
                        .distinct()
                        .sorted()
                        .map(Message::formatted)
                        .collect(Collectors.toList())
        );
    }

    Component clearChat() {
        TextComponent.Builder builder = text();
        for (int i = 0; i < 100; i++) {
            builder.append(newline());
        }
        return builder.build();
    }

    private TextComponent noConversations() {
        return text(CHANNEL_DIVIDER + " ").color(FRAME_COLOR)
                .append(text("No Channels selected. Use ").color(INFO_COLOR))
                .append(text("/ch join <channel> ").color(COMMAND).clickEvent(suggestCommand("/ch join ")))
                .append(text("to join a channel.").color(INFO_COLOR));
    }

    private Component conversation(Chatter chatter, Conversation conversation) {
        boolean isActive = conversation.equals(chatter.getActiveConversation());
        return text().append(conversationName(chatter, conversation, isActive))
                .append(text(" " + CHANNEL_DIVIDER + " ").color(FRAME_COLOR))
                .build();
    }

    private Component conversationName(Chatter chatter, Conversation conversation, boolean isActive) {
        Component channelName = conversation.getDisplayName()
                .replaceText(playerName(chatter))
                .replaceText(conversationPartnerName(chatter, conversation))
                .clickEvent(clickEvent(ClickEvent.Action.RUN_COMMAND, JOIN_CONVERSATION.apply(conversation)));
        if (isActive)
            channelName = channelName.color(ACTIVE_COLOR).decorate(ACTIVE_DECORATION);
        else
            channelName = channelName.color(INACTIVE_COLOR);
        return channelName;
    }

    private TextReplacementConfig playerName(Chatter chatter) {
        return TextReplacementConfig.builder()
                .match("<player_name>").replacement(chatter.getDisplayName()).build();
    }

    private TextReplacementConfig conversationPartnerName(Chatter viewer, Conversation conversation) {
        List<Component> names = conversation.getTargets().stream()
                .filter(target -> !target.equals(viewer))
                .map(ChatTarget::getDisplayName)
                .collect(Collectors.toList());
        return TextReplacementConfig.builder()
                .match("<partner_name>").replacement(Component.join(JoinConfiguration.separator(Component.text(",")), names))
                .build();
    }
}
