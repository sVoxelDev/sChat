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
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import net.silthus.schat.pointer.Setting;
import net.silthus.schat.pointer.Settings;
import net.silthus.schat.ui.format.Format;
import net.silthus.schat.ui.model.ChatterViewModel;
import net.silthus.schat.ui.view.View;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.JoinConfiguration.newlines;
import static net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND;
import static net.kyori.adventure.text.event.ClickEvent.clickEvent;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.TextDecoration.UNDERLINED;
import static net.silthus.schat.channel.Channel.DISPLAY_NAME;
import static net.silthus.schat.channel.ChannelSettings.PRIVATE;
import static net.silthus.schat.pointer.Setting.setting;
import static net.silthus.schat.pointer.Settings.createSettings;
import static net.silthus.schat.ui.util.ViewHelper.renderPrivateChannelName;
import static net.silthus.schat.ui.util.ViewHelper.renderPrivateMessage;
import static net.silthus.schat.util.Iterators.lastN;

@Getter
@Accessors(fluent = true)
public final class TabbedChannelsView implements View {

    public static final Function<Component, Component> ACTIVE_CHANNEL_DECORATION = name ->
        name.colorIfAbsent(GREEN).decorate(UNDERLINED);

    public static final BiFunction<Channel, Component, Component> INACTIVE_CHANNEL_DECORATION = (channel, name) ->
        name.colorIfAbsent(GRAY)
            .hoverEvent(translatable("schat.hover.join-channel")
                .args(channel.get(DISPLAY_NAME))
                .color(GRAY)
            ).clickEvent(
                clickEvent(RUN_COMMAND, "/channel join " + channel.key())
            );

    public static final Setting<JoinConfiguration> CHANNEL_JOIN_CONFIG = setting(JoinConfiguration.class, "channel_join_config", JoinConfiguration.builder()
        .prefix(text("| "))
        .separator(text(" | "))
        .suffix(text(" |"))
        .build());

    public static final Setting<Format> MESSAGE_FORMAT = setting(Format.class, "message", (view, msg) ->
        msg.get(Message.SOURCE)
            .filter(Identity.IS_NOT_NIL)
            .map(identity -> identity.displayName().append(text(": ")))
            .orElse(Component.empty())
            .append(msg.getOrDefault(Message.TEXT, Component.empty())));

    public static final Setting<Format> ACTIVE_CHANNEL_FORMAT = setting(Format.class, "active_channel", (view, channel) ->
        channel.getOrDefault(DISPLAY_NAME, Component.empty())
            .colorIfAbsent(GREEN)
            .decorate(UNDERLINED)
    );

    public static final Setting<Format> INACTIVE_CHANNEL_FORMAT = setting(Format.class, "inactive_channel", (view, channel) ->
        channel.getOrDefault(DISPLAY_NAME, Component.empty())
            .colorIfAbsent(GRAY)
            .hoverEvent(translatable("schat.hover.join-channel")
                .args(channel.getOrDefault(DISPLAY_NAME, Component.empty()))
                .color(GRAY)
            ).clickEvent(
                clickEvent(RUN_COMMAND, "/channel join " + channel.getOrDefault(Channel.KEY, "unknown"))
            )
    );

    public static final Settings.Builder DEFAULT_FORMAT_SETTINGS = Settings.settingsBuilder()
        .withStatic(MESSAGE_FORMAT, MESSAGE_FORMAT.defaultValue())
        .withStatic(ACTIVE_CHANNEL_FORMAT, (view, channel) -> ACTIVE_CHANNEL_DECORATION.apply(channel.getOrDefault(DISPLAY_NAME, Component.empty())))
        .withStatic(INACTIVE_CHANNEL_FORMAT, (view, channel) -> INACTIVE_CHANNEL_DECORATION.apply((Channel) channel, channel.getOrDefault(DISPLAY_NAME, Component.empty())));

    public static final Setting<Settings> CHANNEL_FORMAT = setting(Settings.class, "format", DEFAULT_FORMAT_SETTINGS.create());
    public static final Setting<Settings> PRIVATE_CHANNEL_FORMAT = setting(Settings.class, "private_channel_format", Settings.settingsBuilder()
        .withStatic(MESSAGE_FORMAT, (view, message) -> renderPrivateMessage(((TabbedChannelsView) view).chatter(), (Message) message))
        .withStatic(ACTIVE_CHANNEL_FORMAT, (view, channel) -> ACTIVE_CHANNEL_DECORATION.apply(renderPrivateChannelName(((TabbedChannelsView) view).chatter(), (Channel) channel)))
        .withStatic(INACTIVE_CHANNEL_FORMAT, (view, channel) -> INACTIVE_CHANNEL_DECORATION.apply((Channel) channel, renderPrivateChannelName(((TabbedChannelsView) view).chatter(), (Channel) channel)))
        .create()
    );

