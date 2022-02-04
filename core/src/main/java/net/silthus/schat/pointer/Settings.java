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

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.jetbrains.annotations.Unmodifiable;

/**
 * A collection of {@link Setting settings}.
 *
 * @since next
 */
public sealed interface Settings extends Pointers permits SettingsImpl {
    /**
     * Gets an empty settings collection.
     *
     * @return the settings
     * @since next
     */
    static @NotNull Settings createSettings() {
        return settingsBuilder().create();
    }

    /**
     * Gets a new settings builder.
     *
     * @return the builder
     * @see Builder
     * @since next
     */
    static @NotNull Builder settingsBuilder() {
        return new SettingsImpl.BuilderImpl();
    }

    /**
     * Gets all settings in this collection which have the given value type.
     *
     * @param valueType the type class of the setting
     * @param <V>       the type of the setting
     * @return an unmodifiable set of settings filtered by the given type
     * @since next
     */
    @SuppressWarnings("unchecked")
    default <V> @NotNull @Unmodifiable Set<Setting<V>> settings(final @NonNull Class<V> valueType) {
        final Set<Setting<V>> filteredSettings = new HashSet<>();
        for (final Setting<?> setting : this.settings()) {
            if (valueType.isAssignableFrom(setting.type()))
                filteredSettings.add((Setting<V>) setting);
        }
        return Collections.unmodifiableSet(filteredSettings);
    }

    /**
     * Gets all settings in this settings collection.
     *
     * @return an unmodifiable set of settings in this settings collection
     * @throws UnsupportedOperationException if the implementing class does not support querying for settings
     * @since next
     */
    @NotNull @Unmodifiable Set<Setting<?>> settings();

    /**
     * Gets the value of {@code setting}.
     *
     * <p>Will use the {@link Setting#defaultValue()} if the setting does not exist.</p>
     *
     * @param setting the setting
     * @param <V>     the type
     * @return the value
     * @since next
     */
    <V> @UnknownNullability V get(final @NotNull Setting<V> setting);

    /**
     * Gets the value of {@code setting}.
     *
     * <p>If a value for {@code setting} is unable to be provided, {@code defaultValue} will be returned.</p>
     *
     * @param setting      the setting
     * @param defaultValue the default value
     * @param <V>          the type
     * @return the value
     * @since next
     */
    default <V> @Nullable V getOrDefault(final @NotNull Setting<V> setting, final @Nullable V defaultValue) {
        return getOrDefaultFrom(setting, () -> defaultValue);
    }

    /**
     * Gets the value of {@code setting}.
     *
     * <p>If a value for {@code setting} is unable to be provided, the value supplied by {@code defaultValue} will be returned.</p>
     *
     * @param setting      the setting
     * @param defaultValue the default value supplier
     * @param <V>          the type
     * @return the value
     * @since next
     */
    <V> @UnknownNullability V getOrDefaultFrom(final @NotNull Setting<V> setting, final @NotNull Supplier<? extends V> defaultValue);

    /**
     * Sets the value of the setting of the setting.
     *
     * @param setting the setting
     * @param value   the new value of the setting
     * @param <V>     the type of the value
     * @return the old value if replaced
     * @since next
     */
    <V> @NotNull Optional<V> set(@NotNull Setting<V> setting, @Nullable V value);

    @NotNull Builder toBuilder();

    /**
     * A builder of settings.
     *
     * @see Settings
     * @since next
     */
    interface Builder extends Pointers.Builder {

        /**
         * Adds a setting which type is unknown and will be resolved based on the given key.
         *
         * @param key the key of the setting matched against the alias or keys of configured settings
         * @param value the value that will be resolved by the setting
         * @param <V> the type of the value
         * @return this builder
         * @since next
         */
        <V> @NotNull Builder withUnknown(final @NotNull String key, @NotNull Function<Setting<?>, V> value);

        @Override
        default <T> @NotNull Builder withStatic(final @NonNull Pointer<T> pointer, @Nullable final T value) {
            Pointers.Builder.super.withStatic(pointer, value);
            return this;
        }

        @Override
        <T> @NotNull Builder withDynamic(final @NonNull Pointer<T> pointer, @NonNull Supplier<@Nullable T> value);

        @Override
        default <T> @NotNull Builder withForward(final @NonNull Pointer<T> pointer, final @NonNull Pointered target, final @NonNull Pointer<T> targetPointer) {
            Pointers.Builder.super.withForward(pointer, target, targetPointer);
            return this;
        }

        Settings create();
    }
}
