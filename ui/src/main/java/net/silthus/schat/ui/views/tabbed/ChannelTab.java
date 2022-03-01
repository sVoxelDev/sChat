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
import net.silthus.schat.message.MessageSource;
import net.silthus.schat.pointer.Settings;
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
    private final TabFormatConfig config;
    private final Settings settings;

    protected ChannelTab(@NonNull TabbedChannelsView view,
                         @NonNull Channel channel,
                         @NonNull TabFormatConfig config) {
        this.view = view;
        this.channel = channel;
        this.config = config;
        this.settings = Settings.settingsBuilder()
            .withForward(NAME, channel, Channel.DISPLAY_NAME)
            .withForward(KEY, channel, Channel.KEY)
            .withStatic(CHANNEL, channel)
            .withStatic(VIEWER, view.chatter())
            .create()
            .copyFrom(channel.settings());
    }

    @Override
    public Component renderName() {
        Component name;
        if (isActive()) {
            name = name().colorIfAbsent(config.activeColor());
            if (config.activeDecoration() != null)
                name = name.decorate(config.activeDecoration());
        } else {
            name = name().color(config.inactiveColor());
            if (config.inactiveDecoration() != null)
                name = name.decorate(config.inactiveDecoration());
            name = joinChannel(name);
        }

        return closeChannel().append(name);
    }

    protected Component name() {
        return channel().displayName();
    }

    @Override
    public Component render() {
        return renderMessages(messages());
    }

    protected Component renderMessages(@NonNull Collection<Message> messages) {
        return join(newlines(), messages.stream()
            .map(this::renderMessage)
            .toList());
    }

    protected Component renderMessage(Message message) {
        if (message.source().equals(MessageSource.nil()) && message.type() == Message.Type.SYSTEM)
            return message.getOrDefault(Message.FORMATTED, message.text());
        else if (message.source().equals(view().chatter()))
            return message.getOrDefault(Message.FORMATTED, config.selfMessageFormat().format(view, message));
        else
            return message.getOrDefault(Message.FORMATTED, config.messageFormat().format(view, message));
    }

    @Override
    public int length() {
        return messages().size();
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

    protected boolean isMessageDisplayed(Message message) {
        return message.type() == Message.Type.SYSTEM || message.channels().contains(channel);
    }

    private Component joinChannel(Component component) {
        return component.hoverEvent(translatable("schat.hover.join-channel")
            .args(name())
            .color(GRAY)
        ).clickEvent(
            clickEvent(RUN_COMMAND, "/channel join " + channel.key())
        );
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
