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

package net.silthus.schat.channel.checks;

import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.usecases.JoinChannel;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.channel.Channel.JOIN_PERMISSION;
import static net.silthus.schat.channel.Channel.REQUIRES_JOIN_PERMISSION;
import static net.silthus.schat.usecases.Check.failure;
import static net.silthus.schat.usecases.Check.success;

public final class JoinChannelPermissionCheck implements JoinChannel.Check {

    @Override
    public Result test(JoinChannel.Args args) {
        if (requiresJoinPermission(args.channel()) && hasNoJoinPermission(args.chatter(), args.channel()))
            return failure(new AccessDenied());
        return success();
    }

    @NotNull
    private Boolean requiresJoinPermission(Channel channel) {
        return channel.get(REQUIRES_JOIN_PERMISSION);
    }

    private boolean hasNoJoinPermission(Chatter chatter, Channel channel) {
        return !channel.get(JOIN_PERMISSION).test(chatter);
    }

    public static class AccessDenied extends Error {

    }
}
