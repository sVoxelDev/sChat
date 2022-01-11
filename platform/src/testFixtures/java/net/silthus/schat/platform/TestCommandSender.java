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

import lombok.Getter;
import lombok.Setter;
import net.silthus.schat.IdentityHelper;
import net.silthus.schat.chatter.PermissionHandler;
import net.silthus.schat.identity.Identity;

@Getter
@Setter
public class TestCommandSender {

    public static TestCommandSender randomCommandSender() {
        return new TestCommandSender();
    }

    private final Identity identity = IdentityHelper.randomIdentity();
    private PermissionHandler permissionHandler = permission -> false;
}
