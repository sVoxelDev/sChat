/*
 * This file is part of sChat, licensed under the MIT License.
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.silthus.schat.ui.views;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.message.Message;
import net.silthus.schat.pointer.Settings;
import net.silthus.schat.ui.View;
import net.silthus.schat.ui.ViewModel;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.JoinConfiguration.newlines;
import static net.silthus.schat.pointer.Settings.createSettings;

@Getter
final class TabbedChannelsView implements View {

    private final ViewModel viewModel;
    private final Settings settings = createSettings();

    TabbedChannelsView(@NonNull ViewModel viewModel) {
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
            return empty();
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
            return empty();
    }

    private List<Component> getRenderedChannels() {
        final ArrayList<Component> channels = new ArrayList<>();
        for (final Channel channel : viewModel.getChannels()) {
            channels.add(renderChannel(channel));
        }
        return channels;
    }

    private Component renderChannel(Channel channel) {
        if (viewModel.isActiveChannel(channel))
            return get(ACTIVE_CHANNEL_FORMAT).format(channel.getDisplayName());
        else
            return channel.getDisplayName();
    }

}
