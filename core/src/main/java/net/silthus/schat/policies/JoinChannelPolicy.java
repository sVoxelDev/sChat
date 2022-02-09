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

package net.silthus.schat.policies;

import java.util.function.BiPredicate;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelSettings;
import net.silthus.schat.chatter.Chatter;

import static net.silthus.schat.channel.ChannelSettings.PRIVATE;
import static net.silthus.schat.channel.ChannelSettings.PROTECTED;

@FunctionalInterface
public interface JoinChannelPolicy extends BiPredicate<Chatter, Channel>, Policy {

    JoinChannelPolicy ALLOW = (chatter, channel) -> true;
    JoinChannelPolicy DENY = (chatter, channel) -> false;

    JoinChannelPolicy CAN_JOIN_PRIVATE_CHANNEL = (chatter, channel) -> {
        if (channel.is(PRIVATE))
            return channel.targets().contains(chatter);
        else
            return true;
    };

    JoinChannelPolicy CAN_JOIN_PROTECTED_CHANNEL = (chatter, channel) -> {
        if (channel.get(PROTECTED))
            return chatter.hasPermission(channel.get(ChannelSettings.JOIN_PERMISSION));
        else
            return true;
    };

    JoinChannelPolicy JOIN_CHANNEL_POLICY = (chatter, channel) ->
        CAN_JOIN_PRIVATE_CHANNEL
            .and(CAN_JOIN_PROTECTED_CHANNEL)
            .test(chatter, channel);
}
