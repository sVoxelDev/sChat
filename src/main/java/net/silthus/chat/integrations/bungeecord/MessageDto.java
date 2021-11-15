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

import lombok.Data;
import lombok.experimental.Accessors;
import net.silthus.chat.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Accessors(fluent = true)
class MessageDto {

    private UUID id;
    private String message;
    private IdentityDto sender;
    private ConversationDto conversation;
    private List<IdentityDto> targets = new ArrayList<>();

    public MessageDto(Message message) {
        this.id = message.getId();
        this.message = BungeeHelper.serialize(message.getText());
        sender(toIdentityDto(message.getSource()));
        conversation(toConversationDto(message.getConversation()));
        targets(message.getTargets().stream()
                .filter(target -> !(target instanceof BungeeCord))
                .map(this::toIdentityDto)
                .collect(Collectors.toList()));
    }

    private IdentityDto toIdentityDto(Identity identity) {
        if (identity == null) return null;
        return new IdentityDto(identity);
    }

    private ConversationDto toConversationDto(Conversation conversation) {
        if (conversation == null) return null;
        return new ConversationDto(conversation);
    }

    Message asMessage() {
        return Message.message()
                .id(id)
                .text(BungeeHelper.deserialize(message()))
                .from(sender != null ? sender.asChatIdentity() : ChatSource.nil())
                .to(conversation != null ? conversation.asConversation() : null)
                .to(targets.stream().map(identity -> (ChatTarget) identity.asChatIdentity())
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                .build();
    }
}
