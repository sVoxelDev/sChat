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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.silthus.chat.ChatSource;
import net.silthus.chat.ChatTarget;
import net.silthus.chat.Conversation;
import net.silthus.chat.Message;
import net.silthus.chat.identities.Chatter;
import net.silthus.chat.identities.Console;
import net.silthus.chat.identities.NamedChatSource;
import net.silthus.chat.integrations.bungeecord.MessageDto.Identity.Type;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Accessors(fluent = true)
public class MessageDto {

    private UUID id;
    private String message;
    private Identity sender;
    private Identity conversation;
    private List<Identity> targets = new ArrayList<>();

    public MessageDto(Message message) {
        this.id = message.getId();
        this.message = serialize(message.formatted());
        sender(toIdentityDto(message.getSource()));
        conversation(toIdentityDto(message.getConversation()));
        targets(message.getTargets().stream().map(this::toIdentityDto).collect(Collectors.toList()));
    }

    private Identity toIdentityDto(net.silthus.chat.Identity identity) {
        if (identity == null) return null;
        return new Identity()
                .uniqueId(identity.getUniqueId())
                .name(identity.getName())
                .displayName(serialize(identity.getDisplayName()))
                .type(Type.fromChatIdentity(identity));
    }

    public Message toMessage() {
        return Message.message()
                .id(id)
                .text(deserialize(message()))
                .from(sender != null ? sender.asChatIdentity() : ChatSource.nil())
                .conversation(conversation != null ? conversation.asChatIdentity() : null)
                .to(targets.stream().map(identity -> (ChatTarget) identity.asChatIdentity()).collect(Collectors.toList()))
                .build();
    }

    @Data
    @Accessors(fluent = true)
    static class Identity {

        private UUID uniqueId = UUID.randomUUID();
        private String name = "";
        private String displayName = "";
        private Type type = Type.NIL;

        @SuppressWarnings("unchecked")
        <T extends net.silthus.chat.Identity> T asChatIdentity() {
            return (T) switch (type) {
                case PLAYER -> {
                    Player player = Bukkit.getPlayer(uniqueId);
                    if (player == null)
                        yield ChatSource.named(uniqueId, name, deserialize(name));
                    yield ChatSource.player(player);
                }
                case CONVERSATION -> ChatSource.channel(name);
                case CONSOLE -> ChatSource.console();
                case NAMED -> ChatSource.named(uniqueId, name, deserialize(name));
                default -> ChatSource.nil();
            };
        }

        enum Type {
            PLAYER,
            CONVERSATION,
            CONSOLE,
            NAMED,
            NIL;

            static Type fromChatIdentity(net.silthus.chat.Identity identity) {
                if (identity instanceof Chatter) {
                    return Type.PLAYER;
                } else if (identity instanceof Conversation) {
                    return Type.CONVERSATION;
                } else if (identity instanceof Console) {
                    return Type.CONSOLE;
                } else if (identity instanceof NamedChatSource) {
                    return Type.NAMED;
                }
                return Type.NIL;
            }

        }
    }

    private static String serialize(Component component) {
        return GsonComponentSerializer.gson().serialize(component);
    }

    private static Component deserialize(String message) {
        return GsonComponentSerializer.gson().deserialize(message);
    }
}
