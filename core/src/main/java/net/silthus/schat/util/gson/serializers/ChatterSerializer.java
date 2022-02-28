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
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterRepository;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.util.gson.JObject;

public final class ChatterSerializer implements JsonSerializer<Chatter>, JsonDeserializer<Chatter> {

    private final ChatterRepository chatters;

    public ChatterSerializer(ChatterRepository chatters) {
        this.chatters = chatters;
    }

    @Override
    public JsonElement serialize(Chatter src, Type typeOfSrc, JsonSerializationContext context) {
        return JObject.json()
            .add("identity", context.serialize(src.identity()))
            .add("active_channel", src.activeChannel().map(Channel::key).orElse(null))
            .add("channels", context.serialize(src.channels().stream().map(Channel::key).toList()))
            .create();
    }

    @Override
    public Chatter deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = json.getAsJsonObject();
        final Identity identity = context.deserialize(object.get("identity"), Identity.class);
        final Chatter chatter = chatters.findOrCreate(identity.uniqueId(), uuid -> Chatter.chatter(identity))
            .activeChannel(context.deserialize(object.get("active_channel"), Channel.class));
        for (final JsonElement element : object.getAsJsonArray("channels")) {
            Channel channel = context.deserialize(element, Channel.class);
            if (channel != null)
                chatter.join(channel);
        }
        return chatter;
    }
}
