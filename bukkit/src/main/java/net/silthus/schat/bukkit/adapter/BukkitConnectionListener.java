/*
 * This file is part of sChat, licensed under the MIT License.
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.silthus.schat.bukkit.adapter;

import net.silthus.schat.bukkit.SChatBukkitServer;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.platform.chatter.ConnectionListener;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public final class BukkitConnectionListener extends ConnectionListener implements Listener {

    private final Plugin plugin;
    private final BukkitSenderFactory senderFactory;

    public BukkitConnectionListener(SChatBukkitServer server) {
        super(server.chatterRepository(), server.chatterFactory(), server.messenger(), server.eventBus());
        this.plugin = server.bootstrap().loader();
        this.senderFactory = server.senderFactory();
        Bukkit.getServer().getPluginManager().registerEvents(this, server.bootstrap().loader());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        onJoin(senderFactory.wrap(event.getPlayer()));
    }

    @Override
    protected void sendGlobalJoinPing(final Chatter chatter) {
        // wait until connection is established for sending outgoing plugin messages
        Bukkit.getScheduler().runTaskLater(plugin, () -> super.sendGlobalJoinPing(chatter), 1L);
    }
}
