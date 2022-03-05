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
package net.silthus.schat.util.gson.serializers;

import com.google.gson.Gson;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.chatter.ChatterRepository;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.message.MessageTarget;
import net.silthus.schat.util.gson.GsonProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.ChannelHelper.channelWith;
import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.chatter.ChatterRepository.createInMemoryChatterRepository;
import static org.assertj.core.api.Assertions.assertThat;

class MessageTargetSerializerTest {

    private final ChatterRepository chatterRepository = createInMemoryChatterRepository();
    private final ChannelRepository channelRepository = createInMemoryChannelRepository(EventBus.empty());

    private Gson gson;

    @BeforeEach
    void setUp() {
        gson = GsonProvider.gsonProvider()
            .registerChatterSerializer(chatterRepository)
            .registerChannelSerializer(channelRepository)
            .prettyGson();
    }

    @Test
    void chatter_is_serialized_into_prefixed_with_id() {
        final ChatterMock chatter = randomChatter();
        final String json = gson.toJson(chatter, MessageTarget.class);
        assertThat(json).isEqualTo("\"chatter:" + chatter.uniqueId() + "\"");
    }

    @Test
    void chatter_is_deserialized_into_chatter() {
        final ChatterMock chatter = randomChatter();
        chatterRepository.add(chatter);
        final MessageTarget target = gson.fromJson("\"chatter:" + chatter.uniqueId() + "\"", MessageTarget.class);
        assertThat(target).isInstanceOf(Chatter.class)
            .isSameAs(chatter);
    }

    @Test
    void channel_is_serialized_into_prefixed_with_key() {
        final Channel channel = channelWith("test");
        final String json = gson.toJson(channel, MessageTarget.class);
        assertThat(json).isEqualTo("\"channel:test\"");
    }

    @Test
    void channel_is_deserialized_from_repository() {
        final Channel channel = channelWith("test");
        channelRepository.add(channel);
        final MessageTarget target = gson.fromJson("\"channel:test\"", MessageTarget.class);
        assertThat(target).isInstanceOf(Channel.class)
            .isSameAs(channel);
    }
}