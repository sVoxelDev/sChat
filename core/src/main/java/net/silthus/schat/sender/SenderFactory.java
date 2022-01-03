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

package net.silthus.schat.sender;

import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Factory class to make a thread-safe sender instance.
 *
 * @param <T> the command sender type
 */
public abstract class SenderFactory<T> implements AutoCloseable, PlayerOnlineChecker, PlayerAdapter<T> {

    protected abstract Class<T> getSenderType();

    protected abstract UUID getUniqueId(T sender);

    protected abstract String getName(T sender);

    protected abstract Component getDisplayName(T sender);

    protected abstract void sendMessage(T sender, Component message);

    protected abstract boolean hasPermission(T sender, String node);

    protected abstract void performCommand(T sender, String command);

    protected abstract boolean isConsole(T sender);

    @Override
    public final Sender adapt(@NonNull T sender) {
        if (!getSenderType().isInstance(sender))
            throw new IllegalSenderType();
        return new FactorySender<>(this, sender);
    }

    @SuppressWarnings("unchecked")
    public final T unwrap(@NonNull Sender sender) {
        if (sender instanceof FactorySender<?> factorySender)
            return (T) factorySender.getHandle();
        throw new IllegalSenderType();
    }

    @Override
    public void close() {

    }

    public static final class IllegalSenderType extends RuntimeException {
    }
}
