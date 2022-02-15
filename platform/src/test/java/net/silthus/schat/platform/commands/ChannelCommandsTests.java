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

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.commands.JoinChannelCommand;
import net.silthus.schat.commands.LeaveChannelCommand;
import net.silthus.schat.eventbus.EventBusMock;
import net.silthus.schat.message.Message;
import net.silthus.schat.platform.commands.parser.ChannelArgument;
import net.silthus.schat.platform.sender.Sender;
import net.silthus.schat.policies.JoinChannelPolicy;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.channel.ChannelHelper.channelWith;
import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.platform.locale.Messages.JOIN_CHANNEL_ERROR;
import static net.silthus.schat.platform.locale.Messages.LEFT_CHANNEL;
import static net.silthus.schat.policies.JoinChannelPolicy.DENY;
import static org.assertj.core.api.Assertions.assertThat;

class ChannelCommandsTests extends CommandTest {

    private Channel channel;
    private EventBusMock eventBus;

    @BeforeEach
    void setUp() {
        commands.register(new ChannelCommands());
        channel = addRandomChannel();
        eventBus = EventBusMock.eventBusMock();
        LeaveChannelCommand.prototype(builder -> builder.eventBus(eventBus));
        JoinChannelCommand.prototype(builder -> builder.eventBus(eventBus));
    }

    private Channel addRandomChannel() {
        return addChannel(randomChannel());
    }

    @NotNull
    private Channel addChannel(Channel channel) {
        channelRepository.add(channel);
        return channel;
    }

    @Nested
    @DisplayName("/channel join <channel>")
    class joinChannel {

        @Test
        void given_invalid_chanel_join_command_fails() {
            cmdFails("ch " + "foobar", ChannelArgument.ChannelParseException.class);
        }

        @Nested class given_valid_channel {

            private void executeJoinCommand() {
                cmd("ch " + channel.key());
            }

            @Test
            void then_join_command_succeeds_and_calls_interactor() {
                executeJoinCommand();
                assertThat(chatter.isJoined(channel)).isTrue();
                assertThat(chatter.activeChannel())
                    .isPresent().get().isEqualTo(channel);
            }

            @Nested class given_join_fails {

                @BeforeEach
                void setUp() {
                    channel = addChannel(channelWith(builder -> builder.policy(JoinChannelPolicy.class, DENY)));
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

    @DisplayName("/channel leave <channel>")
    @Nested class leave_channel {
        @BeforeEach
        void setUp() {
            chatter.join(channel);
        }

        private void executeLeaveCommand() {
            cmd("leave " + channel.key());
        }

        @Test
        void can_leave_channel() {
            executeLeaveCommand();
            chatter.assertNotJoinedChannel(channel);
            assertLastMessageIs(LEFT_CHANNEL.build(channel));
        }
    }

    @DisplayName("/channel message <channel> <message>")
    @Nested class quick_message {

        @BeforeEach
        void setUp() {
            chatter.join(channel);
            // TODO: add events to channel repository and dynamically register commands for join with /<channel_name> and quickmessage: /<channel_name> <message>
            Command<Sender> command = commandManager.commandBuilder(channel.key())
                .argument(StringArgument.greedy("message"))
                .handler(commandContext -> Message.message((String) commandContext.get("message"))
                    .source(commandContext.getSender().identity())
                    .to(channel)
                    .send())
                .build();
            commandManager.command(command);
        }

        @Test
        void quick_message_is_sent_to_channel() {
            cmd("/ch " + channel.key() + " Hey there!");
            chatter.assertReceivedMessage(text("Hey there!"));
        }

        @Test
        void alias_command_works() {
            cmd("/" + channel.key() + " test message");
            chatter.assertReceivedMessage(text("test message"));
        }
    }
}
