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

package net.silthus.schat.channel;

import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.policies.CanJoinChannel;
import net.silthus.schat.usecases.JoinChannel;

import static net.silthus.schat.command.Command.failure;
import static net.silthus.schat.command.Command.success;

public class JoinChannelCommand implements JoinChannel {

    public static Builder joinChannel(Chatter chatter, Channel channel) {
        return new Builder(chatter, channel);
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

    public net.silthus.schat.command.Result execute() throws Error {
        if (check.canJoinChannel(chatter, channel))
            return joinChannelAndUpdateView(chatter, channel);
        else
            return handleJoinChannelError(chatter, channel);
    }

    private net.silthus.schat.command.Result joinChannelAndUpdateView(Chatter chatter, Channel channel) {
        if (chatter.isJoined(channel))
            return failure();
        chatter.join(channel);
        notifyJoinChannelPresenter(chatter, channel);
        chatter.updateView();
        return success();
    }

    private void notifyJoinChannelPresenter(Chatter chatter, Channel channel) {
        out.joinedChannel(new Result(chatter, channel));
    }

    private net.silthus.schat.command.Result handleJoinChannelError(Chatter chatter, Channel channel) throws AccessDenied {
        chatter.leave(channel);
        throw new AccessDenied();
    }

    public static class Builder {
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
