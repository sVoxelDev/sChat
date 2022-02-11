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

package net.silthus.schat.messenger;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;
import net.silthus.schat.util.gson.GsonProvider;
import org.jetbrains.annotations.NotNull;

public final class GsonPluginMessageSerializer implements PluginMessageSerializer {

    private final GsonProvider gsonProvider;
    private final Map<String, Type> typeMap = new HashMap<>();

    GsonPluginMessageSerializer(GsonProvider gsonProvider) {
        this.gsonProvider = gsonProvider;
    }

    @Override
    public void registerMessageType(Type type) {
        typeMap.put(type.getTypeName(), type);
    }

    @Override
    public void registerTypeAdapter(Type type, Object adapter) {
        gsonProvider.registerTypeAdapter(type, adapter);
    }

    @Override
    public boolean supports(PluginMessage message) {
        return typeMap.containsKey(message.getClass().getTypeName());
    }

    @Override
    public @NotNull String encode(PluginMessage pluginMessage) {
        final Gson gson = this.gsonProvider.normalGson();
        final JsonObject json = gson.toJsonTree(pluginMessage).getAsJsonObject();
        json.addProperty("type", pluginMessage.getClass().getTypeName());
        return gson.toJson(json);
    }

    @Override
    public @NotNull PluginMessage decode(@NonNull String encodedString) {
        final Gson gson = this.gsonProvider.normalGson();
        final JsonObject json = gson.fromJson(encodedString, JsonObject.class);
        final String type = json.get("type").getAsString();
        return gson.fromJson(json, typeMap.get(type));
    }
}
