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
import net.kyori.adventure.text.Component;
import net.silthus.chat.conversations.Channel;
import net.silthus.chat.conversations.PrivateConversation;

import java.util.Collection;
import java.util.UUID;

public interface Conversation extends ChatTarget, Comparable<Conversation> {

    static Conversation privateConversation(Chatter... targets) {
        return SChat.instance().getConversationManager().getOrCreatePrivateConversation(targets);
    }

    static Conversation privateConversation(UUID id, String name, Component displayName, Collection<ChatTarget> targets) {
        return SChat.instance().getConversationManager().getOrCreatePrivateConversation(id, name, displayName, targets.toArray(new ChatTarget[0]));
    }

    Format getFormat();

    void setFormat(Format format);

    Collection<ChatTarget> getTargets();

    void addTarget(@NonNull ChatTarget target);

    void removeTarget(@NonNull ChatTarget target);

    default Type getType() {
        return Type.fromConversation(this);
    }

    void close();

    enum Type {
        CHANNEL,
        DIRECT,
        OTHER;

        private static Type fromConversation(Conversation conversation) {
            if (conversation instanceof Channel) {
                return CHANNEL;
            } else if (conversation instanceof PrivateConversation) {
                return DIRECT;
            } else {
                return OTHER;
            }
        }
    }
}
