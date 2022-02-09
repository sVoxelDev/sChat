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
import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.command.Command;
import net.silthus.schat.eventbus.EventBusMock;
import net.silthus.schat.events.channel.PostChatterJoinChannelEvent;
import net.silthus.schat.events.channel.PreJoinChannelEvent;
import net.silthus.schat.policies.JoinChannelPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.ChannelAssertions.assertChannelHasNoTargets;
import static net.silthus.schat.channel.ChannelAssertions.assertChannelHasOnlyTarget;
import static net.silthus.schat.channel.ChannelAssertions.assertChannelHasTarget;
import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.chatter.ChatterAssertions.assertChatterHasChannel;
import static net.silthus.schat.chatter.ChatterAssertions.assertChatterHasNoChannels;
import static net.silthus.schat.chatter.ChatterAssertions.assertChatterHasOnlyChannel;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.policies.JoinChannelPolicy.ALLOW;
import static net.silthus.schat.policies.JoinChannelPolicy.DENY;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class JoinChannelCommandTests {

    private EventBusMock eventBus;
    private ChatterMock chatter;
    private Channel channel;

    @BeforeEach
    void setUp() {
        eventBus = new EventBusMock();
        chatter = randomChatter();
        channel = randomChannel();
        canJoin(true);
    }

    private void canJoin(boolean canJoin) {
        if (canJoin)
            JoinChannelCommand.prototype(builder -> builder.eventBus(eventBus).policy(ALLOW));
        else
            JoinChannelCommand.prototype(builder -> builder.eventBus(eventBus).policy(DENY));
    }

    private void assertJoinChannelError() {
        assertThatExceptionOfType(Command.Error.class)
            .isThrownBy(this::joinChannel);
    }

    private void joinChannel() {
        JoinChannelCommand.joinChannel(chatter, channel).execute();
    }

    @Test
    void pre_join_channel_event_is_fired() {
        joinChannel();
        eventBus.assertEventFired(new PreJoinChannelEvent(chatter, channel, ALLOW));
    }

    @Test
    void given_cancelled_pre_join_event_then_join_fails() {
        eventBus.on(PreJoinChannelEvent.class, event -> event.cancelled(true));
        assertJoinChannelError();
    }

    @Test
    void given_channel_with_policy_then_uses_policy() {
        channel = Channel.channel("test").policy(JoinChannelPolicy.class, DENY).create();
        assertJoinChannelError();
    }

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

        @Test
        void then_the_post_join_channel_event_is_fired() {
            joinChannel();
            eventBus.assertEventFired(new PostChatterJoinChannelEvent(chatter, channel));
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

        @Test
        void then_no_post_join_channel_event_is_fired() {
            assertJoinChannelError();
            eventBus.assertNoEventFired(new PostChatterJoinChannelEvent(chatter, channel));
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
