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

import java.io.Serial;
import java.util.function.Consumer;
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
import net.silthus.schat.policies.JoinChannelPolicy;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.channel.ChannelSettings.PRIVATE;
import static net.silthus.schat.command.Result.error;
import static net.silthus.schat.command.Result.success;
import static net.silthus.schat.policies.JoinChannelPolicy.JOIN_CHANNEL_POLICY;

@Getter
@Accessors(fluent = true)
public class JoinChannelCommand implements Command {

    @Getter
    private static @NonNull Consumer<Builder> prototype = builder -> {};

    public static void prototype(Consumer<Builder> consumer) {
        prototype = prototype().andThen(consumer);
    }

    public static Result joinChannel(Chatter chatter, Channel channel) {
        return joinChannelBuilder(chatter, channel).create().execute();
    }

    public static Builder joinChannelBuilder(Chatter chatter, Channel channel) {
        final Builder builder = new Builder(chatter, channel);
        prototype().accept(builder);
        return builder;
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
        return eventBus.post(new JoinChannelEvent(chatter, channel, channel.policy(JoinChannelPolicy.class)
            .orElse(JOIN_CHANNEL_POLICY)));
    }

    private Result joinChannelAndUpdateView(Chatter chatter, Channel channel) {
        if (chatter.isJoined(channel))
            return success();
        chatter.join(channel);
        return success();
    }

    private Result handleJoinChannelError(Chatter chatter, Channel channel) {
        if (channel.isNot(PRIVATE))
            return leaveChannel(chatter, channel);
        else
            return success();
    }

    @NotNull
    private Result leaveChannel(Chatter chatter, Channel channel) {
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
        @Serial private static final long serialVersionUID = -5780339463195577903L;
    }
}
