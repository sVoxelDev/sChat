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

import net.silthus.chat.config.ChannelConfig;
import net.silthus.chat.targets.Channel;
import net.silthus.chat.targets.Chatter;
import net.silthus.chat.targets.Console;
import net.silthus.chat.targets.NilChatTarget;
import org.bukkit.entity.Player;

import java.util.Collection;

public interface ChatTarget {

    static Chatter player(Player player) {
        return Chatter.of(player);
    }

    static ChatTarget nil() {
        return new NilChatTarget();
    }

    static Channel channel(String identifier) {
        return Channel.channel(identifier);
    }

    static Channel channel(String identifier, ChannelConfig config) {
        return Channel.channel(identifier, config);
    }

    static Console console() {
        return Console.console();
    }

    default Message sendMessage(String message) {
        return Message.message(message).to(this).send();
    }

    String getIdentifier();

    void sendMessage(Message message);

    Message getLastReceivedMessage();

    Collection<Message> getReceivedMessages();

}
