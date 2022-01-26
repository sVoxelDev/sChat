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

package net.silthus.schat.command;

import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.command.commands.JoinChannelCommand;

import static net.silthus.schat.channel.Channel.JOIN_PERMISSION;
import static net.silthus.schat.channel.Channel.PROTECTED;
import static net.silthus.schat.command.Result.of;
import static net.silthus.schat.command.Result.success;

public class CanJoinChannelCheck implements Check<JoinChannelCommand> {

    public static final Check.Type<JoinChannelCommand> CAN_JOIN_CHANNEL = Check.check(JoinChannelCommand.class, CanJoinChannelCheck::new);

    @Override
    public Result check(JoinChannelCommand command) {
        final Chatter chatter = command.getChatter();
        final Channel channel = command.getChannel();
        if (!channel.get(PROTECTED))
            return success();
        return of(chatter.hasPermission(channel.get(JOIN_PERMISSION)));
    }
}
