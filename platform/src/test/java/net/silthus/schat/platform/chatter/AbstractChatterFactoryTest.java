package net.silthus.schat.platform.chatter;

import java.util.UUID;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.pointer.Pointer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.pointer.Pointer.pointer;
import static org.assertj.core.api.Assertions.assertThat;

class AbstractChatterFactoryTest {

    public static final @NotNull Pointer<String> POINTER = pointer(String.class, "test");
    private ChatterFactoryStub factory;

    @BeforeEach
    void setUp() {
        factory = new ChatterFactoryStub();
    }

    @Test
    void pointer_added_by_api_gets_added_to_chatter() {
        factory.addPointer((id, pointers) -> pointers.withStatic(POINTER, "TEST"));
        final Chatter chatter = factory.createChatter(UUID.randomUUID());
        assertThat(chatter.get(POINTER))
            .isPresent().get().isEqualTo("TEST");
    }
}