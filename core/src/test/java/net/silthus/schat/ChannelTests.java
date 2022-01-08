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

import net.kyori.adventure.text.TextComponent;
import net.silthus.schat.channel.Channel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.ChannelHelper.channelWith;
import static net.silthus.schat.ChannelHelper.randomChannel;
import static net.silthus.schat.channel.Channel.createChannel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class ChannelTests {

    private void assertInvalidKey(String key) {
        assertCreateChannelThrows(key, Channel.InvalidKey.class);
    }

    @SuppressWarnings({"SameParameterValue"})
    private void assertCreateChannelThrows(String key, Class<? extends Throwable> exceptionType) {
        assertThatExceptionOfType(exceptionType).isThrownBy(() -> createChannel(key));
    }

    @Test
    void givenNullKey_throws() {
        assertInvalidKey(null);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "  ",
        "ab cd"
    })
    void givenInvalidKey_throws(String key) {
        assertInvalidKey(key);
    }

    @Test
    void givenSameKey_areEquals() {
        final Channel channel1 = createChannel("test");
        final Channel channel2 = createChannel("test");

        assertThat(channel1).isEqualTo(channel2);
    }

    @Test
    void givenNoDisplayName_usesKey() {
        final Channel channel = randomChannel();
        assertThat(channel.getDisplayName()).isEqualTo(text(channel.getKey()));
    }

    @Test
    void givenDisplayName_usesDisplayName() {
        final TextComponent name = text("Test Channel");
        final Channel channel = channelWith(builder -> builder.name(name));
        assertThat(channel.getDisplayName()).isEqualTo(name);
    }

    @Test
    void givenNewChannel_usersIsEmpty() {
        final Channel channel = randomChannel();
        assertThat(channel.getTargets()).isEmpty();
    }
}
