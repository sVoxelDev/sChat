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

package net.silthus.schat.events.channel;

import java.util.concurrent.atomic.AtomicBoolean;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.commands.LeaveChannelCommand;
import net.silthus.schat.events.Cancellable;
import net.silthus.schat.events.SChatEvent;
import net.silthus.schat.policies.ChannelPolicy;
import net.silthus.schat.policies.LeaveChannelPolicy;

/**
 * The event is fired before a chatter leaves a channel and allows the manipulation of the {@link LeaveChannelPolicy} and outcome of the {@link LeaveChannelCommand}.
 *
 * <p>The event will not be triggered if the channel is left directly via {@link Chatter#leave(Channel)}.</p>
 */
@Getter
@Setter
@Accessors(fluent = true)
@EqualsAndHashCode(of = {"chatter", "channel"})
public class LeaveChannelEvent implements SChatEvent, Cancellable {
    private final Chatter chatter;
    private final Channel channel;
    private final AtomicBoolean cancellationState = new AtomicBoolean(false);
    private ChannelPolicy policy;

    public LeaveChannelEvent(Chatter chatter, Channel channel, ChannelPolicy policy) {
        this.chatter = chatter;
        this.channel = channel;
        this.policy = policy;
    }
}
