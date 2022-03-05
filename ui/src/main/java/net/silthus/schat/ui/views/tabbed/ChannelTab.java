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

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Stream;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.MessageSource;
import net.silthus.schat.pointer.Settings;
import org.jetbrains.annotations.Nullable;

import static java.util.stream.Collectors.toMap;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.JoinConfiguration.newlines;
import static net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND;
import static net.kyori.adventure.text.event.ClickEvent.clickEvent;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.silthus.schat.channel.ChannelSettings.FORCED;
import static net.silthus.schat.ui.util.ViewHelper.formatMessage;
import static net.silthus.schat.ui.util.ViewHelper.subscriptOf;
import static net.silthus.schat.ui.view.ViewConfig.FORMAT_CONFIG;

@SuppressWarnings("CheckStyle")
@Getter
@Setter
@Accessors(fluent = true)
@EqualsAndHashCode(of = {"channel"})
public class ChannelTab implements Tab {

    private static final Component CLOSE_CHAR = text("\u274C", RED); // ❌
    private static final Comparator<Message> MESSAGE_COMPARATOR = Comparator.comparing(Message::timestamp);

    private final TabbedChannelsView view;
    private final Channel channel;
    private final Settings settings;
    private final SortedMap<Message, Component> messages;
    private int unreadCount = 0;

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
        this.messages = new TreeMap<>(Stream.concat(channel.messages().stream(), view.chatter().messages().stream())
            .filter(this::isMessageDisplayed)
            .distinct()
            .collect(toMap(message -> message, this::renderMessage)));
        if (!isActive())
            unreadCount(messages.size());
    }

    private TabFormatConfig config() {
        return channel().get(FORMAT_CONFIG);
    }

    @Override
    public Component renderName() {
        final Component name;
        if (isActive())
            name = style(name(), config().activeColor(), config().activeDecoration());
        else if (config().highlightUnread() && isUnread())
            name = joinChannel(style(name(), config().unreadColor(), config().unreadDecoration()));
        else
            name = joinChannel(style(name(), config().inactiveColor(), config().inactiveDecoration()));

        final Component tabName = closeChannel().append(name);
        if (config().showUnreadCount() && isUnread())
            return tabName.append(style(text(subscriptOf(unreadCount())), config().unreadCountColor(), config().unreadCountDecoration()));
        else
            return tabName;
    }

    protected Component name() {
        return channel().displayName();
    }

    @Override
    public Component render() {
        if (isActive())
            resetUnreadCounter();
        return join(newlines(), messages.values());
    }

    protected void refresh() {
        for (Map.Entry<Message, Component> entry : messages().entrySet()) {
            entry.setValue(renderMessage(entry.getKey()));
        }
    }

    @Override
    public int length() {
        return messages.size();
    }

    @Override
    public void onReceivedMessage(Message message) {
        if (!isMessageDisplayed(message))
            return;
        this.messages.put(message, renderMessage(message));
        if (!isActive())
            unreadCount++;
    }

    public boolean isUnread() {
        return unreadCount() > 0;
    }

    protected void activate() {
        resetUnreadCounter();
    }

    @Override
    public boolean isActive() {
        return view().chatter().isActiveChannel(channel);
    }

    protected boolean isMessageDisplayed(Message message) {
        return message.type() == Message.Type.SYSTEM || message.channels().contains(channel);
    }

    protected Component renderMessage(Message message) {
        if (message.source().equals(MessageSource.nil()) && message.type() == Message.Type.SYSTEM)
            return formatMessage(view, message, view.config().systemMessageFormat());
        else if (message.source().equals(view().chatter()))
            return formatMessage(view, message, config().selfMessageFormat());
        else
            return formatMessage(view, message, config().messageFormat());
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

    private Component style(@NonNull Component component, @Nullable TextColor color, @Nullable TextDecoration decoration) {
        return decorate(color(component, color), decoration);
    }

    private Component color(@NonNull Component component, @Nullable TextColor color) {
        if (color != null)
            return component.color(color);
        else
            return component;
    }

    private Component decorate(@NonNull Component component, @Nullable TextDecoration decoration) {
        if (decoration != null)
            return component.decorate(decoration);
        else
            return component;
    }

    private void resetUnreadCounter() {
        unreadCount(0);
    }
}
