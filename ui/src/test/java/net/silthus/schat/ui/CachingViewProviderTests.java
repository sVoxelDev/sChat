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
package net.silthus.schat.ui;

import net.silthus.schat.chatter.ChatterMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.AssertionHelper.assertNPE;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.ui.ViewProvider.cachingViewProvider;
import static org.assertj.core.api.Assertions.assertThat;

class CachingViewProviderTests {
    private ViewProvider viewProvider;

    @BeforeEach
    void setUp() {
        viewProvider = cachingViewProvider(chatter -> View.empty());
    }

    @Nested class getView {
        @Test
        @SuppressWarnings("ConstantConditions")
        void given_null_throws_npe() {
            assertNPE(() -> viewProvider.view(null));
        }

        @Nested class given_valid_chatter {
            private ChatterMock chatter;

            @BeforeEach
            void setUp() {
                chatter = randomChatter();
            }

            @Test
            void then_returns_chatter_view() {
                final View view = viewProvider.view(chatter);
                assertThat(view).isNotNull();
            }

            @Test
            void then_view_is_cached() {
                final View view = viewProvider.view(chatter);
                assertThat(view).isSameAs(viewProvider.view(chatter));
            }
        }
    }
}
