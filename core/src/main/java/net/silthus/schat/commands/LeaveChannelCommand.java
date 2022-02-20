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
import net.silthus.schat.events.channel.LeaveChannelEvent;
import net.silthus.schat.events.chatter.ChatterLeftChannelEvent;

import static net.silthus.schat.command.Result.failure;
import static net.silthus.schat.command.Result.success;

@Getter
@Accessors(fluent = true)
public class LeaveChannelCommand implements Command {

    @Getter
    @Setter
    private static @NonNull Function<LeaveChannelCommand.Builder, LeaveChannelCommand.Builder> prototype = builder -> builder;

    public static Result leaveChannel(Chatter chatter, Channel channel) {
        return leaveChannelBuilder(chatter, channel).execute();
    }

    public static LeaveChannelCommand.Builder leaveChannelBuilder(Chatter chatter, Channel channel) {
        return prototype().apply(new LeaveChannelCommand.Builder(chatter, channel));
    }

    private final @NonNull Chatter chatter;
    private final @NonNull Channel channel;
    private final @NonNull EventBus eventBus;

    protected LeaveChannelCommand(Builder builder) {
        this.chatter = builder.chatter;
        this.channel = builder.channel;
        this.eventBus = builder.eventBus;
    }

    @Override
    public Result execute() {
        final LeaveChannelEvent event = fireLeaveChannelEvent();
        if (event.isNotCancelled() && event.policy().test(chatter, channel))
            return leaveChannel();
        else
            return failure();
    }

    private Result leaveChannel() {
        chatter.leave(channel);
        fireLeftChannelEvent();
        return success();
    }

    private LeaveChannelEvent fireLeaveChannelEvent() {
        return eventBus.post(new LeaveChannelEvent(chatter, channel, channel.leavePolicy()));
    }

    private void fireLeftChannelEvent() {
        eventBus.post(new ChatterLeftChannelEvent(chatter, channel));
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    public static class Builder extends CommandBuilder<LeaveChannelCommand.Builder, LeaveChannelCommand> {
        private final Chatter chatter;
        private final Channel channel;
        private EventBus eventBus;

        protected Builder(Chatter chatter, Channel channel) {
            super(LeaveChannelCommand::new);
            this.chatter = chatter;
            this.channel = channel;
        }
    }
}
