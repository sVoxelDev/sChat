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

package net.silthus.schat.platform.sender;

import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.silthus.schat.identity.Identity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

/**
 * Factory class to make a thread-safe sender instance.
 *
 * @param <T> the command sender type
 */
public abstract class SenderFactory<T> implements AutoCloseable {

    public static final UUID CONSOLE_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    public static final @NotNull Identity CONSOLE = Identity.identity(
        CONSOLE_ID,
        "Console"
    );

    protected abstract Class<T> senderType();

    protected abstract Identity identity(T sender);

    protected abstract void sendMessage(T sender, Component message);

    protected abstract boolean hasPermission(T sender, String node);

    protected abstract void performCommand(T sender, String command);

    protected abstract boolean isConsole(T sender);

    protected abstract boolean isPlayerOnline(UUID playerId);

    public final Sender wrap(@NonNull T sender) {
        if (!senderType().isInstance(sender))
            throw new IllegalSenderType();
        return new FactorySender<>(this, sender);
    }

    @SuppressWarnings("unchecked")
    public final T unwrap(@NonNull Sender sender) {
        if (sender instanceof FactorySender<?> factorySender)
            return (T) factorySender.handle();
        throw new IllegalSenderType();
    }

    @Override
    public void close() {

    }

    public static final class IllegalSenderType extends RuntimeException {
    }
}
