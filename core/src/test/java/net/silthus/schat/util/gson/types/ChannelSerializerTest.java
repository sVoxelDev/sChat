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

package net.silthus.schat.util.gson.types;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.message.Targets;
import net.silthus.schat.pointer.Settings;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.ChannelHelper.channelWith;
import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.util.gson.JObject.json;
import static net.silthus.schat.util.gson.types.ChannelSerializer.createChannelSerializer;
import static org.assertj.core.api.Assertions.assertThat;

class ChannelSerializerTest {
    private static final SerializationContextStub SERIALIZATION_CONTEXT = new SerializationContextStub();
    private static final SettingsDeserializationContextStub DESERIALIZATION_CONTEXT = new SettingsDeserializationContextStub();

    private ChannelSerializer serializer;
    private ChannelRepository repository;

    @BeforeEach
    void setUp() {
        repository = createInMemoryChannelRepository();
        serializer = createChannelSerializer(repository, false);
    }

    @NotNull
    private Channel add(Channel channel) {
        repository.add(channel);
        return channel;
    }

    private JsonElement serialize(Channel channel) {
        return serializer.serialize(channel, Channel.class, SERIALIZATION_CONTEXT);
    }

    private Channel deserialize(JsonElement json) {
        return serializer.deserialize(json, Channel.class, DESERIALIZATION_CONTEXT);
    }

    @Test
    void serialization_writes_key() {
        final JsonElement json = serialize(channelWith("test"));
        assertThat(json.getAsJsonObject().get("key").getAsString()).isEqualTo("test");
    }

    @Test
    void deserialization_uses_existing_channel_from_repository() {
        final Channel channel = add(channelWith("test"));
        final Channel deserialized = deserialize(json().add("key", "test").create());
        assertThat(deserialized).isSameAs(channel);
    }

    @Test
    void given_channel_does_not_exist_creates_and_adds_channel() {
        final Channel channel = deserialize(json().add("key", "foobar").create());
        assertThat(channel.key()).isEqualTo("foobar");
        assertThat(repository.all()).contains(channel);
    }

    private static class SerializationContextStub implements JsonSerializationContext {
        @Override
        public JsonElement serialize(Object o) {
            return new JsonObject();
        }

        @Override
        public JsonElement serialize(Object o, Type type) {
            return new JsonObject();
        }
    }

    private static class SettingsDeserializationContextStub implements JsonDeserializationContext {
        @Override
        @SuppressWarnings("unchecked")
        public <T> T deserialize(JsonElement jsonElement, Type type) throws JsonParseException {
            if (type.equals(Settings.class))
                return (T) Settings.createSettings();
            if (type.equals(Targets.class))
                return (T) new Targets();
            return null;
        }
    }
}
