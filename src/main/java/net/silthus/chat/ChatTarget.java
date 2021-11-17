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
import net.silthus.chat.config.ChannelConfig;
import net.silthus.chat.conversations.Channel;
import net.silthus.chat.identities.Chatter;
import net.silthus.chat.identities.Console;
import net.silthus.chat.identities.NilChatIdentity;
import org.bukkit.OfflinePlayer;

import java.util.Collection;

public interface ChatTarget extends Identity {

    ChatTarget NIL = new NilChatIdentity();

    static Chatter player(OfflinePlayer player) {
        return Chatter.of(player);
    }

    static ChatTarget nil() {
        return NIL;
    }

    static Channel channel(String identifier) {
        return Channel.channel(identifier);
    }

    static Channel channel(String identifier, ChannelConfig config) {
        return Channel.channel(identifier, config);
    }

    static Console console() {
        return Console.console();
    }

    default Message sendMessage(String message) {
        return Message.message(message).to(this).send();
    }

    void sendMessage(Message message);

    boolean deleteMessage(Message message);

    Message getLastReceivedMessage();

    Collection<Message> getReceivedMessages();

    Collection<Conversation> getConversations();

    void clearConversations();

    void subscribe(@NonNull Conversation conversation);

    void unsubscribe(@NonNull Conversation conversation);

    Collection<Message> getUnreadMessages(Conversation conversation);

    void setActiveConversation(Conversation conversation);
}
