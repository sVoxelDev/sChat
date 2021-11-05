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

package net.silthus.chat.targets;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.silthus.chat.ChatTarget;
import net.silthus.chat.Message;

import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;

@Data
@EqualsAndHashCode(of = {"identifier"})
public abstract class AbstractChatTarget implements ChatTarget {

    private final String identifier;
    private final Queue<Message> receivedMessages = new LinkedTransferQueue<>();

    protected AbstractChatTarget(String identifier) {
        this.identifier = identifier.toLowerCase();
    }

    @Override
    public Message getLastReceivedMessage() {
        return receivedMessages.peek();
    }

    @Override
    public Collection<Message> getReceivedMessages() {
        return List.copyOf(receivedMessages);
    }

    protected void addReceivedMessage(Message lastMessage) {
        this.receivedMessages.add(lastMessage);
    }

    public String getIdentifier() {
        return this.identifier;
    }
}
