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

package net.silthus.schat.platform.messenger;

import net.silthus.schat.message.Messenger;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

/**
 * Encapsulates the sChat system which accepts incoming {@link PluginMessage}s
 * from implementations of {@link Messenger}.
 */
@NonExtendable
public interface IncomingMessageConsumer {

    /**
     * Consumes a message instance.
     *
     * <p>The boolean returned from this method indicates whether or not the
     * platform accepted the message. Some implementations which have multiple
     * distribution channels may wish to use this result to dispatch the same
     * message back to additional receivers.</p>
     *
     * <p>The implementation will usually return <code>false</code> if a message
     * with the same ping id has already been processed.</p>
     *
     * @param message the message
     * @return true if the message was accepted by the plugin
     */
    boolean consumeIncomingMessage(@NonNull PluginMessage message);

    /**
     * Consumes a message in an encoded string format.
     *
     * <p>This method will decode strings obtained by calling
     * {@link OutgoingMessage#asEncodedString()}. This means that basic
     * implementations can successfully implement {@link Messenger} without
     * providing their own serialisation.</p>
     *
     * <p>The boolean returned from this method indicates whether or not the
     * platform accepted the message. Some implementations which have multiple
     * distribution channels may wish to use this result to dispatch the same
     * message back to additional receivers.</p>
     *
     * <p>The implementation will usually return <code>false</code> if a message
     * with the same ping id has already been processed.</p>
     *
     * @param encodedString the encoded string
     * @return true if the message was accepted by the plugin
     */
    boolean consumeIncomingMessageAsString(@NonNull String encodedString);

}
