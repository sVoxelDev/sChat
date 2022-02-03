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

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NonNull;
import net.silthus.schat.messaging.IncomingMessageConsumer;
import net.silthus.schat.messaging.Messenger;
import net.silthus.schat.messaging.MessengerGateway;
import net.silthus.schat.messaging.MessengerGatewayProvider;
import net.silthus.schat.messaging.PluginMessage;
import net.silthus.schat.messaging.PluginMessageSerializer;
import org.jetbrains.annotations.NotNull;

@Getter
public class MessagingService implements Messenger, IncomingMessageConsumer {

    private final MessengerGateway gateway;
    private final PluginMessageSerializer serializer;
    private final Set<UUID> processedMessages = new HashSet<>();

    public MessagingService(MessengerGatewayProvider gatewayProvider, PluginMessageSerializer serializer) {
        this.serializer = serializer;
        this.gateway = gatewayProvider.obtain(this);
    }

    @Override
    public void sendPluginMessage(@NotNull PluginMessage message) {
        if (processedMessages.add(message.id()))
            gateway.sendOutgoingMessage(serializer.encode(message));
    }

    @Override
    public boolean consumeIncomingMessage(@NonNull PluginMessage message) {
        if (processedMessages.add(message.id())) {
            message.process();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean consumeIncomingMessageAsString(@NonNull String encodedString) {
        return consumeIncomingMessage(serializer.decode(encodedString));
    }
}
