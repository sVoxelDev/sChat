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
import net.silthus.chat.ChatSource;
import net.silthus.chat.ChatTarget;
import net.silthus.chat.Message;
import net.silthus.chat.integrations.bungeecord.IdentityDto.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Accessors(fluent = true)
public class MessageDto {

    private UUID id;
    private String message;
    private IdentityDto sender;
    private IdentityDto conversation;
    private List<IdentityDto> targets = new ArrayList<>();

    public MessageDto(Message message) {
        this.id = message.getId();
        this.message = BungeeHelper.serialize(message.getText());
        sender(toIdentityDto(message.getSource()));
        conversation(toIdentityDto(message.getConversation()));
        targets(message.getTargets().stream().map(this::toIdentityDto).collect(Collectors.toList()));
    }

    private IdentityDto toIdentityDto(net.silthus.chat.Identity identity) {
        if (identity == null) return null;
        return new IdentityDto()
                .uniqueId(identity.getUniqueId())
                .name(identity.getName())
                .displayName(BungeeHelper.serialize(identity.getDisplayName()))
                .type(Type.fromChatIdentity(identity));
    }

    public Message toMessage() {
        return Message.message()
                .id(id)
                .text(BungeeHelper.deserialize(message()))
                .from(sender != null ? sender.asChatIdentity() : ChatSource.nil())
                .conversation(conversation != null ? conversation.asChatIdentity() : null)
                .to(targets.stream().map(identity -> (ChatTarget) identity.asChatIdentity()).collect(Collectors.toList()))
                .build();
    }
}
