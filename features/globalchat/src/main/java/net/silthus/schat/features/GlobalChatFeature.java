/*
 * sChat, a Supercharged Minecraft Chat Plugin
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.silthus.schat.features;

import lombok.Getter;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.eventbus.EventListener;
import net.silthus.schat.events.message.SendChannelMessageEvent;
import net.silthus.schat.message.Message;
import net.silthus.schat.messenger.Messenger;
import net.silthus.schat.messenger.message.type.AbstractPluginMessage;

import static net.silthus.schat.channel.Channel.GLOBAL;

public class GlobalChatFeature implements EventListener {
    private final Messenger messenger;

    public GlobalChatFeature(Messenger messenger) {
        this.messenger = messenger;
    }

    @Override
    public void bind(EventBus bus) {
        bus.on(SendChannelMessageEvent.class, this::onChannelMessage);
    }

    private void onChannelMessage(SendChannelMessageEvent event) {
        if (event.channel().is(GLOBAL)) {
            messenger.sendPluginMessage(new GlobalChannelPluginMessage(event.channel(), event.message()));
        }
    }

    @Getter
    public static final class GlobalChannelPluginMessage extends AbstractPluginMessage {
        private final Channel channel;
        private final Message message;

        public GlobalChannelPluginMessage(Channel channel, Message message) {
            this.channel = channel;
            this.message = message;
        }
    }
}
