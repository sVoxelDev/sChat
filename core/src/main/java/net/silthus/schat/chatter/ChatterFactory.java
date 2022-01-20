package net.silthus.schat.chatter;

import java.util.UUID;

public interface ChatterFactory {
    Chatter createChatter(UUID id);
}
