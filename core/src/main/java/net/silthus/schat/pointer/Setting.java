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
package net.silthus.schat.pointer;

import java.util.function.Supplier;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A setting of a resource with an optional default value.
 *
 * @param <V> the type of the value
 * @since 1.0.0-alpha.4
 */
public sealed interface Setting<V> extends Pointer<V> permits SettingImpl {

    /**
     * Creates a new setting for the given type and key with the given default value.
     *
     * @param type         the type of the setting value
     * @param key          the key of the setting
     * @param defaultValue the default value to use if the setting is not set
     * @param <V>          the type of the value
     * @return the setting
     * @since 1.0.0-alpha.4
     */
    static @NotNull <V> Setting<V> setting(final @NonNull Class<V> type, final @NonNull String key, final @Nullable V defaultValue) {
        return dynamicSetting(type, key, () -> defaultValue);
    }

    /**
     * Creates a new setting for the given type and key with the given dynamic default value supplier.
     *
     * @param type         the type of the setting value
     * @param key          the key of the setting
     * @param defaultValue the default value to use if the setting is not set
     * @param <V>          the type of the value
     * @return the setting
     * @since 1.0.0-alpha.4
     */
    static @NotNull <V> Setting<V> dynamicSetting(final @NonNull Class<V> type, final @NonNull String key, final @NonNull Supplier<@Nullable V> defaultValue) {
        return new SettingImpl<>(type, key, defaultValue);
    }

    /**
     * The default value of the setting that is used if the setting is not set.
     *
     * @return the default value
     * @since 1.0.0-alpha.4
     */
    V defaultValue();
}
