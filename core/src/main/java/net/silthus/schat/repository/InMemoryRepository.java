/*
 * This file is part of sChat, licensed under the MIT License.
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
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
        entities.putIfAbsent(entity.key(), entity);
    }

    @Override
    public void remove(@NotNull final K key) {
        entities.remove(key);
    }
}
