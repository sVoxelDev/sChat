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
import net.silthus.schat.bukkit.BukkitTests;
import net.silthus.schat.platform.SchedulerMock;
import net.silthus.schat.platform.messaging.MessagingServiceMock;
import net.silthus.schat.platform.messaging.MockPluginMessage;
import org.bukkit.Server;
import org.bukkit.plugin.messaging.Messenger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.MessengerGateway.CHANNEL;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BukkitMessengerGatewayTest extends BukkitTests {

    private BukkitMessengerGateway gateway;
    private SchedulerMock scheduler;
    private Server server;
    private Messenger messenger;
    private MessagingServiceMock consumer;

    @BeforeEach
    void setUp() {
        scheduler = new SchedulerMock();
        server = mock(Server.class);
        messenger = mock(Messenger.class);
        when(server.getMessenger()).thenReturn(messenger);
        consumer = new MessagingServiceMock();
        gateway = new BukkitMessengerGateway(mockPlugin, server, scheduler, consumer);
    }

    @Test
    void message_is_sent_async() {
        gateway.sendOutgoingMessage("");
        scheduler.assertExecutedAsync();
    }

    @Test
    void message_is_sent_to_plugin_message_channel() {
        gateway.sendOutgoingMessage("");
        verify(server).sendPluginMessage(mockPlugin, CHANNEL, "".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void plugin_messaging_channel_is_registered_with_messenger() {
        verify(messenger).registerOutgoingPluginChannel(mockPlugin, CHANNEL);
        verify(messenger).registerIncomingPluginChannel(mockPlugin, CHANNEL, gateway);
    }

    @Test
    void incoming_messages_are_dispatched_to_the_consumer() {
        gateway.onPluginMessageReceived(CHANNEL, BukkitTests.server.addPlayer(), encodedDummyMessage());
        consumer.assertProcessedMessageCountIs(1);
    }

    @Test
    void given_wrong_channel_incoming_message_is_not_processed() {
        gateway.onPluginMessageReceived("foobar", BukkitTests.server.addPlayer(), encodedDummyMessage());
        consumer.assertProcessedMessageCountIs(0);
    }

    private byte[] encodedDummyMessage() {
        return consumer.serializer().encode(new MockPluginMessage()).getBytes(StandardCharsets.UTF_8);
    }
}
