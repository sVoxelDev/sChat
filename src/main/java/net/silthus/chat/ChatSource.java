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

import net.kyori.adventure.text.Component;
import net.silthus.chat.config.ChannelConfig;
import net.silthus.chat.conversations.Channel;
import net.silthus.chat.identities.Console;
import net.silthus.chat.identities.NamedChatSource;
import net.silthus.chat.identities.NilChatIdentity;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public interface ChatSource extends Identity {

    ChatSource NIL = new NilChatIdentity();

    static Chatter player(OfflinePlayer player) {
        return Chatter.player(player);
    }

    static ChatSource named(String name) {
        return new NamedChatSource(name);
    }

    static ChatSource named(String name, Component displayName) {
        return new NamedChatSource(name, displayName);
    }

    static ChatSource named(UUID id, String name, Component displayName) {
        return new NamedChatSource(id, name, displayName);
    }

    static Channel channel(String name) {
        return Channel.channel(name);
    }

    static Channel channel(String name, ChannelConfig config) {
        return Channel.channel(name, config);
    }

    static ChatSource nil() {
        return NIL;
    }

    static Console console() {
        return Console.console();
    }

    default Message.MessageBuilder message(String message) {
        return Message.message(this, message);
    }
}
