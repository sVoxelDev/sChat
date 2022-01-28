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

package net.silthus.schat.commands;

import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.usecases.JoinChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.ChannelAssertions.assertChannelHasNoTargets;
import static net.silthus.schat.channel.ChannelAssertions.assertChannelHasOnlyTarget;
import static net.silthus.schat.channel.ChannelAssertions.assertChannelHasTarget;
import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.chatter.ChatterAssertions.assertChatterHasChannel;
import static net.silthus.schat.chatter.ChatterAssertions.assertChatterHasNoChannels;
import static net.silthus.schat.chatter.ChatterAssertions.assertChatterHasOnlyChannel;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.policies.Policy.ALLOW;
import static net.silthus.schat.policies.Policy.DENY;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class JoinChannelCommandTests {

    private final ChannelRepository channelRepository = createInMemoryChannelRepository();

    private ChatterMock chatter;
    private Channel channel;

    @BeforeEach
    void setUp() {
        chatter = randomChatter();
        channel = randomChannel();
        canJoin(true);
    }

    private void canJoin(boolean canJoin) {
        if (canJoin)
            JoinChannelCommand.setPrototype(builder -> builder.check(ALLOW));
        else
            JoinChannelCommand.setPrototype(builder -> builder.check(DENY));
    }

    private void assertJoinChannelError() {
        assertThatExceptionOfType(JoinChannel.Error.class)
            .isThrownBy(this::joinChannel);
    }

    private void joinChannel() {
        JoinChannelCommand.joinChannel(chatter, channel).execute();
    }

    @Nested class joinChannel {

        @Nested class given_valid_chatter_and_channel {

            @Nested class given_successful_can_join_check {
                @BeforeEach
                void setUp() {
                    canJoin(true);
                }

                @Test
                void then_chatter_is_added_as_target_to_channel() {
                    joinChannel();
                    assertChannelHasTarget(channel, chatter);
                }

                @Test
                void then_channel_is_added_to_chatter() {
                    joinChannel();
                    assertChatterHasChannel(chatter, channel);
                }

                @Test
                void then_view_is_updated() {
                    joinChannel();
                    chatter.assertViewUpdated();
                }

                @Nested class given_already_joined {
                    @BeforeEach
                    void setUp() {
                        joinChannel();
                    }

                    @Test
                    void then_only_joins_once() {
                        joinChannel();
                        assertChannelHasOnlyTarget(channel, chatter);
                        assertChatterHasOnlyChannel(chatter, channel);
                    }

                    @Test
                    void then_view_does_not_update() {
                        chatter.resetViewUpdate();
                        joinChannel();
                        chatter.assertViewNotUpdated();
                    }
                }
            }

            @Nested class given_failed_can_join_check {
                @BeforeEach
                void setUp() {
                    canJoin(false);
                }

                @Test
                void then_throws_access_defined_exception() {
                    assertJoinChannelError();
                }

                @Nested class given_already_joined {
                    @BeforeEach
                    void setUp() {
                        chatter.join(channel);
                    }

                    @Test
                    void then_removes_chatter_as_channel_target() {
                        assertJoinChannelError();
                        assertChannelHasNoTargets(channel);
                    }

                    @Test
                    void then_removes_channel_from_chatter() {
                        assertJoinChannelError();
                        assertChatterHasNoChannels(chatter);
                    }
                }
            }
        }
    }
}
