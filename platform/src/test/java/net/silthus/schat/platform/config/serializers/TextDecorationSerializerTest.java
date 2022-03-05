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
package net.silthus.schat.platform.config.serializers;

import lombok.SneakyThrows;
import net.kyori.adventure.text.format.TextDecoration;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.spongepowered.configurate.BasicConfigurationNode;

import static net.kyori.adventure.text.format.TextDecoration.BOLD;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;
import static net.kyori.adventure.text.format.TextDecoration.OBFUSCATED;
import static net.kyori.adventure.text.format.TextDecoration.STRIKETHROUGH;
import static net.kyori.adventure.text.format.TextDecoration.UNDERLINED;
import static org.assertj.core.api.Assertions.assertThat;

class TextDecorationSerializerTest {

    private TextDecorationSerializer serializer;
    private BasicConfigurationNode node;

    @BeforeEach
    void setUp() {
        serializer = new TextDecorationSerializer();
        node = BasicConfigurationNode.root();
    }

    @Nested class serialize {
        @SneakyThrows
        private void assertSerializedDecoration(@Nullable TextDecoration decoration, String expected) {
            serializer.serialize(TextDecoration.class, decoration, node);
            assertThat(node.getString()).isEqualTo(expected);
        }

        @Test
        void null_is_serialized_as_null() {
            assertSerializedDecoration(null, null);
        }

        @Test
        void serializes_text_decorations() {
            assertSerializedDecoration(UNDERLINED, "underlined");
            assertSerializedDecoration(BOLD, "bold");
            assertSerializedDecoration(ITALIC, "italic");
            assertSerializedDecoration(OBFUSCATED, "obfuscated");
            assertSerializedDecoration(STRIKETHROUGH, "strikethrough");
        }
    }

    @Nested class deserialize {
        @SneakyThrows
        private void assertDeserializedDecoration(String decoration, @Nullable TextDecoration expected) {
            node.set(decoration);
            final TextDecoration result = serializer.deserialize(TextDecoration.class, node);
            assertThat(result).isEqualTo(expected);
        }

        @Test
        void null_is_deserialized_as_null() {
            assertDeserializedDecoration(null, null);
        }

        @Test
        void deserializes_decorations() {
            assertDeserializedDecoration("underlined", UNDERLINED);
            assertDeserializedDecoration("bold", BOLD);
            assertDeserializedDecoration("italic", ITALIC);
            assertDeserializedDecoration("obfuscated", OBFUSCATED);
            assertDeserializedDecoration("strikethrough", STRIKETHROUGH);
        }
    }
}
