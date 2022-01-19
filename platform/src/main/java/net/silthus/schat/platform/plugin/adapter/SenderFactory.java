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

package net.silthus.schat.platform.plugin.adapter;

import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.MessageHandler;
import net.silthus.schat.chatter.PermissionHandler;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;

import static net.kyori.adventure.text.Component.text;

public abstract class SenderFactory<T> {

    public static final UUID CONSOLE_UUID = new UUID(0, 0);
    public static final String CONSOLE_NAME = "Console";
    public static final Component CONSOLE_DISPLAY_NAME = text(CONSOLE_NAME);

    public final Chatter wrap(T sender) {
        return new SenderChatter<>(sender,
            getIdentity(sender),
            getPermissionHandler(sender),
            getMessageHandler(sender)
        );
    }

    @SuppressWarnings("unchecked")
    public final T unwrap(Chatter chatter) throws InvalidChatterType {
        if (chatter instanceof SenderChatter sender)
            return (T) sender.sender;
        else
            throw new InvalidChatterType();
    }

    protected abstract Identity getIdentity(T sender);

    protected abstract PermissionHandler getPermissionHandler(T sender);

    protected abstract MessageHandler getMessageHandler(T sender);

    @Setter
    @EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
    static final class SenderChatter<T> extends Chatter {

        private final T sender;
        private final PermissionHandler permissionHandler;
        private final MessageHandler messageHandler;

        SenderChatter(T sender, Identity identity, PermissionHandler permissionHandler, MessageHandler messageHandler) {
            super(identity);
            this.sender = sender;
            this.permissionHandler = permissionHandler;
            this.messageHandler = messageHandler;
        }

        public boolean isConsole() {
            return getUniqueId().equals(CONSOLE_UUID);
        }

        @Override
        public boolean hasPermission(String permission) {
            return permissionHandler.hasPermission(permission);
        }

        @Override
        protected void processMessage(@NonNull Message message) {
            if (isConsole())
                sendRawMessage(message.text());
            else
                super.processMessage(message);
        }

        @Override
        protected void sendRawMessage(Component component) {
            messageHandler.sendMessage(component);
        }
    }

    public static final class InvalidChatterType extends RuntimeException {
    }
}
