/*
 * sChat, a Supercharged Minecraft Chat Plugin
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.silthus.schat.commands;

import java.util.function.Function;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.command.Command;
import net.silthus.schat.command.Result;
import net.silthus.schat.policies.Policy;
import net.silthus.schat.usecases.JoinChannel;

import static net.silthus.schat.command.Result.success;
import static net.silthus.schat.policies.JoinChannelPolicy.canJoinChannel;

@Getter
public class JoinChannelCommand implements JoinChannel, Command {

    @Getter
    @Setter
    private static @NonNull Function<Builder, Builder> prototype = builder -> builder.check(canJoinChannel(builder.chatter, builder.channel).create());
    private final Policy policy;

    private final Chatter chatter;
    private final Channel channel;
    private final Out out;

    protected JoinChannelCommand(Builder builder) {
        this.chatter = builder.chatter;
        this.channel = builder.channel;
        this.out = builder.out;
        this.policy = builder.policy;
    }

    public static Builder joinChannel(Chatter chatter, Channel channel) {
        return getPrototype().apply(new Builder(chatter, channel));
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
        private Policy policy = Policy.ALLOW; // TODO: change to checks

        public Builder(Chatter chatter, Channel channel) {
            this.chatter = chatter;
            this.channel = channel;
        }

        public Builder out(Out out) {
            this.out = out;
            return this;
        }

        public Builder check(Policy policy) {
            this.policy = policy;
            return this;
        }

        public JoinChannelCommand create() {
            return new JoinChannelCommand(this);
        }
    }
}
