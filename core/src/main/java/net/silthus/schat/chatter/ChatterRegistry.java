package net.silthus.schat.chatter;

import java.util.UUID;
import net.silthus.schat.repository.Repository;

public interface ChatterRegistry extends Repository<UUID, Chatter> {

    static ChatterRegistry createInMemoryRegistry() {
        return new InMemoryChatterRegistry();
    }
}
