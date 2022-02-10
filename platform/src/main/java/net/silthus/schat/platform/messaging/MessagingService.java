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

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;
import net.silthus.schat.IncomingMessageConsumer;
import net.silthus.schat.Messenger;
import net.silthus.schat.MessengerGateway;
import net.silthus.schat.MessengerGatewayProvider;
import net.silthus.schat.PluginMessage;
import net.silthus.schat.PluginMessageSerializer;
import net.silthus.schat.platform.config.ConfigKeys;
import net.silthus.schat.platform.config.SChatConfig;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.platform.config.ConfigKeys.DEBUG;

@Getter
@Log(topic = "sChat")
@Accessors(fluent = true)
public class MessagingService implements Messenger, IncomingMessageConsumer {

    public static MessagingService createMessagingService(GatewayProviderRegistry registry, PluginMessageSerializer serializer, SChatConfig config) {
        final MessengerGatewayProvider gatewayProvider = registry.get(config.get(ConfigKeys.MESSENGER));
        if (config.get(DEBUG))
            return new Logging(gatewayProvider, serializer);
        else
            return new MessagingService(gatewayProvider, serializer);
    }

    private final MessengerGateway gateway;
    private final PluginMessageSerializer serializer;
    private final Set<UUID> processedMessages = new HashSet<>();

    MessagingService(MessengerGatewayProvider gatewayProvider, PluginMessageSerializer serializer) {
        this.serializer = serializer;
        this.gateway = gatewayProvider.obtain(this);
    }

    @Override
    public void registerMessageType(Type type) {
        serializer().registerMessageType(type);
    }

    @Override
    public void sendPluginMessage(@NotNull PluginMessage message) throws UnsupportedMessageException {
        if (supports(message))
            sendOutgoingMessage(message);
        else
            throw new UnsupportedMessageException();
    }

    protected boolean supports(@NotNull PluginMessage message) {
        return serializer.supports(message);
    }

    protected void sendOutgoingMessage(@NotNull PluginMessage message) {
        if (addMessage(message))
            gateway.sendOutgoingMessage(serializer.encode(message));
    }

    protected boolean addMessage(@NotNull PluginMessage message) {
        return processedMessages.add(message.id());
    }

    @Override
    public boolean consumeIncomingMessage(@NonNull PluginMessage message) {
        if (shouldProcess(message)) {
            message.process();
            return true;
        } else {
            return false;
        }
    }

    protected boolean shouldProcess(@NotNull PluginMessage message) {
        return processedMessages.add(message.id());
    }

    @Override
    public boolean consumeIncomingMessageAsString(@NonNull String encodedString) {
        try {
            return consumeIncomingMessage(serializer.decode(encodedString));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to process plugin message: " + encodedString, e);
            return false;
        }
    }

    @Override
    public void close() {
        gateway.close();
    }

    @Log(topic = "sChat:MessagingService")
    public static final class Logging extends MessagingService {
        private Logging(MessengerGatewayProvider messengerGatewayProvider, PluginMessageSerializer serializer) {
            super(messengerGatewayProvider, serializer);
        }

        @Override
        protected boolean supports(@NotNull PluginMessage message) {
            final boolean supports = super.supports(message);
            log.info("PluginMessage(" + message + ") - SUPPORTED: " + supports);
            return supports;
        }

        @Override
        protected void sendOutgoingMessage(@NotNull PluginMessage message) {
            log.info("Trying to send: " + message);
            super.sendOutgoingMessage(message);
        }

        @Override
        protected boolean addMessage(@NotNull PluginMessage message) {
            final boolean added = super.addMessage(message);
            if (added)
                log.info("PluginMessage(" + message + ") - added to cache");
            else
                log.info("PluginMessage(" + message + ") - exists");
            return added;
        }

        @Override
        public boolean consumeIncomingMessage(@NonNull PluginMessage message) {
            final boolean processed = super.consumeIncomingMessage(message);
            if (processed)
                log.info("PluginMessage(" + message + ") - processed");
            else
                log.info("PluginMessage(" + message + ") - NOT processed");
            return processed;
        }

        @Override
        public boolean consumeIncomingMessageAsString(@NonNull String encodedString) {
            log.info("Decoding Incoming Message: " + encodedString);
            return super.consumeIncomingMessageAsString(encodedString);
        }
    }
}
