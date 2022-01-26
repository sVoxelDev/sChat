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

package net.silthus.schat.command.checks;

import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.command.CanJoinChannelCheck;
import net.silthus.schat.command.Result;
import net.silthus.schat.command.commands.JoinChannelCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.Channel.PROTECTED;
import static net.silthus.schat.channel.ChannelHelper.channelWith;
import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static org.assertj.core.api.Assertions.assertThat;

class CanJoinChannelCheckTest {
    private ChatterMock chatter;
    private Channel channel;

    @BeforeEach
    void setUp() {
        chatter = randomChatter();
        channel = randomChannel();
    }

    private void assertCheckSuccess() {
        assertThat(executeCheck().wasSuccessful()).isTrue();
    }

    private Result executeCheck() {
        return new CanJoinChannelCheck().check(JoinChannelCommand.joinChannel(chatter, channel).create());
    }

    private void assertCheckFailure() {
        assertThat(executeCheck().wasSuccessful()).isFalse();
    }

    @Nested
    class given_public_channel {
        @BeforeEach
        void setUp() {
            channel = channelWith(PROTECTED, false);
        }

        @Test
        void check_succeeds() {
            assertCheckSuccess();
        }
    }

    @Nested class given_protected_channel {
        @BeforeEach
        void setUp() {
            channel = channelWith(PROTECTED, true);
        }

        @Test
        void check_fails() {
            assertCheckFailure();
        }

        @Nested class given_player_has_permission {
            @BeforeEach
            void setUp() {
                chatter.mockHasPermission(true);
            }

            @Test
            void then_check_succeeds() {
                assertCheckSuccess();
            }
        }
    }
}