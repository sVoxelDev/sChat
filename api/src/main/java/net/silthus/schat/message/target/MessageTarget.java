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

package net.silthus.schat.message.target;

import net.silthus.schat.message.Message;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * The message target can receive and process messages.
 *
 * <p>A target can be anything from a player, to the console or a web application.</p>
 *
 * <p>The target implementation is responsible for processing the messages that are sent to it.</p>
 *
 * @since next
 */
public interface MessageTarget {

    /**
     * Sends a message to this target.
     *
     * <p>Override this method to add custom logic for processing messages.</p>
     *
     * @param message the message to send
     * @since next
     */
    @ApiStatus.Internal
    @ApiStatus.OverrideOnly
    void sendMessage(@NotNull Message message);
}
