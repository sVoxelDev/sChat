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

import net.kyori.adventure.platform.AudienceProvider;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.user.PermissionHandler;
import net.silthus.schat.user.User;

public abstract class UserFactory<T> {

    public final User getUser(T player) {
        return new User(getIdentity(player),
            getPermissionHandler(player),
            getAudienceProvider(player)
        );
    }

    public final void checkPlayerType(Class<?> playerType) {
        if (!getType().isAssignableFrom(playerType))
            throw new InvalidPlayerType();
    }

    protected abstract Class<T> getType();

    protected abstract Identity getIdentity(T player);

    protected abstract PermissionHandler getPermissionHandler(T player);

    protected abstract AudienceProvider getAudienceProvider(T player);

    public static class InvalidPlayerType extends RuntimeException {
    }
}
