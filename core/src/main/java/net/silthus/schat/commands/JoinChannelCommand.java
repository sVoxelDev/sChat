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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.command.Command;
import net.silthus.schat.command.Result;
import net.silthus.schat.policies.CanJoinChannel;
import net.silthus.schat.usecases.JoinChannel;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.command.Result.failure;
import static net.silthus.schat.command.Result.success;

@Getter
public class JoinChannelCommand implements JoinChannel, Command {

    @Accessors(fluent = true)
    @Setter(AccessLevel.PROTECTED)
    private static @NotNull Function<Builder, Builder> joinChannelCommandPrototype = builder -> builder;

    public static Builder joinChannel(Chatter chatter, Channel channel) {
        return joinChannelCommandPrototype.apply(new Builder(chatter, channel));
    }

    private final Chatter chatter;
    private final Channel channel;
    private final Out out;
    private final CanJoinChannel check;

    protected JoinChannelCommand(Builder builder) {
        this.chatter = builder.chatter;
        this.channel = builder.channel;
        this.out = builder.out;
        this.check = builder.check;
    }

    public Result execute() throws Error {
        if (check.canJoinChannel(chatter, channel))
            return joinChannelAndUpdateView(chatter, channel);
        else
            return handleJoinChannelError(chatter, channel);
    }

    private Result joinChannelAndUpdateView(Chatter chatter, Channel channel) {
        if (chatter.isJoined(channel))
            return failure();
        chatter.join(channel);
        notifyJoinChannelPresenter(chatter, channel);
        chatter.updateView();
        return success();
    }

    private void notifyJoinChannelPresenter(Chatter chatter, Channel channel) {
        out.joinedChannel(new Output(chatter, channel));
    }

    private Result handleJoinChannelError(Chatter chatter, Channel channel) throws AccessDenied {
        chatter.leave(channel);
        throw new AccessDenied();
    }

    public static class Builder implements Command.Builder<JoinChannelCommand> {
        private final Chatter chatter;
        private final Channel channel;
        private Out out = Out.empty();
        private CanJoinChannel check = CanJoinChannel.ALLOW;

        public Builder(Chatter chatter, Channel channel) {
            this.chatter = chatter;
            this.channel = channel;
        }

        public Builder out(Out out) {
            this.out = out;
            return this;
        }

        public Builder check(CanJoinChannel check) {
            this.check = check;
            return this;
        }

        public JoinChannelCommand create() {
            return new JoinChannelCommand(this);
        }
    }
}
