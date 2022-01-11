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

package net.silthus.schat.platform.sender;

import net.silthus.schat.chatter.MessageHandler;
import net.silthus.schat.chatter.PermissionHandler;
import net.silthus.schat.identity.Identity;

public abstract class SenderFactory<T> {

    public final Sender getSender(T sender) {
        return new Sender(getIdentity(sender),
            getPermissionHandler(sender),
            getMessageHandler(sender)
        );
    }

    public final void checkSenderType(Class<?> senderType) {
        if (!getType().isAssignableFrom(senderType))
            throw new InvalidPlayerType();
    }

    protected abstract Class<T> getType();

    protected abstract Identity getIdentity(T sender);

    protected abstract PermissionHandler getPermissionHandler(T sender);

    protected abstract MessageHandler getMessageHandler(T sender);

    public static class InvalidPlayerType extends RuntimeException {
    }
}
