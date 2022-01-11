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

import net.silthus.schat.MessageHelper;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.message.Message;
import net.silthus.schat.policies.ChannelPolicies;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.ChannelHelper.randomChannel;
import static net.silthus.schat.ChatterMock.randomChatter;
import static net.silthus.schat.TestHelper.assertNPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UiTests {

    private Ui ui;
    private Chatter chatter;
    private ChannelPolicies policies;

    @BeforeEach
    void setUp() {
        policies = mock(ChannelPolicies.class);
        mockCanJoin(true);
        chatter = spy(randomChatter());
        ui = new Ui(policies);
    }

    private void assertJoinedChannel(Channel channel) {
        assertThat(channel.getTargets()).contains(chatter);
    }

    @NotNull
    private Message chat() {
        return ui.chat(chatter, MessageHelper.randomText());
    }

    @NotNull
    private Channel joinChannel() {
        final Channel channel = randomChannel();
        ui.joinChannel(chatter, channel);
        return channel;
    }

    private Channel join(Channel channel) {
        ui.joinChannel(chatter, channel);
        return channel;
    }

    private void assertJoinSuccess(Channel channel) {
        assertJoinedChannel(join(channel));
    }

    private void assertJoinError() {
        assertThatExceptionOfType(Ui.JoinChannelError.class).isThrownBy(this::joinChannel);
    }

    private void mockCanJoin(boolean result) {
        when(policies.canJoinChannel(any(), any())).thenReturn(result);
    }

    @NotNull
    private Channel setActive(Channel channel) {
        ui.setActiveChannel(chatter, channel);
        return channel;
    }

    @Nested
    class joinChannel {

        private Channel channel;

        @BeforeEach
        void setUp() {
            channel = randomChannel();
        }

        @Test
        @SuppressWarnings("ConstantConditions")
        void given_null_user_or_channel_throws() {
            assertNPE(() -> ui.joinChannel(null, null));
            assertNPE(() -> ui.joinChannel(chatter, null));
        }

        @Test
        void calls_policy_module() {
            joinChannel();
            verify(policies).canJoinChannel(any(), any());
        }

        @Nested class given_successful_can_join_check {

            @BeforeEach
            void setUp() {
                mockCanJoin(true);
            }

            @Test
            void join_succeeds() {
                assertJoinSuccess(channel);
            }

            @Test
            void adds_channel_to_user() {
                final Channel channel = joinChannel();
                assertThat(chatter.getChannels()).contains(channel);
            }
        }

        @Nested class given_failed_can_join_check {
            @BeforeEach
            void setUp() {
                mockCanJoin(false);
            }

            @Test
            void throws_join_error() {
                assertJoinError();
            }
        }
    }

    @Nested class setActive {

        @Test
        @SuppressWarnings("ConstantConditions")
        void given_null_inputs_throws() {
            assertNPE(() -> ui.setActiveChannel(null, null));
            assertNPE(() -> ui.setActiveChannel(chatter, null));
        }

        @Test
        void when_not_joined_channel_joins_channel() {
            final Channel channel = randomChannel();
            assertJoinedChannel(setActive(channel));
            assertThat(chatter.getActiveChannel()).isPresent().get().isEqualTo(channel);
        }
    }

    @Nested
    class ChatTests {

        @Test
        @SuppressWarnings("ConstantConditions")
        void given_null_inputs_throws() {
            assertNPE(() -> ui.chat(null, null));
            assertNPE(() -> ui.chat(chatter, null));
        }

        @Test
        void given_no_active_channel_throws() {
            assertThatExceptionOfType(Ui.NoActiveChannel.class)
                .isThrownBy(UiTests.this::chat);
        }

        @Nested class given_active_channel {

            private Channel channel;

            @BeforeEach
            void setUp() {
                channel = setActive(mock(Channel.class));
            }

            @Test
            void then_sends_message_to_channel() {
                final Message message = chat();
                verify(channel).sendMessage(message);
            }

            @Test
            void then_sets_message_source_to_user() {
                assertThat(chat().getSource()).isEqualTo(chatter);
            }

            @Test
            void then_sets_message_type_to_chat() {
                assertThat(chat().getType()).isEqualTo(Message.Type.CHAT);
            }
        }
    }
}
