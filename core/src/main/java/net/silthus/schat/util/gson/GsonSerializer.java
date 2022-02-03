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

package net.silthus.schat.util.gson;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.NonNull;
import net.silthus.schat.messaging.PluginMessage;
import net.silthus.schat.messaging.PluginMessageSerializer;
import net.silthus.schat.messaging.SerializationType;
import org.jetbrains.annotations.NotNull;

public final class GsonSerializer implements PluginMessageSerializer {

    final GsonBuilder gson;
    final Map<String, Type> typeMap = new HashMap<String, Type>();

    public GsonSerializer(GsonBuilder base) {
        this.gson = base;
    }

    @Override
    public SerializationType[] supportedTypes() {
        return new SerializationType[] {SerializationType.GSON};
    }

    @Override
    public void registerTypeAdapter(Type type, Object adapter) throws IllegalArgumentException {
        gson.registerTypeAdapter(type, adapter);
        typeMap.put(PluginMessage.parseTypeName(type), type);
    }

    @Override
    public @NotNull String encode(PluginMessage.Type pluginMessage) {
        return GsonProvider.normalGson().toJson(new JObject()
            .add("id", pluginMessage.id().toString())
            .add("type", pluginMessage.type())
            .add("content", serialize(pluginMessage.content()))
            .toJson());
    }

    private <T extends PluginMessage> JsonElement serialize(T content) {
        return gson.create().toJsonTree(content);
    }

    @Override
    public @NotNull PluginMessage.Type decode(@NonNull String encodedString) {
        final JsonObject json = GsonProvider.normalGson().fromJson(encodedString, JsonObject.class);
        final String type = json.get("type").getAsString();
        return new PluginMessage.Type(UUID.fromString(json.get("id").getAsString()), type, deserialize(type, json.get("content")));
    }

    private PluginMessage deserialize(String type, JsonElement content) {
        return gson.create().fromJson(content, typeMap.get(type));
    }
}
