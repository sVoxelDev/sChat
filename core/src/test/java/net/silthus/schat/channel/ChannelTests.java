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

import java.util.function.Consumer;
import net.kyori.adventure.text.TextComponent;
import net.silthus.schat.event.EventBusMock;
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
        assertThat(channel.getTargets()).isEmpty();
    }

    @Test
    void given_channel_with_same_key_then_channels_are_equal() {
        Channel channel1 = createChannel("test");
        Channel channel2 = createChannel("test");
        assertThat(channel1).isEqualTo(channel2);
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
            ChannelImpl.setPrototype(builder -> builder.eventBus(eventBus));
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
