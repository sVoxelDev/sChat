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

package net.silthus.schat.command.checks;

import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.checks.CanJoinChannelCheck;
import net.silthus.schat.command.Result;
import net.silthus.schat.commands.JoinChannelCommand;
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