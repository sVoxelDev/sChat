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
import net.silthus.schat.command.Result;
import net.silthus.schat.eventbus.EventBusMock;
import net.silthus.schat.events.channel.JoinChannelEvent;
import net.silthus.schat.events.channel.JoinedChannelEvent;
import net.silthus.schat.policies.JoinChannelPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.ChannelAssertions.assertChannelHasNoTargets;
import static net.silthus.schat.channel.ChannelAssertions.assertChannelHasOnlyTarget;
import static net.silthus.schat.channel.ChannelAssertions.assertChannelHasTarget;
import static net.silthus.schat.channel.ChannelHelper.channelWith;
import static net.silthus.schat.chatter.ChatterAssertions.assertChatterHasChannel;
import static net.silthus.schat.chatter.ChatterAssertions.assertChatterHasNoChannels;
import static net.silthus.schat.chatter.ChatterAssertions.assertChatterHasOnlyChannel;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.policies.JoinChannelPolicy.ALLOW;
import static net.silthus.schat.policies.JoinChannelPolicy.DENY;
import static org.assertj.core.api.Assertions.assertThat;

class JoinChannelCommandTests {

    private EventBusMock eventBus;
    private ChatterMock chatter;
    private Channel channel;

    @BeforeEach
    void setUp() {
        eventBus = EventBusMock.eventBusMock();
        chatter = randomChatter();
        channel = channelWith(builder -> builder.policy(JoinChannelPolicy.class, ALLOW));
        JoinChannelCommand.prototype(builder -> builder.eventBus(eventBus));
    }

    private void assertJoinChannelError() {
        assertThat(joinChannel().wasFailure()).isTrue();
    }

    private Result joinChannel() {
        return JoinChannelCommand.joinChannelBuilder(chatter, channel).execute();
    }

    @Test
    void pre_join_channel_event_is_fired() {
        joinChannel();
        eventBus.assertEventFired(new JoinChannelEvent(chatter, channel, ALLOW));
    }

    @Test
    void given_cancelled_pre_join_event_then_join_fails() {
        eventBus.on(JoinChannelEvent.class, event -> event.cancelled(true));
        assertJoinChannelError();
    }

    @Test
    void given_channel_with_policy_then_uses_policy() {
        channel = Channel.channel("test").policy(JoinChannelPolicy.class, DENY).create();
        assertJoinChannelError();
    }

    @Nested class given_successful_can_join_check {

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
            eventBus.assertEventFired(new JoinedChannelEvent(chatter, channel));
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
            channel = channelWith(builder -> builder.policy(JoinChannelPolicy.class, DENY));
        }

        @Test
        void then_throws_access_defined_exception() {
            assertJoinChannelError();
        }

        @Test
        void then_no_post_join_channel_event_is_fired() {
            assertJoinChannelError();
            eventBus.assertNoEventFired(new JoinedChannelEvent(chatter, channel));
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
