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
import net.silthus.chat.conversations.Channel;
import net.silthus.chat.identities.Chatter;
import net.silthus.chat.identities.Console;
import net.silthus.chat.identities.NamedChatSource;
import net.silthus.chat.identities.NilChatIdentity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
@Accessors(fluent = true)
class IdentityDto {

    private final UUID uniqueId;
    private final String name;
    private final String displayName;
    private final Type type;

    IdentityDto(Identity identity) {
        this.uniqueId = identity.getUniqueId();
        this.name = identity.getName();
        this.displayName = BungeeHelper.serialize(identity.getDisplayName());
        this.type = Type.fromChatIdentity(identity);
    }

    @SuppressWarnings("unchecked")
    <T extends Identity> T asChatIdentity() {
        return (T) switch (type) {
            case CHATTER -> {
                Player player = Bukkit.getPlayer(uniqueId);
                if (player == null)
                    yield Chatter.chatter(Identity.identity(uniqueId, name, BungeeHelper.deserialize(name)));
                yield Chatter.of(player);
            }
            case CHANNEL -> ChatTarget.channel(name);
            case CONVERSATION -> SChat.instance().getConversationManager().getConversation(uniqueId);
            case CONSOLE -> ChatTarget.console();
            case NAMED -> ChatSource.named(uniqueId, name, BungeeHelper.deserialize(name));
            default -> ChatTarget.nil();
        };
    }

    enum Type {
        CHATTER(Chatter.class),
        CHANNEL(Channel.class),
        CONSOLE(Console.class),
        NAMED(NamedChatSource.class),
        NIL(NilChatIdentity.class),
        CONVERSATION(Conversation.class);

        private final Class<? extends Identity> identityClass;

        Type(Class<? extends Identity> identityClass) {
            this.identityClass = identityClass;
        }

        static Type fromChatIdentity(Identity identity) {
            for (Type value : values()) {
                if (value.identityClass.isAssignableFrom(identity.getClass()))
                    return value;
            }
            return Type.NIL;
        }

    }
}
