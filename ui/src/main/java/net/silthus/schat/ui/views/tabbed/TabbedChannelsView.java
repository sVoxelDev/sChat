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
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.eventbus.Subscribe;
import net.silthus.schat.events.channel.ChatterJoinedChannelEvent;
import net.silthus.schat.events.channel.ChatterLeftChannelEvent;
import net.silthus.schat.events.chatter.ChatterChangedActiveChannelEvent;
import net.silthus.schat.events.chatter.ChatterReceivedMessageEvent;
import net.silthus.schat.message.Message;
import net.silthus.schat.pointer.Setting;
import net.silthus.schat.ui.util.ViewHelper;
import net.silthus.schat.ui.view.View;
import net.silthus.schat.ui.view.ViewConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.JoinConfiguration.newlines;
import static net.silthus.schat.channel.ChannelSettings.PRIVATE;
import static net.silthus.schat.pointer.Setting.setting;
import static net.silthus.schat.ui.view.ViewConfig.FORMAT_CONFIG;

@Getter
@Accessors(fluent = true)
public class TabbedChannelsView implements View {

    public static final Setting<JoinConfiguration> CHANNEL_JOIN_CONFIG = setting(JoinConfiguration.class, "channel_join_config", JoinConfiguration.builder()
        .prefix(text("| "))
        .separator(text(" | "))
        .suffix(text(" |"))
        .build());

    private final Chatter chatter;
    private final ViewConfig config;

    private final SortedMap<Channel, Tab> tabs = new TreeMap<>();

    public TabbedChannelsView(Chatter chatter, ViewConfig config) {
        this.chatter = chatter;
        this.config = config;
        chatter.channels().forEach(channel -> addTab(new ChannelTab(this, channel, channel.get(FORMAT_CONFIG))));
        update();
    }

    @Subscribe
    protected void onJoinedChannel(ChatterJoinedChannelEvent event) {
        if (isNotApplicable(event.chatter()))
            return;
        addTab(event.channel());
        update();
    }

    @Subscribe
    protected void onLeftChannel(ChatterLeftChannelEvent event) {
        if (isNotApplicable(event.chatter()))
            return;
        removeTab(event.channel());
        update();
    }

    @Subscribe
    protected void onChangeChannel(ChatterChangedActiveChannelEvent event) {
        if (isNotApplicable(event.chatter()))
            return;
        tab(event.newChannel()).ifPresent(ChannelTab::activate);
        update();
    }

    @Subscribe
    protected void onMessage(ChatterReceivedMessageEvent event) {
        if (isNotApplicable(event.chatter()))
            return;
        tabs().values().forEach(tab -> tab.onReceivedMessage(event.message()));
        update();
    }

    @Override
    public Component render() {
        final TextComponent.Builder content = text()
            .append(ViewHelper.renderBlankLines(blankLineCount()));

        final Collection<Tab> tabs = tabs().values();

        boolean hasActiveTab = false;
        for (final Tab tab : tabs) {
            if (tab.isActive()) {
                content.append(tab.render());
                hasActiveTab = true;
            }
        }

        if (!hasActiveTab)
            content.append(renderSystemMessages());

        return content
            .append(newline())
            .append(joinTabs(tabs.stream().map(Tab::renderName).toList()))
            .build();
    }

    private Component renderSystemMessages() {
        return join(newlines(), chatter().messages().stream()
            .filter(Message.IS_SYSTEM_MESSAGE)
            .map(message -> message.getOrDefault(Message.FORMATTED, config.systemMessageFormat().format(this, message)))
            .toList()
        );
    }

    @NotNull
    private Component joinTabs(List<Component> tabs) {
        if (tabs.isEmpty())
            return Component.empty();
        else
            return join(config().channelJoinConfig(), tabs);
    }

    private void addTab(Channel channel) {
        if (channel.is(PRIVATE))
            addTab(new PrivateChannelTab(this, channel, config().privateChatFormat()));
        else
            addTab(new ChannelTab(this, channel, channel.get(FORMAT_CONFIG)));
    }

    private void addTab(ChannelTab tab) {
        tabs.put(tab.channel(), tab);
    }

    private void removeTab(Channel channel) {
        tabs.remove(channel);
    }

    protected Optional<ChannelTab> tab(@Nullable Channel channel) {
        if (channel == null)
            return Optional.empty();
        return Optional.ofNullable((ChannelTab) tabs.get(channel));
    }

    private boolean isNotApplicable(Chatter chatter) {
        return !chatter().equals(chatter);
    }

    private int blankLineCount() {
        return tabs.values().stream().map(Tab::length).min(Comparator.naturalOrder())
            .orElse(config.height());
    }
}
