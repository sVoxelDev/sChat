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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.message.MessageHelper.randomMessage;
import static org.assertj.core.api.Assertions.assertThat;

@Nested
class ReplacementsTests {

    private Replacements replacements;

    @BeforeEach
    void setUp() {
        replacements = new Replacements();
        replacements.addMessageReplacement(message -> TextReplacementConfig.builder()
                .match("test")
                .replacement("success")
                .build());
    }

    @Test
    void replaces_message_text_after_format() {
        Component result = replacements.applyTo(randomMessage(), text("test"));

        assertThat(result).isEqualTo(text("success"));
    }

    @Test
    void null_replacements_are_ignored() {
        replacements.addMessageReplacement(message -> null);
        Component result = replacements.applyTo(randomMessage(), text("foo"));

        assertThat(result).isNotNull().isEqualTo(text("foo"));
    }
}
