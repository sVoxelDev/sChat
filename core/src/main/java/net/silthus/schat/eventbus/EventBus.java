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
package net.silthus.schat.eventbus;

import java.util.Set;
import java.util.function.Consumer;
import lombok.NonNull;
import net.silthus.schat.events.SChatEvent;
import org.jetbrains.annotations.Unmodifiable;

/**
 * The sChat event bus.
 *
 * <p>Used to subscribe (or "listen") to sChat events.</p>
 *
 * @since next
 */
public interface EventBus extends AutoCloseable {

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
     * Creates a new event bus instance.
     *
     * <p>Make sure to free up the event bus calling {@link #close()}.</p>
     *
     * @return the created event bus
     * @since next
     */
    static EventBus eventBus() {
        return eventBus(false);
    }

    /**
     * Creates a new event bus that is optionally logging all interactions.
     *
     * @param debug true to log all calls to the bus
     * @return the created event bus
     * @since next
     */
    static EventBus eventBus(boolean debug) {
        if (debug)
            return new EventBusImpl.Logging();
        else
            return new EventBusImpl();
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
     * Registers all methods of the given listener annotated with {@link Subscribe} as event handlers.
     *
     * <p>All event handler methods must have a single parameter, being the event they want to subscribe to.</p>
     *
     * @param listener the listener to search for events
     * @return a list of registered event subscriptions
     * @since next
     */
    @NonNull @Unmodifiable Set<EventSubscription<?>> register(Object listener);

    /**
     * Gets a set of all registered handlers for a given event.
     *
     * @param eventClass the event to find handlers for
     * @param <E>        the event class
     * @return an immutable set of event handlers
     * @since next
     */
    <E extends SChatEvent> @NonNull @Unmodifiable Set<EventSubscription<E>> subscriptions(@NonNull Class<E> eventClass);

    /**
     * Closes the event bus unsubscribing all event handlers freeing up resource locks.
     *
     * @since next
     */
    void close();
}
