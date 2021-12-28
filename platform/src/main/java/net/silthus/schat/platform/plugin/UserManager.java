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

package net.silthus.schat.platform.plugin;

import java.util.Collection;
import java.util.UUID;
import lombok.Getter;
import net.silthus.schat.handler.types.UserJoinHandler;
import net.silthus.schat.user.User;
import net.silthus.schat.user.UserRepository;
import net.silthus.schat.user.Users;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

final class UserManager implements Users {

    @Getter
    private final UserRepository repository;
    private final UserJoinHandler joinHandler;

    UserManager(UserRepository repository, UserJoinHandler joinHandler) {
        this.repository = repository;
        this.joinHandler = joinHandler;
    }

    @Override
    public @NotNull @Unmodifiable Collection<User> all() {
        return getRepository().all();
    }

    @Override
    public boolean contains(UUID id) {
        return getRepository().contains(id);
    }

    @Override
    public @NotNull User get(@NotNull UUID id) throws NotFound {
        return getRepository().get(id);
    }

    @Override
    public void add(@NotNull User user) {
        getRepository().add(user);
    }

    @Override
    public void remove(@NotNull UUID key) {
        getRepository().remove(key);
    }

    @Override
    public void join(User user) {
        joinHandler.join(user);
        add(user);
    }

}
