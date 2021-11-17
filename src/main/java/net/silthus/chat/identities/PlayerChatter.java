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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.kyori.adventure.audience.Audience;
import net.silthus.chat.Constants;
import net.silthus.chat.Identity;
import net.silthus.chat.Message;
import net.silthus.chat.SChat;
import net.silthus.chat.persistence.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Objects;
import java.util.Optional;

import static net.kyori.adventure.text.Component.text;

@Getter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class PlayerChatter extends AbstractChatter implements Listener {

    PlayerChatter(OfflinePlayer player) {
        super(player.getUniqueId(), player.getName());
        if (player.isOnline()) {
            setDisplayName(text(Objects.requireNonNull(player.getPlayer()).getDisplayName()));
        }
    }

    PlayerChatter(Identity identity) {
        super(identity.getUniqueId(), identity.getName());
        setDisplayName(identity.getDisplayName());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    void onPlayerChat(AsyncPlayerChatEvent event) {
        if (isNotApplicable(event)) return;

        Message.message()
                .from(this)
                .text(event.getMessage())
                .to(getActiveConversation())
                .send();
        event.setCancelled(true);
    }

    @Override
    public boolean hasPermission(String permission) {
        return getPlayer().map(player -> player.hasPermission(permission)).orElse(false);
    }

    @Override
    public void save() {
        PlayerData.save(this);
    }

    @Override
    public void load() {
        PlayerData.load(this);
    }

    @Override
    public Optional<Audience> getAudience() {
        return getPlayer().map(player -> SChat.instance().getAudiences().player(player));
    }

    public Optional<Player> getPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(getUniqueId()));
    }

    private boolean isNotApplicable(AsyncPlayerChatEvent event) {
        return isNotSamePlayer(event) || noActiveConversation(event);
    }

    private boolean isNotSamePlayer(AsyncPlayerChatEvent event) {
        return getPlayer().map(player -> !event.getPlayer().equals(player))
                .orElse(true);
    }

    private boolean noActiveConversation(AsyncPlayerChatEvent event) {
        if (getActiveConversation() != null) return false;
        event.getPlayer().sendMessage(Constants.Errors.NO_ACTIVE_CHANNEL);
        event.setCancelled(true);
        return true;
    }
}
