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

import java.util.function.Function;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.util.Permissable;

public class JoinChannelPolicy implements Policy {

    @Getter
    @Setter
    private static Function<JoinChannelPolicy.Builder, JoinChannelPolicy.Builder> prototype = builder -> builder;
    private final Permissable permissable;
    private final Channel channel;

    protected JoinChannelPolicy(Builder builder) {
        this.permissable = builder.chatter;
        this.channel = builder.channel;
    }

    public static JoinChannelPolicy.Builder canJoinChannel(@NonNull Permissable permissable, @NonNull Channel channel) {
        return getPrototype().apply(new Builder(permissable, channel));
    }

    @Override
    public boolean validate() throws Error {
        if (!channel.get(Channel.PROTECTED))
            return true;
        return permissable.hasPermission(channel.get(Channel.JOIN_PERMISSION));
    }

    public static class Builder implements Policy.Builder<JoinChannelPolicy> {

        private final Permissable chatter;
        private final Channel channel;

        protected Builder(@NonNull Permissable permissable, @NonNull Channel channel) {
            this.chatter = permissable;
            this.channel = channel;
        }

        @Override
        public JoinChannelPolicy create() {
            return new JoinChannelPolicy(this);
        }
    }
}
