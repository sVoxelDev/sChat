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
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.handler.Handler;

@FunctionalInterface
public interface JoinChannelHandler extends Handler {

    static JoinChannelHandler empty() {
        return new Default();
    }

    static JoinChannelHandler steps(JoinChannelHandler... steps) {
        return new Default(steps);
    }

    void process(Chatter chatter, Channel channel);

    class Default implements JoinChannelHandler {

        private final JoinChannelHandler[] steps;

        protected Default(JoinChannelHandler... steps) {
            this.steps = steps;
        }

        @Override
        public void process(final Chatter chatter, final Channel channel) {
            processSteps(chatter, channel);
        }

        private void processSteps(Chatter chatter, Channel channel) {
            for (final JoinChannelHandler step : steps) {
                step.process(chatter, channel);
            }
        }
    }

    class Error extends RuntimeException {
        public Error() {
        }

        public Error(String message) {
            super(message);
        }

        public Error(String message, Throwable cause) {
            super(message, cause);
        }

        public Error(Throwable cause) {
            super(cause);
        }
    }

    class AccessDenied extends Error {
    }
}
