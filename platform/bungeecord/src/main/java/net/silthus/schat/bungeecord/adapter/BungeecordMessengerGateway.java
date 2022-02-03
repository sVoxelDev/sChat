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

package net.silthus.schat.bungeecord.adapter;

import java.nio.charset.StandardCharsets;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.silthus.schat.bungeecord.BungeecordBootstrap;
import net.silthus.schat.messaging.MessengerGateway;
import net.silthus.schat.platform.plugin.scheduler.SchedulerAdapter;

public final class BungeecordMessengerGateway implements MessengerGateway, Listener {

    public static final String GATEWAY_TYPE = "pluginmessage";
    public static final String CHANNEL = "schat:update";

    private final ProxyServer proxy;
    private final SchedulerAdapter scheduler;

    public BungeecordMessengerGateway(BungeecordBootstrap bootstrap) {
        this.proxy = bootstrap.getProxy();
        this.scheduler = bootstrap.getScheduler();
        this.proxy.getPluginManager().registerListener(bootstrap.getLoader(), this);
    }

    @Override
    public void sendOutgoingMessage(String encodedMessage) {
        sendToAllServers(encodedMessage.getBytes(StandardCharsets.UTF_8));
    }

    private void sendToAllServers(byte[] bytes) {
        for (ServerInfo server : proxy.getServers().values()) {
            scheduler.async().execute(() -> server.sendData(CHANNEL, bytes));
        }
    }

    @EventHandler
    public void onIncomingMessage(PluginMessageEvent event) {
        if (!event.getTag().equals(CHANNEL))
            return;
        sendToAllServers(event.getData());
    }

    @Override
    public void close() {
        this.proxy.getPluginManager().unregisterListener(this);
    }
}
