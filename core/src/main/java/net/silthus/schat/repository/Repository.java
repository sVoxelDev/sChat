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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

/**
 * A generic repository for entities.
 *
 * @param <K> the type of the key
 * @param <E> the type of the entity
 * @since next
 */
public interface Repository<K, E extends Entity<K>> {

    /**
     * Gets all entities in the repository.
     *
     * @return all entities
     * @since next
     */
    @NotNull @Unmodifiable Collection<E> all();

    /**
     * Gets a list of all keys stored in this repository.
     *
     * @return all keys
     * @since next
     */
    @NotNull @Unmodifiable List<K> keys();

    /**
     * Checks if the repository contains an entity with the given key.
     *
     * @param key the key of the entity
     * @return true if the entity exists
     * @since next
     */
    boolean contains(K key);

    /**
     * Checks if the repository contains the given entity.
     *
     * @param entity the entity
     * @return true if the entity exists
     * @since next
     */
    default boolean contains(E entity) {
        return contains(entity.key());
    }

    /**
     * Tries to find an entity by its id.
     *
     * @param id the id of the entity
     * @return the chatter if found, else throws NotFound
     * @throws NotFound if entity does not exist
     * @since next
     */
    @NotNull E get(@NotNull K id) throws NotFound;

    /**
     * Adds the given entity to the repository if it does not exist.
     *
     * <p>Does nothing if the entity already exists.</p>
     *
     * @param entity the entity to add
     * @since next
     */
    void add(@NotNull E entity);

    /**
     * Removes the entity with the given key from the repository.
     *
     * @param key the key of the entity
     * @since next
     */
    void remove(@NotNull K key);

    /**
     * Removes the given entity from the repository if it exists.
     *
     * <p>Does nothing if the entity does not exist.</p>
     *
     * @param entity the entity to remove
     * @since next
     */
    default void remove(@NotNull E entity) {
        remove(entity.key());
    }

    /**
     * Filters the elements in this repository by the given predicate.
     *
     * <p>This is the same as {@code stream().filter(...)} but without the overhead of streams.</p>
     *
     * @param filter the predicate to filter for
     * @return an unmodifiable filtered list of entities matching the filter
     * @since next
     */
    default @NotNull @Unmodifiable List<E> filter(final @NonNull Predicate<E> filter) {
        final ArrayList<E> entities = new ArrayList<>();
        for (final E entity : all()) {
            if (filter.test(entity))
                entities.add(entity);
        }
        return Collections.unmodifiableList(entities);
    }

    default Optional<E> find(K key) {
        return find(e -> e.key().equals(key));
    }

    default E findOrCreate(K key, Function<K, E> creator) {
        return find(e -> e.key().equals(key)).orElseGet(() -> {
            final E entity = creator.apply(key);
            add(entity);
            return entity;
        });
    }

    /**
     * Tries to find an entity matching the given filter and returns the first match.
     *
     * @param filter the filter used to search for the entity
     * @return the entity if found
     * @since next
     */
    default @NotNull Optional<E> find(final @NonNull Predicate<E> filter) {
        for (final E entity : all()) {
            if (filter.test(entity))
                return Optional.of(entity);
        }
        return Optional.empty();
    }

    class NotFound extends RuntimeException {
    }
}
