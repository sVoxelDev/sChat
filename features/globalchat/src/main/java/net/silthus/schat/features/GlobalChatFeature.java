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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.eventbus.EventListener;
import net.silthus.schat.events.message.SendChannelMessageEvent;
import net.silthus.schat.message.Message;
import net.silthus.schat.messaging.Messenger;
import net.silthus.schat.messaging.PluginMessage;
import net.silthus.schat.messaging.PluginMessageSerializer;

import static net.silthus.schat.channel.Channel.GLOBAL;

public class GlobalChatFeature implements EventListener {

    private final Messenger messenger;

    public GlobalChatFeature(Messenger messenger, PluginMessageSerializer serializer) {
        this.messenger = messenger;
        serializer.registerMessageType(GlobalChannelPluginMessage.class);
    }

    @Override
    public void bind(EventBus bus) {
        bus.on(SendChannelMessageEvent.class, this::onChannelMessage);
    }

    private void onChannelMessage(SendChannelMessageEvent event) {
        if (event.channel().is(GLOBAL))
            messenger.sendPluginMessage(new GlobalChannelPluginMessage(event.channel(), event.message()));
    }

    @Getter
    @Accessors(fluent = true)
    @EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
    public static final class GlobalChannelPluginMessage extends PluginMessage {
        private final Channel channel;
        private final Message message;

        public GlobalChannelPluginMessage(Channel channel, Message message) {
            this.channel = channel;
            this.message = message;
        }

        @Override
        public void process() {
            message.copy().to(channel).send();
        }
    }
}
