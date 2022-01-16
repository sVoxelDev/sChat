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

package net.silthus.schat.platform.commands;

import net.silthus.schat.MessageHelper;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.message.Message;
import net.silthus.schat.platform.commands.parser.ChannelParser;
import net.silthus.schat.policies.Policies;
import net.silthus.schat.usecases.ChatListener;
import net.silthus.schat.usecases.JoinChannel;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.AssertionHelper.assertNPE;
import static net.silthus.schat.channel.Channel.createChannel;
import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.locale.Messages.JOIN_CHANNEL_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChannelCommandsTests extends CommandTest {

    private ChannelCommands channelCommands;
    private Policies policies;
    private ChannelRepository channelRepository;

    @BeforeEach
    void setUp() {
        policies = mock(Policies.class);
        when(policies.canJoinChannel(any(), any())).thenReturn(true);
        mockCanJoin(true);
        channelRepository = createInMemoryChannelRepository();
        channelCommands = new ChannelCommands(policies, channelRepository);
        commands.register(channelCommands);
    }

    private void assertJoinedChannel(Channel channel) {
        assertThat(channel.getTargets()).contains(chatter);
    }

    private void mockCanJoin(boolean result) {
        when(policies.canJoinChannel(any(), any())).thenReturn(result);
    }

    @NotNull
    private Channel setActive(Channel channel) {
        channelCommands.setActiveChannel(chatter, channel);
        return channel;
    }

    private Channel addChannel(String channel) {
        final Channel c = createChannel(channel);
        channelRepository.add(c);
        return c;
    }

    private Channel addRandomChannel() {
        final Channel channel = randomChannel();
        channelRepository.add(channel);
        return channel;
    }

    private void assertNoActiveChannel() {
        assertThat(chatter.getActiveChannel()).isNotPresent();
    }

    private void assertActiveChannel(Channel channel) {
        assertThat(chatter.getActiveChannel())
            .isPresent().get().isEqualTo(channel);
    }

    @Nested
    class joinChannel {
        private static final String JOIN_CHANNEL_CMD = "channel join test";
        private Channel channel;

        @BeforeEach
        void setUp() {
            channel = randomChannel();
        }

        @NotNull
        @SuppressWarnings("MethodNameSameAsClassName")
        private Channel joinChannel() {
            final Channel channel = randomChannel();
            channelCommands.joinChannel(chatter, channel);
            return channel;
        }

        private Channel join(Channel channel) {
            channelCommands.joinChannel(chatter, channel);
            return channel;
        }

        private void assertJoinSuccess(Channel channel) {
            assertJoinedChannel(join(channel));
        }

        private void assertJoinError() {
            assertThatExceptionOfType(JoinChannel.Error.class).isThrownBy(this::joinChannel);
        }

        private Chatter executeJoinCommand() {
            return cmd(JOIN_CHANNEL_CMD);
        }

        @Test
        @SuppressWarnings("ConstantConditions")
        void given_null_user_or_channel_throws() {
            assertNPE(() -> channelCommands.joinChannel(null, null));
            assertNPE(() -> channelCommands.joinChannel(chatter, null));
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
                assertJoinedChannel(channel);
            }
        }

        @Test
        void given_invalid_chanel_join_command_fails() {
            cmdFails(JOIN_CHANNEL_CMD, ChannelParser.ChannelParseException.class);
        }

        @Nested class given_valid_channel {
            private Channel channel;

            @BeforeEach
            void setUp() {
                mockCanJoin(true);
                channel = addChannel("test");
            }

            @Test
            void then_join_command_succeeds() {
                executeJoinCommand();
                assertJoinedChannel(channel);
            }

            @Nested class given_failed_can_join_check {
                @BeforeEach
                void setUp() {
                    mockCanJoin(false);
                }

                @Test
                void then_throws_JoinChannelError() {
                    assertJoinError();
                }

                @Test
                void then_join_command_does_not_throw() {
                    executeJoinCommand();
                    assertThat(chatter.getChannels()).doesNotContain(channel);
                    assertLastMessageIs(JOIN_CHANNEL_ERROR.build(channel));
                }
            }
        }
    }

    @Nested class setActiveChannel {

        @Test
        @SuppressWarnings("ConstantConditions")
        void given_null_inputs_throws() {
            assertNPE(() -> channelCommands.setActiveChannel(null, null));
            assertNPE(() -> channelCommands.setActiveChannel(chatter, null));
        }

        @Nested class given_valid_channel {
            private Channel channel;

            @BeforeEach
            void setUp() {
                channel = addRandomChannel();
            }

            private void executeSetActiveChannelCommand() {
                cmd("channel set-active " + channel.getKey());
            }

            @Test
            void when_not_joined_channel_joins_channel_and_sets_active() {
                setActive(channel);
                assertJoinedChannel(channel);
                assertActiveChannel(channel);
            }

            @Test
            void then_setActiveChannel_command_succeeds() {
                executeSetActiveChannelCommand();
                assertActiveChannel(channel);
            }

            @Nested class given_join_channel_fails {
                @BeforeEach
                void setUp() {
                    mockCanJoin(false);
                }

                @Test
                void then_setActiveChannel_command_does_not_throw() {
                    executeSetActiveChannelCommand();
                    assertNoActiveChannel();
                    assertLastMessageIs(JOIN_CHANNEL_ERROR.build(channel));
                }
            }
        }
    }

    @Nested
    class ChatTests {

        @NotNull
        private Message chat() {
            return channelCommands.onChat(chatter, MessageHelper.randomText());
        }

        @Test
        @SuppressWarnings("ConstantConditions")
        void given_null_inputs_throws() {
            assertNPE(() -> channelCommands.onChat(null, null));
            assertNPE(() -> channelCommands.onChat(chatter, null));
        }

        @Test
        void given_no_active_channel_throws() {
            assertThatExceptionOfType(ChatListener.NoActiveChannel.class)
                .isThrownBy(this::chat);
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
