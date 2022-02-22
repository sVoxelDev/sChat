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
package net.silthus.schat.ui.views.tabbed;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.message.Message;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND;
import static net.kyori.adventure.text.event.ClickEvent.clickEvent;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.silthus.schat.channel.ChannelSettings.FORCED;
import static net.silthus.schat.ui.format.Format.ACTIVE_CHANNEL_FORMAT;
import static net.silthus.schat.ui.format.Format.INACTIVE_CHANNEL_FORMAT;

@SuppressWarnings("CheckStyle")
@Getter
@Setter
@Accessors(fluent = true)
public class ChannelTab extends AbstractTab {

    private static final Component CLOSE_CHAR = Component.text("\u274C", RED); // ❌

    private final Channel channel;

    protected ChannelTab(@NonNull TabbedChannelsView view,
                         @NonNull Channel channel) {
        super(view, channel.settings());
        this.channel = channel;
    }

    @Override
    public Component renderName() {
        final Component name;
        if (isActive())
            name = get(ACTIVE_CHANNEL_FORMAT).format(view(), channel());
        else
            name = get(INACTIVE_CHANNEL_FORMAT).format(view(), channel());

        return closeChannel().append(name);
    }

    @Override
    public boolean isActive() {
        return view().viewModel().isActiveChannel(channel());
    }

    @Override
    protected boolean isMessageDisplayed(Message message) {
        final ChatterViewModel viewModel = view().viewModel();
        if (viewModel.noActiveChannel() && viewModel.isSystemMessage(message))
            return true;
        if (viewModel.isPrivateChannel())
            return viewModel.isSentToActiveChannel(message) && !viewModel.isSystemMessage(message);
        return viewModel.isSystemMessage(message) || viewModel.isSentToActiveChannel(message);
    }

    private Component closeChannel() {
        if (channel.isNot(FORCED))
            return CLOSE_CHAR.hoverEvent(
                translatable("schat.hover.leave-channel")
                    .args(channel.displayName()
                        .color(GRAY)
                    )
            ).clickEvent(clickEvent(RUN_COMMAND, "/channel leave " + channel.key()));
        else
            return empty();
    }
}
