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

import lombok.NonNull;
import net.silthus.schat.messenger.Messenger;
import net.silthus.schat.messenger.PluginMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class MessagingServiceTest {

    private MessagingServiceMock service;

    @BeforeEach
    void setUp() {
        service = new MessagingServiceMock();
    }

    @Test
    void message_without_content_is_sent() {
        final MockPluginMessage message = new MockPluginMessage();
        service.sendPluginMessage(message);
        service.assertLastReceivedMessageIs(message);
    }

    @Test
    void same_message_is_only_consumed_once() {
        final MockPluginMessage message = new MockPluginMessage();
        service.consumeIncomingMessage(message);
        service.consumeIncomingMessage(message);
        service.assertProcessedMessageCountIs(1);
    }

    @Test
    void received_message_is_processed() {
        final MockPluginMessage message = new MockPluginMessage();
        service.consumeIncomingMessage(message);
        message.assertProcessed();
    }

    @Test
    void unregistered_message_throws() {
        assertThatExceptionOfType(Messenger.UnsupportedMessageException.class)
            .isThrownBy(() -> service.sendPluginMessage(new PluginMessage() {
                @Override
                public void process() {

                }
            }));
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
            service.sendPluginMessage(new MockPluginMessage());
            service.assertProcessedMessageCountIs(0);
        }
    }

    @Nested class given_invalid_message {
        @Test
        void incoming_consumer_silently_fails() {
            assertThatCode(() -> service.consumeIncomingMessageAsString(""))
                .doesNotThrowAnyException();
        }
    }
}
