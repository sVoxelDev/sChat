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

package net.silthus.schat.repository;

import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public interface RepositoryHolder<R extends Repository<K, E>, K, E extends Entity<K>> extends Repository<K, E> {

    @NotNull R getRepository();

    @Override
    default @NotNull @Unmodifiable Collection<E> all() {
        return getRepository().all();
    }

    @Override
    default @NotNull @Unmodifiable List<K> keys() {
        return getRepository().keys();
    }

    @Override
    default boolean contains(K key) {
        return getRepository().contains(key);
    }

    @Override
    default @NotNull E get(@NotNull K key) throws NotFound {
        return getRepository().get(key);
    }

    @Override
    default void add(@NotNull E chatter) {
        getRepository().add(chatter);
    }

    @Override
    default void remove(@NotNull K key) {
        getRepository().remove(key);
    }
}
