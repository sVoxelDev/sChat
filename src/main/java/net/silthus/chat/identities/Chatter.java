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

package net.silthus.chat.identities;

import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.silthus.chat.*;
import net.silthus.chat.conversations.Channel;
import net.silthus.chat.conversations.DirectConversation;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Chatter extends AbstractChatTarget implements Listener, ChatSource, ChatTarget {

    private static final MessageRenderer RENDERER = MessageRenderer.TABBED;

    public static Chatter of(OfflinePlayer player) {
        return SChat.instance().getChatterManager().getOrCreateChatter(player);
    }

    Chatter(OfflinePlayer player) {
        super(player.getUniqueId(), player.getName());
        if (player.isOnline()) {
            setDisplayName(Objects.requireNonNull(player.getPlayer()).displayName());
        }
    }

    public Optional<Player> getPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(getUniqueId()));
    }

    public boolean canJoin(Channel channel) {
        return channel.canJoin(this);
    }

    public void join(Channel channel) throws AccessDeniedException {
        if (!canJoin(channel))
            throw new AccessDeniedException("You don't have permission to join the channel: " + channel.getName());
        setActiveConversation(channel);
    }

    @Override
    public void sendMessage(Message message) {
        if (getReceivedMessages().contains(message)) return;
        addReceivedMessage(message);

        getPlayer().ifPresentOrElse(
                player -> player.sendMessage(getIdentity(message), renderMessage(message), MessageType.CHAT),
                () -> SChat.instance().getBungeecord().sendGlobalChatMessage(message)
        );
    }

    @NotNull
    private Component renderMessage(Message message) {
        return appendMessageId(RENDERER.render(this, getMessagesToRender(message)));
    }

    @NotNull
    private List<Message> getMessagesToRender(Message message) {
        List<Message> messages = getConversationMessages();
        messages.addAll(getSystemMessages());
        messages.add(message);
        return distinctAndSorted(messages);
    }

    private Collection<Message> getSystemMessages() {
        if (getActiveConversation() instanceof DirectConversation) return new ArrayList<>();
        return getReceivedMessages().stream()
                .filter(msg -> msg.getType() == Message.Type.SYSTEM)
                .collect(Collectors.toList());
    }

    @NotNull
    private List<Message> getConversationMessages() {
        List<Message> messages = new ArrayList<>();
        if (getActiveConversation() != null) {
            messages.addAll(getActiveConversation().getReceivedMessages());
        }
        return messages;
    }

    @NotNull
    private List<Message> distinctAndSorted(List<Message> messages) {
        return messages.stream()
                .distinct()
                .sorted().collect(Collectors.toList());
    }

    private Identity getIdentity(Message message) {
        try {
            return message.getSource() != null ? Identity.identity(UUID.fromString(message.getSource().getName())) : Identity.nil();
        } catch (IllegalArgumentException e) {
            return Identity.nil();
        }
    }

    private Component appendMessageId(Component message) {
        return message.append(Component.storageNBT()
                .nbtPath(Constants.NBT_IS_SCHAT_MESSAGE.asString())
                .storage(Constants.NBT_IS_SCHAT_MESSAGE));
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
        return isNotSamePlayer(event) || noActiveConversation(event);
    }

    private boolean isNotSamePlayer(AsyncChatEvent event) {
        return getPlayer().map(player -> !event.getPlayer().equals(player))
                .orElse(true);
    }

    private boolean noActiveConversation(AsyncChatEvent event) {
        if (getActiveConversation() != null) return false;
        event.getPlayer().sendMessage(Constants.Errors.NO_ACTIVE_CHANNEL);
        event.setCancelled(true);
        return true;
    }
}
