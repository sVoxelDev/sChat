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

import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.handler.Handler;
import net.silthus.schat.message.Message;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.message.Message.message;

@FunctionalInterface
public interface ChatHandler extends Handler {

    Message chat(Chatter chatter, String text);

    class Default implements ChatHandler {

        @Override
        public Message chat(final Chatter chatter, final String text) {
            validateChannel(chatter);
            final Message message = createMessage(text, chatter);
            sendToActiveChannel(message, chatter);
            return message;
        }

        private void validateChannel(final Chatter chatter) {
            if (chatter.getActiveChannel().isEmpty())
                throw new Chatter.NoActiveChannel();
        }

        private @NotNull Message createMessage(final String text, final Chatter chatter) {
            return message(chatter, text);
        }

        private void sendToActiveChannel(final Message message, final Chatter chatter) {
            chatter.getActiveChannel().ifPresent(channel -> channel.sendMessage(message));
        }
    }
}
