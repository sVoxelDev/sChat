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
package net.silthus.schat.util.gson.serializers;

import com.google.gson.Gson;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.chatter.ChatterRepository;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.message.MessageSource;
import net.silthus.schat.util.gson.GsonProviderStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessageSourceSerializerTest {

    private final ChatterRepository chatterRepository = ChatterRepository.createInMemoryChatterRepository();
    private Gson gson;

    @BeforeEach
    void setUp() {
        gson = GsonProviderStub.gsonProviderStub(
            chatterRepository,
            ChannelRepository.createInMemoryChannelRepository(EventBus.eventBus())
        ).prettyGson();
    }

    @Test
    void deserializes_chatter_source_as_id() {
        final ChatterMock chatter = ChatterMock.randomChatter();
        final String json = gson.toJson(chatter, MessageSource.class);
        assertThat(json).isEqualTo("\"chatter:%s\"".formatted(chatter.uniqueId()));
    }

    @Test
    void serializes_chatter_by_id() {
        final ChatterMock chatter = ChatterMock.randomChatter();
        chatterRepository.add(chatter);
        final MessageSource messageSource = gson.fromJson("\"chatter:%s\"".formatted(chatter.uniqueId()), MessageSource.class);
        assertThat(messageSource).isSameAs(chatter);
    }
}