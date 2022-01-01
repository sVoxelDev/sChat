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

package net.silthus.schat.platform;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
import net.kyori.adventure.text.Component;
import net.silthus.schat.handler.types.PermissionHandler;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.sender.Sender;

import static net.silthus.schat.platform.IdentityHelper.randomIdentity;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SenderMock implements Sender {

    public static SenderMock randomSenderMock() {
        return senderMock(randomIdentity());
    }

    public static SenderMock senderMock(Identity identity) {
        return senderMock(identity, permission -> false);
    }

    public static SenderMock senderMock(Identity identity, PermissionHandler permissionHandler) {
        return new SenderMock(identity, permissionHandler);
    }

    @Getter
    private final Identity identity;
    @With
    private final PermissionHandler permissionHandler;

    @Override
    public void sendMessage(Component message) {

    }

    @Override
    public boolean hasPermission(String permission) {
        return permissionHandler.hasPermission(permission);
    }

    @Override
    public void performCommand(String commandLine) {

    }

    @Override
    public boolean isConsole() {
        return false;
    }
}
