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

package net.silthus.chat.integrations.bungeecord;

import net.silthus.chat.ChatTarget;
import net.silthus.chat.Conversation;
import net.silthus.chat.SChat;
import net.silthus.chat.config.ChannelConfig;
import net.silthus.chat.conversations.Channel;
import net.silthus.chat.conversations.PrivateConversation;
import net.silthus.chat.identities.PlayerChatter;

import java.util.List;

class ConversationDto extends IdentityDto {

    private final Type conversationType;
    private final ChannelConfig config;
    private final List<IdentityDto> targets;

    ConversationDto(Conversation conversation) {
        super(conversation);
        this.conversationType = Type.fromConversation(conversation);
        this.config = conversation instanceof Channel ? ((Channel) conversation).getConfig() : null;
        this.targets = conversation.getTargets().stream().filter(target -> target instanceof PlayerChatter).map(IdentityDto::new).toList();
    }

    Conversation asConversation() {
        final List<ChatTarget> targets = this.targets.stream().map(identityDto -> (ChatTarget) identityDto.asChatIdentity()).toList();
        return switch (conversationType) {
            case DIRECT -> Conversation.privateConversation(uniqueId(), name(), displayName(), targets);
            case CHANNEL -> Channel.channel(name(), config);
            case UNKNOWN -> SChat.instance().getConversationManager().getConversation(uniqueId());
        };
    }

    private enum Type {
        DIRECT(PrivateConversation.class),
        CHANNEL(Channel.class),
        UNKNOWN(Conversation.class);

        private final Class<? extends Conversation> conversationClass;

        Type(Class<? extends Conversation> conversationClass) {
            this.conversationClass = conversationClass;
        }

        static Type fromConversation(Conversation conversation) {
            for (Type value : values()) {
                if (value.conversationClass.isAssignableFrom(conversation.getClass()))
                    return value;
            }
            return Type.UNKNOWN;
        }
    }
}
