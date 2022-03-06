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
package net.silthus.schat.events;

import java.util.concurrent.atomic.AtomicBoolean;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an event that can be cancelled.
 *
 * @since 1.0.0
 */
public interface Cancellable {

    /**
     * Gets an {@link AtomicBoolean} holding the cancellation state of the event.
     *
     * @return the cancellation
     * @since 1.0.0
     */
    @NotNull AtomicBoolean cancellationState();

    /**
     * Returns true if the event is currently cancelled.
     *
     * @return if the event is cancelled
     * @since 1.0.0
     */
    default boolean isCancelled() {
        return cancellationState().get();
    }

    /**
     * Returns true if the event is not currently cancelled.
     *
     * @return if the event is not cancelled
     * @since 1.0.0
     */
    default boolean isNotCancelled() {
        return !isCancelled();
    }

    /**
     * Sets the cancellation state of the event.
     *
     * @param cancelled the new state
     * @return the previous state
     * @since 1.0.0
     */
    default boolean cancelled(boolean cancelled) {
        return cancellationState().getAndSet(cancelled);
    }

}
