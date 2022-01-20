package net.silthus.schat.chatter;

import java.util.UUID;
import lombok.NonNull;

import static net.silthus.schat.chatter.ChatterMock.randomChatter;

public final class ChatterProviderStub implements ChatterProvider {

    public static ChatterProviderStub chatterProviderStub() {
        return chatterProviderStub(randomChatter());
    }

    public static ChatterProviderStub chatterProviderStub(Chatter chatter) {
        return new ChatterProviderStub(chatter);
    }

    private final Chatter chatter;

    private ChatterProviderStub(Chatter chatter) {
        this.chatter = chatter;
    }

    @Override
    public Chatter get(@NonNull UUID id) {
        return chatter;
    }
}
