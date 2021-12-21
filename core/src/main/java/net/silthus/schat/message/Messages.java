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

import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.format.Formatted;
import net.silthus.schat.format.Formatter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import static net.kyori.adventure.text.JoinConfiguration.newlines;

/**
 * A container for messages that can be used to store and persist messages in memory.
 *
 * @since next
 */
public final class Messages extends AbstractSequentialList<Message> implements Formatted {

    /**
     * <p>Returns an <a href="Collection.html#unmodview">unmodifiable view</a> of the
     * messages container. Query operations on the returned targets "read through" to the specified
     * messages, and attempts to modify the returned messages, whether direct or via its
     * iterator, result in an {@code UnsupportedOperationException}.</p>
     *
     * @param messages the messages for which an unmodifiable view is to be returned.
     * @return an unmodifiable view of the message container.
     * @since next
     */
    public static @NotNull @Unmodifiable Messages unmodifiable(final @NonNull Messages messages) {
        return new Messages(Collections.unmodifiableList(messages));
    }

    /**
     * Creates an unmodifiable empty message container.
     *
     * @return an empty unmodifiable message container
     * @see #unmodifiable(Messages)
     * @since next
     */
    public static @NotNull @Unmodifiable Messages of() {
        return new Messages(List.of());
    }

    /**
     * Creates a new message container with the given initial messages.
     *
     * @param messages the initial messages
     * @return the new message container
     * @since next
     */
    public static Messages of(final @NonNull Message @NonNull ... messages) {
        return new Messages(new ArrayList<>(List.of(messages)));
    }

    private final Formatter<Messages> formatter = new MessagesFormatter();
    private final List<Message> messages;

    private Messages(final @NonNull List<Message> messages) {
        this.messages = Collections.synchronizedList(messages);
    }

    /**
     * Creates a new message container with the given initial messages.
     *
     * @param messages the initial messages
     * @since next
     */
    public Messages(final @NonNull Collection<Message> messages) {
        this.messages = new ArrayList<>(messages);
    }

    /**
     * Creates a new empty but modifiable message container.
     *
     * @since next
     */
    public Messages() {
        this(new ArrayList<>());
    }

    @Override
    public boolean add(final @NonNull Message message) {
        if (messages.contains(message)) return false;
        messages.add(message);
        messages.sort(Message::compareTo);
        return true;
    }

    @Override
    public @NotNull ListIterator<Message> listIterator(final int index) {
        return messages.listIterator(index);
    }

    @Override
    public int size() {
        return messages.size();
    }

    /**
     * Filters the elements in the collection by the given predicate.
     *
     * <p>This is the same as {@code stream().filter(...)} but without the overhead of streams.</p>
     *
     * @param filter the predicate to filter for
     * @return an unmodifiable filtered list
     * @since next
     */
    public @NotNull @Unmodifiable Messages filter(final @NonNull Predicate<Message> filter) {
        final Collection<Message> elements = new ArrayList<>();
        for (final Message message : this) {
            if (filter.test(message))
                elements.add(message);
        }
        return unmodifiable(new Messages(elements));
    }

    /**
     * Gets the last message that was added or null if the collection is empty.
     *
     * @return the last added message or null
     * @since next
     */
    public @Nullable Message last() {
        if (size() > 0)
            return get(size() - 1);
        return null;
    }

    @Override
    public Component formatted() {
        return formatter.format(this);
    }

    private static class MessagesFormatter implements Formatter<Messages> {

        @Override
        public Component format(final Messages messages) {
            return Component.join(
                newlines(),
                messages
                    .stream()
                    .map(Message::formatted)
                    .toList()
            );
        }
    }
}
