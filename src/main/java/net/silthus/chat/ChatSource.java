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
import net.silthus.chat.targets.Channel;
import net.silthus.chat.targets.Chatter;
import net.silthus.chat.targets.Console;
import org.bukkit.entity.Player;

public interface ChatSource extends Identity {

    static ChatSource player(Player player) {
        return Chatter.of(player);
    }

    static ChatSource named(String identifier) {
        return new NamedChatSource(identifier);
    }

    static ChatSource named(String identifier, Component displayName) {
        return new NamedChatSource(identifier, displayName);
    }

    static Channel channel(String identifier) {
        return Channel.channel(identifier);
    }

    static Channel channel(String identifier, ChannelConfig config) {
        return Channel.channel(identifier, config);
    }

    static ChatSource nil() {
        return new NilChatSource();
    }

    static Console console() {
        return Console.console();
    }

    default Message.MessageBuilder message(String message) {
        return Message.message(this, message);
    }
}
