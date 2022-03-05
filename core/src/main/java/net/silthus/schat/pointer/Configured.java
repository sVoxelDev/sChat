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
    default @NotNull Pointers pointers() {
        return settings();
    }

    /**
     * Gets the value of {@code setting} or the setting's default value if the value is not present.
     *
     * @param setting the setting
     * @param <V>     the type
     * @return the value
     * @since next
     */
    default <V> @UnknownNullability V get(final @NonNull Setting<V> setting) {
        return this.settings().get(setting);
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
    @SuppressWarnings("checkstyle:MethodName")
    default <V> @UnknownNullability V getOrDefault(final @NonNull Setting<V> setting, final @Nullable V defaultValue) {
        return this.settings().getOrDefault(setting, defaultValue);
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
    @SuppressWarnings("checkstyle:MethodName")
    default <V> @UnknownNullability V getOrDefaultFrom(final @NonNull Setting<V> setting, final @NonNull Supplier<? extends V> defaultValue) {
        return this.settings().getOrDefaultFrom(setting, defaultValue);
    }

    /**
     * Checks if the given boolean setting is set and resolves to {@code true}.
     *
     * @param setting the setting to test
     * @return true if the setting is configured as true
     */
    default boolean is(Setting<Boolean> setting) {
        return get(setting);
    }

    /**
     * Checks if the given boolean setting is not configured or resolves to {@code false}.
     *
     * @param setting the setting to test
     * @return true if the setting does not exist or resolves to false
     */
    default boolean isNot(Setting<Boolean> setting) {
        return !is(setting);
    }

    /**
     * Gets the settings for this object.
     *
     * @return the settings
     * @since next
     */
    default @NotNull Settings settings() {
        return Settings.createSettings();
    }

    /**
     * The builder for creating a configured class.
     *
     * @param <T> the type of the builder
     * @since next
     */
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

        /**
         * Sets the settings container to use.
         *
         * @param settings the settings to use
         * @return this builder
         * @since next
         */
        @NotNull T settings(@NonNull Settings settings);
    }
}
