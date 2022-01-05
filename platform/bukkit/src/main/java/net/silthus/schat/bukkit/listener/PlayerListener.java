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

package net.silthus.schat.bukkit.listener;

import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.SenderChatterLookup;
import net.silthus.schat.platform.listener.ConnectionListener;
import net.silthus.schat.sender.PlayerAdapter;
import net.silthus.schat.sender.Sender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public final class PlayerListener implements Listener {

    private final PlayerAdapter<CommandSender> playerAdapter;
    private final ConnectionListener connectionListener;
    private final SenderChatterLookup chatterLookup;

    public PlayerListener(PlayerAdapter<CommandSender> playerAdapter,
                          ConnectionListener connectionListener,
                          SenderChatterLookup chatterLookup) {
        this.playerAdapter = playerAdapter;
        this.connectionListener = connectionListener;
        this.chatterLookup = chatterLookup;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Sender sender = playerAdapter.adapt(event.getPlayer());
        connectionListener.join(sender);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        final Chatter chatter = getChatter(event.getPlayer());
        chatter.chat(event.getMessage());
        event.setCancelled(true);
    }

    private Chatter getChatter(Player player) {
        return chatterLookup.getChatter(playerAdapter.adapt(player));
    }
}
