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

import java.util.Collection;
import java.util.Comparator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.message.Message;
import net.silthus.schat.pointer.Settings;
import net.silthus.schat.ui.util.ViewHelper;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.JoinConfiguration.newlines;
import static net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND;
import static net.kyori.adventure.text.event.ClickEvent.clickEvent;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.silthus.schat.channel.ChannelSettings.FORCED;
import static net.silthus.schat.channel.ChannelSettings.PRIVATE;
import static net.silthus.schat.ui.format.Format.ACTIVE_TAB_FORMAT;
import static net.silthus.schat.ui.format.Format.INACTIVE_TAB_FORMAT;
import static net.silthus.schat.ui.format.Format.MESSAGE_FORMAT;
import static net.silthus.schat.util.Iterators.lastN;

@SuppressWarnings("CheckStyle")
@Getter
@Setter
@Accessors(fluent = true)
@EqualsAndHashCode(of = {"channel"})
public class ChannelTab implements Tab {

    private static final Component CLOSE_CHAR = Component.text("\u274C", RED); // ❌
    private static final Comparator<Message> MESSAGE_COMPARATOR = Comparator.comparing(Message::timestamp);

    private final TabbedChannelsView view;
    private final Channel channel;
    private final Settings settings;

    protected ChannelTab(@NonNull TabbedChannelsView view,
                         @NonNull Channel channel) {
        this.view = view;
        this.channel = channel;
        this.settings = Settings.settingsBuilder()
            .withForward(NAME, channel, Channel.DISPLAY_NAME)
            .withForward(KEY, channel, Channel.KEY)
            .withStatic(CHANNEL, channel)
            .withStatic(VIEWER, view.chatter())
            .create()
            .copyFrom(channel.settings());
    }

    @Override
    public Component name() {
        final Component name;
        if (isActive())
            name = channel.get(ACTIVE_TAB_FORMAT).format(view(), this);
        else
            name = channel.get(INACTIVE_TAB_FORMAT).format(view(), this);

        return closeChannel().append(name);
    }

    @Override
    public Component render() {
        return ViewHelper.renderBlankLines(blankLineCount())
            .append(join(newlines(), new MessageRenderer(view(), channel.get(MESSAGE_FORMAT)).renderMessages(messages())));
    }

    protected @NotNull Collection<Message> messages() {
        return view().chatter().messages().stream()
            .filter(this::isMessageDisplayed)
            .sorted(MESSAGE_COMPARATOR)
            .collect(lastN(100));
    }

    @Override
    public boolean isActive() {
        return view().chatter().isActiveChannel(channel);
    }

    private boolean isMessageDisplayed(Message message) {
        if (channel.is(PRIVATE))
            return message.type() != Message.Type.SYSTEM;
        else
            return message.type() == Message.Type.SYSTEM || message.channels().contains(channel);
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

    private int blankLineCount() {
        return Math.max(0, view().config().height() - messages().size());
    }
}
