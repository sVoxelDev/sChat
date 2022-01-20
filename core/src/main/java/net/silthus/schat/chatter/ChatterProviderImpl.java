package net.silthus.schat.chatter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.NonNull;

final class ChatterProviderImpl implements ChatterProvider {

    private final ChatterFactory factory;
    private final Map<UUID, Chatter> chatters = new HashMap<>();

    ChatterProviderImpl(ChatterFactory factory) {
        this.factory = factory;
    }

    @Override
    public Chatter get(@NonNull UUID id) {
        return chatters.computeIfAbsent(id, uuid -> factory.createChatter(id));
    }
}
