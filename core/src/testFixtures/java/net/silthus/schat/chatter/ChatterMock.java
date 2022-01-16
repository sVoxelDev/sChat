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

package net.silthus.schat.chatter;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.silthus.schat.identity.Identity;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.IdentityHelper.randomIdentity;

@Getter
@Setter
public class ChatterMock extends Chatter {

    public static @NotNull Chatter chatterMock(Identity identity) {
        return new ChatterMock(identity);
    }

    public static @NotNull Chatter randomChatter() {
        return chatterMock(randomIdentity());
    }

    private PermissionHandler permissionHandler = permission -> false;
    private MessageHandler messageHandler = message -> {};

    ChatterMock(Identity identity) {
        super(identity);
    }

    @Override
    public boolean hasPermission(String permission) {
        return permissionHandler.hasPermission(permission);
    }

    @Override
    protected void sendRawMessage(Component component) {
        messageHandler.sendMessage(component);
    }
}
