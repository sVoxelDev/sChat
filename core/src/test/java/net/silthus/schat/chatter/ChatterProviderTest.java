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

package net.silthus.schat.chatter;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static java.util.UUID.randomUUID;
import static net.silthus.schat.AssertionHelper.assertNPE;
import static org.assertj.core.api.Assertions.assertThat;

class ChatterProviderTest {
    private ChatterProviderImpl provider;

    @BeforeEach
    void setUp() {
        provider = new ChatterProviderImpl(new ChatterFactoryStub());
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
