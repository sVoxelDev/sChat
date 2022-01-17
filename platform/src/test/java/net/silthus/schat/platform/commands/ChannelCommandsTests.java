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
import net.silthus.schat.channel.FailingChannelInteractorStub;
import net.silthus.schat.channel.SpyingChannelInteractorStub;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.message.Message;
import net.silthus.schat.platform.commands.parser.ChannelParser;
import net.silthus.schat.usecases.ChatListener;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.AssertionHelper.assertNPE;
import static net.silthus.schat.channel.Channel.createChannel;
import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.locale.Messages.JOIN_CHANNEL_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;

class ChannelCommandsTests extends CommandTest {

    private ChannelCommands channelCommands;
    private ChannelRepository channelRepository;

    private Channel channel;
    private SpyingChannelInteractorStub interactor;

    @BeforeEach
    void setUp() {
        channelRepository = createInMemoryChannelRepository();
        interactor = new SpyingChannelInteractorStub();
        channelCommands = new ChannelCommands(interactor, channelRepository);
        commands.register(channelCommands);

        channel = addRandomChannel();
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

    @Nested
    @DisplayName("/channel join <channel>")
    class joinChannel {
        private static final String JOIN_CHANNEL_CMD = "channel join ";

        private Chatter executeJoinCommand() {
            return cmd(JOIN_CHANNEL_CMD + channel.getKey());
        }

        @Test
        void given_invalid_chanel_join_command_fails() {
            cmdFails(JOIN_CHANNEL_CMD + "foobar", ChannelParser.ChannelParseException.class);
        }

        @Nested class given_valid_channel {

            @Test
            void then_join_command_succeeds_and_calls_interactor() {
                executeJoinCommand();
                assertThat(interactor.isJoinChannelCalled()).isTrue();
            }

            @Nested class given_join_fails {

                @BeforeEach
                void setUp() {
                    channelCommands.interactor(new FailingChannelInteractorStub());
                }

                @Test
                void then_join_command_prints_error_message() {
                    executeJoinCommand();
                    assertThat(chatter.getChannels()).doesNotContain(channel);
                    assertLastMessageIs(JOIN_CHANNEL_ERROR.build(channel));
                }
            }
        }
    }

    @Nested class setActiveChannel {

        @Nested class given_valid_channel {

            private void executeSetActiveChannelCommand() {
                cmd("channel set-active " + channel.getKey());
            }

            @Test
            void then_setActiveChannel_command_succeeds() {
                executeSetActiveChannelCommand();
                assertThat(interactor.isSetActiveChannelCalled()).isTrue();
            }

            @Nested class given_join_channel_fails {

                @BeforeEach
                void setUp() {
                    channelCommands.interactor(new FailingChannelInteractorStub());
                }

                @Test
                void then_setActiveChannel_command_does_not_throw() {
                    executeSetActiveChannelCommand();
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
