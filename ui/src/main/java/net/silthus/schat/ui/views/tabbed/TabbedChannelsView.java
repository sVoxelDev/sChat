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

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.Getter;
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

import static java.util.stream.Collectors.toList;
import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
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

    public TabbedChannelsView(Chatter chatter) {
        this.chatter = chatter;
        this.viewModel = ChatterViewModel.of(this.chatter);
    }

    @Override
    public Component render() {
        final TextComponent.Builder content = text();

        final List<Tab> tabs = tabs();
        if (tabs.isEmpty())
            tabs.add(new NoChannelsTab(this, get(MESSAGE_FORMAT)));

        boolean hasActiveTab = false;
        for (final Tab tab : tabs) {
            if (tab.isActive()) {
                content.append(tab.renderContent());
                hasActiveTab = true;
            }
        }

        if (!hasActiveTab)
            content.append(new NoChannelsTab(this, get(MESSAGE_FORMAT)).renderContent());

        return content
            .append(newline())
            .append(joinTabs(tabs.stream().map(Tab::renderName).toList()))
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
                return (Tab) new ChannelTab(
                    this,
                    channel,
                    format.get(MESSAGE_FORMAT),
                    format.get(ACTIVE_CHANNEL_FORMAT),
                    format.get(INACTIVE_CHANNEL_FORMAT)
                );
            }).collect(toList());
    }
}
