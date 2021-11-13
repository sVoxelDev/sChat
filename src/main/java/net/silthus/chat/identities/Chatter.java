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
import lombok.Setter;
import net.silthus.chat.*;
import net.silthus.chat.conversations.Channel;
import net.silthus.chat.renderer.View;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Objects;
import java.util.Optional;

@Getter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Chatter extends AbstractChatTarget implements Listener, ChatSource, ChatTarget {

    private static final MessageRenderer RENDERER = MessageRenderer.TABBED;

    public static Chatter of(OfflinePlayer player) {
        return SChat.instance().getChatterManager().getOrCreateChatter(player);
    }

    public static Chatter chatter(Identity identity) {
        return SChat.instance().getChatterManager().getOrCreateChatter(identity);
    }

    @Setter
    private View view = new View(this, RENDERER);

    Chatter(OfflinePlayer player) {
        super(player.getUniqueId(), player.getName());
        if (player.isOnline()) {
            setDisplayName(Objects.requireNonNull(player.getPlayer()).displayName());
        }
    }

    Chatter(Identity identity) {
        super(identity.getUniqueId(), identity.getName());
        setDisplayName(identity.getDisplayName());
    }

    public Optional<Player> getPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(getUniqueId()));
    }

    public void updateView() {
        getPlayer().ifPresent(view::sendTo);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    void onPlayerChat(AsyncChatEvent event) {
        if (isNotApplicable(event)) return;

        Message.message()
                .from(this)
                .text(event.message())
                .to(getActiveConversation())
                .send();
        event.setCancelled(true);
    }

    @Override
    protected void processMessage(Message message) {
        getPlayer().ifPresentOrElse(
                view::sendTo,
                () -> SChat.instance().getBungeecord().sendMessage(message)
        );
    }

    public void join(Channel channel) throws AccessDeniedException {
        if (!canJoin(channel))
            throw new AccessDeniedException("You don't have permission to join the channel: " + channel.getName());
        setActiveConversation(channel);
    }

    public boolean canJoin(Channel channel) {
        if (channel.getConfig().protect()) {
            Player player = Bukkit.getPlayer(getUniqueId());
            return player != null && player.hasPermission(channel.getPermission());
        }
        return true;
    }

    public boolean canAutoJoin(Channel channel) {
        if (!canJoin(channel)) return false;
        if (canJoin(channel) && channel.getConfig().autoJoin()) return true;
        Player player = Bukkit.getPlayer(getUniqueId());
        return player != null && player.hasPermission(channel.getAutoJoinPermission());
    }

    public boolean canLeave(Conversation conversation) {
        if (conversation instanceof Channel) {
            return ((Channel) conversation).getConfig().canLeave();
        }
        return true;
    }

    public boolean canSendMessage(Channel channel) {
        return canJoin(channel);
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
