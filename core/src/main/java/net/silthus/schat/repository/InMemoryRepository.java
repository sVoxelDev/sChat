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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

/**
 * A simple in memory implementation of a {@link Repository}.
 *
 * <p>The entity store uses a {@link ConcurrentHashMap} by default.
 * This can be changed using the {@link #InMemoryRepository(Map)} constructor.</p>
 *
 * @since next
 */
public abstract class InMemoryRepository<K, E extends Entity<K>> implements Repository<K, E> {

    private final Map<K, E> entities;

    /**
     * Creates a new repository using a {@link ConcurrentHashMap}.
     *
     * @since next
     */
    public InMemoryRepository() {
        this.entities = new ConcurrentHashMap<>();
    }

    /**
     * Creates a new repository using the provided map as the store.
     *
     * @param entities the entity store
     * @since next
     */
    public InMemoryRepository(final Map<K, E> entities) {
        this.entities = entities;
    }

    @Override
    public @NotNull @Unmodifiable Collection<E> all() {
        return Collections.unmodifiableCollection(entities.values());
    }

    @Override
    public @NotNull @Unmodifiable List<K> keys() {
        return List.copyOf(entities.keySet());
    }

    @Override
    public boolean contains(final K key) {
        return entities.containsKey(key);
    }

    @Override
    public @NotNull E get(@NotNull final K id) throws NotFound {
        if (!entities.containsKey(id))
            throw new NotFound();
        return entities.get(id);
    }

    @Override
    public void add(@NotNull final E entity) {
        entities.putIfAbsent(entity.getKey(), entity);
    }

    @Override
    public void remove(@NotNull final K key) {
        entities.remove(key);
    }
}
