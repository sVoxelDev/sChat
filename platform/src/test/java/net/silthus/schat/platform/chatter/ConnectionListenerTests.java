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
package net.silthus.schat.platform.chatter;

import java.util.UUID;
import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.chatter.ChatterRepository;
import net.silthus.schat.eventbus.EventBusMock;
import net.silthus.schat.events.chatter.ChatterJoinedServerEvent;
import net.silthus.schat.platform.messaging.MessagingServiceMock;
import net.silthus.schat.platform.sender.SenderMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.chatter.ChatterRepository.createInMemoryChatterRepository;
import static net.silthus.schat.platform.sender.SenderMock.randomSender;
import static org.assertj.core.api.Assertions.assertThat;

class ConnectionListenerTests {

    private ChatterRepository chatterRepository;
    private MessagingServiceMock messenger;
    private EventBusMock eventBus;
    private ConnectionListener listener;
    private SenderMock sender;

    @BeforeEach
    void setUp() {
        chatterRepository = createInMemoryChatterRepository();
        messenger = MessagingServiceMock.messengerMock();
        eventBus = EventBusMock.eventBusMock();
        listener = new ConnectionListener(chatterRepository, ChatterMock::randomChatter, messenger, eventBus) {
        };
        sender = randomSender();
    }

    @AfterEach
    void tearDown() {
        eventBus.close();
    }

    private ConnectionListener.ChatterJoined createRandomPluginMessage() {
        return new ConnectionListener.ChatterJoined(ChatterMock.randomChatter())
            .repository(chatterRepository)
            .factory(ChatterMock::randomChatter)
            .eventBus(eventBus);
    }

    private ConnectionListener.ChatterJoined consumeIncomingMessage() {
        final ConnectionListener.ChatterJoined msg = createRandomPluginMessage();
        messenger.consumeIncomingMessage(msg);
        return msg;
    }

    private void assertJoinEventFired(UUID id) {
        eventBus.assertEventFired(new ChatterJoinedServerEvent(chatterRepository.get(id)));
    }

    @Nested class onJoin {
        private void join() {
            listener.onJoin(sender);
        }

        @Test
        void loads_chatter_into_cache() {
            join();
            assertThat(chatterRepository.contains(sender.uniqueId())).isTrue();
        }

        @Test
        void sends_join_ping_to_all_servers() {
            join();
            messenger.assertSentMessage(ConnectionListener.ChatterJoined.class);
        }

        @Test
        void when_ping_is_processed_then_chatter_is_created() {
            final ConnectionListener.ChatterJoined msg = consumeIncomingMessage();
            assertThat(chatterRepository.contains(msg.chatter().uniqueId())).isTrue();
        }

        @Test
        void when_ping_is_processed_then_joined_server_event_is_fired() {
            final ConnectionListener.ChatterJoined msg = consumeIncomingMessage();
            assertJoinEventFired(msg.chatter().uniqueId());
        }

        @Test
        void fires_join_event() {
            join();
            assertJoinEventFired(sender.uniqueId());
        }
    }
}
