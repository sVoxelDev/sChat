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

package net.silthus.schat.platform.commands;

import net.silthus.schat.channel.Channel;
import net.silthus.schat.commands.JoinChannelCommand;
import net.silthus.schat.platform.commands.parser.ChannelArgument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.platform.locale.Messages.JOIN_CHANNEL_ERROR;
import static net.silthus.schat.policies.Policy.DENY;
import static org.assertj.core.api.Assertions.assertThat;

class ChannelCommandsTests extends CommandTest {

    @BeforeEach
    void setUp() {
        commands.register(new ChannelCommands());
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

        @Test
        void given_invalid_chanel_join_command_fails() {
            cmdFails(JOIN_CHANNEL_CMD + "foobar", ChannelArgument.ChannelParseException.class);
        }

        @Nested class given_valid_channel {
            private Channel channel;

            @BeforeEach
            void setUp() {
                channel = addRandomChannel();
            }

            @Test
            void then_join_command_succeeds_and_calls_interactor() {
                executeJoinCommand();
                assertThat(chatter.isJoined(channel)).isTrue();
                assertThat(chatter.activeChannel())
                    .isPresent().get().isEqualTo(channel);
            }

            private void executeJoinCommand() {
                cmd(JOIN_CHANNEL_CMD + channel.key());
            }

            @Nested class given_join_fails {

                @BeforeEach
                void setUp() {
                    JoinChannelCommand.prototype(builder -> builder.validate(DENY));
                }

                @Test
                void then_join_command_prints_error_message() {
                    executeJoinCommand();
                    assertLastMessageIs(JOIN_CHANNEL_ERROR.build(channel));
                }
            }

            @Nested class given_channel_is_already_active {
                @BeforeEach
                void setUp() {
                    chatter.activeChannel(channel);
                }

                @Test
                void then_join_command_does_nothing() {
                    executeJoinCommand();
                    assertLastMessageIs(null);
                }
            }
        }
    }
}
