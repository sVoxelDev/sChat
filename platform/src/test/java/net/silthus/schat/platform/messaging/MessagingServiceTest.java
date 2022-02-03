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

package net.silthus.schat.platform.messaging;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import net.silthus.schat.messaging.PluginMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessagingServiceTest {

    private MessagingServiceMock service;

    @BeforeEach
    void setUp() {
        service = new MessagingServiceMock();
        service.getSerializer().registerMessageType(EmptyPluginMessage.class);
        EmptyPluginMessage.processed = false;
    }

    @Test
    void message_without_content_is_sent() {
        final EmptyPluginMessage message = new EmptyPluginMessage();
        service.sendPluginMessage(message);
        service.assertLastReceivedMessageIs(message);
    }

    @Test
    void same_message_is_only_consumed_once() {
        final EmptyPluginMessage message = new EmptyPluginMessage();
        service.consumeIncomingMessage(message);
        service.consumeIncomingMessage(message);
        service.assertProcessedMessageCountIs(1);
    }

    @Test
    void received_message_is_processed() {
        service.consumeIncomingMessage(new EmptyPluginMessage());
        assertThat(EmptyPluginMessage.processed).isTrue();
    }

    @EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
    private static final class EmptyPluginMessage extends PluginMessage {

        static boolean processed = false;

        @Override
        public void process() {
            processed = true;
        }
    }

    @Nested class given_self_referencing_messenger {
        @BeforeEach
        void setUp() {
            service = new MessagingServiceMock() {
                @Override
                public boolean consumeIncomingMessage(@NonNull PluginMessage message) {
                    final boolean process = super.consumeIncomingMessage(message);
                    if (process)
                        sendPluginMessage(message);
                    return process;
                }
            };
        }

        @Test
        void message_is_not_processed() {
            service.sendPluginMessage(new EmptyPluginMessage());
            service.assertProcessedMessageCountIs(0);
        }
    }
}
