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

import java.util.function.Consumer;
import net.silthus.schat.events.SChatEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a subscription to a {@link SChatEvent}.
 *
 * @param <T> the event class
 * @since 1.0.0
 */
public interface EventSubscription<T extends SChatEvent> extends AutoCloseable {

    /**
     * Gets the class this handler is listening to.
     *
     * @return the event class
     * @since 1.0.0
     */
    @NotNull Class<T> eventClass();

    /**
     * Returns true if this handler is active.
     *
     * @return true if this handler is still active
     * @since 1.0.0
     */
    boolean isActive();

    /**
     * Unregisters this handler from the event bus.
     *
     * @since 1.0.0
     */
    @Override
    void close();

    /**
     * Gets the event consumer responsible for handling the event.
     *
     * @return the event consumer
     * @since 1.0.0
     */
    @NotNull Consumer<? super T> handler();
}
