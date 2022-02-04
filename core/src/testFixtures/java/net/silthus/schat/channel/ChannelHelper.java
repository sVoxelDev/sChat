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

package net.silthus.schat.channel;

import java.util.function.Function;
import net.silthus.schat.pointer.Configured;
import net.silthus.schat.pointer.Setting;

import static net.silthus.schat.channel.Channel.channel;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public final class ChannelHelper {

    private ChannelHelper() {
    }

    public static Channel randomChannel() {
        return channelWith();
    }

    public static <V> Channel channelWith(String key, Setting<V> setting, V value) {
        return channelWith(key, builder -> builder.set(setting, value));
    }

    public static <V> Channel channelWith(Setting<V> setting, V value) {
        return channelWith(builder -> builder.set(setting, value));
    }

    public static Channel channelWith(String key, ConfiguredSetting<?>... settings) {
        return channelWith(key, builder -> {
            for (final ConfiguredSetting<?> setting : settings) {
                setting.configure(builder);
            }
            return builder;
        });
    }

    public static Channel channelWith(ConfiguredSetting<?>... settings) {
        return channelWith(randomAlphabetic(10), settings);
    }

    public static Channel channelWith(String key, Function<Channel.Builder, Channel.Builder> config) {
        return config.apply(channel(key)).create();
    }

    public static Channel channelWith(Function<Channel.Builder, Channel.Builder> config) {
        return channelWith(randomAlphabetic(10).toLowerCase(), config);
    }

    public record ConfiguredSetting<V>(Setting<V> setting, V value) {

        public static <V> ConfiguredSetting<V> set(Setting<V> setting, V value) {
            return new ConfiguredSetting<>(setting, value);
        }

        public void configure(Configured.Builder<?> builder) {
            builder.set(setting(), value());
        }

        public void applyTo(Channel channel) {
            channel.set(setting, value);
        }
    }

}
