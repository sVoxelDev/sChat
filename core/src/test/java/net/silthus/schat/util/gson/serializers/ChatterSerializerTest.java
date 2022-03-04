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
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.chatter.ChatterRepository;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.util.gson.GsonProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.chatter.ChatterRepository.createInMemoryChatterRepository;
import static org.assertj.core.api.Assertions.assertThat;

class ChatterSerializerTest {

    private final ChatterRepository chatterRepository = createInMemoryChatterRepository();

    private Gson gson;

    @BeforeEach
    void setUp() {
        gson = GsonProvider.gsonProvider()
            .registerChatterSerializer(chatterRepository)
            .registerChannelSerializer(createInMemoryChannelRepository(EventBus.empty()))
            .prettyGson();
    }

    private void assertSerialized(Chatter chatter, String result) {
        assertThat(gson.toJson(chatter)).isEqualTo(result);
    }

    @Test
    void chatter_is_serialized() {
        final Chatter chatter = Chatter.chatter(Identity.identity("Bob", text("Bobby")));
        assertSerialized(chatter, """
            {
              "identity": {
                "id": "%s",
                "name": "Bob",
                "display_name": {
                  "text": "Bobby"
                }
              },
              "channels": []
            }""".formatted(chatter.uniqueId().toString()));
    }

    @Test
    void active_channel_is_serialized() {
        final Chatter chatter = Chatter.chatter(Identity.identity("Bob")).activeChannel(Channel.createChannel("test"));
        assertSerialized(chatter, """
            {
              "identity": {
                "id": "%s",
                "name": "Bob",
                "display_name": {
                  "text": "Bob"
                }
              },
              "active_channel": "test",
              "channels": [
                "test"
              ]
            }""".formatted(chatter.uniqueId().toString()));
    }

    @Test
    void channels_are_serialized() {
        final Chatter chatter = Chatter.chatter(Identity.identity("Bob"));
        chatter.join(Channel.createChannel("test"));
        chatter.activeChannel(Channel.createChannel("active"));
        assertSerialized(chatter, """
            {
              "identity": {
                "id": "%s",
                "name": "Bob",
                "display_name": {
                  "text": "Bob"
                }
              },
              "active_channel": "active",
              "channels": [
                "test",
                "active"
              ]
            }""".formatted(chatter.uniqueId().toString()));
    }

    @Test
    void chatter_with_id_only_is_deserialized_from_repository() {
        final ChatterMock chatter = randomChatter();
        chatterRepository.add(chatter);
        final Chatter deserialized = gson.fromJson("\"" + chatter.uniqueId() + "\"", Chatter.class);
        assertThat(deserialized).isSameAs(chatter);
    }
}
