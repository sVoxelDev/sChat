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
import com.google.gson.GsonBuilder;
import java.lang.reflect.Type;
import java.time.Instant;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterRepository;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.MessageSource;
import net.silthus.schat.message.Targets;
import net.silthus.schat.pointer.Settings;
import net.silthus.schat.util.gson.serializers.ChannelSerializer;
import net.silthus.schat.util.gson.serializers.ChatterSerializer;
import net.silthus.schat.util.gson.serializers.ComponentSerializer;
import net.silthus.schat.util.gson.serializers.IdentitySerializer;
import net.silthus.schat.util.gson.serializers.InstantSerializer;
import net.silthus.schat.util.gson.serializers.MessageSerializer;
import net.silthus.schat.util.gson.serializers.MessageSourceSerializer;
import net.silthus.schat.util.gson.serializers.SettingsSerializer;
import net.silthus.schat.util.gson.serializers.TargetsSerializer;

@Getter
@Accessors(fluent = true)
public final class GsonProvider {

    public static GsonProvider gsonProvider() {
        return new GsonProvider();
    }

    private final GsonBuilder gson = new GsonBuilder()
        .disableHtmlEscaping()
        .registerTypeAdapter(Instant.class, new InstantSerializer())
        .registerTypeHierarchyAdapter(Component.class, new ComponentSerializer())
        .registerTypeHierarchyAdapter(Message.class, new MessageSerializer())
        .registerTypeHierarchyAdapter(Settings.class, new SettingsSerializer())
        .registerTypeHierarchyAdapter(Identity.class, new IdentitySerializer());

    private final GsonBuilder prettyPrinting = gson.setPrettyPrinting();

    private GsonProvider() {
    }

    public GsonProvider registerTargetsSerializer(ChatterRepository chatters) {
        gson.registerTypeAdapter(Targets.class, new TargetsSerializer(chatters));
        return this;
    }

    public GsonProvider registerChatterSerializer(ChatterRepository chatters) {
        gson.registerTypeHierarchyAdapter(Chatter.class, new ChatterSerializer(chatters));
        return this;
    }

    public GsonProvider registerChannelSerializer(ChannelRepository channelRepository) {
        gson.registerTypeHierarchyAdapter(Channel.class, new ChannelSerializer(channelRepository));
        return this;
    }

    public GsonProvider registerMessageSourceSerializer(ChatterRepository chatters) {
        gson.registerTypeHierarchyAdapter(MessageSource.class, new MessageSourceSerializer(chatters));
        return this;
    }

    public Gson normalGson() {
        return gson.create();
    }

    public Gson prettyGson() {
        return prettyPrinting.create();
    }

    public void registerTypeAdapter(Type type, @NonNull Object adapter) {
        gson.registerTypeAdapter(type, adapter);
    }
}
