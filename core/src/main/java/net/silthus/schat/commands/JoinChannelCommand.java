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
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.events.channel.JoinChannelEvent;

import static net.silthus.schat.command.Result.error;
import static net.silthus.schat.command.Result.success;

@Getter
@Accessors(fluent = true)
public class JoinChannelCommand implements Command {

    @Getter
    @Setter
    private static @NonNull Function<Builder, Builder> prototype = builder -> builder;

    public static Result joinChannel(Chatter chatter, Channel channel) {
        return joinChannelBuilder(chatter, channel).create().execute();
    }

    public static Builder joinChannelBuilder(Chatter chatter, Channel channel) {
        return prototype().apply(new Builder(chatter, channel));
    }

    private final @NonNull Chatter chatter;
    private final @NonNull Channel channel;
    private final @NonNull EventBus eventBus;

    protected JoinChannelCommand(Builder builder) {
        this.chatter = builder.chatter;
        this.channel = builder.channel;
        this.eventBus = builder.eventBus;
    }

    public Result execute() {
        JoinChannelEvent event = firePreJoinChannelEvent();
        if (event.isNotCancelled() && event.policy().test(chatter, channel))
            return joinChannelAndUpdateView(chatter, channel);
        else
            return handleJoinChannelError(chatter, channel);
    }

    private JoinChannelEvent firePreJoinChannelEvent() {
        return eventBus.post(new JoinChannelEvent(chatter, channel, channel.joinPolicy()));
    }

    private Result joinChannelAndUpdateView(Chatter chatter, Channel channel) {
        if (chatter.isJoined(channel))
            return success();
        chatter.join(channel);
        return success();
    }

    private Result handleJoinChannelError(Chatter chatter, Channel channel) {
        chatter.leave(channel);
        return error(new AccessDenied());
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    public static class Builder extends CommandBuilder<Builder, JoinChannelCommand> {
        private final Chatter chatter;
        private final Channel channel;
        private EventBus eventBus;

        protected Builder(Chatter chatter, Channel channel) {
            super(JoinChannelCommand::new);
            this.chatter = chatter;
            this.channel = channel;
        }
    }

    public static final class AccessDenied extends Error {
    }
}
