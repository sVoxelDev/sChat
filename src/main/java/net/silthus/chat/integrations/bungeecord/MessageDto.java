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
import net.silthus.chat.NamedChatSource;
import net.silthus.chat.integrations.bungeecord.MessageDto.Sender.Type;
import net.silthus.chat.targets.Channel;
import net.silthus.chat.targets.Chatter;
import net.silthus.chat.targets.Console;
import org.bukkit.Bukkit;

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
                .identifier(source.getIdentifier())
                .name(serialize(source.getName()))
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

        private String identifier;
        private String name;
        private Type type = Type.NIL;

        ChatSource asSource() {
            return switch (type) {
                case PLAYER -> ChatSource.player(Bukkit.getPlayer(UUID.fromString(identifier)));
                case CHANNEL -> ChatSource.channel(identifier);
                case CONSOLE -> ChatSource.console();
                case NAMED -> ChatSource.named(identifier, deserialize(name));
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
