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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;
import net.kyori.event.EventSubscriber;
import net.silthus.schat.events.SChatEvent;
import org.jetbrains.annotations.Nullable;

@Getter
@Log(topic = "sChat:EventBus")
@Accessors(fluent = true)
class EventSubscriptionImpl<E extends SChatEvent> implements EventSubscription<E>, EventSubscriber<E> {

    private final EventBusImpl eventBus;
    private final Class<E> eventClass;
    private final Consumer<? super E> handler;
    private final @Nullable Object owner;
    private final AtomicBoolean active = new AtomicBoolean(true);

    EventSubscriptionImpl(final EventBusImpl eventBus,
                          final Class<E> eventClass,
                          final Consumer<? super E> consumer,
                          final @Nullable Object owner) {
        this.eventBus = eventBus;
        this.eventClass = eventClass;
        this.handler = consumer;
        this.owner = owner;
    }

    @Override
    public boolean isActive() {
        return this.active.get();
    }

    @Override
    public void close() {
        if (isUnregistered()) {
            return;
        }

        this.eventBus.unregisterHandler(this);
    }

    private boolean isUnregistered() {
        return !this.active.getAndSet(false);
    }

    @Override
    public void invoke(final @NonNull E event) {
        try {
            this.handler.accept(event);
        } catch (final Throwable t) {
            log.warning("Unable to pass event " + event.getClass().getSimpleName() + " to handler " + this.handler.getClass().getName());
            t.printStackTrace();
        }
    }

    @Log(topic = "sChat:EventBus")
    static final class Logging<E extends SChatEvent> extends EventSubscriptionImpl<E> {

        Logging(EventBusImpl eventBus, Class<E> eventClass, Consumer<? super E> consumer, @Nullable Object owner) {
            super(eventBus, eventClass, consumer, owner);
        }

        @Override
        public void invoke(@NonNull E event) {
            log.info(event + " --> " + this.handler().getClass().getName());
            super.invoke(event);
        }
    }
}
