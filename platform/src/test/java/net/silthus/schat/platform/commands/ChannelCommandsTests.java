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

import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.FailingChannelInteractorStub;
import net.silthus.schat.channel.SpyingChannelInteractorStub;
import net.silthus.schat.platform.commands.parser.ChannelArgument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.platform.locale.Messages.JOIN_CHANNEL_ERROR;
import static org.assertj.core.api.Assertions.assertThat;

class ChannelCommandsTests extends CommandTest {
    private ChannelCommands channelCommands;
    private Channel channel;
    private SpyingChannelInteractorStub interactor;

    @BeforeEach
    void setUp() {
        interactor = new SpyingChannelInteractorStub();
        channelCommands = new ChannelCommands(interactor);
        commands.register(channelCommands);
        channel = addRandomChannel();
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

        private void executeJoinCommand() {
            cmd(JOIN_CHANNEL_CMD + channel.getKey());
        }

        @Test
        void given_invalid_chanel_join_command_fails() {
            cmdFails(JOIN_CHANNEL_CMD + "foobar", ChannelArgument.ChannelParseException.class);
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
}
