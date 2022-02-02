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

package net.silthus.schat.eventbus;

import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.kyori.event.EventSubscriber;
import net.kyori.event.SimpleEventBus;
import net.silthus.schat.events.SChatEvent;

/**
 * The base implementation of the event bus that must be implemented by each platform.
 *
 * @param <P> the type of the plugin of the platform
 * @since next
 */
@Accessors(fluent = true)
public abstract class AbstractEventBus<P> implements EventBus, AutoCloseable {

    private final Bus bus = new Bus();

    protected AbstractEventBus() {
    }

    /**
     * Checks that the given plugin object is a valid plugin instance for the platform.
     *
     * @param plugin the object
     * @return a plugin
     * @throws IllegalArgumentException if the plugin is invalid
     * @since next
     */
    protected abstract P checkPlugin(Object plugin) throws IllegalArgumentException;

    @Override
    public <E extends SChatEvent> E post(final @NonNull E event) {
        this.bus.post(event);
        return event;
    }

    @Override
    public <T extends SChatEvent> @NonNull EventSubscription<T> on(final @NonNull Class<T> eventClass,
                                                                   final @NonNull Consumer<? super T> handler) {
        return registerSubscription(eventClass, handler, null);
    }

    @Override
    public <T extends SChatEvent> @NonNull EventSubscription<T> on(final @NonNull Object plugin,
                                                                   final @NonNull Class<T> eventClass,
                                                                   final @NonNull Consumer<? super T> handler) {
        return registerSubscription(eventClass, handler, checkPlugin(plugin));
    }

    private <T extends SChatEvent> EventSubscription<T> registerSubscription(final Class<T> eventClass,
                                                                             final Consumer<? super T> handler,
                                                                             final Object plugin) {
        final EventSubscriptionImpl<T> eventHandler = new EventSubscriptionImpl<>(this, eventClass, handler, plugin);
        this.bus.register(eventClass, eventHandler);

        return eventHandler;
    }

    @Override
    public <T extends SChatEvent> @NonNull Set<EventSubscription<T>> subscriptions(final @NonNull Class<T> eventClass) {
        return this.bus.handlers(eventClass);
    }

    /**
     * Removes a specific handler from the bus.
     *
     * @param handler the handler to remove
     * @since next
     */
    public void unregisterHandler(final EventSubscriber<?> handler) {
        this.bus.unregister(handler);
    }

    /**
     * Removes all handlers for a specific plugin.
     *
     * @param plugin the plugin
     * @since next
     */
    protected void unregisterHandlers(final P plugin) {
        this.bus.unregister(sub -> ((EventSubscriptionImpl<?>) sub).plugin() == plugin);
    }

    @Override
    public void close() {
        this.bus.unregisterAll();
    }

    private static final class Bus extends SimpleEventBus<SChatEvent> {
        Bus() {
            super(SChatEvent.class);
        }

        @Override
        protected boolean shouldPost(final @NonNull SChatEvent event, final @NonNull EventSubscriber<?> subscriber) {
            return true;
        }

        public <E extends SChatEvent> Set<EventSubscription<E>> handlers(final Class<E> eventClass) {
            //noinspection unchecked
            return super.subscribers().values().stream()
                .filter(s -> s instanceof EventSubscription && ((EventSubscription<?>) s).eventClass().isAssignableFrom(eventClass))
                .map(s -> (EventSubscription<E>) s)
                .collect(Collectors.toSet());
        }
    }
}
