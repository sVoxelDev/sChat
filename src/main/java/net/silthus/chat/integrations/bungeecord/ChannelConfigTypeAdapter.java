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

package net.silthus.chat.integrations.bungeecord;

import com.google.gson.*;
import net.silthus.chat.Format;
import net.silthus.chat.Scopes;
import net.silthus.chat.config.ChannelConfig;
import net.silthus.chat.formats.MiniMessageFormat;

import java.lang.reflect.Type;

public class ChannelConfigTypeAdapter implements JsonDeserializer<ChannelConfig>, JsonSerializer<ChannelConfig> {

    @Override
    public ChannelConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = json.getAsJsonObject();
        final JsonElement name = object.get("name");
        return ChannelConfig.builder()
                .name(name == null ? null : (name.isJsonNull() ? null : name.getAsString()))
                .protect(object.get("protect").getAsBoolean())
                .autoJoin(object.get("auto_join").getAsBoolean())
                .canLeave(object.get("can_leave").getAsBoolean())
                .sendToConsole(object.get("console").getAsBoolean())
                .scope(Scopes.scope(object.get("scope").getAsString()))
                .format(Format.miniMessage(object.get("format").getAsString()))
                .build();
    }

    @Override
    public JsonElement serialize(ChannelConfig src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject object = new JsonObject();
        object.addProperty("name", src.name());
        object.addProperty("protect", src.protect());
        object.addProperty("auto_join", src.autoJoin());
        object.addProperty("can_leave", src.canLeave());
        object.addProperty("console", src.sendToConsole());
        object.addProperty("scope", Scopes.name(src.scope().getClass()));
        object.addProperty("format", src.format() instanceof MiniMessageFormat ? ((MiniMessageFormat) src.format()).getFormat() : null);
        return object;
    }
}
