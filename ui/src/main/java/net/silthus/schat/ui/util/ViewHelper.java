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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.silthus.schat.message.Message;
import net.silthus.schat.ui.format.Format;
import net.silthus.schat.ui.format.MiniMessageFormat;
import net.silthus.schat.ui.placeholder.ReplacementProvider;
import net.silthus.schat.ui.view.View;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;

public final class ViewHelper {

    private static final Map<Integer, Character> numberMap = Map.of(
        0, '₀',
        1, '₁',
        2, '₂',
        3, '₃',
        4, '₄',
        5, '₅',
        6, '₆',
        7, '₇',
        8, '₈',
        9, '₉'
    );

    public static String subscriptOf(int number) {
        StringBuilder str = new StringBuilder();
        while (number > 0) {
            str.insert(0, numberMap.get(number % 10));
            number = number / 10;
        }
        return str.toString();
    }

    public static Component renderBlankLines(int amount) {
        final TextComponent.Builder builder = text();
        for (int i = 0; i < amount; i++) {
            builder.append(newline());
        }
        return builder.build();
    }

    public static Component formatMessage(View view, Message message, Format defaultFormat) {
        final String replacedFormat = message.get(ReplacementProvider.REPLACED_MESSAGE_FORMAT);
        if (replacedFormat != null)
            return new MiniMessageFormat(replacedFormat).format(view, message);
        else
            return defaultFormat.format(view, message);
    }

    private ViewHelper() {
    }
}
