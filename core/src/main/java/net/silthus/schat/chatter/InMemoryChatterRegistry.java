package net.silthus.schat.chatter;

import java.util.UUID;
import net.silthus.schat.repository.InMemoryRepository;

final class InMemoryChatterRegistry extends InMemoryRepository<UUID, Chatter> implements ChatterRegistry {

}
