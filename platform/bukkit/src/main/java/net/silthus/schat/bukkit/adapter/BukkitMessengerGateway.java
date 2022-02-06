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

import java.nio.charset.StandardCharsets;
import net.silthus.schat.IncomingMessageConsumer;
import net.silthus.schat.MessengerGateway;
import net.silthus.schat.platform.plugin.scheduler.SchedulerAdapter;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public class BukkitMessengerGateway implements MessengerGateway, PluginMessageListener {

    public static final String GATEWAY_TYPE = "pluginmessage";

    private final Plugin plugin;
    private final Server server;
    private final SchedulerAdapter scheduler;
    private final IncomingMessageConsumer consumer;

    public BukkitMessengerGateway(Plugin plugin, Server server, SchedulerAdapter scheduler, IncomingMessageConsumer consumer) {
        this.plugin = plugin;
        this.server = server;
        this.scheduler = scheduler;
        this.consumer = consumer;
        server.getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL);
        server.getMessenger().registerIncomingPluginChannel(plugin, CHANNEL, this);
    }

    @Override
    public void sendOutgoingMessage(String encodedMessage) {
        scheduler.async().execute(() -> sendPluginMessage(encodedMessage));
    }

    private void sendPluginMessage(String encodedMessage) {
        server.sendPluginMessage(plugin, CHANNEL, encodedMessage.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] message) {
        if (channel.equalsIgnoreCase(CHANNEL))
            consumer.consumeIncomingMessageAsString(new String(message));
    }

    @Override
    public void close() {
        server.getMessenger().unregisterOutgoingPluginChannel(plugin, CHANNEL);
        server.getMessenger().unregisterIncomingPluginChannel(plugin, CHANNEL);
    }
}
