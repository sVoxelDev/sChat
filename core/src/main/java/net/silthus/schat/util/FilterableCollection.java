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
 * @since 1.0.0-alpha.4
 */
public interface FilterableCollection<T> extends Collection<T> {

    /**
     * Filters the elements in the collection by the given predicate.
     *
     * <p>This is the same as {@code stream().filter(...)} but without the overhead of streams.</p>
     *
     * @param filter the predicate to filter for
     * @return an unmodifiable filtered list
     * @since 1.0.0-alpha.4
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
     * @since 1.0.0-alpha.4
     */
    default @NotNull Optional<T> find(final @NonNull Predicate<T> filter) {
        for (final T element : this) {
            if (filter.test(element))
                return Optional.of(element);
        }
        return Optional.empty();
    }
}
