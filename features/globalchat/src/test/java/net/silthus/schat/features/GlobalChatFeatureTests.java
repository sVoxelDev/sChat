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
import net.silthus.schat.messaging.Messenger;
import net.silthus.schat.messaging.PluginMessage;
import net.silthus.schat.util.gson.GsonSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.Channel.GLOBAL;
import static net.silthus.schat.channel.ChannelHelper.channelWith;
import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.message.MessageHelper.randomMessage;
import static net.silthus.schat.messaging.PluginMessage.of;
import static net.silthus.schat.util.gson.GsonProvider.gsonSerializer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class GlobalChatFeatureTests {

    private Messenger messenger;
    private EventBusMock events;
    private GsonSerializer serializer;

    @BeforeEach
    void setUp() {
        messenger = spy(Messenger.class);
        events = new EventBusMock();
        serializer = gsonSerializer();
        new GlobalChatFeature(messenger, serializer).bind(events);
    }

    @AfterEach
    void tearDown() {
        events.close();
    }

    @Test
    void channel_without_global_flag_is_not_sent() {
        randomChannel().sendMessage(randomMessage());
        verify(messenger, never()).sendPluginMessage(any());
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
            verify(messenger).sendPluginMessage(any());
        }

        @Test
        void plugin_message_is_serializable() {
            final GlobalChatFeature.GlobalChannelPluginMessage message = new GlobalChatFeature.GlobalChannelPluginMessage(channel, randomMessage());
            final String encode = serializer.encode(of(message));
            final PluginMessage.Type decode = serializer.decode(encode);
            assertThat(decode.content()).isEqualTo(message);
        }
    }
}
