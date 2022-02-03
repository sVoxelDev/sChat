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

package net.silthus.schat.pointer;

import java.util.function.Supplier;
import net.silthus.schat.AssertionHelper;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class PointersTest {
    @Test
    public void ofPointers() {
        final Pointer<String> pointer = Pointer.pointer(String.class, "test");

        assertFalse(Pointers.empty().supports(pointer));

        final Pointers p0 = Pointers.pointers()
            .withStatic(pointer, null)
            .create();
        assertTrue(p0.supports(pointer));
        assertFalse(p0.get(pointer).isPresent());

        final Pointers p1 = Pointers.pointers()
            .withStatic(pointer, "test")
            .create();
        assertTrue(p1.supports(pointer));
        assertTrue(p1.get(pointer).isPresent());
        assertEquals("test", p1.get(pointer).get());
        assertEquals("test", p1.get(pointer).get()); // make sure the value doesn't change

        final StringBuilder s = new StringBuilder("test");
        final Supplier<String> supplier = () -> {
            final String result = s.toString();
            s.reverse();
            return result;
        };
        final Pointers p2 = Pointers.pointers()
            .withDynamic(pointer, supplier)
            .create();
        assertTrue(p2.supports(pointer));
        assertEquals("test", p2.getOrDefault(pointer, null));
        assertEquals("tset", p2.getOrDefault(pointer, null)); // make sure the value does change
    }

    @Nested
    class given_empty_pointers {
        private @NotNull Pointers pointers;
        private @NotNull Pointer<String> pointer;

        @BeforeEach
        void setUp() {
            pointers = Pointers.empty();
            pointer = Pointer.pointer(String.class, "test");
        }

        @Test
        void then_always_returns_empty() {
            assertThat(pointers.get(pointer)).isEmpty();
            assertThat(pointers.getPointers()).isEmpty();
            assertThat(pointers.supports(pointer)).isFalse();
            AssertionHelper.assertUnmodifiable(pointers.getPointers(), () -> pointer);
        }
    }
}
