package net.silthus.schat.chatter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.NonNull;

final class ChatterProviderImpl implements ChatterProvider {

    static final ChatterProvider NIL = new NilChatterProvider();

    private final ChatterFactory factory;
    private final Map<UUID, Chatter> chatters = new HashMap<>();

    ChatterProviderImpl(ChatterFactory factory) {
        this.factory = factory;
    }

    @Override
    public Chatter get(@NonNull UUID id) {
        return chatters.computeIfAbsent(id, uuid -> factory.createChatter(id));
    }

    static final class NilChatterProvider implements ChatterProvider {

        @Override
        public Chatter get(@NonNull UUID id) {
            return Chatter.empty();
        }
    }
}
