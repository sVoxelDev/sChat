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
import net.kyori.adventure.text.format.TextColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.spongepowered.configurate.BasicConfigurationNode;

import static net.kyori.adventure.text.format.NamedTextColor.BLACK;
import static net.kyori.adventure.text.format.NamedTextColor.BLUE;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;
import static net.kyori.adventure.text.format.TextColor.color;
import static org.assertj.core.api.Assertions.assertThat;

class ColorSerializerTest {
    private ColorSerializer serializer;
    private BasicConfigurationNode node;

    @BeforeEach
    void setUp() {
        serializer = new ColorSerializer();
        node = BasicConfigurationNode.root();
    }

    @Nested class serialize {
        @SneakyThrows
        private void assertSerializedColor(TextColor color, String expected) {
            serializer.serialize(TextColor.class, color, node);
            assertThat(node.getString()).isEqualTo(expected);
        }

        @Test
        void named_text_color_is_serialized_as_readable_color() {
            assertSerializedColor(RED, "red");
        }

        @Test
        void white_is_serialized_as_white() {
            assertSerializedColor(color(255, 255, 255), "white");
        }

        @Test
        void black_is_serialized_as_black() {
            assertSerializedColor(color(0, 0, 0), "black");
        }

        @Test
        void rgb_color_is_converted_to_hex_color() {
            assertSerializedColor(color(1, 2, 3), "#010203");
        }

        @Test
        void null_is_serialized_as_null() {
            assertSerializedColor(null, null);
        }
    }

    @Nested class deserialize {
        @SneakyThrows
        private void assertDeserializedColor(String color, TextColor expected) {
            node.set(color);
            assertThat(serializer.deserialize(TextColor.class, node)).isEqualTo(expected);
        }

        @Test
        void null_is_deserialized_as_white() {
            assertDeserializedColor(null, WHITE);
        }

        @Test
        void named_text_color_is_deserialized_as_named_color() {
            assertDeserializedColor("red", RED);
            assertDeserializedColor("blue", BLUE);
            assertDeserializedColor("white", WHITE);
            assertDeserializedColor("black", BLACK);
        }

        @Test
        void hex_color_is_deserialized_as_text_color() {
            assertDeserializedColor("#010203", color(1, 2, 3));
            assertDeserializedColor("#abcdef", color(171, 205, 239));
        }
    }
}
