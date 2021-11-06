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
import net.silthus.chat.Message;
import net.silthus.chat.conversations.Channel;
import net.silthus.chat.identities.Chatter;
import net.silthus.chat.identities.Console;
import net.silthus.chat.identities.NamedChatSource;
import net.silthus.chat.integrations.bungeecord.MessageDto.Sender.Type;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
@Accessors(fluent = true)
public class MessageDto {

    private String message;
    private Sender sender;

    public MessageDto(Message message) {
        this.message = serialize(message.formatted());
        sender(message.getSource());
    }

    public MessageDto sender(ChatSource source) {
        this.sender = new Sender()
                .uniqueId(source.getUniqueId())
                .name(source.getName())
                .displayName(serialize(source.getDisplayName()))
                .type(Type.fromChatSource(source));
        return this;
    }

    public Message toMessage() {
        return Message.message()
                .text(deserialize(message()))
                .from(sender.asSource())
                .build();
    }

    @Data
    @Accessors(fluent = true)
    static class Sender {

        private UUID uniqueId;
        private String name;
        private String displayName;
        private Type type = Type.NIL;

        ChatSource asSource() {
            return switch (type) {
                case PLAYER -> {
                    Player player = Bukkit.getPlayer(uniqueId);
                    if (player == null)
                        yield ChatSource.named(uniqueId, name, deserialize(name));
                    yield ChatSource.player(player);
                }
                case CHANNEL -> ChatSource.channel(name);
                case CONSOLE -> ChatSource.console();
                case NAMED -> ChatSource.named(name, deserialize(name));
                default -> ChatSource.nil();
            };
        }

        enum Type {
            PLAYER,
            CHANNEL,
            CONSOLE,
            NAMED,
            NIL;

            static Type fromChatSource(ChatSource source) {
                if (source instanceof Chatter) {
                    return Type.PLAYER;
                } else if (source instanceof Channel) {
                    return Type.CHANNEL;
                } else if (source instanceof Console) {
                    return Type.CONSOLE;
                } else if (source instanceof NamedChatSource) {
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
