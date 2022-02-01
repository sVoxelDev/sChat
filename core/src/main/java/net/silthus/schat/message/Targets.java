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
import net.silthus.schat.util.FilterableCollection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

/**
 * A container for targets that can be used to combine and filter multiple targets into one target.
 *
 * <p>The target container can be made unmodifiable with {@link Targets#unmodifiable(Targets)} to make it unmodifiable.</p>
 *
 * @since next
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
     * @since next
     */
    public static @NotNull @Unmodifiable Targets unmodifiable(final @NonNull Targets targets) {
        return new Targets(Collections.unmodifiableSet(targets));
    }

    /**
     * Creates an unmodifiable empty target container.
     *
     * @return an empty unmodifiable target container
     * @see #unmodifiable(Targets)
     * @since next
     */
    public static @NotNull @Unmodifiable Targets of() {
        return unmodifiable(new Targets());
    }

    /**
     * Creates a new target container with the given initial targets.
     *
     * @param targets the initial targets
     * @return the new target container
     * @since next
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
     * @since next
     */
    public Targets(final @NonNull Collection<MessageTarget> targets) {
        this(new LinkedHashSet<>(targets));
    }

    /**
     * Creates a new empty target container.
     *
     * @since next
     */
    public Targets() {
        this.targets = new LinkedHashSet<>();
    }

    @Override
    public boolean add(final @NonNull MessageTarget target) {
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
    public void sendMessage(@NotNull final Message message) {
        forEach(target -> target.sendMessage(message));
    }
}
