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
package net.silthus.schat.ui.placeholder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.silthus.schat.message.Message;

public class Replacements {

    private final List<Function<Message, TextReplacementConfig>> replacements = new ArrayList<>();

    public void addMessageReplacement(Function<Message, TextReplacementConfig> replacement) {
        replacements.add(replacement);
    }

    public Component applyTo(final Message message, final Component formatted) {
        Component result = formatted;
        for (TextReplacementConfig replacement : replacements.stream()
            .map(fn -> fn.apply(message))
            .filter(Objects::nonNull)
            .toList()) {
            result = result.replaceText(replacement);
        }
        return result;
    }
}
