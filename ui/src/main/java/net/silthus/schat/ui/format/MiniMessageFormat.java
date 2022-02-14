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

package net.silthus.schat.ui.format;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.placeholder.Replacement;
import net.silthus.schat.pointer.Pointer;
import net.silthus.schat.pointer.Pointered;
import org.jetbrains.annotations.Nullable;

import static net.kyori.adventure.text.minimessage.placeholder.PlaceholderResolver.dynamic;

@Getter
@Accessors(fluent = true)
public class MiniMessageFormat implements PointeredFormat {
    private final MiniMessage formatter = MiniMessage.miniMessage();
    private final String format;

    public MiniMessageFormat(String format) {
        this.format = format;
    }

    @Override
    public Component format(Pointered type) {
        return formatter.deserialize(format, dynamic(replacement -> resolveReplacements(replacement, type)));
    }

    private @Nullable Replacement<?> resolveReplacements(String replacement, Pointered type) {
        final String key = replacement.split("\\.")[0];
        for (final Pointer<?> pointer : type.pointers().pointers()) {
            if (pointer.key().equalsIgnoreCase(key)) {
                final Object value = type.getOrDefault(pointer, null);
                if (value != null)
                    return resolveReplacement(value, replacement);
            }
        }
        return null;
    }

    private @Nullable Replacement<?> resolveReplacement(Object value, String path) {
        if (value instanceof String str)
            return Replacement.miniMessage(str);
        else if (value instanceof Component component)
            return Replacement.component(component);
        else if (value instanceof Pointered subtype)
            if (path.indexOf('.') + 1 < path.length())
                return resolveReplacements(path.substring(path.indexOf('.') + 1), subtype);
        return null;
    }
}
