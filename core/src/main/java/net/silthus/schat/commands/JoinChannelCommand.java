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

import java.util.function.Function;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.command.Command;
import net.silthus.schat.command.CommandBuilder;
import net.silthus.schat.command.Result;
import net.silthus.schat.policies.Policy;

import static net.silthus.schat.command.Result.success;
import static net.silthus.schat.policies.JoinChannelPolicy.canJoinChannel;

@Getter
@Accessors(fluent = true)
public class JoinChannelCommand implements Command {

    private static final @NonNull Function<Builder, Builder> DEFAULTS = builder -> builder.validate(canJoinChannel(builder.chatter, builder.channel));
    @Getter
    @Setter
    private static @NonNull Function<Builder, Builder> prototype = builder -> builder;

    public static Builder joinChannel(Chatter chatter, Channel channel) {
        return prototype().apply(DEFAULTS.apply(new Builder(chatter, channel)));
    }

    private final Chatter chatter;
    private final Channel channel;
    private final Policy policy;

    protected JoinChannelCommand(Builder builder) {
        this.chatter = builder.chatter;
        this.channel = builder.channel;
        this.policy = builder.policy;
    }

    public Result execute() throws Error {
        if (policy.validate())
            return joinChannelAndUpdateView(chatter, channel);
        else
            return handleJoinChannelError(chatter, channel);
    }

    private Result joinChannelAndUpdateView(Chatter chatter, Channel channel) {
        if (chatter.isJoined(channel))
            return success();
        chatter.join(channel);
        chatter.updateView();
        return success();
    }

    private Result handleJoinChannelError(Chatter chatter, Channel channel) throws AccessDenied {
        chatter.leave(channel);
        throw new AccessDenied();
    }

    @Getter
    @Accessors(fluent = true)
    public static class Builder extends CommandBuilder<Builder, JoinChannelCommand> {
        private final Chatter chatter;
        private final Channel channel;
        private Policy policy = Policy.ALLOW;

        protected Builder(Chatter chatter, Channel channel) {
            super(JoinChannelCommand::new);
            this.chatter = chatter;
            this.channel = channel;
        }

        public Builder validate(Policy policy) {
            this.policy = policy;
            return this;
        }
    }

    public static final class AccessDenied extends Error {
    }
}
