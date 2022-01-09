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

import lombok.Getter;
import net.kyori.adventure.text.JoinConfiguration;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.TextDecoration.UNDERLINED;

@Getter
public final class ViewConfig {

    public static final Format DEFAULT_ACTIVE_CHANNEL_FORMAT = name -> name.decorate(UNDERLINED);
    public static final @NotNull JoinConfiguration DEFAULT_CHANNEL_JOIN_CONFIG = JoinConfiguration.builder()
        .prefix(text("| "))
        .separator(text(" | "))
        .suffix(text(" |"))
        .build();
    public static final Format DEFAULT_MESSAGE_SOURCE_FORMAT = name -> name.append(text(": "));

    public static ViewConfig defaultViewConfig() {
        return viewConfig().create();
    }

    public static ViewConfig.Builder viewConfig() {
        return new Builder();
    }

    private final Format activeChannelFormat;
    private final JoinConfiguration channelJoinConfig;
    private final Format messageSourceFormat;

    private ViewConfig(Builder builder) {
        this.activeChannelFormat = builder.activeChannelFormat;
        this.channelJoinConfig = builder.channelJoinConfig;
        this.messageSourceFormat = builder.messageSourceFormat;
    }

    public static final class Builder {

        private Format activeChannelFormat = DEFAULT_ACTIVE_CHANNEL_FORMAT;
        private JoinConfiguration channelJoinConfig = DEFAULT_CHANNEL_JOIN_CONFIG;
        private Format messageSourceFormat = DEFAULT_MESSAGE_SOURCE_FORMAT;

        private Builder() {
        }

        public Builder activeChannelFormat(Format activeChannelFormat) {
            this.activeChannelFormat = activeChannelFormat;
            return this;
        }

        public Builder channelJoinConfig(JoinConfiguration channelJoinConfig) {
            this.channelJoinConfig = channelJoinConfig;
            return this;
        }

        public Builder messageSourceFormat(Format messageSourceFormat) {
            this.messageSourceFormat = messageSourceFormat;
            return this;
        }

        public ViewConfig create() {
            return new ViewConfig(this);
        }
    }
}
