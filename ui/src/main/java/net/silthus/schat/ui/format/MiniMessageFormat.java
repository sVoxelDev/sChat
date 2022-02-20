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

import java.util.ArrayList;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.silthus.schat.pointer.Pointer;
import net.silthus.schat.pointer.Pointered;
import net.silthus.schat.ui.View;
import org.jetbrains.annotations.NotNull;

@Getter
@Accessors(fluent = true)
public class MiniMessageFormat implements Format {
    private final MiniMessage formatter = MiniMessage.miniMessage();
    private final String format;

    public MiniMessageFormat(String format) {
        this.format = format;
    }

    @Override
    public Component format(View view, Pointered type) {
        return formatter.deserialize(format, TagResolver.standard(), resolvePlaceholders(type, ""));
    }

    private TagResolver resolvePlaceholders(Pointered type, String path) {
        ArrayList<TagResolver> resolvers = new ArrayList<>();
        for (Pointer<?> pointer : type.pointers().pointers()) {
            resolvers.add(type.get(pointer)
                .map(value -> placeholder(path + pointer.key(), value))
                .orElse(TagResolver.empty()));
        }
        return TagResolver.resolver(resolvers);
    }

    private TagResolver placeholder(@NotNull String key, Object value) {
        if (value instanceof String)
            return Placeholder.parsed(key, (String) value);
        if (value instanceof Component)
            return Placeholder.component(key, (Component) value);
        if (value instanceof Pointered)
            return resolvePlaceholders((Pointered) value, key + ".");
        return TagResolver.empty();
    }
}
