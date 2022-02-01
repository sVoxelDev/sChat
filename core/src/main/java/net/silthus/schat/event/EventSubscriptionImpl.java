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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;
import net.kyori.event.EventSubscriber;
import org.jetbrains.annotations.Nullable;

@Getter
@Log(topic = "sChat")
@Accessors(fluent = true)
final class EventSubscriptionImpl<E extends SChatEvent> implements EventSubscription<E>, EventSubscriber<E> {

    private final AbstractEventBus<?> eventBus;
    private final Class<E> eventClass;
    private final Consumer<? super E> handler;
    private final @Nullable Object plugin;
    private final AtomicBoolean active = new AtomicBoolean(true);

    EventSubscriptionImpl(final AbstractEventBus<?> eventBus,
                          final Class<E> eventClass,
                          final Consumer<? super E> consumer,
                          final @Nullable Object plugin) {
        this.eventBus = eventBus;
        this.eventClass = eventClass;
        this.handler = consumer;
        this.plugin = plugin;
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

    public @Nullable Object plugin() {
        return this.plugin;
    }
}