    private final Chatter chatter;
    private final ChatterViewModel viewModel;
    private final Settings settings = createSettings();

    TabbedChannelsView(Chatter chatter) {
        this.chatter = chatter;
        this.viewModel = ChatterViewModel.of(this.chatter);
    }

    @Override
    public Component render() {
        final TextComponent.Builder content = text();
        final List<Component> tabNames = new ArrayList<>();

        final List<Tab> tabs = tabs();
        if (tabs.isEmpty())
            return renderBlankLines().append(renderMessages()).append(VIEW_MARKER);

        for (final Tab tab : tabs) {
            if (tab.isActive())
                content.append(tab.renderContent());
            tabNames.add(tab.renderName());
        }

        return content
            .append(newline())
            .append(joinTabs(tabNames))
            .append(VIEW_MARKER)
            .build();
    }

    @NotNull
    private Component joinTabs(List<Component> tabs) {
        if (tabs.isEmpty())
            return Component.empty();
        else
            return join(get(CHANNEL_JOIN_CONFIG), tabs);
    }

    private List<Tab> tabs() {
        return viewModel().channels().stream()
            .map(channel -> {
                Settings format = channel.is(PRIVATE) ? get(PRIVATE_CHANNEL_FORMAT) : channel.get(CHANNEL_FORMAT);
                return new Tab(
                    channel,
                    format.get(MESSAGE_FORMAT),
                    format.get(ACTIVE_CHANNEL_FORMAT),
                    format.get(INACTIVE_CHANNEL_FORMAT)
                );
            })
            .toList();
    }

    private Component renderBlankLines() {
        final int blankLineAmount = Math.max(0, get(VIEW_HEIGHT) - viewModel.messages().size());
        return blankLines(blankLineAmount);
    }

    private Component blankLines(int amount) {
        final TextComponent.Builder builder = text();
        for (int i = 0; i < amount; i++) {
            builder.append(newline());
        }
        return builder.build();
    }

    private Component renderMessages() {
        return join(newlines(), getRenderedMessages());
    }

    private List<Component> getRenderedMessages() {
        final ArrayList<Component> messages = new ArrayList<>();
        for (final Message message : viewModel.messages()) {
            if (isMessageDisplayed(message))
                messages.add(get(MESSAGE_FORMAT).format(this, message));
        }
        return messages;
    }

    private boolean isMessageDisplayed(Message message) {
        if (viewModel.noActiveChannel() && viewModel.isSystemMessage(message))
            return true;
        if (viewModel.isPrivateChannel())
            return viewModel.isSentToActiveChannel(message) && !viewModel().isSystemMessage(message);
        return viewModel.isSystemMessage(message) || viewModel.isSentToActiveChannel(message);
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    public class Tab {
        private final Channel channel;

        private Format messageFormat;
        private Format activeFormat;
        private Format inactiveFormat;

        protected Tab(Channel channel, Format messageFormat, Format activeFormat, Format inactiveFormat) {
            this.channel = channel;

            this.messageFormat = messageFormat;
            this.activeFormat = activeFormat;
            this.inactiveFormat = inactiveFormat;
        }

        public Component renderName() {
            if (isActive())
                return activeFormat().format(TabbedChannelsView.this, channel());
            else
                return inactiveFormat().format(TabbedChannelsView.this, channel());
        }

        public Component renderContent() {
            if (!isActive())
                return Component.empty();
            else
                return renderBlankLines()
                    .append(join(newlines(), renderMessages()));
        }

        private List<Component> renderMessages() {
            return messages().stream()
                .map(message -> messageFormat().format(TabbedChannelsView.this, message))
                .toList();
        }

        public boolean isActive() {
            return viewModel().isActiveChannel(channel());
        }

        @NotNull
        private Collection<Message> messages() {
            return viewModel().messages().stream()
                .filter(this::isMessageDisplayed)
                .collect(lastN(100));
        }

        private boolean isMessageDisplayed(Message message) {
            if (viewModel.noActiveChannel() && viewModel.isSystemMessage(message))
                return true;
            if (viewModel.isPrivateChannel())
                return viewModel.isSentToActiveChannel(message) && !viewModel().isSystemMessage(message);
            return viewModel.isSystemMessage(message) || viewModel.isSentToActiveChannel(message);
        }

        private Component renderBlankLines() {
            final int blankLineAmount = Math.max(0, get(VIEW_HEIGHT) - messages().size());
            return blankLines(blankLineAmount);
        }

        private Component blankLines(int amount) {
            final TextComponent.Builder builder = text();
            for (int i = 0; i < amount; i++) {
                builder.append(newline());
            }
            return builder.build();
        }
    }
}
