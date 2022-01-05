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

package net.silthus.schat;

import java.util.function.Function;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.settings.Configured;
import net.silthus.schat.settings.Setting;

import static net.silthus.schat.channel.Channel.channel;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public final class ChannelHelper {

    private ChannelHelper() {
    }

    public static <V> Channel channelWith(String key, Setting<V> setting, V value) {
        return createChannelWith(key, builder -> builder.setting(setting, value));
    }

    public static <V> Channel channelWith(Setting<V> setting, V value) {
        return createChannelWith(builder -> builder.setting(setting, value));
    }

    public static Channel channelWith(ConfiguredSetting<?>... settings) {
        return createChannelWith(builder -> {
            for (final ConfiguredSetting<?> setting : settings) {
                setting.configure(builder);
            }
            return builder;
        });
    }

    public static Channel createChannelWith(String key, Function<Channel.Builder, Channel.Builder> config) {
        return config.apply(channel(key)).create();
    }

    public static Channel createChannelWith(Function<Channel.Builder, Channel.Builder> config) {
        return createChannelWith(randomAlphabetic(10).toLowerCase(), config);
    }

    public record ConfiguredSetting<V>(Setting<V> setting, V value) {

        public static <V> ConfiguredSetting<V> set(Setting<V> setting, V value) {
            return new ConfiguredSetting<>(setting, value);
        }

        public void configure(Configured.Builder<?> builder) {
            builder.setting(setting(), value());
        }
    }

}
