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

package net.silthus.chat.targets;

import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.silthus.chat.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.UUID;

@Data
@EqualsAndHashCode(of = "player", callSuper = false)
public class Chatter extends AbstractChatTarget implements Listener, ChatSource, ChatTarget {

    private final Player player;

    public Chatter(Player player) {
        super(player.getUniqueId().toString());
        this.player = player;
    }

    public static Chatter of(Player player) {
        return SChat.instance().getChatterManager().registerChatter(player);
    }

    public UUID getUniqueId() {
        return getPlayer().getUniqueId();
    }

    @Override
    public String getIdentifier() {
        return getUniqueId().toString();
    }

    @Override
    public Component getName() {
        return getPlayer().displayName();
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    public boolean canJoin(Channel channel) {
        return channel.canJoin(this);
    }

    public void join(Channel channel) throws AccessDeniedException {
        if (!canJoin(channel))
            throw new AccessDeniedException("You don't have permission to join the channel: " + channel.getIdentifier());
        setActiveConversation(channel);
    }

    @Override
    public void sendMessage(Message message) {
        SChat.instance().getChatPacketQueue().queueMessage(message);
        getPlayer().sendMessage(getIdentity(message), appendMessageId(message), MessageType.CHAT);
        addReceivedMessage(message);
    }

    private Identity getIdentity(Message message) {
        try {
            return message.getSource() != null ? Identity.identity(UUID.fromString(message.getSource().getIdentifier())) : Identity.nil();
        } catch (IllegalArgumentException e) {
            return Identity.nil();
        }
    }

    private TextComponent appendMessageId(Message message) {
        return Component.text()
                .append(message.formatted())
                .append(Component.storageNBT()
                        .nbtPath(message.getId().toString())
                        .storage(Constants.NBT_MESSAGE_ID))
                .build();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncChatEvent event) {
        if (isNotApplicable(event)) return;

        Message.message()
                .from(this)
                .text(event.message())
                .to(getActiveConversation())
                .send();
        event.setCancelled(true);
    }

    private boolean isNotApplicable(AsyncChatEvent event) {
        return isNotSamePlayer(event) || noActiveChannel(event);
    }

    private boolean isNotSamePlayer(AsyncChatEvent event) {
        return !event.getPlayer().equals(getPlayer());
    }

    private boolean noActiveChannel(AsyncChatEvent event) {
        if (getActiveConversation() != null) return false;
        event.getPlayer().sendMessage(Constants.Errors.NO_ACTIVE_CHANNEL);
        event.setCancelled(true);
        return true;
    }
}
