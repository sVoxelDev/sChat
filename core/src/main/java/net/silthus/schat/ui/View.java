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

package net.silthus.schat.ui;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.message.Message;
import net.silthus.schat.settings.Configured;
import net.silthus.schat.settings.Setting;
import net.silthus.schat.settings.Settings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.JoinConfiguration.newlines;
import static net.kyori.adventure.text.format.TextDecoration.UNDERLINED;
import static net.silthus.schat.settings.Setting.setting;
import static net.silthus.schat.ui.View.Config.ACTIVE_CHANNEL_FORMAT;
import static net.silthus.schat.ui.View.Config.CHANNEL_JOIN_CONFIG;
import static net.silthus.schat.ui.View.Config.MESSAGE_SOURCE_FORMAT;
import static net.silthus.schat.ui.View.Config.defaultViewConfig;

public class View {

    private final ViewModel model;
    private final Config config;

    public View(@NonNull Chatter chatter, Config config) {
        this.model = new ViewModel(chatter);
        this.config = config;
    }

    public View(@NonNull Chatter chatter) {
        this(chatter, defaultViewConfig());
    }

    public Component render() {
        return combineMessagesAndChannels(renderMessages(), renderChannels());
    }

    @NotNull
    private Component combineMessagesAndChannels(Component messages, Component channels) {
        if (model.getMessages().isEmpty() || model.getChannels().isEmpty())
            return messages.append(channels);
        else
            return messages.append(newline()).append(channels);
    }

    private Component renderMessages() {
        return join(newlines(), getRenderedMessages());
    }

    private Component renderChannels() {
        if (model.getChannels().isEmpty())
            return empty();
        else
            return join(config.get(CHANNEL_JOIN_CONFIG), getRenderedChannels());
    }

    private List<Component> getRenderedMessages() {
        final ArrayList<Component> messages = new ArrayList<>();
        for (final Message message : model.getMessages()) {
            messages.add(renderMessage(message));
        }
        return messages;
    }

    private Component renderMessage(Message message) {
        return source(message).append(message.getText());
    }

    private Component source(Message message) {
        if (message.hasSource())
            return config.get(MESSAGE_SOURCE_FORMAT).format(message.getSource().getDisplayName());
        else
            return empty();
    }

    private List<Component> getRenderedChannels() {
        final ArrayList<Component> channels = new ArrayList<>();
        for (final Channel channel : model.getChannels()) {
            channels.add(renderChannel(channel));
        }
        return channels;
    }

    private Component renderChannel(Channel channel) {
        if (model.isActiveChannel(channel))
            return config.get(ACTIVE_CHANNEL_FORMAT).format(channel.getDisplayName());
        else
            return channel.getDisplayName();
    }

    @Getter
    public static final class Config implements Configured {

        public static final Setting<Format> ACTIVE_CHANNEL_FORMAT = setting(Format.class, "format.active_channel", name -> name.decorate(UNDERLINED));
        public static final Setting<JoinConfiguration> CHANNEL_JOIN_CONFIG = setting(JoinConfiguration.class, "format.channel_join_config", JoinConfiguration.builder()
            .prefix(text("| "))
            .separator(text(" | "))
            .suffix(text(" |"))
            .build());
        public static final Setting<Format> MESSAGE_SOURCE_FORMAT = setting(Format.class, "format.message_source", name -> name.append(text(": ")));

        public static Config defaultViewConfig() {
            return viewConfig().create();
        }

        public static Builder viewConfig() {
            return new Builder();
        }

        private final Settings settings;

        private Config(Builder builder) {
            this.settings = builder.settings.create();
        }

        public static final class Builder implements Configured.Builder<Builder> {
            private final Settings.Builder settings = Settings.settings();

            private Builder() {
            }

            @Override
            public @NotNull <V> View.Config.Builder set(@NonNull Setting<V> setting, @Nullable V value) {
                this.settings.withStatic(setting, value);
                return this;
            }

            public Config create() {
                return new Config(this);
            }
        }
    }
}
