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

import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a subscription to a {@link SChatEvent}.
 *
 * @param <T> the event class
 * @since next
 */
public interface EventSubscription<T extends SChatEvent> extends AutoCloseable {

    /**
     * Gets the class this handler is listening to.
     *
     * @return the event class
     * @since next
     */
    @NotNull Class<T> eventClass();

    /**
     * Returns true if this handler is active.
     *
     * @return true if this handler is still active
     * @since next
     */
    boolean isActive();

    /**
     * Unregisters this handler from the event bus.
     *
     * @since next
     */
    @Override
    void close();

    /**
     * Gets the event consumer responsible for handling the event.
     *
     * @return the event consumer
     * @since next
     */
    @NotNull Consumer<? super T> handler();
}
