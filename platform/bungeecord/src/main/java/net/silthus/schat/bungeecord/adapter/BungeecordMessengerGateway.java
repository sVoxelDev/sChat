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

package net.silthus.schat.bungeecord.adapter;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.extern.java.Log;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.silthus.schat.MessengerGateway;
import net.silthus.schat.bungeecord.BungeecordBootstrap;
import net.silthus.schat.platform.config.ConfigKeys;
import net.silthus.schat.platform.plugin.scheduler.SchedulerAdapter;

public class BungeecordMessengerGateway implements MessengerGateway, Listener {

    public static final String GATEWAY_TYPE = "pluginmessage";

    public static BungeecordMessengerGateway createBungeecordMessengerGateway(BungeecordBootstrap bootstrap) {
        if (bootstrap.plugin().config().get(ConfigKeys.DEBUG))
            return new Logging(bootstrap);
        else
            return new BungeecordMessengerGateway(bootstrap);
    }

    private final ProxyServer proxy;
    private final SchedulerAdapter scheduler;

    private BungeecordMessengerGateway(BungeecordBootstrap bootstrap) {
        this.proxy = bootstrap.proxy();
        this.scheduler = bootstrap.scheduler();
        this.proxy.registerChannel(CHANNEL);
        this.proxy.getPluginManager().registerListener(bootstrap.loader(), this);
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void sendOutgoingMessage(String encodedMessage) {
        final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(encodedMessage);
        sendToAllServers(out.toByteArray());
    }

    private void sendToAllServers(byte[] bytes) {
        for (ServerInfo server : proxy.getServers().values()) {
            scheduler.async().execute(() -> sendToServer(bytes, server));
        }
    }

    protected void sendToServer(byte[] bytes, ServerInfo server) {
        server.sendData(CHANNEL, bytes, true);
    }

    @EventHandler
    public void onIncomingMessage(PluginMessageEvent event) {
        if (!event.getTag().equals(CHANNEL))
            return;

        event.setCancelled(true);
        if (event.getSender() instanceof ProxiedPlayer) // message from proxy -> server
            return;

        sendToAllServers(event.getData());
    }

    @Override
    public void close() {
        this.proxy.getPluginManager().unregisterListener(this);
        this.proxy.unregisterChannel(CHANNEL);
    }

    @Log(topic = "sChat:MessengerGateway")
    private static final class Logging extends BungeecordMessengerGateway {

        private Logging(BungeecordBootstrap bootstrap) {
            super(bootstrap);
        }

        @Override
        public void onIncomingMessage(PluginMessageEvent event) {
            log.info("Received Plugin Message on '" + event.getTag() + "': " + new String(event.getData()));
            super.onIncomingMessage(event);
        }

        @Override
        protected void sendToServer(byte[] bytes, ServerInfo server) {
            log.info("Forwarding Message to: " + server.getName());
            super.sendToServer(bytes, server);
        }
    }
}
