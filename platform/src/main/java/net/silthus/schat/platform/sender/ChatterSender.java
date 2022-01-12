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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.silthus.schat.chatter.AbstractChatter;
import net.silthus.schat.chatter.MessageHandler;
import net.silthus.schat.chatter.PermissionHandler;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import net.silthus.schat.ui.View;

@Setter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
final class ChatterSender extends AbstractChatter implements Sender {

    private final PermissionHandler permissionHandler;
    private final MessageHandler messageHandler;

    @Getter
    private @NonNull View view;

    ChatterSender(Identity identity, PermissionHandler permissionHandler, MessageHandler messageHandler) {
        super(identity);
        this.permissionHandler = permissionHandler;
        this.messageHandler = messageHandler;
        this.view = new View(this);
    }

    @Override
    public boolean hasPermission(String permission) {
        return permissionHandler.hasPermission(permission);
    }

    @Override
    protected void processMessage(Message message) {
        sendRawMessage(getView().render());
    }

    @Override
    public void sendRawMessage(Component message) {
        messageHandler.sendMessage(message);
    }
}
