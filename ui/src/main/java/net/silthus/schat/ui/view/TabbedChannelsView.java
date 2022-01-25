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

package net.silthus.schat.ui.view;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.message.Message;
import net.silthus.schat.pointer.Settings;
import net.silthus.schat.ui.model.ChatterViewModel;
import net.silthus.schat.view.View;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.JoinConfiguration.newlines;
import static net.silthus.schat.pointer.Settings.createSettings;

@Getter
final class TabbedChannelsView implements View {

    private final ChatterViewModel viewModel;
    private final Settings settings = createSettings();

    TabbedChannelsView(ChatterViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public Component render() {
        return renderBlankLines()
            .append(combineMessagesAndChannels(renderMessages(), renderChannels()))
            .append(VIEW_MARKER);
    }

    private Component renderBlankLines() {
        final int blankLineAmount = Math.max(0, get(VIEW_HEIGHT) - viewModel.getMessages().size());
        return blankLines(blankLineAmount);
    }

    private Component blankLines(int amount) {
        final TextComponent.Builder builder = text();
        for (int i = 0; i < amount; i++) {
            builder.append(newline());
        }
        return builder.build();
    }

    @NotNull
    private Component combineMessagesAndChannels(Component messages, Component channels) {
        if (viewModel.getMessages().isEmpty() || viewModel.getChannels().isEmpty())
            return messages.append(channels);
        else
            return messages.append(newline()).append(channels);
    }

    private Component renderMessages() {
        return join(newlines(), getRenderedMessages());
    }

    private Component renderChannels() {
        if (viewModel.getChannels().isEmpty())
            return Component.empty();
        else
            return join(get(CHANNEL_JOIN_CONFIG), getRenderedChannels());
    }

    private List<Component> getRenderedMessages() {
        final ArrayList<Component> messages = new ArrayList<>();
        for (final Message message : viewModel.getMessages()) {
            messages.add(renderMessage(message));
        }
        return messages;
    }

    private Component renderMessage(Message message) {
        return source(message).append(message.text());
    }

    private Component source(Message message) {
        if (message.hasSource())
            return get(MESSAGE_SOURCE_FORMAT).format(message.source().getDisplayName());
        else
            return Component.empty();
    }

    private List<Component> getRenderedChannels() {
        final ArrayList<Component> channels = new ArrayList<>();
        for (final Channel channel : viewModel.getChannels()) {
            if (viewModel.isActiveChannel(channel))
                channels.add(get(ACTIVE_CHANNEL_FORMAT).format(channel));
            else
                channels.add(get(INACTIVE_CHANNEL_FORMAT).format(channel));
        }
        return channels;
    }

}
