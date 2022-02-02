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

package net.silthus.schat.events;

import java.util.concurrent.atomic.AtomicBoolean;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an event that can be cancelled.
 *
 * @since next
 */
public interface Cancellable {

    /**
     * Gets an {@link AtomicBoolean} holding the cancellation state of the event.
     *
     * @return the cancellation
     * @since next
     */
    @NotNull AtomicBoolean cancellationState();

    /**
     * Returns true if the event is currently cancelled.
     *
     * @return if the event is cancelled
     * @since next
     */
    default boolean isCancelled() {
        return cancellationState().get();
    }

    /**
     * Returns true if the event is not currently cancelled.
     *
     * @return if the event is not cancelled
     * @since next
     */
    default boolean isNotCancelled() {
        return !isCancelled();
    }

    /**
     * Sets the cancellation state of the event.
     *
     * @param cancelled the new state
     * @return the previous state
     * @since next
     */
    default boolean cancelled(boolean cancelled) {
        return cancellationState().getAndSet(cancelled);
    }

}
