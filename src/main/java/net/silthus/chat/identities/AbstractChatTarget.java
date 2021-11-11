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

package net.silthus.chat.identities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import net.silthus.chat.ChatTarget;
import net.silthus.chat.Conversation;
import net.silthus.chat.Message;

import java.util.*;

@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public abstract class AbstractChatTarget extends AbstractIdentity implements ChatTarget {

    private final Stack<Message> receivedMessages = new Stack<>();
    private final Set<Conversation> conversations = new HashSet<>();
    @Getter
    private Conversation activeConversation;

    protected AbstractChatTarget(UUID id, String name) {
        super(id, name);
    }

    protected AbstractChatTarget(String name) {
        super(name);
    }

    @Override
    public Message getLastReceivedMessage() {
        if (receivedMessages.isEmpty()) return null;
        return receivedMessages.peek();
    }

    @Override
    public Collection<Message> getReceivedMessages() {
        return List.copyOf(receivedMessages);
    }

    protected void addReceivedMessage(Message lastMessage) {
        this.receivedMessages.push(lastMessage);
    }

    public void setActiveConversation(Conversation conversation) {
        this.activeConversation = conversation;
        if (conversation != null)
            subscribe(conversation);
    }

    public Collection<Conversation> getConversations() {
        return List.copyOf(conversations);
    }

    public void subscribe(@NonNull Conversation conversation) {
        conversation.addTarget(this);
        conversations.add(conversation);
    }

    public void unsubscribe(@NonNull Conversation conversation) {
        conversation.removeTarget(this);
        conversations.removeIf(existingConversation -> existingConversation.equals(conversation));
    }

}
