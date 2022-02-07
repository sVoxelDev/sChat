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

import java.util.LinkedList;
import java.util.Queue;
import lombok.NonNull;
import net.silthus.schat.PluginMessage;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.api.ObjectAssert;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.PluginMessageSerializer.gsonSerializer;
import static org.assertj.core.api.Assertions.assertThat;

public class MessagingServiceMock extends MessagingService {

    private final Queue<PluginMessage> sentMessages = new LinkedList<>();
    private PluginMessage lastReceivedMessage;
    private int processedMessageCount = 0;

    public MessagingServiceMock() {
        super(new MockMessagingGatewayProvider(), gsonSerializer());
        registerMessageType(MockPluginMessage.class);
    }

    @Override
    public void sendPluginMessage(@NotNull PluginMessage message) {
        super.sendPluginMessage(message);
        this.sentMessages.add(message);
    }

    @Override
    public boolean consumeIncomingMessage(@NonNull PluginMessage message) {
        final boolean processed = super.consumeIncomingMessage(message);
        this.lastReceivedMessage = message;
        if (processed)
            processedMessageCount++;
        return processed;
    }

    public void assertLastReceivedMessageIs(PluginMessage message) {
        assertThat(lastReceivedMessage).isNotNull().isEqualTo(message);
    }

    public <M extends PluginMessage> ObjectAssert<M> assertLastReceivedMessage(Class<M> type) {
        return assertThat(lastReceivedMessage)
            .isInstanceOf(type)
            .asInstanceOf(InstanceOfAssertFactories.type(type));
    }

    public void assertProcessedMessageCountIs(int count) {
        assertThat(processedMessageCount).isEqualTo(count);
    }

    public void assertSentMessage(Class<? extends PluginMessage> type) {
        assertThat(sentMessages).hasAtLeastOneElementOfType(type);
    }
}
