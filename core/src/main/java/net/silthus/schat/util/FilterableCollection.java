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

package net.silthus.schat.util;

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
 * Marks the collection as filterable and adds the {@link #filter(Predicate)} method.
 *
 * @param <T> the type of the collection
 * @since next
 */
public interface FilterableCollection<T> extends Collection<T> {

    /**
     * Filters the elements in the collection by the given predicate.
     *
     * <p>This is the same as {@code stream().filter(...)} but without the overhead of streams.</p>
     *
     * @param filter the predicate to filter for
     * @return an unmodifiable filtered list
     * @since next
     */
    default @NotNull @Unmodifiable List<T> filter(final @NonNull Predicate<T> filter) {
        final ArrayList<T> elements = new ArrayList<>();
        for (final T element : this) {
            if (filter.test(element))
                elements.add(element);
        }
        return Collections.unmodifiableList(elements);
    }

    /**
     * Tries to find an element matching the given filter and returns the first match.
     *
     * @param filter the filter used to search for the element
     * @return the element if found
     * @since next
     */
    default @NotNull Optional<T> find(final @NonNull Predicate<T> filter) {
        for (final T element : this) {
            if (filter.test(element))
                return Optional.of(element);
        }
        return Optional.empty();
    }
}
