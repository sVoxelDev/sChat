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

import java.lang.reflect.Type;
import lombok.NonNull;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelHelper;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.channel.ChannelSettings;
import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.chatter.ChatterRepository;
import net.silthus.schat.eventbus.EventBusMock;
import net.silthus.schat.events.message.SendChannelMessageEvent;
import net.silthus.schat.message.MessageHelper;
import net.silthus.schat.messenger.GsonPluginMessageSerializer;
import net.silthus.schat.messenger.Messenger;
import net.silthus.schat.messenger.PluginMessage;
import net.silthus.schat.messenger.PluginMessageSerializer;
import net.silthus.schat.util.gson.GsonProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalChatFeatureTests implements Messenger {

    private final EventBusMock events = EventBusMock.eventBusMock();
    private boolean messengerCalled = false;
    private GsonPluginMessageSerializer serializer;

    @BeforeEach
    void setUp() {
        final GsonProvider gsonProvider = GsonProvider.gsonProvider()
            .registerChannelSerializer(ChannelRepository.createInMemoryChannelRepository(events))
            .registerChatterSerializer(ChatterRepository.createInMemoryChatterRepository())
            .registerTargetsSerializer(ChatterRepository.createInMemoryChatterRepository())
            .registerMessageSourceSerializer(ChatterRepository.createInMemoryChatterRepository());
        serializer = PluginMessageSerializer.gsonSerializer(gsonProvider);
        new GlobalChatFeature(this).bind(events);
    }

    @AfterEach
    void tearDown() {
        events.close();
    }

    @Test
    void channel_without_global_flag_is_not_sent() {
        ChannelHelper.channelWith(ChannelSettings.GLOBAL, false).sendMessage(MessageHelper.randomMessage());
        assertThat(messengerCalled).isFalse();
    }

    @Override
    public void registerMessageType(Type type) {
        serializer.registerMessageType(type);
    }

    @Override
    public void registerTypeAdapter(Type type, Object adapter) {
        serializer.registerTypeAdapter(type, adapter);
    }

    @Override
    public void sendPluginMessage(@NonNull PluginMessage pluginMessage) {
        messengerCalled = true;
    }

    @Nested class channel_with_global_flag {
        private Channel channel;
        private int messageCount = 0;

        @BeforeEach
        void setUp() {
            channel = ChannelHelper.channelWith(ChannelSettings.GLOBAL, true);
            events.on(SendChannelMessageEvent.class, event -> messageCount++);
        }

        @Test
        void sendMessage_dispatches_plugin_message() {
            channel.sendMessage(MessageHelper.randomMessage());
            assertThat(messengerCalled).isTrue();
        }

        @Test
        void plugin_message_is_serializable() {
            final GlobalChatFeature.GlobalChannelPluginMessage message = new GlobalChatFeature.GlobalChannelPluginMessage(channel, MessageHelper.randomMessage());
            final String encode = serializer.encode(message);
            final PluginMessage decode = serializer.decode(encode);
            Assertions.assertThat(decode).isEqualTo(message);
        }

        @Test
        void process_sends_message_to_channel() {
            final GlobalChatFeature.GlobalChannelPluginMessage message = new GlobalChatFeature.GlobalChannelPluginMessage(channel, MessageHelper.randomMessage());
            message.process();
            assertThat(messageCount).isEqualTo(1);
        }

        @Test
        void process_joins_channel_targets_to_channel() {
            final ChatterMock chatter = ChatterMock.randomChatter();
            channel.addTarget(chatter);
            final GlobalChatFeature.GlobalChannelPluginMessage message = new GlobalChatFeature.GlobalChannelPluginMessage(channel, MessageHelper.randomMessage());
            message.process();
            chatter.assertJoinedChannel(channel);
        }
    }
}
