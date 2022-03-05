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
import net.silthus.schat.channel.ChannelHelper;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.chatter.ChatterRepository;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.MessageHelper;
import net.silthus.schat.pointer.Setting;
import net.silthus.schat.util.gson.GsonProviderStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.ChannelHelper.channelWith;
import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.chatter.ChatterRepository.createInMemoryChatterRepository;
import static net.silthus.schat.message.Message.message;
import static org.assertj.core.api.Assertions.assertThat;

class MessageSerializerTest {

    private static final Setting<String> REPLACED_MESSAGE_FORMAT = Setting.setting(String.class, "replaced_message_format", null);

    private final ChatterRepository chatterRepository = createInMemoryChatterRepository();
    private final ChannelRepository channelRepository = createInMemoryChannelRepository(EventBus.empty());

    private Gson gson;

    @BeforeEach
    void setUp() {
        gson = GsonProviderStub.gsonProviderStub(
            chatterRepository,
            channelRepository
        ).prettyGson();
    }

    @Test
    void message_format_is_serialized() {
        final Message message = message(MessageHelper.randomText())
            .to(randomChatter())
            .to(ChannelHelper.randomChannel())
            .set(REPLACED_MESSAGE_FORMAT, "<source_display_name>: <text>")
            .create();
        final String json = gson.toJson(message);
        assertThat(json).contains("\"replaced_message_format\": \"<source_display_name>: <text>\"");
    }

    @Test
    void targets_of_message_are_deserialized() {
        final ChatterMock chatter = randomChatter();
        chatterRepository.add(chatter);
        final Channel channel = channelWith("test");
        channelRepository.add(channel);

        final Message message = gson.fromJson("""
            {
              "id": "a8cc9b78-30d0-4ffc-89a3-cec0e5097e67",
              "timestamp": "2022-03-04T06:08:00.530698700Z",
              "targets": [
                "d14f0c7c-9368-4c04-bbca-a7fa3b6c510f"
              ],
              "text": {
                "text": "QwgyU6XXWM"
              },
              "type": "SYSTEM",
              "settings": {
                "replaced_message_format": "<source_display_name>: <text>"
              },
              "targets": [
                "chatter:%s",
                "channel:%s"
              ]
            }""".formatted(chatter.uniqueId(), "test"), Message.class);

        assertThat(message.targets()).contains(chatter, channel);
    }
}
