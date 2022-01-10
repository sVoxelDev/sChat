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
import lombok.NonNull;
import net.kyori.adventure.text.JoinConfiguration;
import net.silthus.schat.settings.Configured;
import net.silthus.schat.settings.Setting;
import net.silthus.schat.settings.Settings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.TextDecoration.UNDERLINED;
import static net.silthus.schat.settings.Setting.setting;

@Getter
public final class ViewConfig implements Configured {

    public static final Setting<Format> ACTIVE_CHANNEL_FORMAT = setting(Format.class, "format.active_channel", name -> name.decorate(UNDERLINED));
    public static final Setting<JoinConfiguration> CHANNEL_JOIN_CONFIG = setting(JoinConfiguration.class, "format.channel_join_config", JoinConfiguration.builder()
        .prefix(text("| "))
        .separator(text(" | "))
        .suffix(text(" |"))
        .build());
    public static final Setting<Format> MESSAGE_SOURCE_FORMAT = setting(Format.class, "format.message_source", name -> name.append(text(": ")));

    public static ViewConfig defaultViewConfig() {
        return viewConfig().create();
    }

    public static ViewConfig.Builder viewConfig() {
        return new Builder();
    }

    private final Settings settings;

    private ViewConfig(Builder builder) {
        this.settings = builder.settings.create();
    }

    public static final class Builder implements Configured.Builder<Builder> {
        private final Settings.Builder settings = Settings.settings();

        private Builder() {
        }

        @Override
        public @NotNull <V> Builder set(@NonNull Setting<V> setting, @Nullable V value) {
            this.settings.withStatic(setting, value);
            return this;
        }

        public ViewConfig create() {
            return new ViewConfig(this);
        }
    }
}
