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
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;
import net.kyori.event.EventSubscriber;
import net.kyori.event.SimpleEventBus;
import net.silthus.schat.events.SChatEvent;
import org.jetbrains.annotations.NotNull;

@Accessors(fluent = true)
class EventBusImpl implements EventBus, AutoCloseable {

    private final Bus bus = new Bus();

    protected EventBusImpl() {
    }

    @Override
    public <E extends SChatEvent> E post(final @NonNull E event) {
        this.bus.post(event);
        return event;
    }

    @Override
    public <T extends SChatEvent> @NonNull EventSubscription<T> on(final @NonNull Class<T> eventClass,
                                                                   final @NonNull Consumer<? super T> handler) {
        return registerSubscription(eventClass, handler);
    }

    private <T extends SChatEvent> EventSubscription<T> registerSubscription(final Class<T> eventClass,
                                                                             final Consumer<? super T> handler) {
        final EventSubscriptionImpl<T> eventHandler = createSubscription(eventClass, handler);
        this.bus.register(eventClass, eventHandler);

        return eventHandler;
    }

    @NotNull
    protected <T extends SChatEvent> EventSubscriptionImpl<T> createSubscription(Class<T> eventClass, Consumer<? super T> handler) {
        return new EventSubscriptionImpl<>(this, eventClass, handler);
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

        @SuppressWarnings("unchecked")
        public <E extends SChatEvent> Set<EventSubscription<E>> handlers(final Class<E> eventClass) {
            return super.subscribers().values().stream()
                .filter(s -> s instanceof EventSubscription && ((EventSubscription<?>) s).eventClass().isAssignableFrom(eventClass))
                .map(s -> (EventSubscription<E>) s)
                .collect(Collectors.toSet());
        }
    }

    @Log(topic = "sChat:EventBus")
    static final class Logging extends EventBusImpl {

        @Override
        public <E extends SChatEvent> E post(@NonNull E event) {
            log.info("POST: " + event);
            return super.post(event);
        }

        @Override
        public @NonNull <T extends SChatEvent> EventSubscription<T> on(@NonNull Class<T> eventClass, @NonNull Consumer<? super T> handler) {
            final EventSubscription<T> subscription = super.on(eventClass, handler);
            log.info("Subscribed " + handler.getClass().getName() + " to " + eventClass.getSimpleName());
            return subscription;
        }

        @Override
        protected @NotNull <T extends SChatEvent> EventSubscriptionImpl<T> createSubscription(Class<T> eventClass, Consumer<? super T> handler) {
            return new EventSubscriptionImpl.Logging<>(this, eventClass, handler);
        }
    }
}
