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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.kyori.adventure.text.TextComponent;
import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.commands.JoinChannelCommand;
import net.silthus.schat.commands.SendMessageResult;
import net.silthus.schat.eventbus.EventBusMock;
import net.silthus.schat.events.channel.ChannelSettingChangedEvent;
import net.silthus.schat.events.channel.ChannelSettingsChanged;
import net.silthus.schat.events.channel.ChannelTargetAdded;
import net.silthus.schat.events.channel.ChannelTargetRemoved;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.MessageTarget;
import net.silthus.schat.pointer.Settings;
import net.silthus.schat.policies.JoinChannelPolicy;
import net.silthus.schat.policies.SendChannelMessagePolicy;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.channel.Channel.DISPLAY_NAME;
import static net.silthus.schat.channel.Channel.KEY;
import static net.silthus.schat.channel.Channel.createChannel;
import static net.silthus.schat.channel.ChannelHelper.channelWith;
import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.channel.ChannelSettings.GLOBAL;
import static net.silthus.schat.channel.ChannelSettings.PRIORITY;
import static net.silthus.schat.channel.ChannelSettings.PRIVATE;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.message.MessageHelper.randomMessage;
import static net.silthus.schat.policies.SendChannelMessagePolicy.DENY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ChannelTests {

    public static final @NotNull TextComponent MY_CHANNEL = text("My Channel");

    private final EventBusMock eventBus = EventBusMock.eventBusMock();
    private Channel channel;

    @BeforeEach
    void setUp() {
        channel = randomChannel();
        JoinChannelCommand.prototype(builder -> builder.eventBus(eventBus));
    }

    @AfterEach
    void tearDown() {
        eventBus.close();
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

    @Nested
    class given_no_display_name {
        @Test
        void uses_key_as_display_name() {
            assertThat(channel.displayName()).isEqualTo(text(channel.key()));
        }
    }

    @Test
    void when_settings_are_overwritten_key_and_name_is_kept() {
        final Channel channel = channelWith("test");
        channel.settings(Settings.createSettings());

        assertThat(channel.get(KEY)).isPresent().get().isEqualTo("test");
        assertThat(channel.get(DISPLAY_NAME)).isEqualTo(text("test"));
    }

    @Test
    void sendMessage_updates_channel_targets() {
        final ChatterMock chatter = randomChatter();
        channel.addTarget(chatter);
        assertThat(chatter.isJoined(channel)).isFalse();

        channel.sendMessage(randomMessage());

        assertThat(chatter.isJoined(channel)).isTrue();
    }

    @Nested class events {
        @Test
        void when_setting_changes_channel_update_event_is_fired() {
            channel.set(GLOBAL, false);
            eventBus.assertEventFired(new ChannelSettingChangedEvent<>(channel, GLOBAL, true, false));
        }

        @Test
        void when_all_settings_change_a_settings_changed_event_is_fired() {
            channel.settings(Settings.settingsBuilder().withStatic(GLOBAL, false).create());
            eventBus.assertEventFired(ChannelSettingsChanged.class);
        }

        @Test
        void when_target_is_added_then_event_is_fired() {
            final ChatterMock target = randomChatter();
            channel.addTarget(target);
            eventBus.assertEventFired(new ChannelTargetAdded(channel, target));
        }

        @Test
        void given_target_does_not_exist_when_target_is_removed_then_no_event_is_fired() {
            channel.removeTarget(randomChatter());
            eventBus.assertNoEventFired(ChannelTargetAdded.class);
        }

        @Test
        void given_target_exists_when_target_is_removed_then_event_is_fired() {
            final ChatterMock target = randomChatter();
            channel.addTarget(target);
            channel.removeTarget(target);
            eventBus.assertEventFired(new ChannelTargetRemoved(channel, target));
        }
    }

    @Nested
    class given_display_name {
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

    @Nested class policy {

        @Test
        void given_policy_fetches_policy() {
            channel = Channel.channel("test").policy(JoinChannelPolicy.class, JoinChannelPolicy.ALLOW).create();
            assertThat(channel.policy(JoinChannelPolicy.class))
                .isPresent().get().isEqualTo(JoinChannelPolicy.ALLOW);
        }

        @Test
        void send_message_uses_channel_policy() {
            Channel channel = channelWith(builder -> builder.policy(SendChannelMessagePolicy.class, DENY));
            SendMessageResult result = channel.sendMessage(randomMessage());
            assertThat(result.wasFailure()).isTrue();
        }
    }

    @Nested
    class comparison {

        private void assertSorted(Channel... channels) {
            final List<Channel> random = new ArrayList<>(List.of(channels));
            Collections.shuffle(random);
            random.sort(Channel::compareTo);

            assertThat(random).containsExactly(channels);
        }

        @Test
        void channels_are_sorted_by_name() {
            assertSorted(
                channelWith("abc"),
                channelWith("kln"),
                channelWith("zio")
            );
        }

        @Test
        void channels_are_sorted_by_priority() {
            assertSorted(
                channelWith("zzz", PRIORITY, 1),
                channelWith("aaa"),
                channelWith("bcd"),
                channelWith("aaa", PRIORITY, 101)
            );
        }

        @Test
        void private_channels_come_last() {
            assertSorted(
                channelWith("zzz"),
                channelWith("dcb", PRIVATE, true),
                channelWith("abc", PRIORITY, 110)
            );
        }
    }

    @Nested
    class close {
        private ChatterMock chatter;

        @BeforeEach
        void setUp() {
            chatter = randomChatter();
            chatter.join(channel);
        }

        @Test
        void removes_chatter_channels() {
            channel.close();
            chatter.assertNotJoinedChannel(channel);
        }
    }
}
