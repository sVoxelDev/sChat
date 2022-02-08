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
