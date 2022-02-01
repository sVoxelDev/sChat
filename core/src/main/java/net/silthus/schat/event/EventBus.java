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

package net.silthus.schat.event;

import java.util.Set;
import java.util.function.Consumer;
import lombok.NonNull;
import org.jetbrains.annotations.Unmodifiable;

/**
 * The sChat event bus.
 *
 * <p>Used to subscribe (or "listen") to sChat events.</p>
 *
 * @since next
 */
public interface EventBus {

    /**
     * Creates a new empty event bus instance.
     *
     * @return the new empty event bus
     * @since next
     */
    static EventBus empty() {
        return new EmptyEventBus();
    }

    /**
     * Posts a new event to the event bus informing all subscribers about the event.
     *
     * @param event the event to post
     * @since next
     */
    <E extends SChatEvent> E post(@NonNull E event);

    /**
     * Registers a new subscription to the given event.
     *
     * <p>The returned {@link EventSubscription} instance encapsulates the subscription state. It has
     * methods which can be used to terminate the subscription, or view stats about the nature of
     * the subscription.</p>
     *
     * @param eventClass the event class
     * @param handler    the event handler
     * @param <E>        the event class
     * @return an event handler instance representing this subscription
     * @since next
     */
    <E extends SChatEvent> @NonNull EventSubscription<E> on(@NonNull Class<E> eventClass, @NonNull Consumer<? super E> handler);

    /**
     * Registers a new subscription to the given event.
     *
     * <p>The returned {@link EventSubscription} instance encapsulates the subscription state. It has
     * methods which can be used to terminate the subscription, or view stats about the nature of
     * the subscription.</p>
     *
     * <p>Unlike {@link #on(Class, Consumer)}, this method accepts an additional parameter
     * for {@code plugin}. This object must be a "plugin" instance on the platform, and is used to
     * automatically {@link EventSubscription#close() unregister} the subscription when the
     * corresponding plugin is disabled.</p>
     *
     * @param <E>        the event class
     * @param plugin     a plugin instance to bind the subscription to.
     * @param eventClass the event class
     * @param handler    the event handler
     * @return an event handler instance representing this subscription
     * @since next
     */
    <E extends SChatEvent> @NonNull EventSubscription<E> on(Object plugin, @NonNull Class<E> eventClass, @NonNull Consumer<? super E> handler);

    /**
     * Gets a set of all registered handlers for a given event.
     *
     * @param eventClass the event to find handlers for
     * @param <E>        the event class
     * @return an immutable set of event handlers
     * @since next
     */
    <E extends SChatEvent> @NonNull @Unmodifiable Set<EventSubscription<E>> subscriptions(@NonNull Class<E> eventClass);
}
