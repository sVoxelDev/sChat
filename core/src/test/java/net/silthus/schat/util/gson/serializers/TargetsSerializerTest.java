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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.Arrays;
import java.util.UUID;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.chatter.ChatterRepository;
import net.silthus.schat.message.MessageTarget;
import net.silthus.schat.message.Targets;
import net.silthus.schat.util.gson.GsonProvider;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.UUID.randomUUID;
import static net.silthus.schat.chatter.ChatterRepository.createInMemoryChatterRepository;
import static org.assertj.core.api.Assertions.assertThat;

class TargetsSerializerTest {

    private Gson gson;
    private ChatterRepository chatterRepository;

    @BeforeEach
    void setUp() {
        chatterRepository = createInMemoryChatterRepository();
        gson = GsonProvider.gsonProvider()
            .registerChatterSerializer(chatterRepository)
            .prettyGson();
    }

    @NotNull
    private Targets deserialize(JsonArray elements) {
        return gson.fromJson(elements, Targets.class);
    }

    @NotNull
    private JsonElement serialize(Chatter... chatter) {
        return gson.toJsonTree(Targets.of(chatter));
    }

    @NotNull
    private ChatterMock randomChatter() {
        final ChatterMock chatter = ChatterMock.randomChatter();
        chatterRepository.add(chatter);
        return chatter;
    }

    private void assertSerializedJsonContains(Chatter... chatters) {
        final JsonElement json = serialize(chatters);
        assertThat(json.isJsonArray()).isTrue();
        assertThat((JsonArray) json).containsAll(Arrays.stream(chatters)
            .map(Chatter::uniqueId)
            .map(Object::toString)
            .map(s -> "chatter:" + s)
            .map(JsonPrimitive::new)
            .toList()
        );
    }

    private void addChatter(JsonArray elements, UUID id) {
        elements.add("chatter:" + id);
    }

    private JsonArray listOfChatters(UUID... uuids) {
        final JsonArray elements = new JsonArray();
        for (UUID id : uuids)
            addChatter(elements, id);
        return elements;
    }

    private JsonArray listOf(MessageTarget... targets) {
        final JsonArray elements = new JsonArray();
        for (MessageTarget target : targets)
            if (target instanceof Chatter chatter)
                addChatter(elements, chatter.uniqueId());
        return elements;
    }

    @Test
    void empty_serialize_creates_empty_list() {
        assertSerializedJsonContains();
    }

    @Test
    void serialize_creates_list_of_target_ids() {
        assertSerializedJsonContains(randomChatter());
    }

    @Test
    void deserialize_empty_list_of_target_ids_returns_empty_targets() {
        assertThat(deserialize(new JsonArray())).isNotNull().isEmpty();
    }

    @Test
    void deserialize_list_of_chatter_ids_given_no_chatter_in_repository_ignores_chatters() {
        final JsonArray elements = listOfChatters(
            randomUUID(),
            randomUUID()
        );
        assertThat(deserialize(elements)).isNotNull().isEmpty();
    }

    @Test
    void deserialize_list_of_chatter_ids_uses_chatters_from_repository() {
        final ChatterMock chatter = randomChatter();
        assertThat(deserialize(listOf(chatter))).contains(chatter);
    }
}
