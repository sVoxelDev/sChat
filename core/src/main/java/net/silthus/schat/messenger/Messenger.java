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

package net.silthus.schat.messenger;

import java.lang.reflect.Type;
import lombok.NonNull;
import org.jetbrains.annotations.ApiStatus.OverrideOnly;

/**
 * Represents an object which dispatches {@link PluginMessage}s.
 */
@OverrideOnly
public interface Messenger extends AutoCloseable {

    static Messenger empty() {
        return new EmptyMessenger();
    }

    /**
     * Registers the given plugin message type for sending messages.
     *
     * <p>Every {@link PluginMessage} that is sent must be registered
     * or else an {@link UnsupportedMessageException} will be thrown by {@link #sendPluginMessage(PluginMessage)}.</p>
     *
     * @param type the type
     */
    void registerMessageType(Type type);

    void registerTypeAdapter(Type type, Object adapter);

    /**
     * Performs the necessary action to dispatch the message using the means
     * of the messenger.
     *
     * <p>The underlying dispatch should always be made async.</p>
     *
     * @param pluginMessage the outgoing message
     */
    void sendPluginMessage(@NonNull PluginMessage pluginMessage) throws UnsupportedMessageException;

    /**
     * Performs the necessary action to gracefully shut down the messenger.
     */
    @Override
    default void close() {

    }

    class UnsupportedMessageException extends RuntimeException {
    }
}
