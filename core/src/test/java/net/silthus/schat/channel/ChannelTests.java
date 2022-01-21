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

import net.kyori.adventure.text.TextComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.channel.Channel.createChannel;
import static net.silthus.schat.channel.ChannelHelper.channelWith;
import static net.silthus.schat.channel.ChannelHelper.randomChannel;
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

    @Nested class create_channel {
        private Channel channel = randomChannel();

        @BeforeEach
        void setUp() {
            channel = randomChannel();
        }

        @Test
        void given_null_key_throws() {
            assertInvalidKey(null);
        }

        @ParameterizedTest()
        @ValueSource(strings = {
            "",
            "  ",
            "ab cd"
        })
        void given_invalid_key_throws(String key) {
            assertInvalidKey(key);
        }

        @Test
        void targets_are_empty() {
            assertThat(channel.getTargets()).isEmpty();
        }

        @Nested class given_no_display_name {
            @Test
            void uses_key_as_display_name() {
                assertThat(channel.getDisplayName()).isEqualTo(text(channel.getKey()));
            }
        }

        @Nested class given_display_name {
            private final TextComponent name = text("Test Channel");

            @BeforeEach
            void setUp() {
                channel = channelWith(builder -> builder.name(name));
            }

            @Test
            void uses_display_name() {
                assertThat(channel.getDisplayName()).isEqualTo(name);
            }
        }
    }

    @Nested class given_channel_with_same_key {
        private final Channel channel1 = createChannel("test");
        private final Channel channel2 = createChannel("test");

        @Test
        void channels_are_equal() {
            assertThat(channel1).isEqualTo(channel2);
        }
    }
}
