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

package net.silthus.schat.handler.types;

import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelPermissionProvider;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterRepository;
import net.silthus.schat.handler.Handler;
import net.silthus.schat.user.User;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.channel.Channel.AUTO_JOIN;
import static net.silthus.schat.channel.Channel.REQUIRES_JOIN_PERMISSION;
import static net.silthus.schat.handler.types.JoinChannelHandler.steps;

public interface UserJoinHandler extends Handler {

    static Default createUserJoinHandler(ChatterRepository chatters,
                                         ChannelRepository channels,
                                         ChannelPermissionProvider channelPermissions) {
        return new Default(chatters, channels, channelPermissions);
    }

    void join(User user);

    class Default implements UserJoinHandler {

        private final ChatterRepository chatterRepository;
        private final ChannelRepository channels;
        private final ChannelPermissionProvider channelPermissions;

        protected Default(ChatterRepository chatters,
                          ChannelRepository channels,
                          ChannelPermissionProvider channelPermissions) {
            this.chatterRepository = chatters;
            this.channels = channels;
            this.channelPermissions = channelPermissions;
        }

        @Override
        public void join(User user) {
            final Chatter chatter = createChatter(user);
            chatterRepository.add(chatter);
            autoJoinChannels(chatter);
        }

        private void autoJoinChannels(Chatter chatter) {
            for (final Channel channel : channels.filter(channel -> channel.get(AUTO_JOIN))) {
                try {
                    chatter.join(channel);
                } catch (JoinChannelHandler.Error ignored) {
                }
            }
        }

        private Chatter createChatter(User user) {
            return Chatter.chatter(user.getIdentity())
                .joinChannel(steps(new JoinChannelPermissionCheck(user, channelPermissions)))
                .create();
        }

        private record JoinChannelPermissionCheck(User user, ChannelPermissionProvider permissions) implements JoinChannelHandler {

            @Override
            public void process(Chatter chatter, Channel channel) throws JoinChannelHandler.Error {
                if (requiresJoinPermission(channel) && hasNoJoinPermission(channel))
                    throw new JoinChannelHandler.AccessDenied();
            }

            @NotNull
            private Boolean requiresJoinPermission(Channel channel) {
                return channel.get(REQUIRES_JOIN_PERMISSION);
            }

            private boolean hasNoJoinPermission(Channel channel) {
                return !user.hasPermission(permissions.joinChannel(channel));
            }
        }
    }
}
