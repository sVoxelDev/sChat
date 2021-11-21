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

package net.silthus.chat;

import lombok.NonNull;
import net.silthus.chat.identities.Console;
import net.silthus.chat.identities.NilChatIdentity;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ChatTarget extends Identity {

    ChatTarget NIL = new NilChatIdentity();

    static ChatTarget nil() {
        return NIL;
    }

    static Console console() {
        return Console.console();
    }

    default Message sendMessage(String message) {
        return Message.message(message).to(this).send();
    }

    void sendMessage(Message message);

    boolean deleteMessage(Message message);

    Optional<Message> getMessage(UUID messageId);

    Message getLastReceivedMessage();

    Collection<Message> getReceivedMessages();

    Collection<Conversation> getConversations();

    void clearConversations();

    void subscribe(@NonNull Conversation conversation);

    void unsubscribe(@NonNull Conversation conversation);

    Collection<Message> getUnreadMessages(Conversation conversation);

    void setActiveConversation(Conversation conversation);

    Conversation getActiveConversation();
}
