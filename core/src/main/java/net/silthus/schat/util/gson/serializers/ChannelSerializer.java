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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.message.Targets;
import net.silthus.schat.pointer.Settings;
import net.silthus.schat.util.gson.JObject;

public final class ChannelSerializer implements JsonSerializer<Channel>, JsonDeserializer<Channel> {

    private final ChannelRepository channelRepository;

    public ChannelSerializer(ChannelRepository channelRepository) {
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
        if (json.isJsonPrimitive())
            return channelRepository.find(json.getAsString()).orElse(null);

        final JsonObject object = json.getAsJsonObject();
        final String key = object.get("key").getAsString();
        return channelRepository.findOrCreate(key, s -> Channel.channel(s)
            .name(context.deserialize(object.get("name"), Component.class))
            .settings(context.deserialize(object.get("settings"), Settings.class))
            .targets(context.deserialize(object.get("targets"), Targets.class))
            .create());
    }
}
