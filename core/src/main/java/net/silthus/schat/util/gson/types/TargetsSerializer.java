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

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.UUID;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterRepository;
import net.silthus.schat.message.Targets;

import static net.silthus.schat.message.MessageTarget.IS_CHATTER;
import static net.silthus.schat.util.UUIDUtil.isUuid;

public final class TargetsSerializer implements JsonSerializer<Targets>, JsonDeserializer<Targets> {

    public static final Type TARGETS_TYPE = Targets.class;

    private final ChatterRepository chatterRepository;

    public TargetsSerializer(ChatterRepository chatterRepository) {
        this.chatterRepository = chatterRepository;
    }

    @Override
    public JsonElement serialize(Targets targets, Type type, JsonSerializationContext jsonSerializationContext) {
        final JsonArray elements = new JsonArray();
        targets.stream()
            .filter(IS_CHATTER)
            .map(target -> (Chatter) target)
            .forEach(target -> elements.add(target.uniqueId().toString()));
        return elements;
    }

    @Override
    public Targets deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        final Targets targets = new Targets();
        for (final JsonElement element : jsonElement.getAsJsonArray())
            if (isUuid(element.getAsString()))
                chatterRepository.find(UUID.fromString(element.getAsString())).ifPresent(targets::add);

        return targets;
    }
}
