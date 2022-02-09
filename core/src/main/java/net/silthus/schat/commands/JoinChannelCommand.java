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
import net.silthus.schat.events.channel.PostChatterJoinChannelEvent;
import net.silthus.schat.events.channel.PreJoinChannelEvent;
import net.silthus.schat.policies.JoinChannelPolicy;

import static net.silthus.schat.command.Result.success;
import static net.silthus.schat.policies.JoinChannelPolicy.JOIN_CHANNEL_POLICY;

@Getter
@Accessors(fluent = true)
public class JoinChannelCommand implements Command {

    @Getter
    @Setter
    private static @NonNull Function<Builder, Builder> prototype = builder -> builder;

    public static Builder joinChannel(Chatter chatter, Channel channel) {
        return prototype().apply(new Builder(chatter, channel));
    }

    private final Chatter chatter;
    private final Channel channel;
    private final JoinChannelPolicy policy;
    private final EventBus eventBus;

    protected JoinChannelCommand(Builder builder) {
        this.chatter = builder.chatter;
        this.channel = builder.channel;
        this.policy = builder.policy;
        this.eventBus = builder.eventBus;
    }

    public Result execute() throws Error {
        PreJoinChannelEvent event = firePreJoinChannelEvent();
        if (event.isNotCancelled() && event.policy().test(chatter, channel))
            return joinChannelAndUpdateView(chatter, channel);
        else
            return handleJoinChannelError(chatter, channel);
    }

    private PreJoinChannelEvent firePreJoinChannelEvent() {
        JoinChannelPolicy policy = channel.policy(JoinChannelPolicy.class).orElse(this.policy);
        return eventBus.post(new PreJoinChannelEvent(chatter, channel, policy));
    }

    private Result joinChannelAndUpdateView(Chatter chatter, Channel channel) {
        if (chatter.isJoined(channel))
            return success();
        chatter.join(channel);
        chatter.updateView();
        eventBus.post(new PostChatterJoinChannelEvent(chatter, channel));
        return success();
    }

    private Result handleJoinChannelError(Chatter chatter, Channel channel) throws AccessDenied {
        chatter.leave(channel);
        throw new AccessDenied();
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    public static class Builder extends CommandBuilder<Builder, JoinChannelCommand> {
        private final Chatter chatter;
        private final Channel channel;
        private JoinChannelPolicy policy = JOIN_CHANNEL_POLICY;
        private EventBus eventBus = EventBus.empty();

        protected Builder(Chatter chatter, Channel channel) {
            super(JoinChannelCommand::new);
            this.chatter = chatter;
            this.channel = channel;
        }
    }

    public static final class AccessDenied extends Error {
    }
}
