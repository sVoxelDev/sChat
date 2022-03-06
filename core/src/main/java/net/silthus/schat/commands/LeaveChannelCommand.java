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
import net.silthus.schat.events.channel.ChatterLeftChannelEvent;
import net.silthus.schat.events.channel.LeaveChannelEvent;
import net.silthus.schat.policies.LeaveChannelPolicy;
import org.jetbrains.annotations.ApiStatus;

import static net.silthus.schat.command.Result.failure;
import static net.silthus.schat.command.Result.success;
import static net.silthus.schat.policies.LeaveChannelPolicy.LEAVE_CHANNEL_POLICY;

/**
 * Command to leave a channel.
 *
 * <p>The command will check the {@link LeaveChannelPolicy} before executing and
 * then update both the channel and the chatter.</p>
 *
 * <p>This is different from the {@link Chatter#leave(Channel)} method in such a way,
 * that this command fires events, checks the policies and then calls the method.
 * Use the direct method on the chatter to bypass all events and policy checks.</p>
 *
 * <p>If extended, the {@link Builder} must also be overwritten.</p>
 *
 * @see Chatter#leave(Channel)
 * @since 1.0.0
 */
@Getter
@Accessors(fluent = true)
public class LeaveChannelCommand implements Command {

    @Getter
    private static @NonNull Consumer<Builder> prototype = builder -> {
    };

    /**
     * Configures the prototype of the command.
     *
     * @param consumer the prototype builder
     * @since 1.0.0
     */
    @ApiStatus.Internal
    public static void prototype(Consumer<Builder> consumer) {
        prototype = prototype().andThen(consumer);
    }

    /**
     * Creates and directly executes the leave channel command
     * causing the chatter to leave the channel if the policy allows it.
     *
     * <p>The {@link LeaveChannelEvent} is fired in the process and can cancel the command.</p>
     *
     * @param chatter the chatter that is leaving the channel
     * @param channel the channel to be left
     * @return the result of the command. unsuccessful if the policy or event denied the request.
     * @since 1.0.0
     */
    public static Result leaveChannel(Chatter chatter, Channel channel) {
        return leaveChannelBuilder(chatter, channel).execute();
    }

    /**
     * Creates a new command builder that can be further customized.
     *
     * @param chatter the chatter that is leaving the channel
     * @param channel the channel to be left
     * @return the builder of the command
     * @since 1.0.0
     */
    public static LeaveChannelCommand.Builder leaveChannelBuilder(Chatter chatter, Channel channel) {
        final Builder builder = new Builder(chatter, channel);
        prototype().accept(builder);
        return builder;
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
        return success();
    }

    private LeaveChannelEvent fireLeaveChannelEvent() {
        return eventBus.post(new LeaveChannelEvent(chatter, channel, channel.policy(LeaveChannelPolicy.class)
            .orElse(LEAVE_CHANNEL_POLICY)));
    }

    /**
     * The builder of the {@link LeaveChannelCommand}.
     *
     * <p>The builder can be extended together with the command itself to add new features to it.</p>
     *
     * @since 1.0.0
     */
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
