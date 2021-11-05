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

package net.silthus.chat.layout;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.Template;
import net.silthus.chat.ChatLayout;
import net.silthus.chat.Conversation;
import net.silthus.chat.Message;
import net.silthus.chat.targets.PlayerChatter;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.event.ClickEvent.clickEvent;
import static net.kyori.adventure.text.event.ClickEvent.suggestCommand;
import static net.silthus.chat.Constants.Commands.JOIN_CONVERSATION;
import static net.silthus.chat.Constants.View.*;

public class TabbedChatLayout implements ChatLayout {

    @Override
    public Component render(PlayerChatter chatter, Message... messages) {

        return text().append(clearChat())
                .append(renderMessages(List.of(messages)))
                .append(newline())
                .append(footer(chatter))
                .build();
    }

    public Component footer(PlayerChatter chatter) {
        return conversationTabs(chatter);
    }

    Component conversationTabs(PlayerChatter chatter) {
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
        List<Component> components = messages.stream()
                .distinct()
                .sorted()
                .map(Message::formatted)
                .collect(Collectors.toList());
        return Component.join(
                JoinConfiguration.builder()
                        .separator(newline())
                        .build(),
                components
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

    private Component conversation(PlayerChatter chatter, Conversation conversation) {
        boolean isActive = conversation.equals(chatter.getActiveConversation());
        return text().append(conversationName(chatter, conversation, isActive))
                .append(text(" " + CHANNEL_DIVIDER + " ").color(FRAME_COLOR))
                .build();
    }

    private Component conversationName(PlayerChatter chatter, Conversation conversation, boolean isActive) {
        Component channelName = conversation.getName()
                .replaceText(builder -> builder.match("<player_name>").replacement(chatter.getName()))
                .clickEvent(clickEvent(ClickEvent.Action.RUN_COMMAND, JOIN_CONVERSATION.apply(conversation)));
        if (isActive)
            channelName = channelName.color(ACTIVE_COLOR).decorate(ACTIVE_DECORATION);
        else
            channelName = channelName.color(INACTIVE_COLOR);
        return channelName;
    }

    private Template playerName(PlayerChatter chatter) {
        return Template.template("player_name", chatter.getName());
    }
}
