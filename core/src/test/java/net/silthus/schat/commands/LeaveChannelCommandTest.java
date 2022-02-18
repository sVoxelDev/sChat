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
import net.silthus.schat.events.channel.LeaveChannelEvent;
import net.silthus.schat.events.channel.LeftChannelEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.ChannelHelper.channelWith;
import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.channel.ChannelSettings.FORCED;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.commands.LeaveChannelCommand.leaveChannel;
import static net.silthus.schat.eventbus.EventBusMock.eventBusMock;
import static net.silthus.schat.policies.ChannelPolicy.DENY;
import static net.silthus.schat.policies.LeaveChannelPolicy.LEAVE_CHANNEL_POLICY;
import static org.assertj.core.api.Assertions.assertThat;

class LeaveChannelCommandTest {
    private ChatterMock chatter;
    private Channel channel;
    private EventBusMock eventBus;

    @BeforeEach
    void setUp() {
        chatter = randomChatter();
        channel = randomChannel();
        eventBus = eventBusMock();
        LeaveChannelCommand.prototype(builder -> builder.eventBus(eventBus));
    }

    private Result executeLeaveChannel() {
        chatter.join(channel);
        return leaveChannel(chatter, channel);
    }

    private void assertCanLeaveChannel() {
        final Result result = executeLeaveChannel();

        chatter.assertNotJoinedChannel(channel);
        assertThat(result.wasSuccessful()).isTrue();
    }

    private void assertCannotLeaveChannel() {
        final Result result = executeLeaveChannel();

        chatter.assertJoinedChannel(channel);
        assertThat(result.wasFailure()).isTrue();
    }

    @Test
    void chatter_is_removed_from_channel() {
        assertCanLeaveChannel();
    }

    @Test
    void channel_with_leave_protection_cannot_be_left() {
        channel = channelWith(FORCED, true);
        assertCannotLeaveChannel();
    }

    @Test
    void pre_leave_channel_event_is_fired() {
        assertCanLeaveChannel();
        eventBus.assertEventFired(new LeaveChannelEvent(chatter, channel, LEAVE_CHANNEL_POLICY));
    }

    @Test
    void policy_change_causes_leave_to_fail() {
        eventBus.on(LeaveChannelEvent.class, event -> event.policy(DENY));
        assertCannotLeaveChannel();
    }

    @Test
    void cancelled_event_then_leave_channel_command_fails() {
        eventBus.on(LeaveChannelEvent.class, event -> event.cancelled(true));
        assertCannotLeaveChannel();
    }

    @Test
    void left_channel_event_is_fired() {
        assertCanLeaveChannel();
        eventBus.assertEventFired(new LeftChannelEvent(chatter, channel));
    }
}
