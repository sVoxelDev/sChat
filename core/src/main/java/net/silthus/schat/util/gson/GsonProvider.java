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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import net.silthus.schat.util.gson.types.InstantSerializer;

import static net.silthus.schat.util.gson.types.InstantSerializer.INSTANT_TYPE;

public final class GsonProvider {

    private static final GsonBuilder BASE = new GsonBuilder()
        .disableHtmlEscaping()
        .registerTypeAdapter(INSTANT_TYPE, new InstantSerializer());
    private static final Gson NORMAL = BASE.create();
    private static final Gson PRETTY_PRINTING = BASE.setPrettyPrinting().create();
    private static final JsonParser NORMAL_PARSER = new JsonParser();

    private static final GsonSerializer SERIALIZER = new GsonSerializer(BASE);

    public static Gson normalGson() {
        return NORMAL;
    }

    public static Gson prettyGson() {
        return PRETTY_PRINTING;
    }

    public static JsonParser gsonParser() {
        return NORMAL_PARSER;
    }

    public static GsonSerializer gsonSerializer() {
        return SERIALIZER;
    }

    private GsonProvider() {
        throw new AssertionError();
    }
}
