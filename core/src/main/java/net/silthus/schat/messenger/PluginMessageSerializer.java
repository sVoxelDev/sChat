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
package net.silthus.schat.messenger;

import java.lang.reflect.Type;
import lombok.NonNull;
import net.silthus.schat.util.gson.GsonProvider;
import org.jetbrains.annotations.NotNull;

public interface PluginMessageSerializer {

    static GsonPluginMessageSerializer gsonSerializer(GsonProvider gsonProvider) {
        return new GsonPluginMessageSerializer(gsonProvider);
    }

    /**
     * Registers the given message type for serialization and deserialization.
     *
     * <p>Register your custom json serializable types with the {@link GsonProvider#registerTypeAdapter(Type, Object)}.</p>
     *
     * @param type the type
     */
    void registerMessageType(Type type);

    void registerTypeAdapter(Type type, Object adapter);

    boolean supports(PluginMessage message);

    @NotNull String encode(PluginMessage pluginMessage);

    @NotNull PluginMessage decode(@NonNull String encodedString);
}
