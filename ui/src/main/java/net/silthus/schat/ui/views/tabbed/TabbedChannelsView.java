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
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.pointer.Setting;
import net.silthus.schat.pointer.Settings;
import net.silthus.schat.ui.format.Format;
import net.silthus.schat.ui.model.ChatterViewModel;
import net.silthus.schat.ui.view.View;
import net.silthus.schat.ui.view.ViewConfig;
import org.jetbrains.annotations.NotNull;

import static java.util.stream.Collectors.toList;
import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.channel.ChannelSettings.PRIVATE;
import static net.silthus.schat.pointer.Setting.setting;

@Getter
@Accessors(fluent = true)
public final class TabbedChannelsView implements View {

    public static final Setting<JoinConfiguration> CHANNEL_JOIN_CONFIG = setting(JoinConfiguration.class, "channel_join_config", JoinConfiguration.builder()
        .prefix(text("| "))
        .separator(text(" | "))
        .suffix(text(" |"))
        .build());

    private final Chatter chatter;
    private final ChatterViewModel viewModel;
    private final ViewConfig config;

    public TabbedChannelsView(Chatter chatter, ViewConfig config) {
        this.chatter = chatter;
        this.config = config;
        this.viewModel = ChatterViewModel.of(this.chatter);
    }

    @Override
    public Component render() {
        final TextComponent.Builder content = text();

        final List<Tab> tabs = tabs();
        if (tabs.isEmpty())
            tabs.add(new NoChannelsTab(this, config().messageFormat()));

        boolean hasActiveTab = false;
        for (final Tab tab : tabs) {
            if (tab.isActive()) {
                content.append(tab.renderContent());
                hasActiveTab = true;
            }
        }

        if (!hasActiveTab)
            content.append(new NoChannelsTab(this, config().messageFormat()).renderContent());

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
            return join(config().channelJoinConfig(), tabs);
    }

    private List<Tab> tabs() {
        return viewModel().channels().stream()
            .map(channel -> {
                Settings format = channel.is(PRIVATE) ? config.privateChat() : channel.settings();
                return (Tab) new ChannelTab(
                    this,
                    channel,
                    format.get(Format.MESSAGE_FORMAT),
                    format.get(Format.ACTIVE_CHANNEL_FORMAT),
                    format.get(Format.INACTIVE_CHANNEL_FORMAT)
                );
            }).collect(toList());
    }
}
