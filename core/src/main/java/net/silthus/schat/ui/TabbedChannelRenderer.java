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

package net.silthus.schat.ui;

import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.silthus.schat.chatter.Chatter;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.JoinConfiguration.newlines;
import static net.kyori.adventure.text.JoinConfiguration.separator;
import static net.kyori.adventure.text.format.TextDecoration.UNDERLINED;

final class TabbedChannelRenderer implements Renderer<Chatter> {

    public static final int MAX_LINES = 100;
    private static final @NotNull TextComponent LEFT_DIVIDER = text("| ");
    private static final @NotNull TextComponent DIVIDER = text(" | ");
    private static final @NotNull TextComponent RIGHT_DIVIDER = text(" |");
    private static final MessagesRenderer MESSAGES = new MessagesRenderer();
    private static final ChannelRenderer CHANNEL = new ChannelRenderer();

    @Override
    public Component render(final Chatter chatter) {
        final ChatterViewModel viewModel = new ChatterViewModel(chatter);
        return messages(viewModel)
            .append(channels(viewModel));
    }

    private Component blankLines(int amount) {
        final TextComponent.Builder builder = text();
        for (int i = 0; i < amount; i++) {
            builder.append(newline());
        }
        return builder.build();
    }

    private Component messages(final ChatterViewModel chatter) {
        final List<MessageViewModel> messages = chatter.getMessages();
        final Component blankLines = blankLines(Math.max(0, MAX_LINES - messages.size()));
        return messages.isEmpty() ? blankLines.append(empty()) : blankLines.append(MESSAGES.render(messages)).append(newline());
    }

    @NotNull
    private Component channels(ChatterViewModel chatter) {
        final List<ChannelViewModel> channels = chatter.getChannels();
        if (channels.isEmpty())
            return noJoinedChannel();

        return LEFT_DIVIDER
            .append(channelList(chatter, channels))
            .append(RIGHT_DIVIDER);
    }

    @NotNull
    private Component channelList(ChatterViewModel chatter, List<ChannelViewModel> channels) {
        return join(separator(DIVIDER), renderedChannels(chatter, channels));
    }

    private List<@NotNull Component> renderedChannels(ChatterViewModel chatter, List<ChannelViewModel> channels) {
        return channels.stream().map(channel -> channel(chatter, channel)).toList();
    }

    @NotNull
    private Component channel(final ChatterViewModel chatter, final ChannelViewModel channel) {
        return chatter.isActiveChannel(channel) ? CHANNEL.render(channel).decorate(UNDERLINED) : CHANNEL.render(channel);
    }

    @NotNull
    private TextComponent noJoinedChannel() {
        return text().append(text("No joined channels!")).build();
    }

    private static class MessagesRenderer implements Renderer<List<MessageViewModel>> {

        private final MessageRenderer messageFormatter = new MessageRenderer();

        @Override
        public Component render(final List<MessageViewModel> messages) {
            return join(
                newlines(),
                messages
                    .stream()
                    .map(messageFormatter::render)
                    .toList()
            );
        }
    }

    private static class MessageRenderer implements Renderer<MessageViewModel> {

        public Component render(MessageViewModel message) {
            return source(message).append(message.getText());
        }

        private Component source(MessageViewModel message) {
            final Component source = message.getSource();
            if (message.hasSource())
                return source.append(text(": "));
            return source;
        }
    }

    public static class ChannelRenderer implements Renderer<ChannelViewModel> {

        public @NotNull Component render(ChannelViewModel channel) {
            return channel.getDisplayName();
        }
    }
}
