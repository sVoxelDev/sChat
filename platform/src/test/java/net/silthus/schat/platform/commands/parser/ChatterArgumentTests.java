package net.silthus.schat.platform.commands.parser;

import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import java.util.UUID;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.chatter.ChatterRepository;
import net.silthus.schat.platform.commands.ParserTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.chatter.ChatterRepository.createInMemoryChatterRepository;

class ChatterArgumentTests extends ParserTest<Chatter> {

    private ChatterRepository chatterRepository;

    @BeforeEach
    void setUp() {
        chatterRepository = createInMemoryChatterRepository();
        setParser(new ChatterArgument(chatterRepository));
    }

    @Test
    void given_no_input_throws() {
        assertParseFailure(NoInputProvidedException.class);
        assertParseFailure(NoInputProvidedException.class, "   ");
    }

    @Test
    void given_unknown_chatter_id_throws_not_found() {
        assertParseFailure(ChatterArgument.ChatterParseException.class, UUID.randomUUID().toString());
    }

    @Test
    void given_known_id_returns_chatter() {
        final ChatterMock chatter = randomChatter();
        chatterRepository.add(chatter);
        assertParseSuccessful(chatter.uniqueId().toString(), chatter);
    }

    @Test
    void given_unknown_name_throws_not_found() {
        assertParseFailure(ChatterArgument.ChatterParseException.class, "Bob");
    }

    @Test
    void given_valid_name_returns_chatter() {
        ChatterMock chatter = randomChatter();
        chatterRepository.add(chatter);
        assertParseSuccessful(chatter.name(), chatter);
    }
}
