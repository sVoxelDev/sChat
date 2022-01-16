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

import java.util.List;
import net.kyori.adventure.text.TextComponent;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.MessageTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.MessageHelper.randomMessage;
import static net.silthus.schat.channel.Channel.createChannel;
import static net.silthus.schat.channel.ChannelHelper.channelWith;
import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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

    @Nested class given_channel_with_targets {
        private Channel channel;
        private List<MessageTarget> targets;

        @BeforeEach
        void setUp() {
            channel = randomChannel();
            targets = List.of(mock(MessageTarget.class), mock(MessageTarget.class), mock(MessageTarget.class));
            targets.forEach(target -> channel.addTarget(target));
        }

        @Nested class when_sendMessage_is_called {
            private Message message;

            @BeforeEach
            void setUp() {
                message = randomMessage();
                channel.sendMessage(message);
            }

            @Test
            void then_sendMessage_is_called_on_all_targets() {
                for (final MessageTarget target : targets) {
                    verify(target).sendMessage(message);
                }
            }
        }
    }
}
