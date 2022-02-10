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

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.extern.java.Log;
import net.silthus.schat.messenger.IncomingMessageConsumer;
import net.silthus.schat.messenger.MessengerGateway;
import net.silthus.schat.platform.config.ConfigKeys;
import net.silthus.schat.platform.config.SChatConfig;
import net.silthus.schat.platform.plugin.scheduler.SchedulerAdapter;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public class BukkitMessengerGateway implements MessengerGateway, PluginMessageListener {

    public static final String GATEWAY_TYPE = "pluginmessage";

    public static BukkitMessengerGateway createBukkitMessengerGateway(Plugin plugin,
                                                                      Server server,
                                                                      SchedulerAdapter scheduler,
                                                                      IncomingMessageConsumer consumer,
                                                                      SChatConfig config) {
        if (config.get(ConfigKeys.DEBUG))
            return new Logging(plugin, server, scheduler, consumer);
        else
            return new BukkitMessengerGateway(plugin, server, scheduler, consumer);
    }

    private final Plugin plugin;
    private final Server server;
    private final SchedulerAdapter scheduler;
    private final IncomingMessageConsumer consumer;

    private BukkitMessengerGateway(Plugin plugin, Server server, SchedulerAdapter scheduler, IncomingMessageConsumer consumer) {
        this.plugin = plugin;
        this.server = server;
        this.scheduler = scheduler;
        this.consumer = consumer;
        server.getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL);
        server.getMessenger().registerIncomingPluginChannel(plugin, CHANNEL, this);
    }

    @Override
    public void sendOutgoingMessage(String encodedMessage) {
        scheduler.executeAsync(() -> dispatchMessage(encodedMessage));
    }

    @SuppressWarnings("UnstableApiUsage")
    protected void dispatchMessage(String encodedMessage) {
        final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(encodedMessage);
        server.sendPluginMessage(plugin, CHANNEL, out.toByteArray());
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] message) {
        if (channel.equalsIgnoreCase(CHANNEL))
            consumer.consumeIncomingMessageAsString(ByteStreams.newDataInput(message).readUTF());
    }

    @Override
    public void close() {
        server.getMessenger().unregisterOutgoingPluginChannel(plugin, CHANNEL);
        server.getMessenger().unregisterIncomingPluginChannel(plugin, CHANNEL);
    }

    @Log(topic = "sChat:MessengerGateway")
    private static final class Logging extends BukkitMessengerGateway {

        private Logging(Plugin plugin, Server server, SchedulerAdapter scheduler, IncomingMessageConsumer consumer) {
            super(plugin, server, scheduler, consumer);
        }

        @Override
        public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] message) {
            log.info("Received Plugin Message on '" + channel + "': " + new String(message));
            super.onPluginMessageReceived(channel, player, message);
        }

        @Override
        protected void dispatchMessage(String encodedMessage) {
            log.info("Sending Outgoing Message over " + CHANNEL + ": " + encodedMessage);
            super.dispatchMessage(encodedMessage);
        }
    }
}
