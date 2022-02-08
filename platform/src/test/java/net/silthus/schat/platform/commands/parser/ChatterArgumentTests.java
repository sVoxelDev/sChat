/*
 * This file is part of sChat, licensed under the MIT License.
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

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
