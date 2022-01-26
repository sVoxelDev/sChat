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

import java.util.function.Consumer;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.command.Command;
import net.silthus.schat.command.Result;

public class SetActiveChannelCommand implements Command {

    public static SetActiveChannelCommand.Builder setActiveChannel(Chatter chatter, Channel channel) {
        return new Builder(chatter, channel);
    }

    private final Chatter chatter;
    private final Channel channel;
    private final JoinChannelCommand joinChannelCommand;

    protected SetActiveChannelCommand(Builder builder) {
        this.chatter = builder.chatter;
        this.channel = builder.channel;
        this.joinChannelCommand = builder.joinChannelCommand.create();
    }

    @Override
    public Result execute() throws Error {
        joinChannelCommand.execute();
        chatter.setActiveChannel(channel);
        chatter.updateView();
        return Command.success();
    }

    public static class Builder {
        private final Chatter chatter;
        private final Channel channel;
        private final JoinChannelCommand.Builder joinChannelCommand;

        public Builder(Chatter chatter, Channel channel) {
            this.chatter = chatter;
            this.channel = channel;
            this.joinChannelCommand = JoinChannelCommand.joinChannel(chatter, channel);
        }

        public Builder joinChannelCmd(Consumer<JoinChannelCommand.Builder> builder) {
            builder.accept(joinChannelCommand);
            return this;
        }

        public SetActiveChannelCommand create() {
            return new SetActiveChannelCommand(this);
        }
    }
}
