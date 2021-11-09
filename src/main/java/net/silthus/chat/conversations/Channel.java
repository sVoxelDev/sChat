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

package net.silthus.chat.conversations;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.silthus.chat.ChatSource;
import net.silthus.chat.Constants;
import net.silthus.chat.Message;
import net.silthus.chat.SChat;
import net.silthus.chat.config.ChannelConfig;
import net.silthus.chat.identities.Chatter;
import net.silthus.chat.identities.Console;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Getter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Channel extends AbstractConversation implements ChatSource {

    public static Channel channel(String identifier) {
        return SChat.instance().getChannelRegistry().getOrCreate(identifier);
    }

    public static Channel channel(String identifier, ChannelConfig config) {
        return SChat.instance().getChannelRegistry().getOrCreate(identifier, config);
    }

    private final ChannelConfig config;

    Channel(String identifier) {
        this(identifier, ChannelConfig.defaults());
    }

    Channel(String identifier, ChannelConfig config) {
        super(identifier);
        this.config = config;
        if (config.name() != null)
            setDisplayName(Component.text(config.name()));
        setFormat(config.format());
    }

    public String getPermission() {
        return Constants.Permissions.getChannelPermission(this);
    }

    public String getAutoJoinPermission() {
        return Constants.Permissions.getAutoJoinPermission(this);
    }

    public boolean canJoin(Chatter chatter) {
        if (getConfig().protect()) {
            Player player = Bukkit.getPlayer(chatter.getUniqueId());
            return player != null && player.hasPermission(getPermission());
        }
        return true;
    }

    public boolean canSendMessage(Chatter chatter) {
        return canJoin(chatter);
    }

    public boolean canAutoJoin(Chatter chatter) {
        if (!canJoin(chatter)) return false;
        if (canJoin(chatter) && getConfig().autoJoin()) return true;
        Player player = Bukkit.getPlayer(chatter.getUniqueId());
        return player != null && player.hasPermission(getAutoJoinPermission());
    }

    @Override
    public Message sendMessage(String message) {

        return Message.message(message).to(this).send();
    }

    @Override
    public void sendMessage(Message message) {
        if (getReceivedMessages().contains(message)) return;
        addReceivedMessage(message);

        getTargets().forEach(target -> target.sendMessage(message));
        if (getConfig().sendToConsole())
            Console.console().sendMessage(message);
        if (getConfig().global())
            SChat.instance().getBungeecord().sendGlobalChatMessage(message);
    }
}
