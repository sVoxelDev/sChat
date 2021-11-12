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

package net.silthus.chat.renderer;

import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;
import net.silthus.chat.Conversation;
import net.silthus.chat.Message;
import net.silthus.chat.identities.Chatter;

import java.util.Collection;
import java.util.List;

@Value
@Accessors(fluent = true)
public class View {

    Chatter chatter;
    List<Message> messages;
    List<Conversation> conversations;
    Conversation activeConversation;

    public View(@NonNull Chatter chatter, Message... messages) {
        this(chatter, List.of(messages));
    }

    public View(@NonNull Chatter chatter, @NonNull Collection<Message> messages) {
        this.chatter = chatter;
        this.messages = messages.stream().sorted().distinct().toList();
        this.conversations = chatter.getConversations().stream().sorted().toList();
        this.activeConversation = chatter.getActiveConversation();
    }
}