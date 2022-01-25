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
import org.jetbrains.annotations.UnknownNullability;

/**
 * Something that can retrieve a {@link Setting} from {@link Settings}.
 *
 * @since next
 */
public interface Configured extends Pointered {

    @Override
    default @NotNull Pointers getPointers() {
        return Pointered.super.getPointers();
    }

    /**
     * Gets the value of {@code setting} or the setting's default value if the value is not present.
     *
     * @param setting the setting
     * @param <V>     the type
     * @return the value
     * @since next
     */
    default <V> @NotNull V get(final @NonNull Setting<V> setting) {
        return this.getSettings().get(setting);
    }

    /**
     * Gets the value of {@code setting}.
     *
     * <p>If this configured object is unable to provide a value for {@code setting}, the {@code defaultValue} will be returned.</p>
     *
     * @param setting      the setting
     * @param defaultValue the default value
     * @param <V>          the type
     * @return the value
     * @since next
     */
    default <V> @UnknownNullability V getOrDefault(final @NonNull Setting<V> setting, final @Nullable V defaultValue) {
        return this.getSettings().getOrDefault(setting, defaultValue);
    }

    /**
     * Gets the value of {@code setting}.
     *
     * <p>If this configured object is unable to provide a value for {@code setting}, the value supplied by {@code defaultValue} will be returned.</p>
     *
     * @param setting      the setting
     * @param defaultValue the default value supplier
     * @param <V>          the type
     * @return the value
     * @since next
     */
    default <V> @UnknownNullability V getOrDefaultFrom(final @NonNull Setting<V> setting, final @NonNull Supplier<? extends V> defaultValue) {
        return this.getSettings().getOrDefaultFrom(setting, defaultValue);
    }

    /**
     * Gets the settings for this object.
     *
     * @return the settings
     * @since next
     */
    default @NotNull Settings getSettings() {
        return Settings.createSettings();
    }

    interface Builder<T> {

        /**
         * Sets a setting of the configured type to the given value.
         *
         * @param setting the setting
         * @param value   the value of the setting
         * @param <V>     the type of the setting
         * @return this builder
         * @since next
         */
        <V> @NotNull T set(@NonNull Setting<V> setting, @Nullable V value);

        @NotNull T settings(@NonNull Settings settings);
    }

    interface Modifiable<T> extends Configured {
        /**
         * Sets a setting of the configured type to the given value.
         *
         * @param setting the setting
         * @param value   the value of the setting
         * @param <V>     the type of the setting
         * @return this builder
         * @since next
         */
        @SuppressWarnings("unchecked")
        default <V> @NotNull T set(@NonNull Setting<V> setting, @Nullable V value) {
            final T type = (T) this;
            getSettings().set(setting, value);
            return type;
        }
    }
}
