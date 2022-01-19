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

package net.silthus.schat.message;

import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Accessors(fluent = true)
public class MessengerImpl implements Messenger {

    static final Messenger NIL = new NilMessenger();

    @Override
    public Message.Draft process(@NonNull Message.Draft message) {
        return message;
    }

    @Override
    public void deliver(@NonNull Message message) {
        message.targets().forEach(target -> target.sendMessage(message));
    }

    static final class NilMessenger implements Messenger {

        @Override
        public Message.Draft process(Message.Draft message) {
            return message;
        }

        @Override
        public void deliver(Message message) {
        }
    }
}

