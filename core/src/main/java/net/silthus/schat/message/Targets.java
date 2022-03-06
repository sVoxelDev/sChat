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
package net.silthus.schat.message;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.NonNull;
import net.silthus.schat.commands.SendMessageResult;
import net.silthus.schat.util.FilterableCollection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import static net.silthus.schat.commands.SendMessageResult.success;

/**
 * A container for targets that can be used to combine and filter multiple targets into one target.
 *
 * <p>The target container can be made unmodifiable with {@link Targets#unmodifiable(Targets)} to make it unmodifiable.</p>
 *
 * @since 1.0.0
 */
public final class Targets extends AbstractSet<MessageTarget> implements MessageTarget, FilterableCollection<MessageTarget> {

    /**
     * <p>Returns an <a href="Collection.html#unmodview">unmodifiable view</a> of the
     * target container. Query operations on the returned targets "read through" to the specified
     * targets, and attempts to modify the returned targets, whether direct or via its
     * iterator, result in an {@code UnsupportedOperationException}.</p>
     *
     * @param targets the set for which an unmodifiable view is to be returned.
     * @return an unmodifiable view of the target container.
     * @since 1.0.0
     */
    public static @NotNull @Unmodifiable Targets unmodifiable(final @NonNull Targets targets) {
        return new Targets(Collections.unmodifiableSet(targets));
    }

    /**
     * Creates an unmodifiable empty target container.
     *
     * @return an empty unmodifiable target container
     * @see #unmodifiable(Targets)
     * @since 1.0.0
     */
    public static @NotNull @Unmodifiable Targets of() {
        return unmodifiable(new Targets());
    }

    /**
     * Creates a new target container with the given initial targets.
     *
     * @param targets the initial targets
     * @return the new target container
     * @since 1.0.0
     */
    public static @NotNull Targets of(final @NonNull MessageTarget @NonNull ... targets) {
        return new Targets(List.of(targets));
    }

    /**
     * Creates a modifiable copy of the given target container.
     *
     * @param targets the target container to copy
     * @return the new targets containing all targets of the given container
     */
    public static @NotNull Targets copyOf(final @NonNull Targets targets) {
        return new Targets(new HashSet<>(Set.copyOf(targets)));
    }

    private final Set<MessageTarget> targets;

    private Targets(final @NonNull Set<MessageTarget> targets) {
        this.targets = targets;
    }

    /**
     * Creates a new target container with the given initial targets.
     *
     * @param targets the initial targets
     * @since 1.0.0
     */
    public Targets(final @NonNull Collection<MessageTarget> targets) {
        this(new LinkedHashSet<>(targets));
    }

    /**
     * Creates a new empty target container.
     *
     * @since 1.0.0
     */
    public Targets() {
        this.targets = new LinkedHashSet<>();
    }

    /**
     * Adds the given target to the collection.
     *
     * @param target the target to add. null values are silently ignored.
     * @return true if the target was added. false if it was not added because it existed or was null.
     * @since 1.0.0
     */
    @Override
    public boolean add(final @Nullable MessageTarget target) {
        if (target == null)
            return false;
        return this.targets.add(target);
    }

    @Override
    public @NotNull Iterator<MessageTarget> iterator() {
        return targets.iterator();
    }

    @Override
    public int size() {
        return targets.size();
    }

    @Override
    public SendMessageResult sendMessage(@NotNull final Message message) {
        forEach(target -> target.sendMessage(message));
        return success(message);
    }
}
