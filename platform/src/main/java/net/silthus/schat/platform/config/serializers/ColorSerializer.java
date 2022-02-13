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

import java.lang.reflect.Type;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import static net.kyori.adventure.text.format.NamedTextColor.WHITE;

public final class ColorSerializer implements TypeSerializer<TextColor> {
    @Override
    public void serialize(Type type, @Nullable TextColor color, ConfigurationNode node) throws SerializationException {
        if (color instanceof NamedTextColor)
            node.set(color.toString());
        else if (color != null)
            node.set(color.asHexString());
    }

    @Override
    public TextColor deserialize(Type type, ConfigurationNode node) {
        final String color = node.getString();
        if (node.isNull() || color == null)
            return WHITE;
        return deserializeColor(color);
    }

    @org.jetbrains.annotations.Nullable
    private TextColor deserializeColor(String colorString) {
        TextColor namedTextColor = NamedTextColor.NAMES.value(colorString);
        if (namedTextColor != null) {
            return namedTextColor;
        } else {
            return TextColor.fromHexString(colorString);
        }
    }
}
