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

import net.silthus.schat.channel.Channel;
import net.silthus.schat.eventbus.EventBusMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.Channel.GLOBAL;
import static net.silthus.schat.channel.ChannelHelper.channelWith;
import static net.silthus.schat.message.MessageHelper.randomMessage;

class GlobalChatTests {

    private OutIsInMessengerMock messenger;
    private GlobalChatFeature feature;
    private EventBusMock events;

    @BeforeEach
    void setUp() {
        messenger = new OutIsInMessengerMock();
        feature = new GlobalChatFeature(messenger);
        events = new EventBusMock();
        feature.bind(events);
    }

    @AfterEach
    void tearDown() {
        events.close();
    }

    @Nested class channel_with_global_flag {
        private Channel channel;

        @BeforeEach
        void setUp() {
            channel = channelWith(GLOBAL, true);
        }

        @Test
        void sendMessage_dispatches_plugin_message() {
            channel.sendMessage(randomMessage());
            messenger.assertOutgoingMessageSent();
        }
    }
}
