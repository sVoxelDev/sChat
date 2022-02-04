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

import java.util.function.Consumer;
import net.kyori.adventure.text.TextComponent;
import net.silthus.schat.eventbus.EventBusMock;
import net.silthus.schat.events.message.SendChannelMessageEvent;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.MessageTarget;
import net.silthus.schat.message.Targets;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
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
import static net.silthus.schat.checks.CanJoinChannelCheck.CAN_JOIN_CHANNEL;
import static net.silthus.schat.message.MessageHelper.randomMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class ChannelTests {

    public static final @NotNull TextComponent MY_CHANNEL = text("My Channel");

    private Channel channel = randomChannel();

    @BeforeEach
    void setUp() {
        channel = randomChannel();
    }

    private MessageTarget addMockTarget() {
        final MessageTarget target = mock(MessageTarget.class);
        channel.addTarget(target);
        return target;
    }

    private void assertInvalidKey(String key) {
        assertCreateChannelThrows(key, Channel.InvalidKey.class);
    }

    @SuppressWarnings({"SameParameterValue"})
    private void assertCreateChannelThrows(String key, Class<? extends Throwable> exceptionType) {
        assertThatExceptionOfType(exceptionType).isThrownBy(() -> createChannel(key));
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
        assertThat(channel.targets()).isEmpty();
    }

    @Test
    void given_channel_with_same_key_then_channels_are_equal() {
        Channel channel1 = createChannel("test");
        Channel channel2 = createChannel("test");
        assertThat(channel1).isEqualTo(channel2);
    }

    @Test
    void same_message_is_only_processed_once() {
        final MessageTarget target = mock(MessageTarget.class);
        final Message message = randomMessage();
        channel.addTarget(target);
        channel.sendMessage(message);
        channel.sendMessage(message);

        verify(target).sendMessage(message);
    }

    @Nested class given_no_display_name {
        @Test
        void uses_key_as_display_name() {
            assertThat(channel.displayName()).isEqualTo(text(channel.key()));
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
            assertThat(channel.displayName()).isEqualTo(name);
        }

        @Nested class when_display_name_changes {
            @BeforeEach
            void setUp() {
                channel.set(DISPLAY_NAME, MY_CHANNEL);
            }

            @Test
            void then_updates_property() {
                assertThat(channel.displayName()).isEqualTo(MY_CHANNEL);
            }
        }
    }

    @Nested class given_checks {
        @BeforeEach
        void setUp() {
            channel = Channel.channel("test").check(CAN_JOIN_CHANNEL).create();
        }
    }

    @Nested class events {
        private EventBusMock eventBus;
        private boolean calledEvent = false;

        @BeforeEach
        void setUp() {
            eventBus = new EventBusMock();
            channel = randomChannel();
        }

        @AfterEach
        void tearDown() {
            eventBus.close();
        }

        private void onEvent(Consumer<SendChannelMessageEvent> handler) {
            eventBus.on(SendChannelMessageEvent.class, handler);
        }

        @Test
        void sendMessage_fires_ChannelMessageEvent() {
            onEvent(event -> calledEvent = true);

            channel.sendMessage(randomMessage());

            assertThat(calledEvent).isTrue();
        }

        @Test
        void sendMessage_uses_modified_targets() {
            final MessageTarget target = mock(MessageTarget.class);
            onEvent(event -> event.targets(Targets.of(target)));

            channel.sendMessage(randomMessage());

            verify(target).sendMessage(any());
        }

        @Test
        void cancelled_event_cancels_message_sending() {
            final MessageTarget target = addMockTarget();
            onEvent(event -> event.cancelled(true));

            channel.sendMessage(randomMessage());

            verify(target, never()).sendMessage(any());
        }

        @Test
        void sendMessage_uses_message_of_event() {
            final MessageTarget target = addMockTarget();
            final Message replacedMessage = randomMessage();
            onEvent(event -> event.message(replacedMessage));

            channel.sendMessage(randomMessage());

            verify(target).sendMessage(replacedMessage);
        }
    }
}
