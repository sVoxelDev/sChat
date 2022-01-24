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

package net.silthus.schat.channel;

import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.channel.Channel.DISPLAY_NAME;
import static net.silthus.schat.channel.Channel.createChannel;
import static net.silthus.schat.channel.ChannelHelper.channelWith;
import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class ChannelTests {

    public static final @NotNull TextComponent MY_CHANNEL = text("My Channel");

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

            @Nested class when_display_name_changes {
                @BeforeEach
                void setUp() {
                    channel.set(DISPLAY_NAME, MY_CHANNEL);
                }

                @Test
                void then_updates_property() {
                    assertThat(channel.getDisplayName()).isEqualTo(MY_CHANNEL);
                }
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
