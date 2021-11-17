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
import net.kyori.adventure.text.format.NamedTextColor;
import net.silthus.chat.*;
import net.silthus.chat.config.Language;
import net.silthus.chat.identities.PlayerChatter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND;
import static net.kyori.adventure.text.event.ClickEvent.clickEvent;
import static net.kyori.adventure.text.event.ClickEvent.suggestCommand;
import static net.kyori.adventure.text.event.HoverEvent.showText;
import static net.silthus.chat.Constants.Commands.JOIN_CONVERSATION;
import static net.silthus.chat.Constants.Commands.LEAVE_CONVERSATION;
import static net.silthus.chat.Constants.PERMISSION_SELECT_MESSAGE;
import static net.silthus.chat.Constants.View.*;

public final class TabbedMessageRenderer implements MessageRenderer {

    @Override
    public Component render(View view) {
        return text().append(clearChat())
                .append(renderMessages(view))
                .append(newline())
                .append(footer(view))
                .append(conversationTabs(view))
                .build();
    }

    Component footer(View view) {
        return view.footer();
    }

    Component conversationTabs(View view) {
        if (view.conversations().isEmpty())
            return noConversations();

        TextComponent.Builder builder = text().append(CHANNEL_DIVIDER.append(text(" ")).color(FRAME_COLOR));
        view.conversations()
                .forEach(conversation -> builder.append(conversation(view, conversation)));

        return builder.build();
    }

    Component renderMessages(View view) {
        return Component.join(
                JoinConfiguration.builder()
                        .separator(newline())
                        .build(),
                view.messages().stream()
                        .map(message -> renderMessage(view, message))
                        .toList()
        );
    }

    Component clearChat() {
        TextComponent.Builder builder = text();
        for (int i = 0; i < 100; i++) {
            builder.append(newline());
        }
        return builder.build();
    }

    private Component renderMessage(View view, Message message) {
        if (view.chatter().hasPermission(PERMISSION_SELECT_MESSAGE)) {
            final TextComponent prefix = view.selectedMessage().filter(msg -> msg.equals(message))
                    .map(msg -> text("> ").color(NamedTextColor.RED))
                    .orElse(empty());
            return prefix.append(message.formatted()
                    .clickEvent(clickEvent(RUN_COMMAND, Constants.Commands.SELECT_MESSAGE.apply(message)))
                    .hoverEvent(showText(lang(Constants.Language.Formats.SELECT_MESSAGE))));
        } else {
            return message.formatted();
        }
    }

    private TextComponent noConversations() {
        return text().append(CHANNEL_DIVIDER.append(text(" ")).color(FRAME_COLOR))
                .append(text("Use ").color(INFO_COLOR))
                .append(text("/ch join <channel> ").color(COMMAND).clickEvent(suggestCommand("/ch join ")))
                .append(text("to join a channel.").color(INFO_COLOR))
                .build();
    }

    private Component conversation(View view, Conversation conversation) {
        boolean isActive = view.activeConversation()
                .filter(active -> active.equals(conversation))
                .isPresent();
        return text().append(conversationName(view, conversation, isActive))
                .append(text(" ").append(CHANNEL_DIVIDER).append(text(" "))).color(FRAME_COLOR)
                .build();
    }

    private Component conversationName(View view, Conversation conversation, boolean isActive) {
        Component conversationName = leaveConversationIcon(view, conversation)
                .append(conversation.getDisplayName()
                        .replaceText(playerName(view.chatter()))
                        .replaceText(conversationPartnerName(view.chatter(), conversation))
                        .clickEvent(clickEvent(RUN_COMMAND, JOIN_CONVERSATION.apply(conversation)))
                );
        if (isActive)
            conversationName = conversationName.color(ACTIVE_COLOR).decorate(ACTIVE_DECORATION);
        else if (view.unreadMessageCount(conversation) > 0)
            conversationName = conversationName.color(UNREAD_COLOR).append(smallNumber(view.unreadMessageCount(conversation)).color(UNREAD_COUNT_COLOR));
        else
            conversationName = conversationName.color(INACTIVE_COLOR);
        return conversationName;
    }

    @NotNull
    private Component leaveConversationIcon(View view, Conversation conversation) {
        if (!view.chatter().canLeave(conversation)) return Component.empty();
        return text().append(CLOSE_CHANNEL.color(CLOSE_CHANNEL_COLOR)
                .clickEvent(clickEvent(RUN_COMMAND, LEAVE_CONVERSATION.apply(conversation)))).build();
    }

    private final Map<Integer, Character> numberMap = Map.of(
            0, '₀',
            1, '₁',
            2, '₂',
            3, '₃',
            4, '₄',
            5, '₅',
            6, '₆',
            7, '₇',
            8, '₈',
            9, '₉'
    );

    private Component smallNumber(int number) {
        StringBuilder str = new StringBuilder();
        while (number > 0) {
            str.insert(0, numberMap.get(number % 10));
            number = number / 10;
        }
        return Component.text(str.toString());
    }

    private TextReplacementConfig playerName(Chatter chatter) {
        return TextReplacementConfig.builder()
                .match("<player_name>").replacement(chatter.getDisplayName()).build();
    }

    private TextReplacementConfig conversationPartnerName(Chatter viewer, Conversation conversation) {
        List<Component> names = conversation.getTargets().stream()
                .filter(target -> !target.equals(viewer))
                .filter(target -> target instanceof PlayerChatter)
                .map(ChatTarget::getDisplayName)
                .collect(Collectors.toList());
        return TextReplacementConfig.builder()
                .match("<partner_name>").replacement(Component.join(JoinConfiguration.separator(Component.text(",")), names))
                .build();
    }

    private Component lang(String key) {
        return lang().get(key);
    }

    private Language lang() {
        return SChat.instance().language().section(Constants.Language.Formats.BASE_KEY);
    }
}
