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
package net.silthus.schat.velocity.adapter;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.java.Log;
import net.silthus.schat.messenger.MessengerGateway;
import net.silthus.schat.platform.config.ConfigKeys;
import net.silthus.schat.platform.plugin.scheduler.SchedulerAdapter;
import net.silthus.schat.velocity.VelocityBootstrap;

import static com.velocitypowered.api.event.connection.PluginMessageEvent.ForwardResult.forward;
import static com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier.create;

public class VelocityMessengerGateway implements MessengerGateway {

    public static final String GATEWAY_TYPE = "pluginmessage";
    public static final ChannelIdentifier CHANNEL = create("schat", "update");

    public static VelocityMessengerGateway createVelocityMessengerGateway(VelocityBootstrap bootstrap) {
        if (bootstrap.plugin().config().get(ConfigKeys.DEBUG))
            return new Logging(bootstrap);
        else
            return new VelocityMessengerGateway(bootstrap);
    }

    private final ProxyServer proxy;
    private final SchedulerAdapter scheduler;
    private final VelocityBootstrap bootstrap;
    private final Map<String, Queue<byte[]>> queuedPackets = new ConcurrentHashMap<>();

    private VelocityMessengerGateway(VelocityBootstrap bootstrap) {
        this.proxy = bootstrap.proxy();
        this.scheduler = bootstrap.scheduler();
        this.bootstrap = bootstrap;
        this.proxy.getChannelRegistrar().register(CHANNEL);
        this.proxy.getEventManager().register(bootstrap, this);
    }

    @Override
    public void sendOutgoingMessage(String encodedMessage) {
        sendToAllServers(encodedMessage.getBytes(StandardCharsets.UTF_8));
    }

    private void sendToAllServers(byte[] bytes) {
        for (RegisteredServer server : proxy.getAllServers()) {
            scheduler.async().execute(() -> sendToServer(server, bytes));
        }
    }

    protected boolean sendToServer(RegisteredServer server, byte[] bytes) {
        if (!server.sendPluginMessage(CHANNEL, bytes)) {
            queuedPackets.computeIfAbsent(server.getServerInfo().getName(), s -> new LinkedList<>()).add(bytes);
            return false;
        } else {
            return true;
        }
    }

    @Subscribe
    public void onIncomingMessage(PluginMessageEvent event) {
        if (!event.getIdentifier().equals(CHANNEL))
            return;
        sendToAllServers(event.getData());
        event.setResult(forward());
    }

    @Subscribe
    @SuppressWarnings("UnstableApiUsage")
    public void onConnect(ServerPostConnectEvent event) {
        event.getPlayer().getCurrentServer()
            .ifPresent(serverConnection -> scheduler.executeAsync(() -> flushMessageQueue(serverConnection)));
    }

    protected void flushMessageQueue(ServerConnection connection) {
        final Queue<byte[]> queue = queuedPackets.remove(connection.getServerInfo().getName());
        if (queue != null)
            while (!queue.isEmpty())
                sendToServer(connection.getServer(), queue.poll());
    }

    @Override
    public void close() {
        this.proxy.getEventManager().unregisterListener(bootstrap, this);
        this.proxy.getChannelRegistrar().unregister(CHANNEL);
    }

    @Log(topic = "sChat:MessengerGateway")
    private static final class Logging extends VelocityMessengerGateway {

        private Logging(VelocityBootstrap bootstrap) {
            super(bootstrap);
        }

        @Override
        public void onIncomingMessage(PluginMessageEvent event) {
            log.info("Received Plugin Message on '" + event.getIdentifier() + "': " + new String(event.getData()));
            super.onIncomingMessage(event);
        }

        @Override
        protected boolean sendToServer(RegisteredServer server, byte[] bytes) {
            log.info("Forwarding Message to: " + server.getServerInfo().getName());
            final boolean delivered = super.sendToServer(server, bytes);
            if (!delivered)
                log.info("---- QUEUED MESSAGE ----");
            return delivered;
        }

        @Override
        protected void flushMessageQueue(ServerConnection connection) {
            log.info("Player Connected to " + connection.getServerInfo().getName() + " - FLUSHING MESSAGE QUEUE");
            super.flushMessageQueue(connection);
        }
    }
}
