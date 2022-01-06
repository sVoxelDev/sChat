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

import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.message.Message.message;

@FunctionalInterface
public interface ChatHandler extends Handler {

    static ChatHandler sendToActiveChannel() {
        return new SendToActiveChannel();
    }

    Message chat(Chatter chatter, String text);

    class SendToActiveChannel implements ChatHandler {

        protected SendToActiveChannel() {
        }

        @Override
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        public Message chat(final Chatter chatter, final String text) {
            validateChannel(chatter);
            return message()
                .source(chatter)
                .text(text(text))
                .type(Message.Type.CHAT)
                .send(chatter.getActiveChannel().get());
        }

        private void validateChannel(final Chatter chatter) {
            if (chatter.getActiveChannel().isEmpty())
                throw new Chatter.NoActiveChannel();
        }
    }
}
