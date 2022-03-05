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
package net.silthus.schat.features;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.eventbus.Subscribe;
import net.silthus.schat.events.channel.ChatterJoinedChannelEvent;
import net.silthus.schat.events.message.SendChannelMessageEvent;
import net.silthus.schat.events.message.SendGlobalMessageEvent;
import net.silthus.schat.message.Message;
import net.silthus.schat.messenger.Messenger;
import net.silthus.schat.messenger.PluginMessage;

import static net.silthus.schat.channel.ChannelSettings.GLOBAL;

public class GlobalChatFeature {

    private final EventBus eventBus;
    private final Messenger messenger;

    public GlobalChatFeature(EventBus eventBus, Messenger messenger) {
        this.eventBus = eventBus;
        this.messenger = messenger;

        messenger.registerMessageType(SendGlobalMessage.class);
        messenger.registerMessageType(ChatterJoinedChannel.class);
        eventBus.register(this);
    }

    @Subscribe
    protected void onChannelMessage(SendChannelMessageEvent event) {
        if (event.channel().is(GLOBAL))
            sendGlobalMessage(event.channel(), event.message());
    }

    @Subscribe
    protected void onChatterJoined(ChatterJoinedChannelEvent event) {
        messenger.sendPluginMessage(new ChatterJoinedChannel(event.chatter(), event.channel()));
    }

    private void sendGlobalMessage(Channel channel, Message message) {
        final SendGlobalMessageEvent event = eventBus.post(new SendGlobalMessageEvent(channel, message));
        if (event.isNotCancelled())
            messenger.sendPluginMessage(new SendGlobalMessage(event.message()));
    }

    @Getter
    @Accessors(fluent = true)
    @EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
    public static final class SendGlobalMessage extends PluginMessage {
        private final Message message;

        public SendGlobalMessage(Message message) {
            this.message = message;
        }

        @Override
        public void process() {
            message.send();
        }
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
    public static final class ChatterJoinedChannel extends PluginMessage {

        private Chatter chatter;
        private Channel channel;

        @Override
        public void process() {
            chatter.join(channel);
        }
    }
}
