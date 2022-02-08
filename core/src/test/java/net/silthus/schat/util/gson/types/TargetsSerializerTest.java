package net.silthus.schat.util.gson.types;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.UUID;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.chatter.ChatterRepository;
import net.silthus.schat.message.Targets;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.chatter.ChatterRepository.createInMemoryChatterRepository;
import static org.assertj.core.api.Assertions.assertThat;

class TargetsSerializerTest {

    private static final SerializationContextStub SERIALIZATION_CONTEXT = new SerializationContextStub();
    private static final DeserializationContextStub DESERIALIZATION_CONTEXT = new DeserializationContextStub();

    private TargetsSerializer serializer;
    private ChatterRepository chatterRepository;

    @BeforeEach
    void setUp() {
        chatterRepository = createInMemoryChatterRepository();
        serializer = new TargetsSerializer(chatterRepository);
    }

    @NotNull
    private Targets deserialize(JsonArray elements) {
        return serializer.deserialize(elements, Targets.class, DESERIALIZATION_CONTEXT);
    }

    @NotNull
    private JsonElement serialize(Chatter... chatter) {
        return serializer.serialize(Targets.of(chatter), Targets.class, SERIALIZATION_CONTEXT);
    }

    private void assertSerializedJsonContains(Chatter... chatters) {
        final JsonElement json = serialize(chatters);
        assertThat(json.isJsonArray()).isTrue();
        assertThat((JsonArray) json).containsAll(Arrays.stream(chatters)
            .map(Chatter::uniqueId)
            .map(Object::toString)
            .map(JsonPrimitive::new)
            .toList()
        );
    }

    @Test
    void empty_serialize_creates_empty_list() {
        assertSerializedJsonContains();
    }

    @Test
    void serialize_creates_list_of_target_ids() {
        final ChatterMock chatter = randomChatter();
        assertSerializedJsonContains(chatter);
    }

    @Test
    void deserialize_empty_list_of_target_ids_returns_empty_targets() {
        final JsonArray elements = new JsonArray();
        assertThat(deserialize(elements)).isNotNull().isEmpty();
    }

    @Test
    void deserialize_list_of_chatter_ids_given_no_chatter_in_repository_ignores_chatters() {
        final JsonArray elements = new JsonArray();
        elements.add(UUID.randomUUID().toString());
        elements.add(UUID.randomUUID().toString());
        assertThat(deserialize(elements)).isNotNull().isEmpty();
    }

    @Test
    void deserialize_list_of_chatter_ids_uses_chatters_from_repository() {
        final ChatterMock chatter = randomChatter();
        chatterRepository.add(chatter);
        final JsonArray elements = new JsonArray();
        elements.add(chatter.uniqueId().toString());
        assertThat(deserialize(elements)).contains(chatter);
    }

    private static class SerializationContextStub implements JsonSerializationContext {
        @Override
        public JsonElement serialize(Object o) {
            return new JsonObject();
        }

        @Override
        public JsonElement serialize(Object o, Type type) {
            return new JsonObject();
        }
    }

    private static class DeserializationContextStub implements JsonDeserializationContext {
        @Override
        @SuppressWarnings("unchecked")
        public <T> T deserialize(JsonElement jsonElement, Type type) throws JsonParseException {
            return (T) Targets.of();
        }
    }
}
