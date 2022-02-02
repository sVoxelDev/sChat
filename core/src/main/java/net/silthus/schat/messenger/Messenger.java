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

package net.silthus.schat.messenger;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.ApiStatus.OverrideOnly;

/**
 * Represents an object which dispatches {@link PluginMessage}s.
 */
@OverrideOnly
public interface Messenger extends AutoCloseable {

    /**
     * Performs the necessary action to dispatch the message using the means
     * of the messenger.
     *
     * <p>This call should always be made async.</p>
     *
     * @param pluginMessage the outgoing message
     */
    void sendPluginMessage(@NonNull PluginMessage pluginMessage);

    /**
     * Performs the necessary action to gracefully shut down the messenger.
     */
    @Override
    default void close() {

    }
}
