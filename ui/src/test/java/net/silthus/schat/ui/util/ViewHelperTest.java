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
package net.silthus.schat.ui.util;

import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ViewHelperTest {

    private static final Map<Character, Character> subscriptMap = Map.of(
        '0', '₀',
        '1', '₁',
        '2', '₂',
        '3', '₃',
        '4', '₄',
        '5', '₅',
        '6', '₆',
        '7', '₇',
        '8', '₈',
        '9', '₉'
    );

    private void assertSubscript(int number, String expected) {
        final char[] chars = expected.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i] = subscriptMap.get(chars[i]);
        }
        assertThat(ViewHelper.subscriptOf(number)).isEqualTo(String.valueOf(chars));
    }

    @Test
    void subscript_tests() {
        assertSubscript(1, "1");
        assertSubscript(10, "10");
        assertSubscript(11, "11");
        assertSubscript(25, "25");
        assertSubscript(10327032, "10327032");
    }
}
