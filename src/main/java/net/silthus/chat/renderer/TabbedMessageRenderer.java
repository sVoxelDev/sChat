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
import net.kyori.adventure.text.format.TextDecoration;
import net.silthus.chat.ChatTarget;
import net.silthus.chat.Conversation;
import net.silthus.chat.Message;
import net.silthus.chat.MessageRenderer;
import net.silthus.chat.identities.Chatter;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.event.ClickEvent.clickEvent;
import static net.kyori.adventure.text.event.ClickEvent.suggestCommand;
import static net.silthus.chat.Constants.Commands.JOIN_CONVERSATION;
import static net.silthus.chat.Constants.View.*;

public final class TabbedMessageRenderer implements MessageRenderer {

    @Override
    public Component render(View view) {
        return text().append(clearChat())
                .append(renderMessages(view.messages()))
                .append(newline())
                .append(footer(view))
                .build();
    }

    Component footer(View view) {
        return ChatUtil.wrapText(empty(),
                        LEFT_FRAME.color(FRAME_COLOR),
                        FRAME_SPACER.color(FRAME_COLOR).decorate(TextDecoration.STRIKETHROUGH),
                        FRAME_SPACER.color(FRAME_COLOR).decorate(TextDecoration.STRIKETHROUGH)
                ).append(newline())
                .append(conversationTabs(view));
    }

    Component conversationTabs(View view) {
        if (view.conversations().isEmpty())
            return noConversations();

        TextComponent.Builder builder = text().append(CHANNEL_DIVIDER.append(text(" ")).color(FRAME_COLOR));
        view.conversations()
                .forEach(conversation -> builder.append(conversation(view, conversation)));

        return builder.build();
    }

    Component renderMessages(Collection<Message> messages) {
        return Component.join(
                JoinConfiguration.builder()
                        .separator(newline())
                        .build(),
                sortMessages(messages)
        );
    }

    Component clearChat() {
        TextComponent.Builder builder = text();
        for (int i = 0; i < 100; i++) {
            builder.append(newline());
        }
        return builder.build();
    }

    @NotNull
    private List<Component> sortMessages(Collection<Message> messages) {
        return messages.stream()
                .distinct()
                .sorted()
                .map(Message::formatted)
                .collect(Collectors.toList());
    }

    private TextComponent noConversations() {
        return text().append(CHANNEL_DIVIDER.append(text(" ")).color(FRAME_COLOR))
                .append(text("Use ").color(INFO_COLOR))
                .append(text("/ch join <channel> ").color(COMMAND).clickEvent(suggestCommand("/ch join ")))
                .append(text("to join a channel.").color(INFO_COLOR))
                .build();
    }

    private Component conversation(View view, Conversation conversation) {
        boolean isActive = conversation.equals(view.activeConversation());
        return text().append(conversationName(view, conversation, isActive))
                .append(text(" ").append(CHANNEL_DIVIDER).append(text(" "))).color(FRAME_COLOR)
                .build();
    }

    private Component conversationName(View view, Conversation conversation, boolean isActive) {
        Component conversationName = conversation.getDisplayName()
                .replaceText(playerName(view.chatter()))
                .replaceText(conversationPartnerName(view.chatter(), conversation))
                .clickEvent(clickEvent(ClickEvent.Action.RUN_COMMAND, JOIN_CONVERSATION.apply(conversation)));
        if (isActive)
            conversationName = conversationName.color(ACTIVE_COLOR).decorate(ACTIVE_DECORATION);
        else
            conversationName = conversationName.color(INACTIVE_COLOR);
        return conversationName;
    }

    private TextReplacementConfig playerName(Chatter chatter) {
        return TextReplacementConfig.builder()
                .match("<player_name>").replacement(chatter.getDisplayName()).build();
    }

    private TextReplacementConfig conversationPartnerName(Chatter viewer, Conversation conversation) {
        List<Component> names = conversation.getTargets().stream()
                .filter(target -> !target.equals(viewer))
                .filter(target -> target instanceof Chatter)
                .map(ChatTarget::getDisplayName)
                .collect(Collectors.toList());
        return TextReplacementConfig.builder()
                .match("<partner_name>").replacement(Component.join(JoinConfiguration.separator(Component.text(",")), names))
                .build();
    }
}
