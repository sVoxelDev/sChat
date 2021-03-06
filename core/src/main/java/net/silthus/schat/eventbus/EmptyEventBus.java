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

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import lombok.NonNull;
import net.silthus.schat.events.SChatEvent;
import org.jetbrains.annotations.Unmodifiable;

final class EmptyEventBus implements EventBus {

    @Override
    public <E extends SChatEvent> E post(@NonNull E event) {
        return event;
    }

    @Override
    public @NonNull <E extends SChatEvent> EventSubscription<E> on(@NonNull Class<E> eventClass, @NonNull Consumer<? super E> handler) {
        return new EmptySubscription<>(eventClass, handler);
    }

    @Override
    public @NonNull @Unmodifiable Set<EventSubscription<?>> register(Object listener) {
        return new HashSet<>();
    }

    @Override
    public @NonNull @Unmodifiable <E extends SChatEvent> Set<EventSubscription<E>> subscriptions(@NonNull Class<E> eventClass) {
        return Set.of();
    }

    @Override
    public void close() {

    }

    private record EmptySubscription<E extends SChatEvent>(Class<E> eventClass, Consumer<? super E> handler) implements EventSubscription<E> {

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public void close() {

        }
    }
}
