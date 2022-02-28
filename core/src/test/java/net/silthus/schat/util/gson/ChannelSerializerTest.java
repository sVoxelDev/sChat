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
package net.silthus.schat.util.gson;

import com.google.gson.Gson;
import com.google.gson.JsonPrimitive;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.eventbus.EventBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.ChannelHelper.channelWith;
import static net.silthus.schat.channel.ChannelSettings.GLOBAL;
import static net.silthus.schat.chatter.ChatterRepository.createInMemoryChatterRepository;
import static org.assertj.core.api.Assertions.assertThat;

class ChannelSerializerTest {

    private Gson gson;
    private ChannelRepository channelRepository;

    @BeforeEach
    void setUp() {
        channelRepository = ChannelRepository.createInMemoryChannelRepository(EventBus.empty());
        gson = GsonProvider.gsonProvider()
            .registerChannelSerializer(channelRepository)
            .registerTargetsSerializer(createInMemoryChatterRepository())
            .prettyGson();
    }

    @Test
    void serializes_channel_properties() {
        final String json = gson.toJson(channelWith("test"));
        assertThat(json).contains(
            "\"key\": \"test\"",
            "\"targets\": []",
            "\"settings\":",
            "\"join_permission\": \"schat.channel.test.join\"");
    }

    @Test
    void deserializes_channel_with_key_from_repository() {
        final Channel channel = channelWith("test");
        channelRepository.add(channel);

        final Channel result = gson.fromJson(new JsonPrimitive("test"), Channel.class);
        assertThat(result).isNotNull().isSameAs(channel);
    }

    @Test
    void deserializes_unknown_channel_to_null() {
        final Channel channel = gson.fromJson(new JsonPrimitive("foobar"), Channel.class);
        assertThat(channel).isNull();
    }

    @Test
    void deserializes_full_channel_from_repository_if_existant() {
        final Channel channel = channelWith("test").set(GLOBAL, true);
        channelRepository.add(channel);

        final Channel result = gson.fromJson(JObject.json().add("key", "test").create(), Channel.class);
        assertThat(result).isSameAs(channel);
    }
}
