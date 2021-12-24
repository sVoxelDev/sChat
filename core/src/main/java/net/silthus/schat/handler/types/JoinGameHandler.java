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

import net.silthus.schat.User;
import net.silthus.schat.chatter.ChatterRegistry;
import net.silthus.schat.handler.Handler;
import net.silthus.schat.handler.HandlerFactory;

import static net.silthus.schat.chatter.Chatter.chatter;

@FunctionalInterface
public interface JoinGameHandler extends Handler {

    void joinGame(User user);

    class Default implements JoinGameHandler {

        private final ChatterRegistry registry;
        private final HandlerFactory<User, JoinChannelHandler> joinChannelFactory;

        public Default(ChatterRegistry registry, HandlerFactory<User, JoinChannelHandler> joinChannelFactory) {
            this.registry = registry;
            this.joinChannelFactory = joinChannelFactory;
        }

        @Override
        public void joinGame(final User user) {
            registry.add(chatter(user.getIdentity()).joinChannelHandler(joinChannelFactory.create(user)).create());
        }
    }
}
