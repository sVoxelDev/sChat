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

public class DefaultMessenger<T> implements Messenger<T> {

    private final Strategy<T> strategy;
    private final Messages messages = new Messages();

    protected DefaultMessenger(Strategy<T> strategy) {
        this.strategy = strategy;
    }

    @Override
    public final @NotNull @Unmodifiable Messages getMessages() {
        return messages.filter(Message.NOT_DELETED);
    }

    @Override
    public void sendMessage(@NonNull T target, @NonNull Message message) {
        addMessage(message);
        deliver(target, message);
    }

    protected final void addMessage(Message message) {
        this.messages.add(message);
    }

    protected final void deliver(T target, Message message) {
        strategy.deliver(message, new Context<>(target, getMessages()));
    }
}
