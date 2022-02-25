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
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import lombok.extern.java.Log;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.message.Targets;
import net.silthus.schat.pointer.Settings;
import net.silthus.schat.util.gson.JObject;

/**
 * A Gson serializer to serialize and deserialize a {@link Channel} into JSON format.
 *
 * <p>Used for sending packets across servers.</p>
 *
 * @since next
 */
public class ChannelSerializer implements JsonSerializer<Channel>, JsonDeserializer<Channel> {

    public static final Type CHANNEL_TYPE = new TypeToken<Channel>() {
    }.getType();

    /**
     * Creates a new channel serializer.
     *
     * @param channelRepository the channel repository to lookup existing channels
     * @param debug             set true to log the serialization process
     * @return the created serializer
     * @since next
     */
    public static ChannelSerializer createChannelSerializer(ChannelRepository channelRepository, boolean debug) {
        if (debug)
            return new Logging(channelRepository);
        else
            return new ChannelSerializer(channelRepository);
    }

    private final ChannelRepository channelRepository;

    private ChannelSerializer(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public JsonElement serialize(Channel src, Type typeOfSrc, JsonSerializationContext context) {
        return JObject.json()
            .add("key", src.key())
            .add("name", context.serialize(src.displayName(), Component.class))
            .add("settings", context.serialize(src.settings(), Settings.class))
            .add("targets", context.serialize(src.targets(), Targets.class))
            .create();
    }

    @Override
    public Channel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = json.getAsJsonObject();
        final String key = object.get("key").getAsString();
        return channelRepository.findOrCreate(key, k -> createChannel(k, object, context));
    }

    protected Channel createChannel(String key, JsonObject object, JsonDeserializationContext context) {
        return Channel.channel(key)
            .name(context.deserialize(object.get("name"), Component.class))
            .settings(context.deserialize(object.get("settings"), Settings.class))
            .targets(context.deserialize(object.get("targets"), Targets.class))
            .create();
    }

    @Log(topic = "sChat:ChannelSerializer")
    private static final class Logging extends ChannelSerializer {
        private Logging(ChannelRepository repository) {
            super(repository);
        }

        @Override
        public JsonElement serialize(Channel src, Type typeOfSrc, JsonSerializationContext context) {
            final JsonElement json = super.serialize(src, typeOfSrc, context);
            log.info("Serialized Channel " + src + " into " + json);
            return json;
        }

        @Override
        public Channel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final Channel channel = super.deserialize(json, typeOfT, context);
            log.info("Deserialized Channel " + channel + " from " + json);
            return channel;
        }

        @Override
        protected Channel createChannel(String key, JsonObject object, JsonDeserializationContext context) {
            final Channel channel = super.createChannel(key, object, context);
            log.info("Created Channel " + channel);
            return channel;
        }
    }
}
