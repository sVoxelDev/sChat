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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
        return contains(entity.getKey());
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
        remove(entity.getKey());
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
