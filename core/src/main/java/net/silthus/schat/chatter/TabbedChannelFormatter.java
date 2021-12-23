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

package net.silthus.schat.chatter;

import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.format.Formatter;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.Messages;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.JoinConfiguration.newlines;
import static net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND;
import static net.kyori.adventure.text.event.ClickEvent.clickEvent;
import static net.kyori.adventure.text.format.TextDecoration.UNDERLINED;

public final class TabbedChannelFormatter implements Formatter<Chatter> {

    private static final String JOIN_COMMAND = "/schat channel join ";
    private static final @NotNull TextComponent DIVIDER = text(" | ");
    private static final MessagesFormatter MESSAGES = new MessagesFormatter();
    private static final ChannelFormatter CHANNEL = new ChannelFormatter();

    @Override
    public Component format(final Chatter chatter) {
        return messages(chatter).append(channels(chatter));
    }

    private Component messages(final Chatter chatter) {
        return MESSAGES.format(chatter.getMessages());
    }

    @NotNull
    private TextComponent channels(Chatter chatter) {
        final List<Channel> channels = chatter.getChannels();
        if (channels.isEmpty())
            return empty();

        final TextComponent.Builder builder = text().append(text("| "));
        for (final Channel channel : channels) {
            builder.append(channel(chatter, channel)).append(DIVIDER);
        }

        return builder.build();
    }

    @NotNull
    private Component channel(final Chatter chatter, final Channel channel) {
        return chatter.isActiveChannel(channel) ? CHANNEL.format(channel).decorate(UNDERLINED) : CHANNEL.format(channel);
    }

    @NotNull
    private TextComponent empty() {
        return text().append(text("No joined channels!")).build();
    }

    private static class MessageFormatter implements Formatter<Message> {

        public Component format(Message message) {
            return source(message).append(text(message.getText()));
        }

        private Component source(Message message) {
            if (message.getSource() != null)
                return message.getSource().getDisplayName().append(text(": "));
            else
                return Component.empty();
        }
    }

    private static class MessagesFormatter implements Formatter<Messages> {

        private final MessageFormatter messageFormatter = new MessageFormatter();

        @Override
        public Component format(final Messages messages) {
            return Component.join(
                newlines(),
                messages
                    .stream()
                    .map(messageFormatter::format)
                    .toList()
            );
        }
    }

    public static class ChannelFormatter implements Formatter<Channel> {

        public @NotNull Component format(Channel channel) {
            return channel.getDisplayName()
                .clickEvent(clickEvent(RUN_COMMAND, JOIN_COMMAND + channel.getKey()));
        }
    }
}
