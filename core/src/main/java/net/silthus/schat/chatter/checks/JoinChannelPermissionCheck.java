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

package net.silthus.schat.chatter.checks;

import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.sender.Sender;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.channel.Channel.JOIN_PERMISSION;
import static net.silthus.schat.channel.Channel.REQUIRES_JOIN_PERMISSION;

public record JoinChannelPermissionCheck(Sender sender) implements Chatter.JoinChannel {

    @Override
    public void joinChannel(Chatter chatter, Channel channel) throws Error {
        if (requiresJoinPermission(channel) && hasNoJoinPermission(channel))
            throw new AccessDenied();
    }

    @NotNull
    private Boolean requiresJoinPermission(Channel channel) {
        return channel.get(REQUIRES_JOIN_PERMISSION);
    }

    private boolean hasNoJoinPermission(Channel channel) {
        return !channel.get(JOIN_PERMISSION).test(sender);
    }
}
