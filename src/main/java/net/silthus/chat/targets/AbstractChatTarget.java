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
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.chat.ChatTarget;
import net.silthus.chat.Conversation;
import net.silthus.chat.Message;

import java.util.*;
import java.util.concurrent.LinkedTransferQueue;

@Data
@EqualsAndHashCode(of = {"identifier"})
public abstract class AbstractChatTarget implements ChatTarget {

    private final String identifier;
    private final Queue<Message> receivedMessages = new LinkedTransferQueue<>();
    private final Set<Conversation> conversations = new HashSet<>();
    private Conversation activeConversation;
    private Component name;

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

    public void setActiveConversation(Conversation conversation) {
        this.activeConversation = conversation;
        if (conversation != null)
            subscribe(conversation);
    }

    public Conversation getActiveConversation() {
        return this.activeConversation;
    }

    public Collection<Conversation> getConversations() {
        return List.copyOf(conversations);
    }

    public void subscribe(@NonNull Conversation conversation) {
        conversation.subscribe(this);
        conversations.add(conversation);
    }

    public void unsubscribe(@NonNull Conversation conversation) {
        conversation.unsubscribe(this);
        conversations.removeIf(existingConversation -> existingConversation.equals(conversation));
    }

    public Component getName() {
        if (name != null)
            return name;
        return Component.text(getIdentifier());
    }
}
