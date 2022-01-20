package net.silthus.schat.chatter;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static java.util.UUID.randomUUID;
import static net.silthus.schat.AssertionHelper.assertNPE;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static org.assertj.core.api.Assertions.assertThat;

class ChatterProviderTest {
    private ChatterProviderImpl provider;

    @BeforeEach
    void setUp() {
        provider = new ChatterProviderImpl(id -> randomChatter());
    }

    @Nested class get {
        @SuppressWarnings("ConstantConditions")
        @Test void given_null_throws_npe() {
            assertNPE(() -> provider.get(null));
        }

        @Nested class given_valid_identity {
            @Test void then_chatter_is_cached() {
                final UUID id = randomUUID();
                final Chatter chatter = provider.get(id);
                assertThat(chatter).isSameAs(provider.get(id));
            }
        }
    }
}
