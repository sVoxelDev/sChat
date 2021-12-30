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

package net.silthus.schat.message.messenger;

import lombok.NonNull;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.Messages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public interface Messenger<T> {

    static <T> Messenger<T> noDelivery() {
        return new DefaultMessenger<>((message, context) -> {});
    }

    static <T> Messenger<T> messenger(@NonNull Strategy<T> strategy) {
        return new DefaultMessenger<>(strategy);
    }

    @NotNull @Unmodifiable Messages getMessages();

    void sendMessage(@NonNull T target, @NonNull Message message);

    @FunctionalInterface
    interface Strategy<T> {

        void deliver(final @NotNull Message message, final @NotNull Context<T> context);
    }

    record Context<T>(@NonNull T target, @NonNull Messages messages) {
    }
}
