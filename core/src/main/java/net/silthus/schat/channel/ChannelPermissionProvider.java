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

import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.channel.Channel.JOIN_PERMISSION;

public interface ChannelPermissionProvider {

    ChannelPermissionProvider DEFAULT = new Default();

    @NotNull String joinChannel(Channel channel);

    class Default implements ChannelPermissionProvider {

        public static final String BASE_PERMISSION = "schat.channel.";
        public static final String JOIN_SUFFIX = ".join";

        @Override
        public @NotNull String joinChannel(Channel channel) {
            final String defaultPermission = BASE_PERMISSION + channel.getKey() + JOIN_SUFFIX;
            return channel.getOrDefault(JOIN_PERMISSION, defaultPermission);
        }
    }
}
