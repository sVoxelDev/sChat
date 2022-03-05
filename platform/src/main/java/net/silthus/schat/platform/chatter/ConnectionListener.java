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

import com.google.gson.InstanceCreator;
import java.lang.reflect.Type;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterFactory;
import net.silthus.schat.chatter.ChatterRepository;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.events.chatter.ChatterJoinedServerEvent;
import net.silthus.schat.messenger.Messenger;
import net.silthus.schat.messenger.PluginMessage;
import net.silthus.schat.platform.sender.Sender;
import org.jetbrains.annotations.NotNull;

public abstract class ConnectionListener {
    private final ChatterRepository chatterRepository;
    private final ChatterFactory chatterFactory;
    private final Messenger messenger;
    private final EventBus eventBus;

    public ConnectionListener(ChatterRepository chatterRepository, ChatterFactory chatterFactory, Messenger messenger, EventBus eventBus) {
        this.chatterRepository = chatterRepository;
        this.chatterFactory = chatterFactory;
        this.messenger = messenger;
        this.eventBus = eventBus;
        registerMessageType();
    }

    private void registerMessageType() {
        messenger.registerMessageType(ChatterJoined.class);
        messenger.registerTypeAdapter(ChatterJoined.class, new MessageCreator());
    }

    protected final void onJoin(Sender sender) {
        final Chatter chatter = getOrCreateChatter(sender);
        fireJoinServerEvent(chatter);
        sendGlobalJoinPing(chatter);
    }

    @NotNull
    @SuppressWarnings("checkstyle:MethodName")
    private Chatter getOrCreateChatter(Sender sender) {
        return chatterRepository.findOrCreate(sender.uniqueId(), chatterFactory::createChatter);
    }

    private void fireJoinServerEvent(Chatter chatter) {
        eventBus.post(new ChatterJoinedServerEvent(chatter));
    }

    protected void sendGlobalJoinPing(Chatter chatter) {
        messenger.sendPluginMessage(new ChatterJoined(chatter));
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    @NoArgsConstructor
    @EqualsAndHashCode(of = {"chatter"}, callSuper = true)
    final static class ChatterJoined extends PluginMessage {
        private Chatter chatter;
        private transient ChatterRepository repository;
        private transient ChatterFactory factory;
        private transient EventBus eventBus;

        ChatterJoined(Chatter chatter) {
            this.chatter = chatter;
        }

        @Override
        public void process() {
            if (!repository.contains(chatter.uniqueId())) {
                repository.add(chatter);
                eventBus.post(new ChatterJoinedServerEvent(chatter));
            }
        }
    }

    private final class MessageCreator implements InstanceCreator<ChatterJoined> {
        @Override
        public ChatterJoined createInstance(Type type) {
            return new ChatterJoined()
                .repository(chatterRepository)
                .factory(chatterFactory)
                .eventBus(eventBus);
        }
    }
}
