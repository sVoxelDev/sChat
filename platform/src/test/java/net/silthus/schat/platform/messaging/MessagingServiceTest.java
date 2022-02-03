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

package net.silthus.schat.platform.messaging;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import net.silthus.schat.messaging.PluginMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MessagingServiceTest {

    // TODO: messenger outline
    //  - incoming json is processed by some kind of plugin message parser or factory where dynamic types and parsers can be registered
    //  - the plugin message interface itself has a process() method to processed the message after it was consumed by the incoming messenger
    //  - the serialization is not done by the message, but by the factory or lets call it serializer

    private MessagingServiceMock service;

    @BeforeEach
    void setUp() {
        service = new MessagingServiceMock();
        service.registerGsonTypeAdapter(EmptyPluginMessage.class, new EmptyPluginMessage.Type());
    }

    @Test
    void message_without_content_is_sent() {
        final EmptyPluginMessage message = new EmptyPluginMessage();
        service.sendPluginMessage(message);
        service.assertLastReceivedMessageIs(message);
    }

    @Test
    void same_message_is_only_consumed_once() {
        final PluginMessage.Type message = PluginMessage.of(new EmptyPluginMessage());
        service.consumeIncomingMessage(message);
        service.consumeIncomingMessage(message);
        service.assertProcessedMessageCountIs(1);
    }

    @Test
    void message_is_dispatched_async() {
        service.sendPluginMessage(new EmptyPluginMessage());
        service.getScheduler().assertExecutedAsync();
    }

    @EqualsAndHashCode
    private static final class EmptyPluginMessage implements PluginMessage {

        @Override
        public void process() {

        }

        static class Type extends TypeAdapter<EmptyPluginMessage> {

            @Override
            public void write(JsonWriter jsonWriter, EmptyPluginMessage emptyPluginMessage) throws IOException {
                jsonWriter.beginObject().endObject();
            }

            @Override
            public EmptyPluginMessage read(JsonReader jsonReader) throws IOException {
                return new EmptyPluginMessage();
            }
        }
    }
}
